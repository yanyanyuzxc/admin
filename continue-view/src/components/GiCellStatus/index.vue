<template>
    <span v-if="props.status === 1">
        <GiSvgIcon name="check-circle" size="15" color="#52c41a"></GiSvgIcon>
        <span class="status-text">启用</span>
    </span>
    <span v-if="props.status === 2">
        <GiSvgIcon name="minus-circle" size="15" color="#f5222d"></GiSvgIcon>

        <span class="status-text">禁用</span>
    </span>
</template>

<script setup lang="ts">
defineOptions({ name: 'GiCellStatus' })

const props = withDefaults(defineProps<Props>(), {
    status: 1,
})

interface Props {
    status: 1 | 2
}
</script>

<style scoped lang="scss">
.status-text {
    display: inline-block;
    vertical-align: middle;
    margin-left: 5px;
}
</style>