<script lang="ts" setup>
import { ref, computed } from 'vue'
import type { PropType } from 'vue';
import type { ShortcutType } from '@arco-design/web-vue'
import dayjs from 'dayjs'
const size = ref<'default' | 'large' | 'small'>('default')

const props = defineProps({
    format: {
        type: String,
        default: 'YYYY-MM-DD HH:mm:ss',
    },
    showTime: {
        type: Boolean,
        default: true,
    },
    placeholder: {
        type: [Array, String] as PropType<string[] | string>,
        default: (): string[] => ['开始时间', '结束时间'],
    },
    allowClear: {
        type: Boolean,
        default: true,
    },
})
const shortcuts = computed(() => {
    return [
        {
            text: '今天',
            value: (): Date[] => [dayjs().startOf('day').toDate(), dayjs().toDate()],
        },
        {
            text: '昨天',
            value: (): Date[] => [
                dayjs().subtract(1, 'day').startOf('day').toDate(),
                dayjs().subtract(1, 'day').endOf('day').toDate(),
            ],
        },
        {
            text: '本周',
            value: (): Date[] => [dayjs().startOf('week').add(1, 'day').toDate(), dayjs().toDate()],
        },
        {
            text: '本月',
            value: (): Date[] => [dayjs().startOf('month').toDate(), dayjs().toDate()],
        },
        {
            text: '本年',
            value: (): Date[] => [dayjs().startOf('year').toDate(), dayjs().toDate()],
        },
    ]
})
</script>

<template>
    <el-date-picker type="datetimerange" :value-format="format" unlink-panels range-separator="到"
        :start-placeholder="placeholder[0]" :end-placeholder="placeholder[1]" :shortcuts="shortcuts"
        :format="props.format" :size="size" />
</template>

<style lang="scss" scoped>
.demo-date-picker {
    display: flex;
    width: 100%;
    padding: 0;
    flex-wrap: wrap;
}

.demo-date-picker .block {
    padding: 1.5rem 0;
    text-align: center;
    border-right: solid 1px var(--el-border-color);
    flex: 1;
    min-width: 300px;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.demo-date-picker .block:last-child {
    border-right: none;
}

.demo-date-picker .demonstration {
    display: block;
    color: var(--el-text-color-secondary);
    font-size: 14px;
    margin-bottom: 1rem;
}

@media screen and (max-width: 1200px) {
    .demo-date-picker .block {
        flex: 0 0 100%;
        padding: 1rem 0;
        min-width: auto;
        border-right: none;
        border-bottom: solid 1px var(--el-border-color);
    }

    .demo-date-picker .block:last-child {
        border-bottom: none;
    }
}
</style>