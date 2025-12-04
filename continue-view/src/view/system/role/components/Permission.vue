<script lang="ts" setup>
import { ref, nextTick, watch, computed, onMounted } from 'vue'
import { isMobile } from '@/utils'
import { useTable } from '@/hooks'
import { ElMessage } from 'element-plus'
import has from '@/utils/has'
import { type RolePermissionResp, getRole, listRolePermissionTree, updateRolePermission } from '@/apis/system/role'
import { type TableInstance, type TreeNodeData } from '@arco-design/web-vue'
import GiTable from '@/components/GiTable/src/GiTable.vue'
const columns: TableInstance['columns'] = [
    { title: '菜单', dataIndex: 'title', slotName: 'title', width: 170, ellipsis: true, tooltip: true, fixed: !isMobile() ? 'left' : undefined },
    { title: '权限', dataIndex: 'permissions', slotName: 'permissions' },
]
interface ExtendedRolePermissionResp extends RolePermissionResp {
    checkedPermissions?: (string | number)[]
    isChecked?: boolean
    disabled?: boolean
}

const props = withDefaults(defineProps<Props>(), {
    roleId: '',
})

interface Props {
    roleId: string
}
const disabled = ref(false)
//格式化数据，让菜单原始项和权限项分开
const transformMenu = (menus: RolePermissionResp[]): ExtendedRolePermissionResp[] => {
    return menus.map((item): ExtendedRolePermissionResp => {
        // 如果当前项有子项，递归处理子项
        if (item.children && item.children.length > 0) {
            // 过滤出 permission 不为空的子项
            const permissions = item.children.filter((child) => child.permission).map((child): RolePermissionResp => ({
                id: child.id,
                title: child.title,
                parentId: child.parentId,
                permission: child.permission,
                isChecked: false,
            }))

            // 过滤出 permission 为空的子项
            item.children = item.children.filter((child) => !child.permission)

            // 如果有权限，将其添加到当前项的 permissions 属性中
            if (permissions.length > 0) {
                // 关键：用 ref 包装数组，确保响应式
                const checkedPermissions = ref<(string | number)[]>(
                    permissions.filter((p) => p.isChecked).map((p) => p.id)
                );
                (item as ExtendedRolePermissionResp).permissions = permissions;
                (item as ExtendedRolePermissionResp).checkedPermissions = checkedPermissions.value;
            }

            // 递归处理剩余的子项
            item.children = transformMenu(item.children)

            // 如果 children 为空数组，移除 children 属性
            if (item.children.length === 0) {
                delete item.children
            }
        }

        return item
    })
}
// 查找指定菜单 - 使用 Map 缓存优化查找性能
const menuMap = ref<Map<string | number, ExtendedRolePermissionResp>>(new Map())
// 构建菜单映射缓存
const buildMenuMap = (data: ExtendedRolePermissionResp[]) => {
    const map = new Map<string | number, ExtendedRolePermissionResp>()

    const traverse = (items: ExtendedRolePermissionResp[]) => {
        items.forEach((item) => {
            map.set(item.id, item)
            if (item.children?.length) {
                traverse(item.children as ExtendedRolePermissionResp[])
            }
        })
    }

    traverse(data)
    menuMap.value = map
}
// 更新表格数据的选中状态
const updateTableDataCheckedStatus = (data: ExtendedRolePermissionResp[], selectedKeys: (string | number)[]) => {
    data.forEach((item) => {
        // item.disabled = disabled.value
        // 设置菜单项的选中状态
        item.isChecked = selectedKeys.includes(item.id)
        // 设置权限的选中状态
        if (item.permissions) {
            item.permissions.forEach((permission) => {
                permission.isChecked = selectedKeys.includes(permission.id)
            })
            item.checkedPermissions = item.permissions
                .filter((permission) => selectedKeys.includes(permission.id))
                .map((permission) => permission.id)
        }
        // 递归处理子菜单
        if (item.children) {
            updateTableDataCheckedStatus(item.children as ExtendedRolePermissionResp[], selectedKeys)
        }
    })
}
// 是否父子联动
const isCascade = ref(true)
const tableRef = ref()

const selectedKeys = ref<Set<string | number>>(new Set())

const {
    tableData,
    loading,
    search,
} = useTable(() => listRolePermissionTree(), {
    immediate: true,
    formatResult(data: TreeNodeData[]) {
        return transformMenu(data)
    },
    onSuccess: () => {
        nextTick(() => {

        })
        // 构建菜单映射缓存
        buildMenuMap(tableData.value as ExtendedRolePermissionResp[])
        fetchRole()
        console.log(tableData.value)
        // 初始加载时应用已选中的权限
        if (selectedKeys.value.size > 0) {
            updateTableDataCheckedStatus(tableData.value as ExtendedRolePermissionResp[], Array.from(selectedKeys.value))
        }
    },
})
// 加载角色详情
const fetchRole = async (id: string = '1') => {
    try {
        loading.value = true
        disabled.value = !has.hasPermOr(['system:role:updatePermission'])
        // 查询角色详情
        const { data } = await getRole(id)
        if (!disabled.value) {
            disabled.value = data.isSystem
        }
        // 更新选中键集合
        selectedKeys.value = new Set(data.menuIds)
        // 重新构建菜单映射缓存
        buildMenuMap(tableData.value as ExtendedRolePermissionResp[])
        // 更新表格数据的选中状态
        updateTableDataCheckedStatus(tableData.value as ExtendedRolePermissionResp[], data.menuIds)
        // 手动设置表格行的选中状态，确保组件响应
        await nextTick(() => {
            // tableRef.value?.tableRef?.selectAll(false)
            tableRef.value?.tableRef?.toggleRowSelection(data, true)
        })
    } catch (err: any) {
        console.log(err)
    } finally {
        loading.value = false
    }
}
// 监听 roleId 的变化
watch(
    () => props.roleId,
    async (newRoleId) => {
        if (newRoleId) {
            await fetchRole(newRoleId)
        }
    },
    { immediate: true },
)
const cascadeSelectChild = (record: ExtendedRolePermissionResp, isCascade: boolean) => {
    if (!isCascade) return
    //批量处理子菜单
    if (record.children && record.children.length > 0) {
        record.children.forEach((child) => {
            const extendChild = child as ExtendedRolePermissionResp
            extendChild.isChecked = record.isChecked
            tableRef.value?.tableRef?.toggleRowSelection(extendChild, extendChild.isChecked)
            //批量操作提高性能
            if (extendChild.isChecked) {
                selectedKeys.value.add(extendChild.id)
            }
            else {
                selectedKeys.value.delete(extendChild.id)
            }
            if (child.children && child.children.length > 0) {
                cascadeSelectChild(child, isCascade)
            }
        })
    }
    //批量处理权限

    // 批量处理权限
    if (record.permissions && record.permissions.length > 0) {
        const checkedPermissions: (string | number)[] = []
        record.permissions.forEach((permission) => {
            permission.isChecked = record.isChecked
            if (permission.isChecked) {
                selectedKeys.value.add(permission.id)
                checkedPermissions.push(permission.id)
            } else {
                selectedKeys.value.delete(permission.id)
            }
        })
        record.checkedPermissions = checkedPermissions
    }
}
// 查找指定菜单
const findItem = (id: string | number): ExtendedRolePermissionResp | null => {
    return menuMap.value.get(id) || null
}
// 级联选中父项目
const cascadeSelectParent = (record: ExtendedRolePermissionResp, isCascade: boolean) => {
    if (!isCascade || !record.parentId || record.parentId === '0') return

    const parent = findItem(record.parentId)
    if (!parent) return

    // 检查父项目的所有子项是否有被选中的
    const hasCheckedChildren = parent.children?.some((child) => (child as ExtendedRolePermissionResp).isChecked)
    parent.isChecked = hasCheckedChildren || false

    // 更新表格选中状态
    tableRef.value?.tableRef?.toggleRowSelection(parent.id, parent.isChecked)

    // 更新选中键集合
    if (parent.isChecked) {
        selectedKeys.value.add(parent.id)
    } else {
        selectedKeys.value.delete(parent.id)
    }

    // 递归处理父级的父级
    if (parent.parentId && parent.parentId !== '0') {
        cascadeSelectParent(parent, isCascade)
    }
}
// 选中
const select: TableInstance['onSelect'] = (selection: any, record: any) => {
    const extendedRecord = record as ExtendedRolePermissionResp
    // 如果处于禁用状态，直接返回，不执行任何逻辑
    if (disabled.value || extendedRecord.disabled) {
        return
    }

    // selection 是所有选中行的数组，若包含当前行，则为选中状态
    const isChecked = selection.includes(record);

    // 更新全局选中集合（选中则添加，取消则删除）
    if (isChecked) {
        selectedKeys.value.add(extendedRecord.id);
    } else {
        selectedKeys.value.delete(extendedRecord.id);
    }

    // 更新当前行自身的选中状态标记
    extendedRecord.isChecked = isChecked;

    // 级联操作（不变，继续支持父子联动）
    cascadeSelectChild(extendedRecord, isCascade.value);
    cascadeSelectParent(extendedRecord, isCascade.value);
    console.log(record)
}
// 保存
const save = async () => {
    await updateRolePermission(props.roleId, {
        menuIds: Array.from(selectedKeys.value),
        menuCheckStrictly: isCascade.value,
    })
    ElMessage.success('保存成功')
}
//选中权限
const selectPermissions = (row: ExtendedRolePermissionResp) => {
    // 如果处于禁用状态，直接返回，不执行任何逻辑
    if (disabled.value || row.disabled) {
        return
    }
    // console.log(row)
    // const checkPermissions = record.checkedPermissions || []
    const checkPermissions = row.checkedPermissions || []


    if (checkPermissions.length === 0) {
        if (isCascade.value) {
            row.isChecked = false
            selectedKeys.value.delete(row.id)
            // tableRef.value?.tableRef?.select(row.id, row.isChecked)
            tableRef.value.tableRef.toggleRowSelection(row, row.isChecked)
            cascadeSelectParent(row, isCascade.value)
        }
        row.permissions?.forEach((permission: any) => {
            permission.isChecked = false
            selectedKeys.value.delete(permission.id)
        })


    }
    // 选中
    if (checkPermissions.length > 0) {
        if (isCascade.value) {
            row.isChecked = true
            selectedKeys.value.add(row.id)
            // 正确写法

            tableRef.value.tableRef.toggleRowSelection(row, row.isChecked)
            // console.log(tableRef.value?.tableRef?.getSelectionRows())

            cascadeSelectParent(row, isCascade.value)
        }
        row.permissions?.forEach((permission: any) => {
            permission.isChecked = checkPermissions.includes(permission.id)
            permission.isChecked
                ? selectedKeys.value.add(permission.id)
                : selectedKeys.value.delete(permission.id)
        })
        // 找到 tableData 中的源对象（通过 menuMap 或 findItem）

        // console.log(row)

        // console.log(tableData.value)
        // console.log(selectedKeys.value)

    }
    const sourceItem = findItem(row.id);
    if (sourceItem) {
        // 将 row 的属性合并到 sourceItem（源对象），覆盖源对象的旧值
        Object.assign(sourceItem, row);
    }

}
</script>

<template>

    <GiTable :data="tableData" :pagination="false" :loading="loading" :default-expand-all="true"
        :disabled-tools="['fullscreen', 'size', 'setting']"
        :row-selection="disabled ? false : { type: 'checkbox', selectedRowKeys: selectedKeys }" :columns="columns"
        ref="tableRef" row-key="id" @select="select">
        <template #toolbar-left>
            <el-button @click="save" v-permission="['system:role:updatePermission']" :disabled="disabled">
                <GiSvgIcon name="save"></GiSvgIcon>
                保存权限
            </el-button>
        </template>
        <template #toolbar-right>
            <el-space>
                <el-radio-group v-model="isCascade" :disabled="disabled">
                    <el-radio-button :value="true">节点关联</el-radio-button>
                    <el-radio-button :value="false">节点独立</el-radio-button>
                </el-radio-group>
                <el-button>
                    <GiSvgIcon name="fold"></GiSvgIcon>
                    <span>折叠</span>
                </el-button>
            </el-space>
        </template>
        <template #title="{ row }">
            <GiSvgIcon :name="row.icon" :size="15" />
            <span style="margin-left: 5px; vertical-align: middle">{{ row.title }}</span>
        </template>
        <template #permissions="{ row }">
            <div v-if="row.permissions && row.permissions.length > 0">
                <el-checkbox-group v-model="row.checkedPermissions" @change="selectPermissions(row)"
                    :disabled="disabled || row.disabled">
                    <el-checkbox v-for="permission in row.permissions" :key="permission.id" :value="permission.id">
                        {{ permission.title }}
                    </el-checkbox>
                </el-checkbox-group>
            </div>
        </template>
    </GiTable>
</template>

<style lang="scss" scoped></style>