<script lang="ts" setup>
// const props = defineProps<Props>()
// interface Props {
//     title: string
//     icon: string
//     size?: number
// }

import { ref, computed } from 'vue'
import { useAppStore } from '@/store';
let appStore = useAppStore()
declare const window: Window & typeof globalThis;
defineProps(['icon', 'size'])
// 定义颜色
let color = ref('rgba(142, 143, 145)') // 初始颜色

const changeColor = () => {
    color.value = 'rgba(64,158,255)' // 点击后的颜色
}


// let tag = ref()
// let tagColor = computed(() => {
//     return window.getComputedStyle(tag.value) || 'rgba(142, 143, 145)'
// })

</script>

<template>
    <div class="logo" @click="changeColor">
        <!-- 注意：组件名是 GiSvgIcon 还是 GisVgIcon？ -->
        <GiSvgIcon :name="icon" :size="size || 24"></GiSvgIcon>
        <!-- 将p标签的样式颜色绑定到colorv-if="!appStore.menuCollapse" -->
    </div>
</template>

<style lang="scss" scoped>
.logo {
    display: flex;
    align-items: center;
    justify-content: center;

    p {
        margin-left: 15px;

    }
}
</style>