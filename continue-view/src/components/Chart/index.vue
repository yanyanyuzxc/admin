<template>
    <VCharts ref="chart" :option="option" :autoresize="autoResize" :style="{ width, height }" />
</template>

<script setup lang="ts">
import { ref, defineProps, defineExpose } from 'vue'
import { registerMap } from 'echarts/core'
import VCharts from 'vue-echarts'
// 导入 ECharts 核心模块
import 'echarts'
import worldMap from './world.json'
import chinaMap from './china.json'

defineProps({
    option: {
        type: Object,
        default() {
            return {}
        },
    },
    autoResize: {
        type: Boolean,
        default: true,
    },
    width: {
        type: String,
        default: '100%',
    },
    height: {
        type: String,
        default: '100%',
    },
})

// 无需导入任何类型，直接用 unknown 断言（比 any 更安全，且不会有类型污染）
registerMap('world', worldMap as unknown as Parameters<typeof registerMap>[1])
registerMap('china', chinaMap as unknown as Parameters<typeof registerMap>[1])
//Paramaters 表示提取函数的参数类型，并返回一个类型数组，as unkonwn as 安全的类型断言
const chart = ref(null)
defineExpose({
    chart,
})
</script>

<style scoped lang="less"></style>