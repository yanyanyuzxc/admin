<script lang="ts" setup>
import { ElMessage } from 'element-plus'

import { type TreeNodeData } from '@arco-design/web-vue'
import { useClipboard } from '@vueuse/core'
import { type GeneratePreviewResp, genPreview } from '@/apis/code/generator'

const emit = defineEmits<{
  (e: 'download', tableNames: any): void
  (e: 'generate', tableNames: any): void
}>()
const { copy, copied } = useClipboard()
// 复制
const onCopy = () => {
  if (currentPreview.value) {
    copy(currentPreview.value?.content)
  }
}
watch(copied, () => {
  if (copied.value) {
    ElMessage.success('复制成功'); // 复制成功后弹窗提示
  }
});
const genPreviewList = ref<GeneratePreviewResp[]>([])
const currentPreview = ref<GeneratePreviewResp>()
const visible = ref(false)
const previewTableNames = ref<string[]>([])

const mergeDir = (node: TreeNodeData) => {
  if (node.children.length === 1 && typeof node.children[0].key === 'number') {
    const child = node.children[0]
    node.label = `${node.label}/${child.label}`
    node.children = child.children
    mergeDir(node)
  }
  if (node?.children) { for (let child of node.children) { mergeDir(child) } }
}
const pushDir = (children: TreeNodeData[] | undefined, node: TreeNodeData) => {
  if (children) {
    for (let child of children) {
      if (child.label === node.label) {
        return child.children
      }
    }
  }
  children?.push(node)
  return node.children

}
// 自增的一个key 因为key相同的节点会出现一些问题
let autoIncrementKey = 0
const assembleTree = (genPreview: GeneratePreviewResp) => {
  const sparator = genPreview.path.includes('/') ? '/' : '\\'
  const paths = genPreview.path.split(sparator)
  let tempChildren: TreeNodeData[] = treeData.value
  for (let path of paths) {
    tempChildren = pushDir(tempChildren, { label: path, key: autoIncrementKey++, children: new Array<TreeNodeData>() })
  }
  tempChildren?.push({ label: genPreview.fileName, key: genPreview.fileName, children: new Array<TreeNodeData>() })
}
const selectedKeys = ref()
const onOpen = async (tableNames: string[]) => {
  previewTableNames.value = tableNames

  const { data } = await genPreview(tableNames)

  genPreviewList.value = data
  for (const genPreview of genPreviewList.value) {
    assembleTree(genPreview)
  }
  for (const valueElement of treeData.value) {
    mergeDir(valueElement)
  }
  selectedKeys.value = [genPreviewList.value[0].fileName]
  currentPreview.value = genPreviewList.value[0]
  visible.value = true
}
const treeRef = ref()
const treeData = ref<TreeNodeData[]>([])
defineExpose({
  onOpen
})


// 校验文件类型
const checkFileType = (label: string, type: string) => {
  return label.endsWith(type)
}
const handleClick = (node: TreeNodeData) => {

  const { key } = node

  //如果点击的是文件
  if (typeof key === 'string') {
    if (key === selectedKeys.value[0]) {
      return
    }
    currentPreview.value = genPreviewList.value.find(item => item.fileName === key)
    selectedKeys.value = key
    return
  }
  // console.log('lll')
}
// 下载
const onDownload = () => {
  emit('download', [previewTableNames.value])
}

// 下载
const onGenerator = () => {
  emit('generate', [previewTableNames.value])
}
</script>

<template>
  <el-dialog v-model="visible" class="dialog" top="2vh" style="height:95vh;width:90vw">
    <template #header>
      <div class="custom-dialog-header">
        {{ previewTableNames.length === 1 ? `生成 ${previewTableNames[0]} 表预览` : '批量生成预览' }}
        <el-link v-permission="['code:generator:generate']" type="primary" style="margin-left: 10px" icon="Link"
          @click="onDownload">下载源码</el-link>
        <el-link v-permission="['code:generator:generate']" type="primary" style="margin-left: 10px" icon="Link"
          @click="onGenerator">生成源码</el-link>
      </div>
    </template>
    <template #default>
      <div class="body">
        <el-scrollbar>
          <div class="left">

            <el-tree :data="treeData" ref="treeRef" :default-expand-all="true" style="height:100%;"
              @node-click="handleClick" highlight-current>
              <template #default="{ node }">
                <GiSvgIcon v-if="!node.isLeaf && !node.expanded" :size="16" name="directory-blue" />
                <GiSvgIcon v-if="!node.isLeaf && node.expanded" :size="16" name="directory-open-blue" />
                <GiSvgIcon v-if="node.isLeaf && checkFileType(node.data.label, '.java')" :size="16" name="file-java" />
                <GiSvgIcon v-if="node.isLeaf && checkFileType(node.data.label, '.vue')" :size="16" name="file-vue" />
                <GiSvgIcon v-if="node.isLeaf && checkFileType(node.data.label, '.ts')" :size="16"
                  name="file-typescript" />
                <GiSvgIcon v-if="node.isLeaf && checkFileType(node.data.label, '.js')" :size="16"
                  name="file-javascript" />
                <GiSvgIcon v-if="node.isLeaf && checkFileType(node.data.label, '.json')" :size="16" name="file-json" />
                <GiSvgIcon v-if="node.isLeaf && checkFileType(node.data.label, 'pom.xml')" :size="16"
                  name="file-maven" />
                <GiSvgIcon
                  v-if="node.isLeaf && checkFileType(node.data.label, '.xml') && !checkFileType(node.data.label, 'pom.xml')"
                  :size="16" name="file-xml" />
                <GiSvgIcon v-if="node.isLeaf && checkFileType(node.data.label, '.sql')" :size="16" name="file-sql" />
                <span class="label">{{
                  node.data.label }}</span>
              </template>
            </el-tree>

          </div>
        </el-scrollbar>
        <div class="right">
          <div class="header">
            <span> {{ currentPreview?.path }}{{ currentPreview?.path.indexOf('/') !== -1 ? '/' : '\\' }}{{
              currentPreview?.fileName }}</span>
          </div>
          <div class="code">
            <el-scrollbar style="height: 650px; overflow: auto">
              <el-link style="position: absolute; right: 20px; z-index: 999" title="复制" @click="onCopy" type="primary">

                <template #default>复制</template>
              </el-link>
              <GiCodeView :type="'vue' === currentPreview?.fileName.split('.')[1] ? 'vue' : 'javascript'"
                :code-json="currentPreview?.content" />
            </el-scrollbar>
          </div>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
.body {
  display: flex;

}

.label {
  display: inline-block;
  margin-left: 10px;

  &.active {
    color: $color-theme;
  }
}

.custom-dialog-header {
  @include flex-center;
  width: 100%;
  height: 100%;
  padding: 10px;
  font-size: 1.2em;
  border-bottom: 1px solid #c8c5c5;
}

.body {
  height: 80vh;
  overflow: hidden;
}

.left {
  flex: 1.3;
  padding: 10px;
  border-right: 1px solid #eee;
  overflow-y: auto;
  max-height: 100%;
}

.right {
  flex: 3;
  padding: 10px;
}

.header {
  font-size: 1.1em;

}
</style>