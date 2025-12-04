<script lang="ts" setup>
import FileRightMenu from './FileRightMenu.vue'
import type { FileItem } from '@/apis/system'
import has from '@/utils/has'
import type { PopoverInstance } from 'element-plus'

interface Props {
  data?: FileItem[]
  selectedFileIds?: string[]
  isBatchMode?: boolean
}
const props = withDefaults(defineProps<Props>(), {
  data: () => [], // 文件数据
  selectedFileIds: () => [], // 批量模式下选中的文件id数组
  isBatchMode: false, // 是否是批量模式
})
const emit = defineEmits<{
  (e: 'click', record: FileItem): void
  (e: 'dblclick', record: FileItem): void
  (e: 'select', record: FileItem): void
  (e: 'right-menu-click', mode: string, item: FileItem): void
}>()
const handleSelect = (item: any) => {
  emit('select', item)

}

// 右键菜单点击事件
const handleRightMenuClick = (mode: string, item: FileItem) => {
  emit('right-menu-click', mode, item)
}
const FileImage = defineAsyncComponent(() => import('./FileImage.vue'))
// 1. 文件项 ref 数组：存储 .file 元素的 DOM（HTMLElement）
const fileRefs = ref<(Element | null | ComponentPublicInstance)[]>(props.data?.map(() => null) || [])
// 2. 菜单 ref 数组：存储 el-popover 实例（PopoverInstance）
const popoverRefs = ref<(Element | null | ComponentPublicInstance)[]>(props.data?.map(() => null) || [])
// 点击事件
const handleClickFile = (item: FileItem) => {
  emit('click', item)
}
// 双击事件
const handleDblclickFile = (item: FileItem) => {

  emit('dblclick', item)
}
</script>

<template>
  <div class="image">
    <div class="file" v-for="(item, index) in data" :key="item.id" :class="{ batch: isBatchMode }"
      @click.stop="handleClickFile(item)" @dblclick="handleDblclickFile(item)">

      <div class="item" :ref="el => fileRefs[index] = el">
        <div class="file-item">
          <FileImage :data="item" :title="item.originalName"></FileImage>
        </div>
      </div>
      <p class="file-name">{{ item.originalName }}</p>
      <div class="checkbox" @click="handleSelect(item)" v-show="isBatchMode">
        <el-checkbox :model-value="props.selectedFileIds.includes(item.id)"
          :class="{ 'checked': props.selectedFileIds.includes(item.id) }"></el-checkbox>
      </div>

      <el-popover placement="bottom" :virtual-ref="() => fileRefs[index]" :ref="el => popoverRefs[index] = el"
        trigger="contextmenu" :show-after="10" virtual-triggering width="50">


        <FileRightMenu :data="item" @click="handleRightMenuClick($event, item)"
          v-if="has.hasPermOr(['system:file:update', 'system:file:get', 'system:file:download', 'system:file:delete'])">
        </FileRightMenu>
        <span class="no-perm" v-else>无操作权限</span>
      </el-popover>
    </div>
  </div>

</template>

<style lang="scss" scoped>
.image {
  display: grid;
  grid-template-columns: repeat(9, 1fr);
  grid-gap: 30px;
  max-width: 1200px;
}



:deep(.el-popover) {
  --el-popover-padding: 0;
}

.item {
  display: flex;
  justify-content: center;

}

.file {
  height: 100px;
  position: relative;
  width: 100%;
  flex-wrap: nowrap;
  overflow: hidden;
  @include flex-center;
  flex-direction: column;



  &.batch {
    background: rgba(33, 33, 33, 0.09);
  }
}

.checkbox {
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  z-index: 9;

  .el-checkbox {
    position: absolute;
    top: 5px;
    left: 5px;
    padding-left: 0;
  }

  &.checked {
    background: var(--color-bg-1);
  }
}

.file-item {
  width: 80%;
  height: 60px;
}

.file-name {
  width: 100%;
  font-size: 12px;
  margin-top: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  padding: 0 5px;
  text-align: center;
  box-sizing: border-box;
}
</style>