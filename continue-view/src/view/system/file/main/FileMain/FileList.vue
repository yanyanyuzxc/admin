<script lang="ts" setup>
import FileRightMenu from './FileRightMenu.vue'
import { type FileItem, calcDirSize } from '@/apis/system'
import { ElMessage } from 'element-plus'
import { formatFileSize } from '@/utils'
import has from '@/utils/has'
import { text } from 'stream/consumers'
interface Props {
  data?: FileItem[]
  selectedFileIds?: string[]
  isBatchMode?: boolean
}
const FileImage = defineAsyncComponent(() => import('./FileImage.vue'))

const props = withDefaults(defineProps<Props>(), {
  data: () => [], // 文件数据
  selectedFileIds: () => [],
  isBatchMode: false, // 是否是批量模式
})
// 复制文本到剪贴板
const handleCopy = async (text: string) => {
  try {
    await navigator.clipboard.writeText(text)
    ElMessage.success('复制成功')
  }
  catch (e) {
    console.log(e)
  }
}
</script>

<template>
  <el-table :data="data">
    <el-table-column label="名称" width="400px">
      <template #default="{ row }">
        <div class="file-item">
          <div class="image">
            <FileImage :data="row" />
          </div>
          <span class="file-name">{{ row.originalName }}</span>
          <el-tooltip effect="dark" content="复制链接" placement="top">

            <el-button v-if="row.type !== 0" circle @click="handleCopy(row.url)" class="copy-btn">
              <GiSvgIcon name="copy" size="14"></GiSvgIcon>
            </el-button>
          </el-tooltip>

          <!-- 文本内容 -->

        </div>
      </template>
    </el-table-column>
    <el-table-column label="大小" prop="size"></el-table-column>
    <el-table-column label="存储名称" prop="storageName"></el-table-column>
    <el-table-column label="修改时间" prop="updateTime"></el-table-column>
    <el-table-column label="操作">
      <template #default>

      </template>
    </el-table-column>
  </el-table>
</template>

<style lang="scss" scoped>
.file-item {
  display: flex;
  align-items: center;
}

.image {
  width: 40px;
  height: 40px;
}

span {
  display: inline-block;
  margin: 0 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;

}
</style>