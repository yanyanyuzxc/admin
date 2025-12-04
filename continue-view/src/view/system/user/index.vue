<script lang="ts" setup>
import { ref, reactive } from 'vue'
import LeftMenu from './dept/index.vue'
import GiTable from '@/components/GiTable/src/GiTable.vue';
import GiForm from '@/components/GiForm/src/GiForm.vue';
import { DisEnableStatusList } from '@/constant/common'
import type { ColumnItem } from '@/components/GiForm/src/type.ts'
import type { TableInstance } from '@arco-design/web-vue'
import { isMobile } from '@/utils'
import { listUser, type UserResp, deleteUser, exportUser } from '@/apis/system/user'
import { useTable } from '@/hooks'
import { useResetReactive, useDownload } from '@/hooks'
import AddDrawer from './AddDrawer.vue'
import DeatilDrawer from './DeatilDrawer.vue';
import ImportDrawer from './ImportDrawer.vue';
import PwdReetModal from './PwdReetModal.vue';
import RoleUpdatModal from './RoleUpdatModal.vue';
import has from '@/utils/has.ts'
//第一步：调用useResetReactive方法初始化或者重置表单数据，得到queryForm查询参数和resetForm方法
const [queryForm, resetForm] = useResetReactive({
    sort: ['t1.id,desc'],
})//queryForm是传递给后端的排序字段是按照t1.id,desc默认降序排序
const columns: TableInstance['columns'] = [
    {
        title: '序号',
        dataIndex: 'index',
        width: 66,
        align: 'center',
        // render: ({ rowIndex }) => {
        //     // 从 pagination 中获取当前页码和每页条数
        //     const { currentPage, pageSize } = pagination;
        //     return (currentPage - 1) * pageSize + rowIndex + 1;
        // },
        fixed: !isMobile() ? 'left' : undefined,
    },
    {
        title: '昵称',
        dataIndex: 'nickname',
        slotName: 'nickname',
        width: 140,
        ellipsis: true,
        tooltip: true,
        fixed: !isMobile() ? 'left' : undefined,
    },
    { title: '用户名', dataIndex: 'username', slotName: 'username', width: 140, ellipsis: true, tooltip: true },
    { title: '状态', dataIndex: 'status', slotName: 'status', align: 'center' },
    { title: '性别', dataIndex: 'gender', slotName: 'gender', align: 'center' },
    { title: '所属部门', dataIndex: 'deptName', width: 180, ellipsis: true, tooltip: true },
    { title: '角色', dataIndex: 'roleNames', slotName: 'roleNames', width: 165 },
    { title: '手机号', dataIndex: 'phone', width: 170, ellipsis: true, tooltip: true },
    { title: '邮箱', dataIndex: 'email', width: 170, ellipsis: true, tooltip: true },
    { title: '系统内置', dataIndex: 'isSystem', slotName: 'isSystem', width: 100, align: 'center', show: false },
    {
        title: '描述', dataIndex: 'description',
        width: 200, ellipsis: true, tooltip: true
    },
    { title: '创建人', dataIndex: 'createUserString', width: 140, ellipsis: true, tooltip: true, show: false },
    { title: '创建时间', dataIndex: 'createTime', width: 180 },
    { title: '修改人', dataIndex: 'updateUserString', width: 140, ellipsis: true, tooltip: true, show: false },
    { title: '修改时间', dataIndex: 'updateTime', width: 180, show: false },
    {
        title: '操作',
        dataIndex: 'action',
        slotName: 'action',
        width: 160,
        align: 'center',
        fixed: !isMobile() ? 'right' : undefined,
        show: has.hasPermOr([
            'system:user:get',
            'system:user:update',
            'system:user:resetPwd',
            'system:user:updateRole',
            'system:user:delete',
        ]),
    },
]
const queryFormColumns: ColumnItem[] = reactive([
    {
        type: 'input',
        label: '用户名',
        field: 'description',
        style: {
            width: '250px'
        },
        props: {
            placeholder: '用户名/昵称/描述',
        },
    },
    {
        type: 'select',
        label: '状态',
        field: 'status',
        style: {
            width: '250px'
        },
        props: {
            options: DisEnableStatusList,
            placeholder: '请选择状态',
        },
    },
    {
        type: 'date-picker',
        label: '创建时间',
        field: 'createTime',
        style: {
            width: '300px !important'
        }
    },
])
//第二步，调用useTable方法，传递一个查询的api和是否立即调用，
//获取到表格数据，以及分页信息，以及搜索方法
const {
    tableData: dataList,
    loading,
    pagination,
    search,
    handleDelete,
} = useTable((page) => listUser({ ...queryForm, ...page }), { immediate: true })
console.log(dataList)
const AddDrawerInstance = ref<any>()
const DeatilDrawerInstance = ref<any>()
const ImportDrawerInstance = ref<any>()
const PwdReetModalInstance = ref<any>()
const RoleUpdatModalInstance = ref<any>()
const handleAdd = () => {
    AddDrawerInstance.value.onAdd()
}
const handleEdit = (row: any) => {

    AddDrawerInstance.value.onEdit(row.id)
}
const handleDeatil = (row: any) => {
    DeatilDrawerInstance.value.open(row.id as string)
}
const handleImport = () => {
    ImportDrawerInstance.value.open()
}
const handlePwdReet = (row: any) => {
    PwdReetModalInstance.value.open(row.id as string)
}
const handleRoleUpdat = (row: any) => {
    RoleUpdatModalInstance.value.open(row.id as string)
}
// 删除
const onDelete = (record: UserResp) => {
    return handleDelete(() => deleteUser(record.id), {
        content: `是否确定删除用户「${record.nickname}(${record.username})」？`,
        showModal: true,
    })
}
//对左边菜单进行搜索

// 根据选中部门查询
const handleSelectDept = (keys: any) => {
    queryForm.deptId = keys ? keys.key : undefined
    // console.log(queryForm.deptId)
    search()
}
// 重置
const reset = () => {
    resetForm()
    search()
}
//导出
const handleExport = () => {
    useDownload(() => exportUser(queryForm))
}
</script>

<template>
    <div class="layout">
        <GiPageLayout>
            <template #left>
                <LeftMenu @node-click="handleSelectDept" />
            </template>
            <GiTable :data="dataList" :columns="columns" :loading="loading" :pagination="pagination"
                :disabled-tools="['size']" :disabled-column-keys="['nickname']" class="table-container"
                :default-expand-all="true" @refresh="search">
                <template #top>
                    <GiForm @reset="reset" @search="search" v-model="queryForm" search :columns="queryFormColumns">
                    </GiForm>
                </template>
                <template #toolbar-left>
                    <el-button icon="Plus" @click="handleAdd">新增</el-button>
                    <el-button icon="Upload" @click="handleImport">导入</el-button>
                </template>
                <template #toolbar-right>
                    <el-button @click="handleExport">
                        <GiSvgIcon name="download"></GiSvgIcon>
                        导出
                    </el-button>
                </template>
                <template #nickname="{ row }">

                    <GiCellAvatar :avatar="row.avatar" :name="row.nickname" />
                </template>
                <template #gender="{ row }">
                    <GiCellGender :gender="row.gender" />
                </template>
                <template #roleNames="{ row }">
                    <GiCellTags :data="row.roleNames" />
                </template>
                <template #status="{ row }">
                    <GiCellStatus :status="row.status" />
                </template>
                <template #isSystem="{ row }">
                    <a-tag v-if="row.isSystem" color="red" size="small">是</a-tag>
                    <a-tag v-else color="arcoblue" size="small">否</a-tag>
                </template>
                <template #action="{ row }">
                    <el-space>
                        <el-link @click="handleDeatil(row)"><span style="color:blue;">详情</span></el-link>
                        <el-link @click="handleEdit(row)"><span style="color:blue;">修改</span></el-link>
                        <el-dropdown>
                            <GiSvgIcon name="more" size="18" color="blue"></GiSvgIcon>
                            <template #dropdown>
                                <el-dropdown-item @click="handlePwdReet(row)">重置密码</el-dropdown-item>
                                <el-dropdown-item @click="handleRoleUpdat(row)">分配角色</el-dropdown-item>
                                <el-dropdown-item @click="onDelete(row)">删除</el-dropdown-item>
                            </template>
                        </el-dropdown>

                    </el-space>
                </template>
            </GiTable>
        </GiPageLayout>
        <AddDrawer ref="AddDrawerInstance" @save-success="search" />
        <ImportDrawer ref='ImportDrawerInstance' />
        <DeatilDrawer ref="DeatilDrawerInstance" />
        <PwdReetModal ref="PwdReetModalInstance" />
        <RoleUpdatModal ref="RoleUpdatModalInstance" @save-success="search" />
    </div>
</template>
<style lang="scss" scoped>
.layout {
    margin: 0px;
    padding: 0px !important;

}



:focus-visible {
    outline: none;
}

.el-button {
    margin-right: 7px;
}
</style>