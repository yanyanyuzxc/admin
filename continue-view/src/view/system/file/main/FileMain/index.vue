<script lang="ts" setup>
import FileGrid from './FileGrid.vue'
import { Modal, type RequestOption } from '@arco-design/web-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import mittBus from '@/utils/mitt.ts'
import useFileManage from './useFileManage'
import { api as viewerApi } from 'v-viewer'
import 'viewerjs/dist/viewer.css'
import type { ExcelConfig } from '@/components/FilePreview/type'
// const FilePreview = defineAsyncComponent(() => import('@/components/FilePreview/index.vue'))
import { useTable } from '@/hooks'
import { type FileItem, type FileQuery, createDir, deleteFile, listFile, uploadFile } from '@/apis/system/file'
import { ImageTypes, OfficeTypes } from '@/constant/file'
import { useWindowSize } from '@vueuse/core'
const FileList = defineAsyncComponent(() => import('./FileList.vue'))
const route = useRoute()
let { width } = useWindowSize()
const queryForm = reactive<FileQuery>({
  originalName: undefined,
  parentPath: (!route.query.type || route.query.type?.toString() === '0') ? '/' : undefined,
  type: route.query.type?.toString() && route.query.type?.toString() !== '0' ? route.query.type?.toString() : undefined,
  sort: ['type,asc', 'updateTime,desc'],
})
const createDirModalVisible = ref(false)
const isBatchMode = ref(false)
const { mode, selectedFileIds, toggleMode, addSelectedFileItem } = useFileManage()
const paginationOption = reactive({
  defaultPageSize: 30,
  defaultSizeOptions: [30, 40, 50, 100, 120],
})
const filePreviewRef = ref()
// 上传
const handleUpload = (options: any): Promise<unknown> => { // 注意：这里将 options 类型改为 any（或适配 Element Plus 的 UploadRequestOptions）
  const controller = new AbortController()

  // 用 Promise 包装上传逻辑，符合 UploadRequestHandler 的返回值要求
  const promise = new Promise((resolve, reject) => {
    (async function requestWrap() {
      const { onProgress, onError, onSuccess, file, name = 'file' } = options // Element Plus 中文件对象是 file，而非 fileItem
      onProgress({ percent: 20 }) // Element Plus 的 onProgress 需要传入 { percent: 数值 }

      const formData = new FormData()
      formData.append('parentPath', queryForm.parentPath ?? '/')
      formData.append(name, file) // Element Plus 中文件是 options.file，而非 fileItem.file

      try {
        const res = await uploadFile(formData)
        ElMessage.success('上传成功')
        onSuccess(res) // 通知上传组件成功
        search()
        resolve(res) // 解决 Promise
      } catch (error) {
        onError(error) // 通知上传组件失败
        reject(error) // 拒绝 Promise
      } finally {
        // mittBus.emit('file-total-refresh')
      }
    })()
  });
  // 给 Promise 挂载 abort 方法，支持中断上传
  (promise as any).abort = () => {
    controller.abort()
  }

  return promise
}
const handleSelect = (item: FileItem) => {
  addSelectedFileItem(item)

}
// 批量删除
const handleMulDelete = () => {
  ElMessageBox.confirm(
    `是否确定删除所选的${selectedFileIds.value.length}个文件？`,
    '提示',
    {
      confirmButtonText: 'OK',
      cancelButtonText: 'Cancel',
      type: 'warning',
    }
  )
    .then(async () => {
      await deleteFile(selectedFileIds.value)
      ElMessage.success('删除成功')
      search()
      mittBus.emit('file-total-refresh')
      isBatchMode.value = false
    },)
    .catch(() => {
      ElMessage({
        type: 'info',
        message: 'Delete canceled',
      })
    })
}
const {
  tableData: fileList,
  loading,
  pagination,
  search,
} = useTable((page) => listFile({ ...queryForm, ...page }), { immediate: true, paginationOption })
const newDirName = ref()
// 新建文件夹弹窗窗口确认事件
const handleCreateDir = async () => {
  await createDir(queryForm.parentPath ?? '/', newDirName.value)
  newDirName.value = undefined
  createDirModalVisible.value = false
  search()
}
// 新建文件夹弹窗窗口取消事件
const handleCancel = () => {
  newDirName.value = undefined
  createDirModalVisible.value = false
}
// 右键菜单

const handleRightMenuClick = async (mode: string, fileInfo: FileItem) => {
  if (mode === 'delete') {
    ElMessageBox.confirm(
      `是否确定删除${fileInfo.type === 0 ? '文件夹' : '文件'}「${fileInfo.originalName}」？`,
      '提示',
      {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning',
      }
    )
      .then(async () => {
        await deleteFile([fileInfo.id])
        ElMessage.success('删除成功')
        search()
        mittBus.emit('file-total-refresh')
      },)
      .catch(() => {
        ElMessage({
          type: 'info',
          message: 'Delete canceled',
        })
      })
    // } else if (mode === 'rename') {
    //   openFileRenameModal(fileInfo, search)
    // } else if (mode === 'detail') {
    //   openFileDetailModal(fileInfo)
    // } else if (mode === 'download') {
    //   await onDownload(fileInfo)
    // }
  }
}
//点击文件
const handleClickFile = (item: FileItem) => {

  if (ImageTypes.includes(item.extension)) { // 1. 匹配图片类型
    if (item.url) { // 2. 确保文件有预览链接（url 不为空）
      // 3. 筛选出所有图片类型的文件，提取它们的 url（组成预览图片列表）
      const imgList: string[] = fileList.value
        .filter((i) => ImageTypes.includes(i.extension)) // 只保留图片文件
        .map((a) => a.url || '') // 提取每个图片的 url（空值用空字符串兜底）

      // 4. 找到当前点击图片在列表中的索引（用于预览时默认显示当前图）
      const index = imgList.findIndex((i) => i === item.url)

      // 5. 调用图片预览 API（viewerApi），传入预览配置
      if (imgList.length) { // 确保有图片可预览
        viewerApi({
          options: {
            initialViewIndex: index, // 预览时默认显示的图片索引（当前点击的图）
          },
          images: imgList, // 所有可预览的图片 url 列表（支持切换）
        })
      }
    }
  }
  if (OfficeTypes.includes(item.extension)) {
    const excelConfig: ExcelConfig = {
      xls: item.extension === 'xls' || item.extension === 'xlsx',
      minColLength: 0,
      minRowLength: 0,
      widthOffset: 0,
      heightOffset: 0,
      beforeTransformData: (workbookData: any) => {
        return workbookData
      },
      transformData: (workbookData: any) => {
        return workbookData
      }
    }
    filePreviewRef.value.onPreview({
      fileInfo: { data: item.url, fileType: item.extension, fileName: item.originalName },
      excelConfig,
    })
  }
}
// 双击文件
const handleDblclickFile = (item: FileItem) => {
  console.log('双击事件')
  if (item.type === 0) {
    queryForm.parentPath = `${item.parentPath === '/' ? '' : item.parentPath}/${item.name}`
    search()
  }
}
const breadcrumbList = computed(() => {
  const path = queryForm.parentPath ?? '/'
  const parts = path.split('/').filter((p) => p !== '')
  return parts.map((part, index) => {
    const fullpath = parts.slice(0, index + 1).join('/')
    return { name: part || '根目录', path: `/${fullpath}` }
  })
})
// 处理面包屑点击
const handleBreadcrumbClick = (item: any) => {
  queryForm.parentPath = item.path
  search()
}
const visible = ref(false)
</script>

<template>
  <div class="file-main">

    <el-breadcrumb class="file-main__breadcrumb">
      <el-breadcrumb-item v-if="queryForm.parentPath"
        @click="handleBreadcrumbClick({ name: '根目录', path: '/' })">根目录</el-breadcrumb-item>
      <el-breadcrumb-item v-else>全部</el-breadcrumb-item>
      <el-breadcrumb-item v-for="(item, index) in breadcrumbList" :key="index" @click="handleBreadcrumbClick(item)">
        {{ item.name || '根目录' }}
      </el-breadcrumb-item>
    </el-breadcrumb>
    <el-row class="upload">
      <el-space>
        <el-dropdown>
          <el-button type="primary" round icon="Upload">
            上传文件
          </el-button>
          <template #dropdown>
            <el-space direction="vertical">
              <el-upload v-permission="['system:file:upload']" :http-request="handleUpload" :show-file-list="false">
                <template #trigger>
                  <el-button>普通上传</el-button>
                </template>
              </el-upload>
              <el-button @click="visible = true">
                分片上传
              </el-button>

            </el-space>

          </template>
        </el-dropdown>
        <el-dialog v-model="visible" title="分片上传" :width="width > 1350 ? 1350 : '100%'" :footer="false" @close="search"
          top="0" style="height: 100vh; ;" :close-on-click-modal="false" :close-on-press-escape="false">
          <MultipartUpload v-if="visible" :root-path="queryForm.parentPath" :chunk-size="5 * 1024 * 1024"
            :max-concurrent-files="3" />
        </el-dialog>
        <el-input placeholder="在当前目录下搜索名称" v-model="queryForm.originalName">
          <template #append>
            <el-button icon="Search" @click="search">
              查询
            </el-button>
          </template>
        </el-input>
      </el-space>

      <!--右侧区域-->
      <el-space>

        <el-button v-if="isBatchMode" :disabled="!selectedFileIds.length" type="primary" status="danger"
          @click="handleMulDelete" icon="Delete">

        </el-button>
        <el-button v-permission="['system:file:createDir']" type="primary"
          @click="createDirModalVisible = !createDirModalVisible" icon="folder">

          <template #default>新建文件夹</template>
        </el-button>
        <el-button v-permission="['system:file:delete']" type="primary" @click="isBatchMode = !isBatchMode">

          <template #default>
            <GiSvgIcon name="select-all"></GiSvgIcon>
            {{ isBatchMode ? '取消批量' : '批量操作' }}
          </template>
        </el-button>
        <el-button-group>
          <el-tooltip content="视图">
            <el-button @click="toggleMode">
              <GiSvgIcon size="15" :name="mode === 'grid' ? 'list' : 'apps'">
              </GiSvgIcon>

            </el-button>
          </el-tooltip>
        </el-button-group>
      </el-space>
    </el-row>
    <el-row class="main">
      <FileGrid v-show="mode === 'grid'" :data="fileList" :selected-file-ids="selectedFileIds"
        :isBatchMode="isBatchMode" @select="handleSelect" @right-menu-click="handleRightMenuClick"
        @click="handleClickFile" @dblclick="handleDblclickFile">
      </FileGrid>
      <FileList v-show="mode === 'list'" :data="fileList" :selected-file-ids="selectedFileIds"
        :isBatchMode="isBatchMode" @select="handleSelect" @dblclick="handleDblclickFile">
      </FileList>
    </el-row>
    <div class="pagination">
      <el-pagination v-bind="pagination" />
    </div>
    <el-dialog title="新建文件夹" v-model="createDirModalVisible" align-center>
      <template #default>
        <el-input v-model="newDirName" placeholder="请输入文件夹名称"></el-input>
      </template>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleCancel">Cancel</el-button>
          <el-button type="primary" @click="handleCreateDir">
            Confirm
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
  <FilePreview ref="filePreviewRef" />
</template>

<style lang="scss" scoped>
.file-main {
  height: 100%;
  // width: 100%;
  padding: 10px;
  background: var(--color-bg-1);
  position: relative;
}

.main {
  margin-top: 25px;
}

.file-main-title {
  padding: 8px 16px;
  background: var(--color-bg-2);
  border-radius: 4px;
  font-size: 14px;
  color: var(--color-text-2);
  border-bottom: 1px solid var(--color-border-3);
}

.pagination {
  position: absolute;
  bottom: 5%;
  right: 5%;
}

.upload {
  margin-top: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;

}
</style>