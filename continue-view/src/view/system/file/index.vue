<template>
    <el-row :gutter="14" class="file-manage">
        <el-col :xs="0" :sm="8" :md="7" :lg="4" :xl="3" :xxl="3" class="h-full overflow-hidden">
            <FileAside></FileAside>
        </el-col>
        <el-col :xs="24" :sm="16" :md="17" :lg="20" :xl="21" :xxl="21" class="h-full overflow-hidden">
            <FileMain></FileMain>
        </el-col>
    </el-row>
</template>

<script setup lang="ts">
import FileAside from './main/FileAside.vue'
import FileMain from './main/FileMain/index.vue'

defineOptions({ name: 'SystemFile' })
</script>

<style scoped lang="scss">
.file-manage {
    flex: 1;
    padding: $margin;
    overflow: hidden;
}
</style>