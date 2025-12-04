<template>
    <div class="container">
        <div class="left">
            <p class="count">统计实列</p>
            <span class="count-num">{{ count }}</span>
            <span class="today"> 今日 </span>
            <span v-if="growth > 0" class="growth" style="color: #13ce66">{{ growth }}
                <GiSvgIcon name="arrow-up" color="#13ce66" />
            </span>
            <span v-else class="down" style="color: #ff4949">{{ growth }}
                <GiSvgIcon name="arrow-down" color="#ff4949" />
            </span>
        </div>
        <div class="chart">
            <Chart v-if="!loading" :option="chartOption" />

        </div>
    </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import { useChart } from '@/hooks'
import { useAppStore } from '@/store'

const appStore = useAppStore()
const isDark = computed(() => appStore.theme === 'dark')

const count = ref(0)
const growth = ref(0)

const { chartOption } = useChart(() => {
    return {
        grid: {
            left: 0,
            right: 0,
            top: 0,
            bottom: 0,
        },
        legend: {
            show: true,
            top: 'center',
            right: '20%',
            orient: 'vertical',
            icon: 'circle',
            itemWidth: 6,
            itemHeight: 6,
            textStyle: {
                color: '#4E5969',
            },
        },
        tooltip: {
            show: true,
        },
        series: [
            {
                name: '总计',
                type: 'pie',
                radius: ['50%', '70%'],
                center: ['30%', '50%'],
                label: {
                    show: false,
                },
                data: chartData.value,
            },
        ],
    }
})

const loading = ref(false)
const colors = ['#8D4EDA', '#00B2FF', '#86DF6C']
interface ChartData {
    name: string
    value: number
    itemStyle: {
        color: string
    }
}
const chartData = ref<ChartData[]>([])
// 查询图表数据
const getChartData = async () => {
    try {
        loading.value = true
        count.value = 88888
        growth.value = 88.8
        const data = [30, 20, 10]
        data.forEach((item, index) => {
            chartData.value.push({
                name: `示例${index + 1}`,
                value: item,
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


<style lang="scss" scoped>
.container {
    height: 134px;
    background: rgb(238, 238, 255);
    display: flex;
    border-radius: 10px;

    .count-num {
        font-size: 1.8em;
        display: inline-block;
        margin-left: 8px;
        margin-bottom: 15px;
        display: block;
    }

    .count {
        margin-top: 15px;
        font-size: 1.2em;
        font-weight: 600;
    }

    .today {
        margin-top: 15px;
        font-size: 1.0em;
    }

    .left {
        height: 100%;
        padding-left: 17px;
    }

    .chart {
        // float: right;
        width: calc(100% - 108px);
        height: 90%;
        // vertical-align: bottom;
    }
}
</style>