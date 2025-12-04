<script lang="ts" setup>
defineOptions({
    name: 'Logo'
})
const props = withDefaults(defineProps<Props>(), {
    collapse: false
})

interface Props {
    collapse: boolean
}
</script>

<template>
    <div class="box1" :class="{ 'collpase': props.collapse }">
        <div class="logo">
            <img src="@/assets/images/logo.png" alt="">
        </div>

        <span v-if="!props.collapse">Continue View</span>
    </div>
</template>

<style lang="scss" scoped>
.box1 {
    height: 56px;
    padding: 0 12px;
    // color: var(--color-text-1);
    font-size: 20px;
    line-height: 1;
    display: flex;

    align-items: center;
    flex-shrink: 0;
    cursor: pointer;
    user-select: none;
    box-sizing: border-box;

    &.collapse {
        padding: 0;
        display: flex;
        justify-content: center;
        align-items: center;

        span {
            display: none;
        }
    }

    .logo {
        width: 35px;
        height: 35px;
        transition: all 0.2s;
        overflow: hidden;

        img {
            width: 100%;
            height: 100%;
            object-fit: contain;

        }
    }

    span {
        padding-left: 6px;
        white-space: nowrap;
        transition: color 0.3s;
        line-height: 1.5;
        display: inline-flex;
        align-items: center;
        white-space: nowrap;
        /* 关键：防止换行 */
        overflow: hidden;
        text-overflow: ellipsis;
        /* 溢出显示省略号 */
        min-width: 0;
        /* 关键：允许收缩 */
        flex-shrink: 1;

        /* 允许收缩 */
        &:hover {
            color: skyblue !important;
            cursor: pointer;
        }
    }
}
</style>