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

package top.continew.admin.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.common.base.service.BaseServiceImpl;
import top.continew.admin.common.constant.CacheConstants;
import top.continew.admin.common.constant.SysConstants;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.admin.system.enums.MenuTypeEnum;
import top.continew.admin.system.mapper.MenuMapper;
import top.continew.admin.system.mapper.RoleMapper;
import top.continew.admin.system.model.entity.MenuDO;
import top.continew.admin.system.model.entity.RoleDO;
import top.continew.admin.system.model.entity.RoleMenuDO;
import top.continew.admin.system.model.query.MenuQuery;
import top.continew.admin.system.model.req.MenuReq;
import top.continew.admin.system.model.resp.MenuResp;
import top.continew.admin.system.service.MenuService;
import top.continew.admin.system.service.RoleMenuService;
import top.continew.starter.cache.redisson.util.RedisUtils;
import top.continew.starter.core.constant.StringConstants;
import top.continew.starter.core.util.validation.CheckUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 菜单业务实现
 *
 * @author Charles7c
 * @since 2023/2/15 20:30
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends BaseServiceImpl<MenuMapper, MenuDO, MenuResp, MenuResp, MenuQuery, MenuReq> implements MenuService {

    private final RoleMenuService roleMenuService;
    private final RoleMapper roleMapper;

    @Override
    public Long create(MenuReq req) {
        this.checkTitleRepeat(req.getTitle(), req.getParentId(), null);
        // 目录和菜单的组件名称不能重复
        if (!MenuTypeEnum.BUTTON.equals(req.getType())) {
            this.checkNameRepeat(req.getName(), null);
        }
        // 目录类型菜单，默认为 Layout
        if (MenuTypeEnum.DIR.equals(req.getType())) {
            req.setComponent(StrUtil.blankToDefault(req.getComponent(), "Layout"));
        }
        RedisUtils.deleteByPattern(CacheConstants.ROLE_MENU_KEY_PREFIX + StringConstants.ASTERISK);
        return super.create(req);
    }

    @Override
    public void update(MenuReq req, Long id) {
        this.checkTitleRepeat(req.getTitle(), req.getParentId(), id);
        // 目录和菜单的组件名称不能重复
        if (!MenuTypeEnum.BUTTON.equals(req.getType())) {
            this.checkNameRepeat(req.getName(), id);
        }
        MenuDO oldMenu = super.getById(id);
        CheckUtils.throwIfNotEqual(req.getType(), oldMenu.getType(), "不允许修改菜单类型");
        super.update(req, id);
        RedisUtils.deleteByPattern(CacheConstants.ROLE_MENU_KEY_PREFIX + StringConstants.ASTERISK);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        baseMapper.lambdaUpdate().in(MenuDO::getParentId, ids).remove();
        super.delete(ids);
        RedisUtils.deleteByPattern(CacheConstants.ROLE_MENU_KEY_PREFIX + StringConstants.ASTERISK);
    }

    @Override
    public Set<String> listPermissionByUserId(Long userId) {
        return baseMapper.selectPermissionByUserId(userId);
    }

    @Override
    @Cached(key = "#roleId", name = CacheConstants.ROLE_MENU_KEY_PREFIX)
    public List<MenuResp> listByRoleId(Long roleId) {
        if (SysConstants.SUPER_ROLE_ID.equals(roleId)) {
            return super.list(new MenuQuery(DisEnableStatusEnum.ENABLE), null);
        }
        List<MenuDO> menuList = baseMapper.selectListByRoleId(roleId);
        List<MenuResp> list = BeanUtil.copyToList(menuList, MenuResp.class);
        list.forEach(super::fill);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTenantMenus(List<MenuDO> menuList) {
        if (CollUtil.isEmpty(menuList)) {
            return;
        }
        List<Long> delIds = new ArrayList<>();
        for (MenuDO menu : menuList) {
            MenuDO parentMenu = baseMapper.query()
                .eq(menu.getType().equals(MenuTypeEnum.BUTTON), "CONCAT(title,permission)", menu.getTitle() + menu
                    .getPermission())
                .eq(!menu.getType().equals(MenuTypeEnum.BUTTON), "name", menu.getName())
                .one();
            if (parentMenu != null) {
                delIds.add(parentMenu.getId());
            }
        }
        if (!delIds.isEmpty()) {
            // 菜单删除
            this.delete(delIds);
            // 删除绑定关系
            roleMenuService.remove(Wrappers.lambdaQuery(RoleMenuDO.class).in(RoleMenuDO::getMenuId, delIds));
        }
        // 删除缓存
        RedisUtils.deleteByPattern(CacheConstants.ROLE_MENU_KEY_PREFIX + StringConstants.ASTERISK);
    }

    @Override
    public void addTenantMenu(MenuDO menu, MenuDO parentMenu) {
        Long parentId = SysConstants.SUPER_PARENT_ID;
        if (parentMenu != null) {
            MenuDO parent = baseMapper.query()
                .eq(parentMenu.getType().equals(MenuTypeEnum.BUTTON), "CONCAT(title,permission)", parentMenu
                    .getTitle() + parentMenu.getPermission())
                .eq(!parentMenu.getType().equals(MenuTypeEnum.BUTTON), "name", parentMenu.getName())
                .one();
            parentId = parent.getId();
        }
        menu.setId(null);
        menu.setParentId(parentId);
        // 菜单新增
        baseMapper.insert(menu);
        // 管理员绑定菜单
        RoleDO role = roleMapper.selectOne(Wrappers.lambdaQuery(RoleDO.class)
            .eq(RoleDO::getCode, SysConstants.TENANT_ADMIN_ROLE_CODE));
        roleMenuService.save(new RoleMenuDO(role.getId(), menu.getId()));
        // 删除缓存
        RedisUtils.deleteByPattern(CacheConstants.ROLE_MENU_KEY_PREFIX + StringConstants.ASTERISK);
    }

    @Override
    public List<Long> listExcludeTenantMenu() {
        RoleDO role = roleMapper.selectOne(Wrappers.lambdaQuery(RoleDO.class)
            .eq(RoleDO::getCode, SysConstants.TENANT_ADMIN_ROLE_CODE));
        if (role == null) {
            return ListUtil.of();
        }
        List<Long> allMenuList = list().stream().map(MenuDO::getId).toList();
        List<Long> menuList = baseMapper.selectListByRoleId(role.getId()).stream().map(MenuDO::getId).toList();
        return CollUtil.disjunction(allMenuList, menuList).stream().toList();
    }

    /**
     * 检查标题是否重复
     *
     * @param title    标题
     * @param parentId 上级 ID
     * @param id       ID
     */
    private void checkTitleRepeat(String title, Long parentId, Long id) {
        CheckUtils.throwIf(baseMapper.lambdaQuery()
            .eq(MenuDO::getTitle, title)
            .eq(MenuDO::getParentId, parentId)
            .ne(id != null, MenuDO::getId, id)
            .exists(), "标题为 [{}] 的菜单已存在", title);
    }

    /**
     * 检查组件名称是否重复
     *
     * @param name 组件名称
     * @param id   ID
     */
    private void checkNameRepeat(String name, Long id) {
        CheckUtils.throwIf(baseMapper.lambdaQuery()
            .eq(MenuDO::getName, name)
            .ne(MenuDO::getType, MenuTypeEnum.BUTTON)
            .ne(id != null, MenuDO::getId, id)
            .exists(), "组件名称为 [{}] 的菜单已存在", name);
    }
}
