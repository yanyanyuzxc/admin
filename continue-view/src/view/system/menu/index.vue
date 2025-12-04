<script lang="ts" setup>
import type { TableInstance } from '@arco-design/web-vue'
import AddModal from './AddModal.vue'
import { type MenuResp, clearMenuCache, deleteMenu, listMenu } from '@/apis/system/menu.ts'
import GiTable from '@/components/GiTable/src/GiTable.vue'
import { useTable } from '@/hooks'
import { ref, computed, watch, onMounted, nextTick } from 'vue'
import { isMobile } from '@/utils'
import GiSvgIcon from '@/components/GiSvgIcon/index.vue'
import has from '@/utils/has'
import Ad from '@/layout/ad.vue'
const {
    tableData,
    loading,
    search,
    handleDelete,
} = useTable(() => listMenu(), { immediate: true })
const columns: TableInstance['columns'] = [
    { title: '菜单标题', dataIndex: 'title', slotName: 'title', width: 170, fixed: !isMobile() ? 'left' : undefined },
    { title: '类型', dataIndex: 'type', slotName: 'type', align: 'center' },
    { title: '状态', dataIndex: 'status', slotName: 'status', align: 'center' },
    { title: '排序', dataIndex: 'sort', align: 'center', show: false },
    { title: '路由地址', dataIndex: 'path', ellipsis: true, tooltip: true },
    { title: '组件名称', dataIndex: 'name', ellipsis: true, tooltip: true },
    { title: '组件路径', dataIndex: 'component', minWidth: 180, ellipsis: true, tooltip: true },
    { title: '权限标识', dataIndex: 'permission', minWidth: 180, ellipsis: true, tooltip: true },
    { title: '外链', dataIndex: 'isExternal', slotName: 'isExternal', align: 'center' },
    { title: '隐藏', dataIndex: 'isHidden', slotName: 'isHidden', align: 'center' },
    { title: '缓存', dataIndex: 'isCache', slotName: 'isCache', align: 'center' },
    { title: '创建人', dataIndex: 'createUserString', ellipsis: true, tooltip: true, show: false },
    { title: '创建时间', dataIndex: 'createTime', width: 180 },
    { title: '修改人', dataIndex: 'updateUserString', ellipsis: true, tooltip: true, show: false },
    { title: '修改时间', dataIndex: 'updateTime', width: 180, show: false },
    {
        title: '操作',
        dataIndex: 'action',
        slotName: 'action',
        width: 160,
        align: 'center',
        fixed: !isMobile() ? 'right' : undefined,
        show: has.hasPermOr(['system:menu:update', 'system:menu:delete', 'system:menu:create']),
    },
]
const dataList = computed(() => {
    if (!title.value && !path.value && !permission.value) return tableData.value
    return searchData(title.value, path.value, permission.value)
})
//过滤树
// 重置
const reset = () => {
    title.value = '',
        path.value = '',
        permission.value = ''
}
const searchData = (title: string, path: string, permission: string) => {
    const loop = (data: MenuResp[]) => {
        const result = [] as MenuResp[]
        data.forEach((item: MenuResp) => {
            if (
                (!title || item.title?.toLowerCase().includes(title.toLowerCase()))
                && (!path || item.path?.toLowerCase().includes(path.toLowerCase()))
                && (!permission || item.permission?.toLowerCase().includes(permission.toLowerCase()))
            ) {
                result.push({ ...item })
            } else if (item.children) {
                const filterData = loop(item.children)
                if (filterData.length) {
                    result.push({
                        ...item,
                        children: filterData,
                    })
                }
            }
        })
        return result
    }
    return loop(tableData.value)
}
const title = ref('')
const path = ref('')
const permission = ref('')
const isExpanded = ref(false)
const tableRef = ref<typeof GiTable>()
const onExpanded = () => { }
const onClearCache = () => { }
// 删除
const onDelete = (row: MenuResp) => {
    return handleDelete(() => deleteMenu(row.id), {
        content: `是否确定菜单「${row.title}」？`,
        showModal: true,
    })
}
const AddModalRef = ref<InstanceType<typeof AddModal>>()
const onAdd = (parentId?: number) => {
    AddModalRef.value?.Add()
}
const onUpdate = (row: MenuResp) => { AddModalRef.value?.onUpdate(row.id) }
</script>

<template>
    <GiPageLayout class="page-layout">
        <GiTable ref="tableRef" :default-expand-all="false" row-key="id" :data="dataList" :columns="columns"
            :loading="loading" :pagination="false" :disabled-column-keys="['title']" @refresh="search">

            <template #toolbar-left>
                <el-input v-model="title" placeholder="搜索菜单标题">
                    <template #prefix>
                        <Search />
                    </template>
                </el-input>
                <el-input v-model="path" placeholder="搜索路由地址">
                    <template #prefix>
                        <Search />
                    </template>
                </el-input>
                <el-input v-model="permission" placeholder="搜索权限标识">
                    <template #prefix>
                        <Search />
                    </template>
                </el-input>
                <el-button @click="reset" icon="Refresh">

                    <template #default>重置</template>
                </el-button>
            </template>
            <template #toolbar-right>
                <el-button v-permission="['system:menu:create']" type="primary" @click="onAdd()" icon="Plus">

                    <template #default>新增</template>
                </el-button>
                <el-button v-permission="['system:menu:clearCache']" type="danger" icon="Delete" status="warning"
                    @click="onClearCache">

                    <template #default>清除缓存</template>
                </el-button>
                <el-button @click="onExpanded">
                    <template #icon>
                        <icon-list v-if="isExpanded" />
                        <icon-mind-mapping v-else />
                    </template>
                    <template #default>
                        <span v-if="!isExpanded">展开</span>
                        <span v-else>折叠</span>
                    </template>
                </el-button>
            </template>
            <template #title="{ row }">
                <GiSvgIcon :name="row.icon" :size="15" />
                <span style="margin-left: 5px; vertical-align: middle">{{ row.title }}</span>
            </template>
            <template #type="{ row }">
                <el-tag v-if="row.type === 1" color="arcoblue">目录</el-tag>
                <el-tag v-if="row.type === 2" color="green">菜单</el-tag>
                <el-tag v-if="row.type === 3">按钮</el-tag>
            </template>
            <template #status="{ row }">
                <GiCellStatus :status="row.status" />
            </template>
            <template #isExternal="{ row }">
                <el-tag v-if="row.isExternal" size="small">是</el-tag>
                <el-tag v-else size="small" type="danger">否</el-tag>
            </template>
            <template #isHidden="{ row }">
                <el-tag v-if="row.isHidden" size="small">是</el-tag>
                <el-tag v-else size="small" type="danger">否</el-tag>
            </template>
            <template #isCache="{ row }">
                <el-tag v-if="row.isCache" size="small">是</el-tag>
                <el-tag v-else size="small" type="danger">否</el-tag>
            </template>
            <template #action="{ row }">
                <el-space>
                    <el-link v-permission="['system:menu:update']" title="修改" @click="onUpdate(row)">修改</el-link>
                    <el-link v-permission="['system:menu:delete']" type="danger" title="删除"
                        @click="onDelete(row)">删除</el-link>
                    <el-link v-permission="['system:menu:create']" :disabled="![1, 2].includes(row.type)"
                        :title="![1, 2].includes(row.type) ? '不可添加下级菜单' : '新增'" @click="onAdd(row.id)">
                        新增
                    </el-link>
                </el-space>
            </template>
        </GiTable>
        <AddModal ref="AddModalRef" :menus="dataList" @save-success="search"></AddModal>
    </GiPageLayout>
</template>

<style lang="scss" scoped>
.page-layout {
    padding: 10px;
}

:deep(.el-table th.el-table__cell) {
    /* 修改表头背景色 */
    background-color: rgb(246, 248, 250)
        /* 这里替换成你想要的颜色 */
}

.el-input {
    width: 200px;
    margin-right: 20px;


}
</style>