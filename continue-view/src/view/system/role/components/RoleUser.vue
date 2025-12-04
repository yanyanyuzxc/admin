<script lang="ts" setup>
import { ref, computed, watch, onMounted } from 'vue'
import { TableData, type TableInstance } from '@arco-design/web-vue'
import GiSvgIcon from "@/components/GiSvgIcon/index.vue"
import { useResetReactive, useTable } from '@/hooks'
import { type RoleUserQuery, type RoleUserResp, listRoleUser, unassignFromUsers } from '@/apis/system/role'
import { isMobile } from '@/utils'
import has from '@/utils/has'
import { ElDialog, ElMessage, ElMessageBox } from 'element-plus'
import AssignModal from '../AssignModal.vue'


interface Props {
    roleId?: string
}
const props = withDefaults(defineProps<Props>(), {
    roleId: '1',
})
const [queryForm, resetForm] = useResetReactive<RoleUserQuery>({
    sort: ['t1.id,desc'],
})
const {
    tableData: dataList,
    loading,
    select,
    pagination,
    search,
    selectedKeys,
    handleDelete,
} = useTable((page) => listRoleUser(props.roleId, { ...queryForm, ...page }), { immediate: false },)
// console.log(props.roleId)
// console.log(dataList)
// 监听 roleId 的变化

watch(
    () => props.roleId,
    async (newRoleId) => {
        if (newRoleId) {
            search()
        }
    },
    // { immediate: true },
)
onMounted(() => {

    if (props.roleId) {
        search()
    }
})
const columns: TableInstance['columns'] = [
    {
        title: '序号',
        width: 66,
        align: 'center',
        // render: ({ rowIndex }) => h('span', {}, rowIndex + 1 + (pagination.current - 1) * pagination.pageSize),
        fixed: !isMobile() ? 'left' : undefined,
    },
    {
        title: '昵称',
        dataIndex: 'nickname',
        slotName: 'nickname',
        minWidth: 130,
        ellipsis: true,
        tooltip: true,
        fixed: !isMobile() ? 'left' : undefined,
    },
    { title: '用户名', dataIndex: 'username', slotName: 'username', minWidth: 120, ellipsis: true, tooltip: true },
    { title: '状态', dataIndex: 'status', slotName: 'status', align: 'center' },
    { title: '性别', dataIndex: 'gender', slotName: 'gender', align: 'center' },
    { title: '所属部门', dataIndex: 'deptName', minWidth: 140, ellipsis: true, tooltip: true },
    { title: '角色', dataIndex: 'roleNames', slotName: 'roleNames', minWidth: 165 },
    { title: '描述', dataIndex: 'description', minWidth: 130, ellipsis: true, tooltip: true },
    {
        title: '操作',
        dataIndex: 'action',
        slotName: 'action',
        width: 100,
        align: 'center',
        fixed: !isMobile() ? 'right' : undefined,
        show: has.hasPermOr([
            'system:role:unassign',
        ]),
    },
]

const AssignModalInstance = ref<InstanceType<typeof AssignModal>>()
const onAssign = () => {
    AssignModalInstance.value?.open(props.roleId)
}

// 批量删除
const onMulDelete = () => {
    if (!selectedKeys.value.length) {
        return ElMessage.warning('请选择数据')
    }
    ElMessageBox.confirm(

        `是否确定取消分配角色给所选的${selectedKeys.value.length}个用户？`,
        '提示',
        {
            confirmButtonText: 'OK',
            cancelButtonText: 'Cancel',
            type: 'warning',
            center: true,
        }
    )
        .then(async () => {
            await unassignFromUsers(selectedKeys.value)
            // selectedKeys.value = []
            ElMessage.success('取消成功')
            search()
        },
        )
        .catch(() => {
            ElMessage({
                type: 'info',
                message: 'Delete canceled',
            })
        })
}

// 删除
const onDelete = (row: RoleUserResp) => {
    return handleDelete(() => unassignFromUsers([row.id]), {
        content: `是否确定取消分配角色给用户「${row.nickname}(${row.username})」？`,
        successTip: '取消成功',
        showModal: true,
    })
}
// 重置
const reset = () => {
    resetForm()
    search()
}
const disabled = ref(false)
</script>

<template>
    <GiTable :data="dataList" :columns="columns" :pagination="pagination" @select="select" row-key="id"
        :default-expand-all="true"
        :row-selection="disabled ? false : { type: 'checkbox', selectedRowKeys: selectedKeys }"
        :disabled-tools="['size', 'setting', 'fullscreen']">
        <template #toolbar-left>
            <el-button v-permission="['system:role:assign']" type="primary" @click="onAssign">
                <GiSvgIcon name="plus" size="15"></GiSvgIcon>
                分配角色
            </el-button>
            <el-button v-permission="['system:role:unassign']" type="primary" @click="onMulDelete"
                :disabled="!selectedKeys.length">
                <GiSvgIcon name="delete" size="15"></GiSvgIcon>
                取消分配
            </el-button>
        </template>
        <template #toolbar-right>
            <el-input placeholder="搜索用户名/昵称/描述" v-model="queryForm.description" @change="search">
                <template #suffix>
                    <GiSvgIcon name="search" size="15"></GiSvgIcon>
                </template>
            </el-input>
            <el-button @click="reset">
                <GiSvgIcon name="refresh" size="15"></GiSvgIcon>
                重置
            </el-button>
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
        <template #action="{ row }">
            <a-space>
                <el-link v-permission="['system:role:unassign']" :disabled="row.disabled" type="danger"
                    :title="row.disabled ? '该用户为系统内置用户不能取消分配' : '取消分配'" @click="onDelete(row)">
                    取消分配
                </el-link>
            </a-space>
        </template>
    </GiTable>
    <AssignModal ref="AssignModalInstance" @save-success="search" />
</template>

<style lang="scss" scoped>
.el-input {
    margin-right: 10px;
}
</style>