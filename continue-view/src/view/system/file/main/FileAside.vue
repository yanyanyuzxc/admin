<script lang="ts" setup>
import { FileTypeList, type FileTypeListItem } from '@/constant/file'
import FileAsideStatistics from '@/view/system/file/main/FileAsideStatistics.vue'
const selectedKey = ref('0')
const onClickItem = (item: FileTypeListItem) => { }
</script>

<template>
  <el-card>
    <el-menu :default-active="'0'" :selected-keys="[selectedKey]">
      <el-sub-menu index="0">
        <template #title>
          <GiSvgIcon name="apps"></GiSvgIcon>
          <span>文件类型</span>
        </template>
        <el-menu-item v-for="item in FileTypeList" :key="item.value.toString()" @click="onClickItem(item)"
          :index="item.value.toString()">


          <template #default>
            <GiSvgIcon :size="28" :name="item.icon"></GiSvgIcon>
            <span>{{ item.name }}</span>
          </template>

        </el-menu-item>
      </el-sub-menu>
    </el-menu>
  </el-card>
  <FileAsideStatistics />
</template>

<style lang="scss" scoped>
:deep(.el-menu) {
  border-right: 0px !important;
}

:deep(.el-card__body) {
  padding: 0px
}

.el-card {}

span {
  display: inline-block;
  margin-left: 10px;
}
</style>