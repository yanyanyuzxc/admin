/*
 * Copyright (c) 2022-present Charles7c Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.continew.admin.tenant.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import me.ahoo.cosid.provider.IdGeneratorProvider;
import org.springframework.stereotype.Service;
import top.continew.admin.common.base.service.BaseServiceImpl;
import top.continew.admin.common.config.TenantProperties;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.admin.tenant.constant.TenantCacheConstants;
import top.continew.admin.tenant.constant.TenantConstants;
import top.continew.admin.tenant.handler.TenantDataHandler;
import top.continew.admin.tenant.mapper.TenantMapper;
import top.continew.admin.tenant.model.entity.PackageDO;
import top.continew.admin.tenant.model.entity.TenantDO;
import top.continew.admin.tenant.model.enums.TenantIsolationLevelEnum;
import top.continew.admin.tenant.model.query.TenantQuery;
import top.continew.admin.tenant.model.req.TenantReq;
import top.continew.admin.tenant.model.resp.TenantAvailableResp;
import top.continew.admin.tenant.model.resp.TenantDetailResp;
import top.continew.admin.tenant.model.resp.TenantResp;
import top.continew.admin.tenant.service.DatasourceService;
import top.continew.admin.tenant.service.PackageMenuService;
import top.continew.admin.tenant.service.PackageService;
import top.continew.admin.tenant.service.TenantService;
import top.continew.starter.cache.redisson.util.RedisUtils;
import top.continew.starter.core.util.validation.CheckUtils;
import top.continew.starter.extension.crud.model.entity.BaseIdDO;
import top.continew.starter.extension.tenant.TenantHandler;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 租户业务实现
 *
 * @author 小熊
 * @author Charles7c
 * @since 2024/11/26 17:20
 */
@Service
@RequiredArgsConstructor
public class TenantServiceImpl extends BaseServiceImpl<TenantMapper, TenantDO, TenantResp, TenantDetailResp, TenantQuery, TenantReq> implements TenantService {

    private final TenantProperties tenantProperties;
    private final IdGeneratorProvider idGeneratorProvider;
    private final PackageMenuService packageMenuService;
    private final PackageService packageService;
    private final DatasourceService datasourceService;
    private final TenantDataHandler tenantDataHandler;

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public Long create(TenantReq req) {
        this.checkNameRepeat(req.getName(), null);
        // 检查租户套餐
        List<Long> menuIds = packageMenuService.listMenuIdsByPackageId(req.getPackageId());
        CheckUtils.throwIfEmpty(menuIds, "所选套餐无可用菜单");
        // 生成租户编码
        req.setCode(this.generateCode());
        // 新增信息
        Long id = super.create(req);
        // 初始化数据库
        if (TenantIsolationLevelEnum.DATASOURCE.equals(req.getIsolationLevel())) {
            datasourceService.initDb(TenantConstants.TENANT_DB_PREFIX + req.getCode(), req.getDatasourceId());
        }
        // 初始化租户数据
        req.setId(id);
        tenantDataHandler.init(req);
        return id;
    }

    @Override
    public void beforeUpdate(TenantReq req, Long id) {
        this.checkNameRepeat(req.getName(), id);
    }

    @Override
    public void afterUpdate(TenantReq req, TenantDO entity) {
        // 更新租户缓存
        RedisUtils.set(TenantCacheConstants.TENANT_KEY_PREFIX + entity.getId(), entity);
    }

    @Override
    public void beforeDelete(List<Long> ids) {
        // 在租户中执行数据清除
        for (Long id : ids) {
            SpringUtil.getBean(TenantHandler.class).execute(id, tenantDataHandler::clear);
        }
    }

    @Override
    public void afterDelete(List<Long> ids) {
        ids.forEach(id -> RedisUtils.delete(TenantCacheConstants.TENANT_KEY_PREFIX + id));
    }

    @Override
    @Cached(name = TenantCacheConstants.TENANT_KEY_PREFIX, key = "#id")
    public TenantDO getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    public TenantDO checkStatus(Long id) {
        TenantDO tenant = this.getById(id);
        if (!tenantProperties.isEnabled() || id.equals(tenantProperties.getSuperTenantId())) {
            return tenant;
        }
        CheckUtils.throwIfNotEqual(DisEnableStatusEnum.ENABLE.getValue(), tenant.getStatus(), "此租户已被禁用");
        CheckUtils.throwIf(tenant.getExpireTime() != null && tenant.getExpireTime()
            .isBefore(LocalDateTime.now()), "此租户已过期");
        // 检查套餐
        PackageDO tenantPackage = packageService.getById(tenant.getPackageId());
        CheckUtils.throwIfNotEqual(DisEnableStatusEnum.ENABLE.getValue(), tenantPackage.getStatus(), "此租户套餐已被禁用");
        return tenant;
    }

    @Override
    public List<TenantAvailableResp> getAvailableList() {
        List<TenantDO> tenantList = baseMapper.selectList(Wrappers.lambdaQuery(TenantDO.class)
            .select(TenantDO::getName, BaseIdDO::getId, TenantDO::getDomain)
            .eq(TenantDO::getStatus, DisEnableStatusEnum.ENABLE.getValue())
            .and(t -> t.isNull(TenantDO::getExpireTime).or().ge(TenantDO::getExpireTime, DateUtil.date())));
        return BeanUtil.copyToList(tenantList, TenantAvailableResp.class);
    }

    /**
     * 名称是否存在
     *
     * @param name 名称
     * @param id   ID
     */
    private void checkNameRepeat(String name, Long id) {
        CheckUtils.throwIf(baseMapper.lambdaQuery()
            .eq(TenantDO::getName, name)
            .ne(id != null, TenantDO::getId, id)
            .exists(), "名称为 [{}] 的租户已存在", name);
    }

    /**
     * 生成租户编码
     *
     * @return 租户编码
     */
    private String generateCode() {
        String code;
        do {
            code = idGeneratorProvider.getRequired(TenantConstants.CODE_GENERATOR_KEY).generateAsString();
        } while (baseMapper.lambdaQuery().eq(TenantDO::getCode, code).exists());
        return code;
    }
}