<script lang="ts" setup>
import type { TableInstance } from '@arco-design/web-vue'
import { useTable } from '@/hooks'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useDict } from '@/hooks/app'
import mittBus from '@/utils/mitt'
import { useWindowSize } from '@vueuse/core'
import {
    type MessageQuery,
    type MessageResp,
    deleteMessage,
    getUserMessage,
    listMessage,
    readAllMessage,
    readMessage,
} from '@/apis'
defineOptions({ name: 'UserMyMessage' })
const { width } = useWindowSize()
const { message_type_enum } = useDict('message_type_enum')
const columns: TableInstance['columns'] = [
    {
        title: '序号',
        width: 66,
        align: 'center',
        dataIndex: 'index',
        index: (rowIndex: any) => { return (rowIndex + 1 + (pagination.current - 1) * pagination.pageSize) },
    },
    { title: '标题', dataIndex: 'title', slotName: 'title', minWidth: 100, ellipsis: true, tooltip: true },
    { title: '类型', dataIndex: 'type', slotName: 'type', width: 180, ellipsis: true, tooltip: true },
    { title: '状态', dataIndex: 'isRead', slotName: 'isRead', minWidth: 100, align: 'center' },
    { title: '时间', dataIndex: 'createTime', width: 180 },
]
const queryForm = reactive<MessageQuery>({
    sort: ['createTime,desc'],
})
// 表格更新回调
const onSuccess = () => {
    mittBus.emit('count-refresh')
}
// 重置
const reset = () => {
    queryForm.title = undefined
    queryForm.type = undefined
    queryForm.isRead = undefined
    search()
}
const {
    tableData: dataList,
    loading,
    pagination,
    selectedKeys,
    select,
    search,
    handleDelete,
} = useTable((page) => listMessage({ ...queryForm, ...page }), { immediate: true, onSuccess })
// 删除
const onDelete = () => {

    if (!selectedKeys.value.length) {
        return ElMessage.warning('请选择数据')
    }
    return handleDelete(() => deleteMessage(selectedKeys.value as string[]), { showModal: true, content: `是否确定删除所选的${selectedKeys.value.length}条消息？`, multiple: true })
}

// 全部已读
const onReadAll = async () => {
    ElMessageBox({
        type: 'confirm',
        title: '全部已读',
        message: '确定要标记全部消息为已读吗？',
        confirmButtonText: '确定',
        cancelButtonText: '取消',

        callback: async (action: any) => {
            if (action === 'confirm') {
                await readAllMessage()
                ElMessage.success('操作成功')
                search()
            }
        },
    })
}


// 标记为已读
const onRead = async () => {
    if (!selectedKeys.value.length) {
        return ElMessage.warning('请选择数据')
    }
    await readMessage(selectedKeys.value as string[])
    ElMessage.success('操作成功')
    search()
}
const messageDetailVisible = ref(false)
const currentMessage = ref<MessageResp>()
// 显示消息详情
const showMessageDetail = async (record: any) => {
    messageDetailVisible.value = true
    const { data } = await getUserMessage(record.id)
    currentMessage.value = data
    record.isRead = currentMessage.value?.isRead
    onSuccess()
}
</script>

<template>

    <GiTable :columns="columns" row-key="id" :data="dataList" :pagination="pagination"
        :disabled-tools="['size', 'setting', 'fullscreen']" :row-selection="{ type: 'checkbox', showCheckedAll: true }"
        v-model:selected-keys="selectedKeys">

        <template #toolbar-left>
            <el-space>
                <el-input v-model="queryForm.title" placeholder="搜索标题" style="width: 150px"></el-input>
                <el-select v-model="queryForm.type" placeholder="请选择类型" style="width: 150px"
                    :options="message_type_enum" @change="search">
                </el-select>
                <el-select v-model="queryForm.isRead" placeholder="请选择状态" style="width: 150px" @change="search">
                    <el-option :value="false">未读</el-option>
                    <el-option :value="true">已读</el-option>
                </el-select>
                <el-button @click="reset" icon="Refresh">

                    <template #default>重置</template>
                </el-button>
            </el-space>
        </template>
        <template #toolbar-right>
            <el-button type="primary" status="danger" :disabled="!selectedKeys.length"
                :title="!selectedKeys.length ? '请选择' : ''" @click="onDelete" icon="Delete">

                删除
            </el-button>
            <el-button type="primary" :disabled="!selectedKeys.length" :title="!selectedKeys.length ? '请选择' : ''"
                @click="onRead">
                标记已读
            </el-button>
            <el-button type="primary" :disabled="selectedKeys.length > 0" :title="!selectedKeys.length ? '请选择' : ''"
                @click="onReadAll">
                全部已读
            </el-button>
        </template>
        <template #title="{ row }">
            <el-link @click="showMessageDetail(row)">

                {{ row.title }}

            </el-link>
        </template>
        <template #type="{ row }">
            <GiCellTag :value="row.type" :dict="message_type_enum" />
        </template>
        <template #isRead="{ row }">
            <el-tag :color="row.isRead ? '' : 'arcoblue'">
                {{ row.isRead ? '已读' : '未读' }}
            </el-tag>
        </template>

    </GiTable>
    <!-- 消息详情弹窗 -->
    <el-dialog align-center v-model="messageDetailVisible" :width="width >= 500 ? 500 : '100%'" :footer="false"
        :mask-closable="true" class="message-detail-modal">
        <template #header>
            <div class="header-title">{{ currentMessage?.title }}</div>
        </template>
        <div class="message-detail-content">
            <el-space direction="vertical">
                <div class="message-content">{{ currentMessage?.content }}</div>
                <div class="message-footer">
                    <div class="time-info">

                        <GiSvgIcon name="clock-circle"></GiSvgIcon>
                        <span class="time-label">发送时间：</span>
                        <span class="time-value">{{ currentMessage?.createTime }}</span>
                    </div>
                </div>
            </el-space>
        </div>
    </el-dialog>
</template>

<style lang="scss" scoped>
.header-title {
    width: 100%;
    @include flex-center;
}

.message-detail-content {
    width: 100%;
    padding: 10px;

    .message-content {
        font-size: 1em;
        min-height: 100px;
    }

}
</style>