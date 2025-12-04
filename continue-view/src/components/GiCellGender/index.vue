<template>
    <el-tag v-if="props.gender === 1" color="arcoblue" size="small" class="gi_round">
        <!-- <template #icon><icon-man /></template> -->
        <template #default>男</template>
    </el-tag>
    <el-tag v-else-if="props.gender === 2" color="magenta" size="small" class="gi_round">
        <!-- <GiSvgIcon name="icon-woman"></GiSvgIcon> -->
        <template #default> 女</template>

    </el-tag>
    <el-tag v-else color="gray" size="small" class="gi_round">
        <template #default>未知</template>
    </el-tag>
</template>

<script setup lang="ts">
defineOptions({ name: 'GiCellGender' })

const props = withDefaults(defineProps<Props>(), {
    gender: 1,
})

interface Props {
    gender: 1 | 2 | 0
}
</script>

<style scoped lang="scss"></style>