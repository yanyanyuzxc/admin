<script lang="ts" setup>
import 'vue3-tree-org/lib/vue3-tree-org.css'
import { Vue3TreeOrg } from 'vue3-tree-org'
import { type DeptQuery, type DeptResp, deleteDept, exportDept, listDept } from '@/apis/system/dept'
import { useDownload, useTable } from '@/hooks'
import { isMobile } from '@/utils'
import has from '@/utils/has'
import type { TableInstance } from '@arco-design/web-vue'
const columns: TableInstance['columns'] = [
    { title: '名称', dataIndex: 'name', minWidth: 170, ellipsis: true, tooltip: true },
    { title: '状态', dataIndex: 'status', slotName: 'status', align: 'center' },
    { title: '排序', dataIndex: 'sort', align: 'center', show: false },
    { title: '系统内置', dataIndex: 'isSystem', slotName: 'isSystem', align: 'center', show: false },
    { title: '描述', dataIndex: 'description', ellipsis: true, tooltip: true },
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
        show: has.hasPermOr(['system:dept:update', 'system:dept:delete', 'system:dept:create']),
    },
]
// 组织架构图右键菜单
const menus = [
    { name: '添加部门', command: 'add' },
    { name: '编辑部门', command: 'edit' },
    { name: '删除部门', command: 'delete' },
]
const handleAdd = (node: any) => { }
const tableRef = ref<TableInstance>()
const queryForm = reactive<DeptQuery>({})
const {
    tableData,
    loading,
    search,
    handleDelete,
} = useTable(() => listDept(queryForm), {
    immediate: true,
    onSuccess: () => {
        nextTick(() => {
        })
    }
},
)
const name = ref('')
const dataList = computed(() => {
    if (!name.value) return tableData.value
    return searchData(name.value)
})
//过滤树
const searchData = (name: string) => {

    const loop = (data: DeptResp[]) => {
        const result: DeptResp[] = []
        data.forEach((item: any) => {
            if (item.name?.toLowerCase().includes(name.toLowerCase())) {
                result.push({ ...item })
            }
            else if (item.children) {
                const filterData = loop(item.children)
                if (filterData.length) {
                    result.push({ ...item, children: filterData })
                }
            }

        })
        return result
    }
    return loop(tableData.value)
}
const onExport = () => { }
const onAdd = (id?: string) => { }
const onUpdate = (row: DeptResp) => { }
const onDelete = (row: DeptResp) => { }
const reset = () => { }
const viewType = ref('table')

</script>

<template>
    <GiPageLayout>
        <div class="main-container">
            <div class="filter-container">
                <el-radio-group v-model="viewType" style="margin-bottom: 16px;">
                    <el-radio-button value="table">表格视图</el-radio-button>
                    <el-radio-button value="tree">组织架构图</el-radio-button>
                </el-radio-group>
            </div>
            <GiTable ref="tableRef" row-key="id" :data="dataList" :columns="columns" :loading="loading"
                v-show="viewType === 'table'" :scroll="{ x: '100%', y: '100%', minWidth: 1000 }" :pagination="false"
                :disabled-column-keys="['name']" @refresh="search" default-expand-all>
                <!-- <template #expand-icon="{ expanded }">
                <IconDown v-if="expanded" />
                <IconRight v-else />
            </template> -->
                <template #toolbar-left>
                    <el-input v-model="name" placeholder="搜索名称" style="width: 200px; margin-right: 16px;">
                        <template #prefix>
                            <GiSvgIcon name="search" size="15"></GiSvgIcon>
                        </template>


                    </el-input>
                    <el-button @click="reset" icon="Refresh">

                        <template #default>重置</template>
                    </el-button>
                </template>
                <template #toolbar-right>
                    <el-button v-permission="['system:dept:create']" type="primary" @click="onAdd()">

                        <template #default>新增</template>
                    </el-button>
                    <el-button v-permission="['system:dept:export']" @click="onExport">

                        <template #default>导出</template>
                    </el-button>
                </template>
                <template #status="{ row }">
                    <GiCellStatus :status="row.status" />
                </template>
                <template #is-system="{ row }">
                    <el-tag v-if="row.isSystem" color="red" size="small">是</el-tag>
                    <el-tag v-else color="arcoblue" size="small">否</el-tag>
                </template>
                <template #action="{ row }">
                    <el-space>
                        <el-link v-permission="['system:dept:update']" title="修改" @click="onUpdate(row)"
                            type="primary">修改</el-link>
                        <el-link v-permission="['system:dept:delete']" type="danger" :disabled="row.isSystem"
                            :title="row.isSystem ? '系统内置数据不能删除' : '删除'" @click="onDelete(row)">
                            删除
                        </el-link>
                        <el-link v-permission="['system:dept:create']" title="新增" @click="onAdd(row.id)"
                            type="primary">新增</el-link>
                    </el-space>
                </template>
            </GiTable>
            <div v-show="viewType === 'tree'" class="tree-container">
                <el-card>
                    <el-dropdown>
                        <template #default>
                            <Vue3TreeOrg v-if="dataList.length" :data="dataList[0]"
                                :props="{ id: 'id', parentId: 'parentId', label: 'name', children: 'children' }" center
                                :collapsable="true" :horizontal="false" :define-menus="menus" :expand-all="true"
                                :default-expand-level="999" :node-add="handleAdd" :node-delete="onDelete"
                                :node-edit="onUpdate">

                            </Vue3TreeOrg>

                        </template>

                    </el-dropdown>
                </el-card>
            </div>
        </div>
    </GiPageLayout>
</template>

<style scoped lang="scss">
.main-container {
    width: 100%;
    height: 100%;

}

.filter-container {
    padding: 10px;
}

.el-dropdown {
    width: 100%;
}


:deep(.el-card) {
    box-shadow: none !important;
    border: none !important;
    border-radius: 0 !important;
}

:deep(.zm-draggable) {
    margin-top: 4px;
}




:deep(.zm-tree-org .zoom-container) {
    background-color: var(--color-bg-1);
    color: var(--color-text-1);
    width: 100%;
}

:deep(.tree-org-node__content) {
    background-color: var(--color-bg-2);
    color: var(--color-text-1);
    cursor: pointer;
    position: relative;

}

:deep(.zm-tree-org) {
    background-color: var(--color-bg-1);
    height: calc(100vh - 265px);
    width: 100% !important;
    position: relative;
}

:deep(.zm-tree-handle) {
    position: absolute;
    right: 0;
}

:deep(.zm-tree-contextmenu) {
    color: var(--color-text-1) !important;
    position: fixed !important;
    background: var(--color-bg-2) !important;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1) !important;
    border: 1px solid var(--color-border) !important;
    border-radius: 4px !important;
    padding: 4px 0 !important;
    min-width: 120px !important;
    z-index: 999 !important;

    ul {
        background: var(--color-bg-1) !important;
        list-style-type: none !important;
        padding: 10px !important;
        margin: 0 !important;
    }

    .zm-tree-menu-item {
        background-color: var(--color-bg-1) !important;
        padding: 5px 15px !important;
        margin-top: 10px !important;
        cursor: pointer !important;
        transition: background-color 0.1s ease !important;
        list-style: none !important;
    }
}

:deep(.tree-org-node__expand) {
    background-color: var(--color-bg-1) !important;
}
</style>
