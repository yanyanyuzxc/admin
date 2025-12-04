<script lang="ts" setup>
import type { TableInstance } from '@arco-design/web-vue'
import { ElMessage } from 'element-plus'
import GenConfigDrawer from './GenConfigDrawer.vue'
import { downloadCode, generateCode, listGenConfig } from '@/apis/code/generator.ts'
import { useTable } from '@/hooks'
import { isMobile } from '@/utils'

const GenPreviewModal = defineAsyncComponent(() => import('./GenPreviewModal.vue'))
const queryForm = reactive({
    tableName: undefined,
})

const {
    tableData: dataList,
    loading,
    pagination,
    selectedKeys,

    search,
} = useTable((page) => listGenConfig({ ...queryForm, ...page }), { immediate: true, formatResult: (data) => data.map((i) => ({ ...i, disabled: !i.createTime })) })
const columns: TableInstance['columns'] = [
    {
        title: '序号',
        width: 66,
        align: 'center',
        dataIndex: 'index',
        index: (rowIndex: any) => { return (rowIndex + 1 + (pagination.current - 1) * pagination.pageSize) },
    },
    { title: '表名称', dataIndex: 'tableName', minWidth: 225, ellipsis: true, tooltip: true },
    { title: '描述', dataIndex: 'comment', ellipsis: true, tooltip: true },
    { title: '类名前缀', dataIndex: 'classNamePrefix', ellipsis: true, tooltip: true },
    { title: '作者名称', dataIndex: 'author', ellipsis: true, tooltip: true },
    { title: '所属模块', dataIndex: 'moduleName', ellipsis: true, tooltip: true },
    { title: '模块包名', dataIndex: 'packageName', ellipsis: true, tooltip: true },
    { title: '配置时间', dataIndex: 'createTime', width: 180 },
    { title: '修改时间', dataIndex: 'updateTime', width: 180 },
    { title: '操作', dataIndex: 'action', slotName: 'action', width: 160, align: 'center', fixed: !isMobile() ? 'right' : undefined },
]
const select = (selection: any[], row: any) => {

    selectedKeys.value = selection.map((i) => i.tableName)
}
// 重置
const reset = () => {
    queryForm.tableName = undefined
    search()
}
const GenPreviewModalRef = ref<InstanceType<typeof GenPreviewModal>>()
const onPreview = (tableNames: (string | number)[]) => {

    GenPreviewModalRef.value?.onOpen(tableNames as string[])
}
// 配置
const GenConfigDrawerRef = ref<InstanceType<typeof GenConfigDrawer>>()
const onConfig = (tableName: string, comment: string) => {
    GenConfigDrawerRef.value?.onOpen(tableName, comment)
}
const onClearSelected = () => { }

// 生成（下载）函数，接收要下载的表名数组
const onDownload = async (tableNames: Array<string>) => {
    // 1. 调用后端接口，获取文件流响应（res 是接口返回的响应对象）
    const res = await downloadCode(tableNames);
    console.log('111')
    // 2. 从响应头中提取文件名（后端通过 Content-Disposition 传递文件名）
    const contentDisposition = res.headers['content-disposition'];
    // 正则表达式：匹配 "filename=xxx.xxx" 格式（提取文件名和后缀）
    const pattern = /filename=([^;]+\.[^.;]+);*/;
    // 执行正则匹配，result 是匹配结果数组（result[1] 是提取到的文件名）
    const result = pattern.exec(contentDisposition) || '';
    // 解码文件名（避免中文/特殊字符乱码，后端编码后前端需解码）
    const fileName = window.decodeURI(result[1]);
    // 创建下载的链接
    const blob = new Blob([res.data])
    const downloadElement = document.createElement('a')
    const href = window.URL.createObjectURL(blob)
    downloadElement.style.display = 'none'
    downloadElement.href = href
    // 下载后文件名
    downloadElement.download = fileName
    document.body.appendChild(downloadElement)
    // 点击下载
    downloadElement.click()
    // 下载完成，移除元素
    document.body.removeChild(downloadElement)
    // 释放掉 blob 对象
    window.URL.revokeObjectURL(href)
}
// 生成
const onGenerate = async (tableNames: Array<string>) => {
    const res = await generateCode(tableNames)

    if (res.data.code === 0) {
        ElMessage.success('代码生成成功')
    }
}
</script>

<template>

    <GiPageLayout>
        <GiTable row-key="tableName" :data="dataList" :columns="columns" :loading="loading"
            :scroll="{ x: '100%', y: '100%', minWidth: 1000 }" :pagination="pagination"
            :disabled-tools="['size', 'setting']" :disabled-column-keys="['tableName']"
            :row-selection="{ type: 'checkbox', showCheckedAll: true }" @select="select" @refresh="search">
            <template #toolbar-left>
                <el-space>
                    <el-input v-model="queryForm.tableName" placeholder="搜索表名称" @search="search" style="width: 250px" />
                    <el-button @click="reset" icon="Refresh">

                        <template #default>重置</template>
                    </el-button>
                </el-space>
            </template>
            <template #toolbar-right>
                <el-button type="primary" :disabled="!selectedKeys.length" :title="!selectedKeys.length ? '请选择' : ''"
                    @click="onPreview(selectedKeys)" class="generate-btn">

                    <template #default>
                        <GiSvgIcon name="code-sandbox" size="15"></GiSvgIcon>批量生成
                    </template>
                </el-button>
            </template>
            <template #toolbar-bottom>
                <el-alert>
                    <template v-if="selectedKeys.length > 0">
                        已选中 {{ selectedKeys.length }} 条记录(可跨页)
                    </template>
                    <template v-else>未选中任何记录</template>
                    <template v-if="selectedKeys.length > 0" #action>
                        <el-link @click="onClearSelected">清空</el-link>
                    </template>
                </el-alert>
            </template>
            <template #action="{ row }">
                <el-space>
                    <el-link v-permission="['code:generator:config']" title="配置"
                        @click="onConfig(row.tableName, row.comment)">配置</el-link>
                    <el-link v-permission="['code:generator:preview']" :disabled="!row.createTime"
                        :title="row.createTime ? '生成' : '请先进行生成配置'" @click="onPreview([row.tableName])">
                        生成
                    </el-link>
                </el-space>
            </template>
        </GiTable>
        <GenConfigDrawer ref="GenConfigDrawerRef" @save-success="search" />
        <GenPreviewModal ref="GenPreviewModalRef" @generate="onGenerate" @download="onDownload" />
    </GiPageLayout>

</template>

<style lang="scss" scoped>
.generate-btn.el-button.is-disabled {

    background: var(--el-color-primary-dark-2) !important;
}

.generate-btn.el-button {
    background: var(--el-color-primary) !important;
}
</style>