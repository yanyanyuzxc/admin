<script lang="ts" setup>
import { type DashboardChartCommonResp, getDashboardOverviewPv as getData } from '@/apis'
import { ref, onMounted, computed } from 'vue'
import { useAppStore } from '@/store'
import { useChart } from '@/hooks'
const appStore = useAppStore()
const isDark = computed(() => appStore.theme === 'dark')
const count = ref(0)
const today = ref(0)
const growth = ref(0)
const loading = ref(false)
// 查询图表数据
const xAxis = ref<string[]>([])
const chartData = ref<number[]>([])
const getChartData = async () => {
    try {
        loading.value = true
        const { data } = await getData()
        // console.log(data)
        count.value = data.total
        today.value = data.today
        growth.value = data.growth
        data.dataList.forEach((item: DashboardChartCommonResp) => {
            xAxis.value.push(item.name)
            chartData.value.push(item.value)
        })
    } finally {
        loading.value = false
    }
}
const { chartOption } = useChart(() => {
    return {
        grid: {
            left: 0,
            right: 30,
            top: 10,
            bottom: 0,
        },
        xAxis: {
            type: 'category',
            data: xAxis.value,
            // show: false,
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
                name: '访问次数',
                data: chartData.value,
                type: 'line',
                showSymbol: false,
                lineStyle: {
                    color: '#246EFF',
                    width: 2,
                },
            },
        ],
    }
})
onMounted(() => {
    getChartData()
})
</script>

<template>
    <div class="container">
        <div class="left">
            <p class="count">访问次数</p>
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
            <Chart :option="chartOption" v-if="!loading" />

        </div>
    </div>
</template>

<style lang="scss" scoped>
.container {
    height: 134px;
    background: rgb(236, 246, 254);
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