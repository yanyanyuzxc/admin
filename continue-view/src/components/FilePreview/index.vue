<script lang="ts" setup>
import VueOfficePdf from '@vue-office/pdf'
import VueOfficeDocx from '@vue-office/docx'
import '@vue-office/docx/lib/index.css'
import VueOfficeExcel from '@vue-office/excel'
import '@vue-office/excel/lib/index.css'
import type { FilePreview } from './type.ts'
import { ExcelTypes, WordTypes } from '@/constant/file'
import { useWindowSize } from '@vueuse/core'
const visible = ref(false)
const { width } = useWindowSize()
// 用于关闭弹框时销毁,释放内存
const blobUrl = ref<string>('')
// 用于关闭弹框时销毁,释放内存
onUnmounted(() => {
    if (blobUrl.value) {
        URL.revokeObjectURL(blobUrl.value)
    }
})

// 新标签页打开
const onOpen = () => {
    const data = filePreview.fileInfo?.data

    if (!data) {
        console.error('没有数据提供')
        return
    }

    let url: string | null = null

    if (typeof data === 'string') {
        // 如果是字符串，假设它是一个 URL，直接使用
        url = data
    } else if (data instanceof Blob || data instanceof ArrayBuffer) {
        // 如果之前创建了 Blob URL，先释放
        if (blobUrl.value) {
            URL.revokeObjectURL(blobUrl.value)
        }

        // 如果是 Blob 或 ArrayBuffer，则将其转换为 Blob
        const blob = data instanceof Blob ? data : new Blob([data])
        url = URL.createObjectURL(blob)
        blobUrl.value = url
    } else {
        console.error('不支持的类型')
        return
    }

    // 打开生成的 URL
    window.open(url)
}
// 关闭弹框
const onClose = () => {
    Object.assign(filePreview, {
        fileInfo: {},
        excelConfig: {},
    })

    visible.value = false

    // // 关闭时释放 Blob URL
    // if (blobUrl.value) {
    //     URL.revokeObjectURL(blobUrl.value)
    //     blobUrl.value = ''
    // }
}
// 文件预览对象
const filePreview = reactive<FilePreview>({
    fileInfo: {},
    excelConfig: {},
})

//预览方法
// 弹框标题
const modalTitle = computed(() => {
    const { fileName, fileType } = filePreview.fileInfo || {}
    return fileName && fileType ? `${fileName}.${fileType}` : '文件预览'
})

// 预览
const onPreview = (previewInfo: FilePreview) => {
    filePreview.fileInfo = previewInfo.fileInfo
    visible.value = true

}
defineExpose({
    onPreview,
})
</script>

<template>
    <el-dialog v-model="visible" @close="onClose">
        <template #header>
            <div class="title">
                {{ modalTitle }}

                <el-tooltip content="在新标签页打开">
                    <el-icon @click="onOpen">
                        <Position />
                    </el-icon>
                </el-tooltip>
            </div>
        </template>
        <template #default>
            <el-card>

                <VueOfficePdf :src="filePreview.fileInfo?.data" v-if="filePreview.fileInfo?.fileType === 'pdf'">
                </VueOfficePdf>
                <VueOfficeDocx style="height:100vh" :src="filePreview.fileInfo?.data"
                    v-if="filePreview.fileInfo?.fileType === 'docx'">
                </VueOfficeDocx>
                <VueOfficeExcel :src="filePreview.fileInfo?.data"
                    v-else-if="filePreview.fileInfo?.fileType === 'xls' || filePreview.fileInfo?.fileType === 'xlsx'"
                    :config="filePreview.excelConfig"></VueOfficeExcel>
            </el-card>
        </template>
    </el-dialog>
</template>

<style lang="scss" scoped>
.title {
    width: 100%;
    @include flex-between;
}
</style>
