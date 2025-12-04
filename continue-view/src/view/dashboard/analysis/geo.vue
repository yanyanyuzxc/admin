<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import type { EChartsOption } from 'echarts'
import { getAnalysisGeo as getData } from '@/apis/common/dashboard'
import { useChart } from '@/hooks'
import { map } from 'lodash'

// const chartRef = useTemplateRef('chartRef')
const chartData = ref<chartDataItem[]>([])
const totalValue = ref(0)
interface topDataItem {
    name: string
    value: number
}
interface chartDataItem {
    name: string
    value: number
}
const topData = ref<topDataItem[]>([])
const radio = ref('中国')
const loading = ref(false)
//获取数据
const getChartData = async () => {
    try {
        loading.value = true
        const { data } = await getData()
        console.log(data)
        chartData.value = data.filter((item) => item.value !== 0)
        totalValue.value = data.reduce((acc, cur) => acc + cur.value, 0)
        topData.value = data.sort((a, b) => b.value - a.value).slice(0, 7)
    }
    catch (error) {
        console.log(error)
    }
    finally {
        loading.value = false
    }
}
onMounted(() => {
    getChartData()
})
const { chartOption } = useChart((isDark: boolean) => {
    return {
        visualMap: {
            left: 'left',
            min: 0,
            max: 20000,
            inRange: {
                color: ['#EAF4FF', '#3C7EFF'],
            },
            orient: 'horizontal', // 水平排列
            calculable: false,    // 不显示可拖动的滑块（关闭交互调整）
        },
        tooltip: {
            trigger: 'item',
            formatter(data: any) {
                return `${data.name}<br/>访问次数：${data.value || 0}`
            }
        },
        series: [{
            type: 'map',
            map: 'china',
            zoom: 1.1,
            label: {
                show: false,
            },
            itemStyle: {
                normal: {
                    // 根据主题动态设置地图区域的基础颜色
                    areaColor: isDark ? '#313132' : '#F6F6F6',
                },
            },
            data: chartData.value,
        }]
    } as EChartsOption;
})
</script>

<template>
    <div class="layout">
        <div class="title">
            <span>地理位置</span>
            <div class="radio">
                <el-radio-group type="button" size="large" v-model="radio" fill="#fff"
                    text-color="rgb(var(--primary-6))">
                    <el-radio-button label="中国" value="中国" />
                    <el-radio-button label="世界" value="世界" />
                </el-radio-group>
            </div>
        </div>

        <div class="content">
            <div class="mapChart">
                <Chart ref="chartRef" :option="chartOption" style="height: 468px" />
            </div>
            <div class="dataShow">
                <div v-for="item in topData" :key="item.name" class="dataItem">
                    <div class="dataName">
                        <span>{{ item.name }}</span>
                        <span>{{ item.value }}</span>
                    </div>
                    <el-progress :percentage="item.value / totalValue || 0" />
                </div>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
.layout {
    background: var(--color-bg-1);
    margin-top: 20px;
    padding: 10px;
}

.title {
    display: flex;
    background: var(--color-bg-1);
    justify-content: space-between;
    align-items: center;
    padding: 10px;

    span {
        font-size: 1.1em;
    }

    .el-radio-group * {
        background: rgb(242, 243, 245);
        padding: 5px;
        border: none;
    }

    .el-radio-group * :hover {
        color: var(--primary-6)
    }
}

.content {
    display: flex;
    justify-content: center;

    .mapChart {
        width: 73%;
    }

    .dataShow {
        flex: 1;
        // margin-left: 20px;
        background: rgb(246, 248, 250);
        height: 80%;
        padding: 20px;

        .dataItem {
            margin-bottom: 20px;
        }

        .dataName {
            @include flex-between;

            span {
                font-size: 1.1em;

            }
        }

    }
}
</style>