<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import type { EChartsOption } from 'echarts'
import { useChart } from '@/hooks'
import { type DashboardChartCommonResp, getAnalysisOs as getData } from '@/apis/common'
const loading = ref(false)

//查询图表的数据
// interface xAxisitem {
//     name: string
// }
interface chartDataitem {
    name: string
    value: number
    itemStyle: {
        color: string
    }
}
const xAxis = ref<string[]>([])
const chartData = ref<chartDataitem[]>([])
const colors = ['#246EFF', '#00B2FF', '#81E2FF', '#846BCE', '#86DF6C']
const getChartData = async () => {
    try {
        loading.value = true
        const { data } = await getData()
        data.forEach((item: DashboardChartCommonResp) => {
            xAxis.value.push(item.name)
            chartData.value.push({
                ...item,
                itemStyle: {
                    color: colors[Math.floor(Math.random() * colors.length)]
                }
            })
        })
    } catch (error) {
        console.log(error)
    }
    finally {
        loading.value = false
    }
}
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
onMounted(() => {
    getChartData()
})
</script>

<template>
    <div class="terminal">
        <p>终端</p>
        <div class="chart">
            <Chart v-if="!loading" :option="chartOption" style="height: 210px" />
        </div>
    </div>
</template>

<style lang="scss" scoped>
.terminal {
    // width: 100%;
    height: 100%;
    background: var(--color-bg-1);
    margin-top: 20px;
    padding: 10px 20px;
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