<script lang="ts" setup>
import type { TableInstance } from '@arco-design/web-vue'
import { type NoticeQuery, type NoticeResp, listUserNotice } from '@/apis/system'
import { useTable } from '@/hooks'
import { useDict } from '@/hooks/app'

defineOptions({ name: 'UserMyNotice' })

const { notice_type } = useDict('notice_type')

const queryForm = reactive<NoticeQuery>({
    sort: ['createTime,desc'],
})

const {
    tableData: dataList,
    loading,
    pagination,
    search,
} = useTable((page) => listUserNotice({ ...queryForm, ...page }), { immediate: true })

const columns: TableInstance['columns'] = [
    {
        title: '序号',
        width: 66,
        align: 'center',
        dataIndex: 'index',
        index: (rowIndex: any) => { return (rowIndex + 1 + (pagination.current - 1) * pagination.pageSize) },
    },
    { title: '公告标题', dataIndex: 'title', slotName: 'title', ellipsis: true, tooltip: true },
    { title: '分类', dataIndex: 'type', slotName: 'type', align: 'center' },
    { title: '状态', dataIndex: 'isRead', slotName: 'isRead', align: 'center' },
    { title: '发布人', dataIndex: 'createUserString', ellipsis: true, tooltip: true },
    { title: '发布时间', dataIndex: 'publishTime', width: 180 },
]

// 重置
const reset = () => {
    queryForm.title = undefined
    queryForm.type = undefined
    search()
}

const router = useRouter()
// 查看
const onView = (record: NoticeResp) => {
    router.push({ path: '/user/notice', query: { id: record.id } })
}

</script>
<template>
    <div class="my-notice">
        <GiTable row-key="id" :data="dataList" :columns="columns" :loading="loading" :scroll="{ x: '100%', y: '100%' }"
            :pagination="pagination" :disabled-tools="['size', 'setting']" @refresh="search">
            <template #toolbar-left>
                <el-space>
                    <el-input v-model="queryForm.title" placeholder="搜索标题" @search="search" style="width:150px" />
                    <el-select v-model="queryForm.type" :options="notice_type" placeholder="请选择类型" allow-clear
                        style="width: 150px" @change="search" />
                    <el-button @click="reset" icon="Refresh">

                        <template #default>重置</template>
                    </el-button>
                </el-space>
            </template>
            <template #title="{ row }">
                <el-link @click="onView(row)">

                    {{ row.title }}

                </el-link>
            </template>
            <template #type="{ row }">
                <GiCellTag :value="row.type" :dict="notice_type" />
            </template>
            <template #isRead="{ row }">
                <el-tag :color="row.isRead ? '' : 'arcoblue'">
                    {{ row.isRead ? '已读' : '未读' }}
                </el-tag>
            </template>
        </GiTable>
    </div>
</template>

<style lang="scss" scoped>
.my-notice {
    padding: 10px;
}
</style>