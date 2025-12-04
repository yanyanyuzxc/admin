<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue'
import { useChart } from '@/hooks'
import { useAppStore } from '@/store'
import { on } from 'events'
const loading = ref(false)
// 查询图表数据
const xAxis = ref<string[]>([])
const chartData = ref<number[]>([])
const count = ref(0)
const growth = ref(0)
const { chartOption } = useChart(() => {
    return {
        grid: {
            left: 0,
            right: 30,
            top: 40,
            bottom: 0,
        },
        xAxis: {
            type: 'category',
            data: xAxis.value,
            show: false,
        },
        yAxis: {
            show: false,
        },
        tooltip: {
            show: true,
            trigger: 'axis',
        },
        series: [
            {
                name: '示例',
                data: chartData.value,
                type: 'line',
                showSymbol: false,
                smooth: true,
                lineStyle: {
                    color: '#246EFF',
                    width: 2,
                    type: 'dashed',
                },
            },
        ],
    }
})
//获取数据
const getChartData = async () => {
    try {
        loading.value = true
        xAxis.value = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
        count.value = 88888
        growth.value = 88.8
        chartData.value = [4, 5, 6, 2, 3, 4, 25, 21, 6, 7, 8, 1]
    } finally {
        loading.value = false
    }
}
onMounted(() => {
    getChartData()
})
</script>

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

<style lang="scss" scoped>
.container {
    height: 134px;
    background: rgb(239, 247, 254);
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