<script lang="ts" setup>
const props = withDefaults(defineProps<Props>(), {
    data: () => [],
})

interface Props {
    data: string[]
}
</script>

<template>
    <el-space v-if="props.data && props.data.length === 1">

        <el-tag>{{ props.data[0] }}</el-tag>
    </el-space>
    <el-space v-if="props.data && props.data.length > 1">
        <el-tag>{{ props.data[0] }}</el-tag>
        <el-popover width="200px">
            <template #reference>

                <el-tag>+{{ props.data.length - 1 }}</el-tag>
            </template>
            <el-tag v-for="(tag, index) in props.data" :key="index">
                {{ tag }}
            </el-tag>
        </el-popover>
    </el-space>
</template>

<style lang="scss" scoped>
.el-popover {
    .el-tag {
        margin: 3px;
    }
}
</style>