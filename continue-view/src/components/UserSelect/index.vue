<script lang="ts" setup>
import { TableRowSelection, type TableInstance, type TreeNodeData } from '@arco-design/web-vue'
import { ref, reactive, onMounted } from 'vue'
import { type UserQuery, type UserResp, listAllUser, listUser, listRole } from '@/apis'
import { type Options, useTable } from '@/hooks'
import { useDept } from '@/hooks/app'
import { isMobile } from '@/utils'

// 在脚本顶部导入 h 函数
import { h } from 'vue';
// 表格列定义
const listColumns: TableInstance['columns'] = [
    {
        title: '序号',
        width: 66,
        align: 'center',
        render: (rowIndex: any) => h('span', {}, rowIndex + 1 + (pagination.current - 1) * pagination.pageSize),
        fixed: !isMobile() ? 'left' : undefined,
    },
    {
        title: '昵称',
        dataIndex: 'nickname',
        slotName: 'nickname',
        minWidth: 140,
        ellipsis: true,
        tooltip: true,
        fixed: !isMobile() ? 'left' : undefined,
    },
    { title: '用户名', dataIndex: 'username', slotName: 'username', minWidth: 140, ellipsis: true, tooltip: true },
    { title: '状态', slotName: 'status', align: 'center' },
    { title: '性别', dataIndex: 'gender', slotName: 'gender', align: 'center' },
    { title: '所属部门', dataIndex: 'deptName', minWidth: 180, ellipsis: true, tooltip: true },
    { title: '描述', dataIndex: 'description', minWidth: 130, ellipsis: true, tooltip: true },
]
interface Props {
    multiple?: boolean
    value: string | string[]
    roleId?: string
}
const props = withDefaults(defineProps<Props>(), {
    multiple: true,
    value: () => [],
})

const disabled = ref(false)
// 用户列表
// 查询表单
const queryForm = reactive<UserQuery>({
    sort: ['t1.createTime,desc', 't1.id,desc'],
    roleId: props.roleId,
})

// 用户列表
const { tableData: dataList, loading, pagination, search } = useTable(
    (page) => listUser({ ...queryForm, ...page }),
    { immediate: true, formatResult: (data) => data.map((i) => ({ ...i, id: `${i?.id}`, disabled: false })) },
)

// 右侧已选用户列定义
const selectedColumns = [
    { title: '用户', dataIndex: 'nickname', slotName: 'nickname', minWidth: 140, ellipsis: true, tooltip: true },
    { title: '操作', dataIndex: 'action', slotName: 'action', align: 'center', width: 90 },
]

// 选中用户 ID
const selectedKeys = ref<string[]>([])
// 部门列表
const { deptList, getDeptList } = useDept()
// 清空所有选中数据
const onClearSelected = () => {
    selectedUsers.value = []
    selectedKeys.value = []
    reset()
    tableRef.value.tableRef.clearSelection()
}
onMounted(async () => {
    getDeptList()

})
// 重置
const reset = () => {
    queryForm.description = undefined
    queryForm.deptId = undefined
    search()
}
const emit = defineEmits<{
    (e: 'select-user', keys: Array<any>): void
}>()
const selectedUsers = ref<UserResp[]>([])
const select = (rows: UserResp[]) => {
    selectedUsers.value = rows
    selectedKeys.value = rows.map((i) => i.id)
    emit('select-user', selectedKeys.value)
}
const paginationOptions: Options = {
    defaultPageSize: 10,
    defaultSizeOptions: [10, 20, 30, 40, 50],
}
const tableRef = ref<TableInstance>()
const handleDeleteSelectedUser = (row: UserResp) => {
    selectedUsers.value = selectedUsers.value.filter((i) => i.id !== row.id)
    selectedKeys.value = selectedKeys.value.filter((i) => i !== row.id)
    // 2. 关键：根据 id 从表格原始数据 dataList 中找到原始行对象
    const originalRow = dataList.value.find(item => item.id === row.id);

    // 3. 用原始行对象调用 toggleRowSelection（确保引用一致）
    if (originalRow) { // 防止找不到的异常情况
        tableRef.value.tableRef.toggleRowSelection(originalRow, false);
    }
    emit('select-user', selectedKeys.value)
}
defineExpose({
    onClearSelected,
})
</script>

<template>
    <div class="container">
        <el-row :gutter="16">
            <el-col :span="24" :md="17">
                <GiTable :columns="listColumns" ref="tableRef" @refresh="search" :default-expand-all="true"
                    :row-selection="disabled ? false : { type: 'checkbox', selectedRowKeys: selectedKeys }"
                    :data="dataList" :loading="loading" :pagination="pagination"
                    :disabled-tools="['size', 'fullscreen', 'setting', 'refresh']" @select="select">
                    <template #top>
                        <el-space wrap :size="[8, 8]" :fill-ratio=30 style="margin-bottom:10px;">
                            <el-input placeholder="搜索用户名/昵称/描述" v-model="queryForm.description" @change="search">
                                <template #suffix>
                                    <GiSvgIcon name="search" size="15"></GiSvgIcon>
                                </template>
                            </el-input>
                            <el-tree-select default-expand-all v-model="queryForm.deptId" :data="deptList"
                                placeholder="请选择所属部门" allow-clear allow-search @change="search" style="width: 240px" />
                            <el-button @click="reset">
                                <GiSvgIcon name="refresh" size="15"></GiSvgIcon>
                                重置
                            </el-button>
                        </el-space>

                        <el-alert type="warning">
                            <template v-if="selectedKeys.length > 0">
                                已选中 {{ selectedKeys.length }} 条记录(可跨页)
                            </template>
                            <template v-else>未选中任何记录</template>
                            <template v-if="selectedKeys.length > 0" #action>
                                <el-link @click="onClearSelected">清空</el-link>
                            </template>
                        </el-alert>

                    </template>

                    <template #gender="{ row }">
                        <GiCellGender :gender="row.gender" />
                    </template>
                    <template #status="{ row }">
                        <GiCellStatus :status="row.status" />
                    </template>
                </GiTable>
            </el-col>
            <el-col :span="24" :md="7" class="section">
                <div class="title">
                    <span>已选择</span>
                </div>
                <GiTable :columns="selectedColumns" :data="selectedUsers" :pagination="paginationOptions"
                    :default-expand-all="true" :disabled-tools="['size', 'fullscreen', 'setting', 'refresh']">
                    <template #nickname="{ row }">
                        {{ row.nickname }}({{ row.username }})
                    </template>
                    <template #action="{ row }">
                        <el-button type="danger" icon="Delete" @click="handleDeleteSelectedUser(row)">

                        </el-button>
                    </template>
                </GiTable>
            </el-col>
        </el-row>
    </div>
</template>
<style lang="scss" scoped></style>