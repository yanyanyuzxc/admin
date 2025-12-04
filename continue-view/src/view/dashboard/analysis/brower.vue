<template>
    <div :loading="loading" class="brower">
        <p>浏览器</p>
        <div class="general-card">
            <div class="chart">
                <Chart v-if="!loading" :option="chartOption" style="height: 210px" />
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { EChartsOption } from 'echarts'
import { useChart } from '@/hooks'
import { type DashboardChartCommonResp, getAnalysisBrowser as getData } from '@/apis/common'
interface chartDataItem {
    name: string
    value: number
    itemStyle: {
        color: string
    }
}
const xAxis = ref<string[]>([])
const chartData = ref<chartDataItem[]>([])
const { chartOption } = useChart((isDark: boolean) => {
    return {
        legend: {
            data: xAxis.value,
            bottom: 0,
            icon: 'circle',
            itemWidth: 8,
            textStyle: {
                color: isDark ? 'rgba(255,255,255,0.7)' : '#4E5969',
            },
            itemStyle: {
                borderWidth: 0,
            },
        },
        tooltip: {
            show: true,
            trigger: 'item',
        },
        series: [
            {
                type: 'pie',
                radius: ['35%', '60%'],
                center: ['50%', '42%'],
                label: {
                    formatter: '{d}% ',
                    color: isDark ? 'rgba(255, 255, 255, 0.7)' : '#4E5969',
                },
                itemStyle: {
                    borderColor: isDark ? '#000' : '#fff',
                    borderWidth: 1,
                },
                data: chartData.value,
            },
        ],
    }
})

const loading = ref(false)
const colors = ['#246EFF', '#00B2FF', '#81E2FF', '#846BCE', '#86DF6C']
// 查询图表数据
const getChartData = async () => {
    try {
        loading.value = true
        const { data } = await getData()
        data.forEach((item: DashboardChartCommonResp, index: number) => {
            xAxis.value.push(item.name)
            chartData.value.push({
                ...item,
                itemStyle: {
                    color: data.length > 1 && index === data.length - 1 ? colors[colors.length - 1] : colors[index],
                },
            })
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
.brower {
    // width: 100%;
    height: 100%;
    background: var(--color-bg-1);
    padding: 10px 20px;
    margin-top: 20px;
    color: var(--color-text-1);
    font-size: 1.1em;

    p {
        margin-bottom: 0px;
        padding-bottom: 0px;
    }

    .chart {
        margin-top: 0px;
        height: 100%;
    }
}
</style>