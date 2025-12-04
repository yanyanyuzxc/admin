<script lang="ts" setup>
import { useAppStore } from '@/store/modules/app';
import { ref } from 'vue';
let appStore = useAppStore();
let isCircle = ref(false);
const onClick = () => {
    appStore.setMenuCollapse(!appStore.menuCollapse);
}
</script>

<template>
    <button style="background: rgba(242,243,245);" @mouseenter="isCircle = true" @mouseleave="isCircle = false"
        :class="{ 'circle': isCircle }" @click="onClick">
        <GiSvgIcon name="unfold" v-if="appStore.menuCollapse" size="20" color="var(--color-text-2)"></GiSvgIcon>
        <GiSvgIcon name="fold" v-else size="20" color="var(--color-text-2)"></GiSvgIcon>
    </button>
</template>

<style lang="scss" scoped>
button {
    border: none;
    transition: all 0.3s ease-in-out;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 25px;
    height: 25px;
    border-radius: 5px;
    background-color: var(--color-secondary-hover) !important;
    flex-shrink: 0;
    margin-right: 5px;

    &.circle {
        border-radius: 50%;

    }
}
</style>