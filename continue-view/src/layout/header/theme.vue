<script lang="ts" setup>
import { useAppStore } from '@/store'
let appStore = useAppStore()
import { useDark, useToggle } from '@vueuse/core'
const isDark = useDark({
    selector: 'body',
    attribute: 'arco-theme',
    valueDark: 'dark',
    valueLight: 'light',
    storageKey: 'arco-theme',
    onChanged(dark: boolean) {
        appStore.toggleTheme(dark)
    },
})
//useToggle函数返回一个函数，当点击时，会切换isDark的值，从而触发onChanged函数
const toggleTheme = useToggle(isDark)

const handleClick = () => {
    toggleTheme()
    console.log('111')
}
</script>
<template>
    <el-tooltip placement="top">

        <GiSvgIcon name="moon-fill" v-if="appStore.theme === 'light'" @click="handleClick"></GiSvgIcon>
        <GiSvgIcon name="sun-fill" v-else @click="handleClick"> </GiSvgIcon>
        <template #content> 主题切换 </template>
    </el-tooltip>

</template>

<style lang="scss" scoped></style>