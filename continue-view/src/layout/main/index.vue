<script lang="ts" setup>
import { useAppStore } from '@/store'
let appStore = useAppStore()
defineOptions({
    name: 'Main'
})
</script>

<template>
    <div class="layout" :class="{ 'color': appStore.theme === 'dark' }">
        <router-view></router-view>
    </div>

</template>

<style lang="scss" scoped>
.layout {
    width: 100%;
    height: 100%;
    box-sizing: border-box;
    background: rgb(245, 247, 253);
    padding: 15px;


    &.color {
        background-color: var(--color-bg-2);
    }
}
</style>