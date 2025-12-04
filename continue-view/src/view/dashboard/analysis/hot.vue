<template>
    <div class="hot-chart" style="width: 100%">
        <p>热门模块分析(TOP10)</p>
        <div class="general-card">
            <div class="chart">
                <Chart v-if="!loading" :option="chartOption" style="width: 100%; height: 370px" />
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import type { EChartsOption } from 'echarts'
import { useChart } from '@/hooks'
import { type DashboardChartCommonResp, getAnalysisModule as getData } from '@/apis/common'
import { onMounted, ref } from 'vue'
const yAxis = ref<string[]>([])
const chartData = ref<number[]>([])
const { chartOption } = useChart((isDark: boolean) => {
    return {
        grid: {
            left: 55,
            right: 20,
            top: 0,
            bottom: 20,
        },
        xAxis: {
            type: 'value',
            axisLabel: {
                show: true,
                formatter(value: number, idx: number) {
                    if (idx === 0) return String(value)
                    return `${Number(value) / 1000}k`
                },
            },
            splitLine: {
                lineStyle: {
                    color: isDark ? '#484849' : '#E5E8EF',
                },
            },
        },
        yAxis: {
            type: 'category',
            data: yAxis.value,
            axisLabel: {
                show: true,
                color: '#4E5969',
            },
            axisTick: {
                show: true,
                length: 2,
                lineStyle: {
                    color: '#A9AEB8',
                },
                alignWithLabel: true,
            },
            axisLine: {
                lineStyle: {
                    color: isDark ? '#484849' : '#A9AEB8',
                },
            },
        },
        tooltip: {
            show: true,
            trigger: 'axis',
        },
        series: [
            {
                data: chartData.value,
                type: 'bar',
                barWidth: 7,
                itemStyle: {
                    color: '#4086FF',
                    borderRadius: 4,
                },
            },
        ],
    }
})

const loading = ref(false)
// 查询图表数据
const getChartData = async () => {
    try {
        loading.value = true
        const { data } = await getData()
        console.log(data)
        data.forEach((item: DashboardChartCommonResp) => {
            yAxis.value.unshift(item.name)
            chartData.value.unshift(item.value)
        })
    } finally {
        loading.value = false
    }
}

onMounted(() => {
    getChartData()
})
</script>

<style scoped lang="scss">
.hot-chart {
    background: var(--color-bg-1);
    padding: 10px 20px;
    margin-left: 20px;

    p {
        font-size: 1.2em;
    }
}
</style>