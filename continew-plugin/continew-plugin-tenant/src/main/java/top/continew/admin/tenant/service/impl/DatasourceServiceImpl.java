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
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.util.StrUtil;
import com.alicp.jetcache.anno.Cached;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import top.continew.admin.common.base.service.BaseServiceImpl;
import top.continew.admin.common.util.SecureUtils;
import top.continew.admin.tenant.constant.TenantCacheConstants;
import top.continew.admin.tenant.mapper.DatasourceMapper;
import top.continew.admin.tenant.mapper.TenantMapper;
import top.continew.admin.tenant.model.entity.DatasourceDO;
import top.continew.admin.tenant.model.entity.TenantDO;
import top.continew.admin.tenant.model.query.DatasourceQuery;
import top.continew.admin.tenant.model.req.DatasourceReq;
import top.continew.admin.tenant.model.resp.DatasourceDetailResp;
import top.continew.admin.tenant.model.resp.DatasourceResp;
import top.continew.admin.tenant.service.DatasourceService;
import top.continew.starter.cache.redisson.util.RedisUtils;
import top.continew.starter.core.constant.StringConstants;
import top.continew.starter.core.util.ExceptionUtils;
import top.continew.starter.core.util.validation.CheckUtils;
import top.continew.starter.core.util.validation.ValidationUtils;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

/**
 * 数据源业务实现
 *
 * @author 小熊
 * @author Charles7c
 * @since 2024/12/12 19:13
 */
@Service
@RequiredArgsConstructor
public class DatasourceServiceImpl extends BaseServiceImpl<DatasourceMapper, DatasourceDO, DatasourceResp, DatasourceDetailResp, DatasourceQuery, DatasourceReq> implements DatasourceService {

    private final TenantMapper tenantMapper;

    @Override
    @Cached(name = TenantCacheConstants.TENANT_DATASOURCE_KEY_PREFIX, key = "#id")
    public DatasourceDetailResp get(Long id) {
        return super.get(id);
    }

    @Override
    public void beforeCreate(DatasourceReq req) {
        // 解密密码
        req.setPassword(this.decryptPassword(req.getPassword(), null));
        // 检查是否重复
        this.checkRepeat(req, null);
        // 测试连接
        req.getDatabaseType().testConnection(req);
    }

    @Override
    public void beforeUpdate(DatasourceReq req, Long id) {
        DatasourceDO oldDatasource = super.getById(id);
        // 解密密码
        req.setPassword(this.decryptPassword(req.getPassword(), oldDatasource));
        // 检查是否重复
        this.checkRepeat(req, id);
        CheckUtils.throwIf(oldDatasource.getDatabaseType() != req.getDatabaseType(), "数据库类型不能修改");
        // 测试连接
        boolean isUpdated = this.isUpdated(req, oldDatasource);
        if (isUpdated) {
            req.getDatabaseType().testConnection(req);
        }
    }

    @Override
    public void afterUpdate(DatasourceReq req, DatasourceDO entity) {
        RedisUtils.delete(TenantCacheConstants.TENANT_DATASOURCE_KEY_PREFIX + entity.getId());
    }

    @Override
    public void beforeDelete(List<Long> ids) {
        CheckUtils.throwIf(tenantMapper.lambdaQuery().in(TenantDO::getDatasourceId, ids).exists(), "所选数据源存在关联租户，不允许删除");
    }

    @Override
    public void afterDelete(List<Long> ids) {
        ids.forEach(id -> RedisUtils.delete(TenantCacheConstants.TENANT_DATASOURCE_KEY_PREFIX + id));
    }

    @Override
    public void testConnection(Long id) {
        DatasourceDO datasource = super.getById(id);
        datasource.getDatabaseType().testConnection(BeanUtil.copyProperties(datasource, DatasourceReq.class));
    }

    @Override
    public void initDb(String databaseName, Long id) {
        DatasourceDO datasource = super.getById(id);
        DataSource ds = datasource.getDatabaseType()
            .buildDataSource(BeanUtil.copyProperties(datasource, DatasourceReq.class));
        JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
        // 建库
        jdbcTemplate.execute("CREATE DATABASE %s CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
            .formatted(databaseName));
        jdbcTemplate.execute("USE %s;".formatted(databaseName));
        // TODO 初始化数据
        Resource resource = new ClassPathResource("db/changelog/mysql/tenant_table.sql");
        Arrays.stream(resource.readUtf8Str().split(StringConstants.SEMICOLON))
            .map(String::trim)
            .filter(StrUtil::isNotBlank)
            .forEach(jdbcTemplate::execute);
    }

    /**
     * 解密密码
     *
     * @param encryptPassword 加密的密码
     * @param oldDatasource   旧数据源
     * @return 解密后的密码
     */
    private String decryptPassword(String encryptPassword, DatasourceDO oldDatasource) {
        // 修改时，密码为空将不更改密码
        if (oldDatasource != null && StrUtil.isBlank(encryptPassword)) {
            return oldDatasource.getPassword();
        }
        // 解密
        String decryptPassword = ExceptionUtils.exToNull(() -> SecureUtils.decryptByRsaPrivateKey(encryptPassword));
        ValidationUtils.throwIfNull(decryptPassword, "密码解密失败");
        ValidationUtils.throwIf(decryptPassword.length() > 128, "密码长度不能超过 128 个字符");
        return decryptPassword;
    }

    /**
     * 检查数据源是否存在
     *
     * @param req 数据源信息
     * @param id  ID
     */
    private void checkRepeat(DatasourceReq req, Long id) {
        CheckUtils.throwIf(baseMapper.lambdaQuery()
            .eq(DatasourceDO::getHost, req.getHost())
            .eq(DatasourceDO::getPort, req.getPort())
            .eq(DatasourceDO::getUsername, req.getUsername())
            .ne(id != null, DatasourceDO::getId, id)
            .exists(), "相同配置数据源已存在");
    }

    /**
     * 是否更新了配置
     *
     * @param req           数据源请求参数
     * @param oldDatasource 旧数据源
     * @return 是否更新了配置
     */
    private boolean isUpdated(DatasourceReq req, DatasourceDO oldDatasource) {
        return !(oldDatasource.getHost().equals(req.getHost()) && oldDatasource.getPort()
            .equals(req.getPort()) && oldDatasource.getUsername().equals(req.getUsername()) && oldDatasource
                .getPassword()
                .equals(req.getPassword()));
    }
}