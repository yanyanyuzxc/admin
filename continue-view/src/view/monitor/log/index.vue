<script lang="ts" setup>
import dayjs from 'dayjs'
import type { TableInstance } from '@arco-design/web-vue'
import { type LogQuery, exportLoginLog, listLog } from '@/apis/monitor/index.ts'
import DateRangePicker from '@/components/DateRangePicker/index.vue'
import { useDownload, useTable } from '@/hooks'

defineOptions({ name: 'LoginLog' })

const queryForm = reactive<LogQuery>({
    module: '登录',
    createTime: [
        dayjs().subtract(6, 'day').startOf('day').format('YYYY-MM-DD HH:mm:ss'),
        dayjs().endOf('day').format('YYYY-MM-DD HH:mm:ss'),
    ],
    sort: ['createTime,desc'],
})

const {
    tableData: dataList,
    loading,
    pagination,
    search,
} = useTable((page) => listLog({ ...queryForm, ...page }), { immediate: true })
</script>
<template>
    <div>
        <DateRangePicker v-model="queryForm.createTime" @change="search" />
    </div>
</template>

<style lang="scss" scoped></style>