<script lang="ts" setup>
import VCharts from 'vue-echarts'
import { useChart } from '@/hooks'
import { use } from 'echarts/core'
import { PieChart } from 'echarts/charts'
import { LegendComponent, TitleComponent, TooltipComponent, GridComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { type FileStatisticsResp, getFileStatistics } from '@/apis/system'
import { formatFileSize } from '@/utils'
import { FileTypeList } from '@/constant/file'
import mittBus from '@/utils/mitt'
import { h, ref } from 'vue'
import { ElDivider } from 'element-plus'
const chartData = ref<Array<{ name: string, value: number, size: string }>>([])
const size = ref(20)
use([TitleComponent, TooltipComponent, LegendComponent, PieChart, CanvasRenderer, GridComponent])
const totalData: any = {}
const statisticValueStyle = { 'color': '#5856D6', 'font-size': '18px' }
const loading = ref(false)
const spacer = h(ElDivider, { direction: 'vertical' })
const { chartOption } = useChart(() => {
  return {
    grid: {
      left: 0,
      right: 0,
      top: 100,
      bottom: 0,
    },
    legend: {
      show: true,
      bottom: -5,
      icon: 'circle',
      itemWidth: 6,
      itemHeight: 6,
      textStyle: {
        color: '#4E5969',
      },
    },
    tooltip: {
      show: true,
      // formatter(params) {
      //   return `总计：${params.value}<br>${params.data.size}`
      // },
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: true,
        label: {
          show: false,
          position: 'center',
        },
        data: chartData.value,
      },
    ],
  }
})

const getStatisticsData = async () => {
  try {
    loading.value = true
    chartData.value = []
    const { data: resData } = await getFileStatistics()

    const formatSize = formatFileSize(resData.size).split(' ')
    totalData.value = {
      type: '',
      size: Number.parseFloat(formatSize[0]),
      number: resData.number ?? 0,
      unit: formatSize[1],
      data: [],
    }
    resData.data?.forEach((fs: FileStatisticsResp) => {
      const matchedItem = FileTypeList.find((item) => item.value === fs.type)
      chartData.value.unshift({
        name: matchedItem ? matchedItem.name : '',
        value: fs.number,
        size: formatFileSize(fs.size),
      })
    })
  } finally {

    loading.value = false
  }
}
onMounted(() => {
  getStatisticsData()
  mittBus.on('file-total-refresh', () => {
    getStatisticsData()
  })
})
</script>

<template>
  <div class="percent">
    <el-space class="statistic-space" :size="size" :spacer="spacer">

      <el-statistic class="statistic-item" title="存储量" :value="totalData.size" :value-style="statisticValueStyle">
      </el-statistic>
      <el-statistic class="statistic-item" title="数量" :value="totalData.number" :value-style="statisticValueStyle" />
    </el-space>
    <div v-if="chartData.length > 0">
      <el-divider />
      <VCharts :option="chartOption" autoresize :style="{ height: '200px', width: '200px' }" />
    </div>
  </div>
</template>

<style lang="scss" scoped>
.percent {
  margin-top: 10px;
  padding: 20px;

  box-sizing: border-box;
  background-color: var(--color-bg-1);
}

:deep(.el-divider) {
  margin-bottom: 0px;
}

.statistic-space {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>