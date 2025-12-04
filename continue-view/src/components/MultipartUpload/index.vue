<script lang="ts" setup>
import { useMultipartUploader } from '@/hooks/modules/useMultipartUploader'
import { h, ref, resolveComponent } from 'vue'
import { getFilesFromDataTransferItems, isFileSystemAccessAPISupported } from '@/utils/drag-drop-file-util'

import { useTable } from '@/hooks'



// 表格列定义
const columns = [
    {

        title: '名称',
        dataIndex: 'fileName',
        ellipsis: true,
        showOverflowTooltip: true,
        render: (record: any) => h(
            resolveComponent('el-tooltip'),
            { content: record.fileName, placement: 'top' },
            () => h('span', record.fileName),
        ),
    },
    {
        title: '文件目录',
        dataIndex: 'relativePath',
        ellipsis: true,
        showOverflowTooltip: true,
        render: (row: any) => {
            // 显示完整路径
            const displayPath = row.parentPath

            // 确保路径格式正确
            if (row.relativePath && row.relativePath !== '/') {
                // 对于文件夹上传，relativePath格式为：folderName/file.txt
                // 我们只需要显示parentPath，因为它已经包含了正确的路径
                const pathParts = row.relativePath.split('/')
                if (pathParts.length > 1) {
                    // 如果是文件夹内的文件，只显示parentPath
                    // parentPath已经是/test/upload这样的格式
                }
            }

            return h(
                resolveComponent('el-tooltip'),
                { content: displayPath, placement: 'top' },
                () => h('span', displayPath),
            )
        },
    },
    {
        title: '文件类型',
        dataIndex: 'fileType',
        showOverflowTooltip: true,
        ellipsis: true,
        render: (record: any) => h(
            resolveComponent('el-tooltip'),
            { content: record.fileType, placement: 'top' },
            () => h('span', record.fileType),
        ),
    },
    {
        title: '文件大小',
        dataIndex: 'fileSize',
        showOverflowTooltip: true,
        render: (row: any) => { return h('span', formatFileSize(row.fileSize)) },
        width: 120,
    },
    { title: '进度', slotName: 'progress', width: 140 },
    { title: '状态', slotName: 'status', width: 80 },
    { title: '操作', slotName: 'actions', width: 150 },
]
// 组件props定义
const props = defineProps<{
    extraParams?: Record<string, any>
    maxConcurrentFiles?: number
    maxConcurrentChunks?: number
    maxUploadWorkers?: number
    rootPath?: string
}>()
// 拖拽高亮状态
const isDragOver = ref(false)
// 拖拽进入区域
function onDragOver(_e: DragEvent) {
    isDragOver.value = true
}
async function onDrop(e: DragEvent) {
    isDragOver.value = false
    e.preventDefault()
    e.stopPropagation()
    let files: File[]
    if (isFileSystemAccessAPISupported()) {
        files = await getFilesFromDataTransferItems(e.dataTransfer!.items)
        addFiles(files, props.rootPath || '', true)
    } else {
        files = Array.from(e.dataTransfer?.files || [])

        // 验证文件的有效性
        const validFiles = files.filter((file) => {
            return !(!file || file.size === 0)
        })
        if (validFiles.length === 0) {
            return
        }
        //检查是否有文件结构
        const hasFolder = validFiles.some((file) => {
            if ((file as any).webkitRelativePath) {
                return true
            }//通过webkitRelativePath属性判断是否为文件夹
            return file.name.includes('/') || file.name.includes('\\')//通过文件名判断是否为文件夹
        })

        addFiles(validFiles, props.rootPath || '', hasFolder)
    }

}
// 拖拽离开区域
function onDragLeave(_e: DragEvent) {
    isDragOver.value = false
}
// 使用 useMultipartUploader composable
const {
    fileTasks,
    uploadingCount: _uploadingCount,
    maxConcurrent: _maxConcurrent,
    maxChunkConcurrent: _maxChunkConcurrent,
    startAllUpload,
    addFiles,
    pauseTask,
    resumeTask,
    cancelTask,
    startTask,
    retryTask,
    clearAllTasks,
    removeTask,
    formatFileSize: _formatFileSize,
    md5CalculatingTaskUid,
} = useMultipartUploader({
    maxConcurrentFiles: props.maxConcurrentFiles,
    maxConcurrentChunks: props.maxConcurrentChunks,
    maxUploadWorkers: props.maxUploadWorkers,
    rootPath: props.rootPath,
})
// 文件/文件夹选择input引用
const fileInput = ref<HTMLInputElement | null>(null)
const folderInput = ref<HTMLInputElement | null>(null)

// 文件选择事件处理
function onFileChange(e: Event) {

    const files = (e.target as HTMLInputElement).files

    if (!files) return
    search()

    // 移除 clearAllTasks()，改为追加模式
    // 普通文件上传路径 = rootPath
    addFiles(Array.from(files), props.rootPath || '', false)
        // 不要自动 startAllUpload()
        ; (e.target as HTMLInputElement).value = ''
}
// 文件夹选择事件处理
function onFolderChange(e: Event) {
    const files = (e.target as HTMLInputElement).files

    if (!files) return
    search()
    // 移除 clearAllTasks()，改为追加模式
    // 带目录文件上传路径 = rootPath
    // 文件夹上传时，webkitRelativePath会自动包含文件夹路径
    addFiles(Array.from(files), props.rootPath || '', true)
        // 不要自动 startAllUpload()
        ; (e.target as HTMLInputElement).value = ''
}
// 触发文件选择
function triggerFileInput() {
    fileInput.value?.click()
}
// 触发文件夹选择
function triggerFolderInput() {
    folderInput.value?.click()
}

const total = ref<number>(fileTasks.value.length); // 总数（动态同步）
// 3. 定义适配 useTable 的 api 函数（核心）
const tableApi = async ({ page, size }: { page: number; size: number }) => {
    // 模拟接口请求延迟（可选，贴合真实场景）
    await new Promise(resolve => setTimeout(resolve, 300));

    // 4. 模拟分页逻辑：根据 page 和 size 截取当前页数据
    const startIndex = (page - 1) * size; // 起始索引
    const endIndex = startIndex + size; // 结束索引
    const currentPageData = fileTasks.value.slice(startIndex, endIndex); // 截取当前页数据
    total.value = fileTasks.value.length;
    // 5. 返回 useTable 要求的格式（PageRes 分页对象）
    return {
        success: true,
        data: {
            list: currentPageData, // 当前页数据
            total: total.value, // 总个数
            page, // 当前页
            size, // 每页条数
        },
    };
};
const {
    pagination,
    search,
    tableData
} = useTable(tableApi as any)
// 定义获取动态数据的函数

// 文件大小格式化工具
function formatFileSize(bytes: number) {

    if (bytes === 0) return '0 Bytes'
    const k = 1024
    const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    return `${Number.parseFloat((bytes / k ** i).toFixed(2))} ${sizes[i]}`
}
// 状态文本映射
function statusText(status: string) {
    switch (status) {
        case 'waiting': return '等待中'
        case 'uploading': return '上传中'
        case 'paused': return '已暂停'
        case 'completed': return '已完成'
        case 'failed': return '失败'
        case 'cancelled': return '已取消'
        default: return status
    }
}
// 状态颜色映射
function statusColor(status: string) {
    switch (status) {
        case 'waiting': return '#909399'
        case 'uploading': return '#409EFF'
        case 'paused': return '#E6A23C'
        case 'completed': return '#67C23A'
        case 'failed': return '#F56C6C'
        case 'cancelled': return '#C0C4CC'
        default: return '#909399'
    }
}
</script>

<template>
    <el-row :gutter="20" class="multipart-upload">
        <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <div class="title">
                <div class="select" :class="{ dragover: isDragOver }" @dragover.prevent="onDragOver"
                    @dragleave.prevent="onDragLeave" @drop.prevent="onDrop">
                    <el-space>
                        <el-button @click="triggerFileInput">选择文件</el-button>
                        <el-button @click="triggerFolderInput">选择文件夹</el-button>
                        <input ref="fileInput" type="file" multiple style="display: none" @change="onFileChange" />
                        <input ref="folderInput" type="file" webkitdirectory directory style="display: none"
                            @change="onFolderChange" />
                    </el-space>
                </div>
                <div class="upload">
                    <el-space>
                        <el-button @click="startAllUpload">开始上传</el-button>
                        <el-button type="danger" @click="clearAllTasks">清空</el-button>
                    </el-space>
                </div>
            </div>
            <div style="margin-bottom: 8px;color: #888; font-size: 13px;">
                支持拖拽文件到此区域上传（文件夹请使用"选择文件夹"按钮）
                <br />
                <span style="color: #999;">提示：拖拽上传时，所有文件将上传到根目录</span>
            </div>
            <!-- 表格区域 -->
            <!-- 表格区域 -->
            <div class="gi-table-flex-body">
                <div class="gi-table-flex-container">
                    <GiTable :data="tableData" row-key="uid" :pagination="pagination" :columns="columns"
                        style="height: 100%; background: transparent;">

                        <!-- 2. 修复 template 结构，使用 v-if 来判断不同的 slotName -->

                        <!-- 进度列 -->
                        <template #progress="{ row }">
                            <template v-if="md5CalculatingTaskUid === row.uid">
                                <span style="color: #888;">正在计算MD5...</span>
                            </template>
                            <template v-else>
                                <el-progress :percentage="row.progress * 100" size="large" />
                            </template>
                        </template>

                        <!-- 状态列 -->
                        <template #status="{ row }">
                            <div>
                                <el-tag :color="statusColor(row.status)" size="small">
                                    <span style="color:white">{{ statusText(row.status) }}</span>
                                </el-tag>
                                <div v-if="row.status === 'failed' && row.errorMessage"
                                    style="margin-top: 4px; font-size: 12px; color: #f56c6c;">
                                    {{ row.errorMessage }}
                                </div>
                            </div>
                        </template>

                        <!-- 操作列 -->
                        <template #actions="{ row }">
                            <el-space class="actions">
                                <el-tooltip v-if="row.status === 'waiting'" content="开始">
                                    <el-button @click="startTask(row)" size="small">
                                        <GiSvgIcon name="play-arrow" size="15"></GiSvgIcon>
                                    </el-button>
                                </el-tooltip>
                                <el-tooltip v-if="row.status === 'uploading'" content="暂停">
                                    <el-button @click="pauseTask(row)" size="small">
                                        <GiSvgIcon name="pause" size="15"></GiSvgIcon>
                                    </el-button>
                                </el-tooltip>
                                <el-tooltip v-if="row.status === 'paused'" content="继续">
                                    <el-button @click="resumeTask(row)" size="small">
                                        <GiSvgIcon name="play-arrow" size="15"></GiSvgIcon>
                                    </el-button>
                                </el-tooltip>
                                <el-tooltip v-if="row.status === 'failed'" content="重试">
                                    <el-button @click="retryTask(row)" icon="Refresh" size="small"></el-button>
                                </el-tooltip>
                                <el-tooltip content="取消">
                                    <el-button v-if="row.status !== 'completed' && row.status !== 'cancelled'"
                                        @click="cancelTask(row)" icon="Close" size="small"></el-button>
                                </el-tooltip>
                                <el-tooltip content="删除">
                                    <el-button type="danger" @click="removeTask(row)" icon="Delete"
                                        size="small"></el-button>
                                </el-tooltip>
                            </el-space>
                        </template>
                    </GiTable>

                </div>
            </div>
            <!-- <div class="footer">
                <el-pagination layout="jumper,prev, pager, next,total" :total="50" />
            </div> -->
        </el-col>
    </el-row>
</template>

<style lang="scss" scoped>
.multipart-upload {
    height: 100%;


}

.actions {
    .el-button {
        background: white !important;
        box-shadow: none !important;
        border: none !important;
        color: $color-theme !important;
    }
}

.title {
    @include flex-between;
    margin-bottom: 10px;

    .select {
        .el-button {
            color: black !important;
            background-color: white !important;
            border: 1px solid #d9d9d9 !important;
        }
    }

}

.footer {
    position: absolute;
    bottom: 3%;
    right: 3%;
}

.gi-table-flex-body {
    height: 700px;
}
</style>