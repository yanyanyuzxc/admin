<script lang="ts" setup>
import type { TableInstance } from '@arco-design/web-vue'
// import DetailDrawer from './DetailDrawer.vue'
import { type NoticeQuery, type NoticeResp, deleteNotice, listNotice } from '@/apis/system'
import { useTable } from '@/hooks'
import { useDict } from '@/hooks/app'
import { isMobile } from '@/utils'
import has from '@/utils/has'
// 在脚本顶部导入 h 函数
import { h } from 'vue';
const router = useRouter()
const queryForm = reactive<NoticeQuery>({
    sort: ['id,desc'],
})
const {
    tableData: dataList,
    loading,
    pagination,
    search,
    handleDelete,
} = useTable((page) => listNotice({ ...queryForm, ...page }), { immediate: true })
const columns: TableInstance['columns'] = [
    {
        title: '序号',
        width: 66,
        align: 'center',
        dataIndex: 'index',
        index: (rowIndex: any) => { return (rowIndex + 1 + (pagination.current - 1) * pagination.pageSize) },
    },
    { title: '公告标题', dataIndex: 'title', slotName: 'title', maxWidth: 180, ellipsis: true, tooltip: true },
    { title: '发布人', dataIndex: 'createUserString', maxWidth: 120, ellipsis: true, tooltip: true },
    { title: '通知范围', dataIndex: 'noticeScope', slotName: 'noticeScope', width: 110, align: 'center' },
    { title: '通知方式', dataIndex: 'noticeMethods', slotName: 'noticeMethods', maxWidth: 165, ellipsis: true, tooltip: true },
    { title: '分类', dataIndex: 'type', slotName: 'type', maxWidth: 100, align: 'center' },
    { title: '状态', dataIndex: 'status', slotName: 'status', maxWidth: 100, align: 'center' },
    { title: '是否定时', dataIndex: 'isTiming', slotName: 'isTiming', width: 110, align: 'center' },
    { title: '发布时间', dataIndex: 'publishTime', slotName: 'publishTime', width: 180 },
    { title: '是否置顶', dataIndex: 'isTop', slotName: 'isTop', show: false, maxWidth: 100, align: 'center' },
    {
        title: '操作',
        dataIndex: 'action',
        slotName: 'action',
        width: 160,
        align: 'center',
        fixed: !isMobile() ? 'right' : undefined,
        show: has.hasPermOr(['system:notice:get', 'system:notice:view', 'system:notice:update', 'system:notice:delete']),
    },
]

const { notice_type, notice_scope_enum, notice_method_enum, notice_status_enum } = useDict('notice_type', 'notice_scope_enum', 'notice_method_enum', 'notice_status_enum')
const reset = () => { }
// 新增
// 删除
const onDelete = (record: NoticeResp) => {
    return handleDelete(() => deleteNotice(record.id as string), {
        content: `是否确定删除公告「${record.title}」？`,
        showModal: true,
    })
}
const onAdd = () => {

    router.push({ path: '/system/notice/add' })
}
const onDetail = (row: NoticeResp) => { }
const onView = (row: NoticeResp) => { }
const onUpdate = (row: NoticeResp) => {
    router.push({ path: '/system/notice/add', query: { id: row.id, type: 'update' } })
}

// 格式化通知方式（转换为GiCellTags所需格式）
const formatNoticeMethods = (noticeMethods: string[]) => {
    return noticeMethods.map((method) => {
        const dictItem = notice_method_enum.value.find((item) => item.value === method)
        return dictItem?.label || method
    })
}
</script>

<template>


    <GiPageLayout>
        <div class="notice-container">
            <GiTable row-key="id" :data="dataList" :columns="columns" :loading="loading"
                :scroll="{ x: '100%', y: '100%', minWidth: 1000 }" :pagination="pagination"
                :disabled-column-keys="['title']" @refresh="search">
                <template #toolbar-left>
                    <el-space>
                        <el-input v-model="queryForm.title" placeholder="搜索标题" allow-clear @search="search"
                            style="width: 250px" />

                        <el-select v-model="queryForm.type" :options="notice_type" placeholder="请选择分类" allow-clear
                            style="width: 150px" @change="search" />

                        <el-button @click="reset" icon="Refresh">

                            <template #default>重置</template>
                        </el-button>
                    </el-space>
                </template>
                <template #toolbar-right>
                    <el-button v-permission="['system:notice:create']" type="primary" @click="onAdd" icon="Plus">

                        <template #default>新增</template>
                    </el-button>
                </template>
                <template #noticeScope="{ row }">

                    <GiCellTag :value="row.noticeScope" :dict="notice_scope_enum" />
                </template>
                <template #noticeMethods="{ row }">
                    <span v-if="!row.noticeMethods">无</span>
                    <GiCellTags v-else :data="formatNoticeMethods(row.noticeMethods)" />
                </template>
                <template #type="{ row }">
                    <GiCellTag :value="row.type" :dict="notice_type" />
                </template>
                <template #status="{ row }">
                    <GiCellTag :value="row.status" :dict="notice_status_enum" />
                </template>
                <template #isTiming="{ row }">
                    <el-tag v-if="row.isTiming" color="arcoblue">是</el-tag>
                    <el-tag v-else color="red">否</el-tag>
                </template>
                <template #action="{ row }">
                    <el-space>
                        <el-link v-permission="['system:notice:get']" title="详情" @click="onDetail(row)">详情</el-link>
                        <el-link v-permission="['system:notice:view']" title="预览" @click="onView(row)">查看</el-link>
                        <el-dropdown placement="bottom-start">
                            <el-button v-if="has.hasPermOr(['system:notice:update', 'system:notice:delete'])"
                                type="text" title="更多" icon="More">

                            </el-button>
                            <template #dropdown>
                                <el-dropdown-item>
                                    <el-link title="修改" @click="onUpdate(row)" type="primary">修改</el-link>
                                </el-dropdown-item>
                                <el-dropdown-item>
                                    <el-link type="danger" title="删除" @click="onDelete(row)">删除</el-link>
                                </el-dropdown-item>
                            </template>
                        </el-dropdown>
                    </el-space>
                </template>
            </GiTable>
        </div>
        <!-- <DetailDrawer ref="DetailDrawerRef" /> -->
    </GiPageLayout>

</template>

<style lang="scss" scoped>
.notice-container {
    width: 100%;
    height: 100%;
    padding: 10px;
}
</style>