<script lang="ts" setup generic="T extends TableData">
import { computed, ref, watch, useSlots } from 'vue'
import type { DropdownInstance, TableColumnData, TableData, TableInstance } from '@arco-design/web-vue'
import { omit } from 'lodash-es'
import { ElTable } from 'element-plus'
import type { TableProps } from '../type.ts'
import ColumnSetting from './components/ColumnSetting.vue'
import { useAttrs } from 'vue'
import { useTemplateRef } from 'vue'
import Sortable from 'sortablejs'  // 导入拖拽库
import { Sort } from '@element-plus/icons-vue'  // 导入拖拽图标
import { useWindowSize } from '@vueuse/core'
// import { useTable } from '@/hooks'
defineOptions({ name: 'GiTable' })
const { width } = useWindowSize()
// 页面挂载后初始化拖拽
// 初始化行拖拽
// 表格组件实例的类型 = InstanceType<typeof 组件>
type ElTableInstance = InstanceType<typeof ElTable>
const tableRef = useTemplateRef<ElTableInstance>('tableRef')
const initRowDrag = () => {
    // 获取表格的tbody元素（表格行的父容器）
    const tbody = tableRef.value?.$el?.querySelector('.el-table__body-wrapper tbody')

    // 创建Sortable实例，配置拖拽规则
    new Sortable(tbody, {
        handle: '.drag-handle',  // 仅允许通过.drag-handle类的元素触发拖拽（可选）
        animation: 150,  // 拖拽动画时长（毫秒）
        ghostClass: 'sortable-ghost',  // 拖拽时占位元素的类名（可自定义样式）
        onEnd: (evt: any) => {
            // 拖拽结束时触发，evt包含拖拽前后的索引
            let { oldIndex, newIndex } = evt
            // 关键修正：删除元素后，newIndex 需调整（如果新位置在原位置之后）
            if (newIndex > oldIndex) {
                newIndex -= 1; // 因为删除了原位置的元素，后续索引自动减1
            }
            // 1. 调整表格数据顺序（关键：让表格重新渲染正确的顺序）
            // 从原位置删除元素

            const newDataList = [...props.data]
            const [movedItem] = newDataList.splice(oldIndex, 1)
            // 插入到新位置
            newDataList.splice(newIndex, 0, movedItem)
            emit('change', newDataList,)
        }
    })
}
onMounted(() => {
    initRowDrag()
})
type dragType = {
    type: string,
    width: number
}
/** Props 类型定义 */
interface Props extends TableProps {
    /** 表格标题 */
    title?: string,
    draggable?: dragType,
    /** 禁止控制显示隐藏的列 */
    disabledColumnKeys?: string[]
    /** 禁止显示的工具 */
    disabledTools?: string[]
    /** 表格数据 */
    data: T[],
    selecedKeys?: string[]
    /** 表格标识，用于存储列设置 */
    tableId?: string,
    loading?: boolean,
    showOverflowTooltip?: boolean,
}
// Props 默认值
const props = withDefaults(defineProps<Props>(), {
    title: '',
    draggable: () => ({ type: '', width: 0 }),
    disabledColumnKeys: () => [],
    disabledTools: () => [],
    data: () => [],
    loading: false,
})

/** Slots 类型定义 */
const Slots = defineSlots<{
    'th': (props: { column: TableColumnData }) => void
    'thead': () => void
    'empty': (props: { column: TableColumnData }) => void
    'summary-cell': (props: { column: TableColumnData, record: T, rowIndex: number }) => void
    'pagination-right': () => void
    'pagination-left': () => void
    'td': (props: { column: TableColumnData, record: T, rowIndex: number }) => void
    'tr': (props: { record: T, rowIndex: number }) => void
    'tbody': () => void
    'drag-handle-icon': () => void
    'footer': () => void
    'expand-row': (props: { record: T }) => void
    'expand-icon': (props: { record: T, expanded?: boolean }) => void
    'columns': () => void
    'custom-title': () => void
    'top': () => void
    'toolbar-left': () => void
    'toolbar-right': () => void
    'toolbar-bottom': () => void
    [propsName: string]: (props: any) => void
}>()
const showRefreshBtn = computed(() => !props.disabledTools?.includes('refresh'))
const showSizeBtn = computed(() => !props.disabledTools?.includes('size'))
const showFullscreenBtn = computed(() => !props.disabledTools?.includes('fullscreen'))
//内部维护数据
const innerColumns = ref<TableColumnData[]>([])
/** 监听 props.columns 变化 */
watch(() => props.columns, (newColumns) => {
    if (newColumns && innerColumns.value.length === 0) {
        innerColumns.value = [...newColumns]
    }
}, { immediate: true })

const slots = useSlots()

const emit = defineEmits<{
    (e: 'change', ...args: any[]): void
    (e: 'refresh'): void
    (e: 'update:selected-keys', ...args: any[]): void
}>()

const handleRefresh = () => {
    console.log('refresh')
    emit('refresh');
}
const attrs = useAttrs()

const tableProps = computed(() => ({
    ...omit(props, ['title', 'disabledColumnKeys', 'disabledTools']),
    ...attrs,
}))
const tableColumns = ref<TableColumnData[]>([])
const isFullscreen = ref(false)
const visibleColums = computed(() => {
    if (tableColumns.value && tableColumns.value.length > 0) {
        return tableColumns.value
    }
    return props.columns?.filter((col: any) => col.show !== false) || []
})
/** 处理列设置组件的可见列变化 */
const handleVisibleColumnsChange = (columns: TableColumnData[]) => {
    tableColumns.value = columns
}


const handleSelectable = (row: T) => {
    // 可根据业务调整判断逻辑，示例：禁用“行自身 disabled 为 true”的行
    return !row.disabled;
};
// watch(
//     () => props.rowSelection?.selectedRowKeys,
//     (newSelectedKeys) => {
//         // 若父组件更新了已选行，同步到子组件的表格实例
//         if (tableRef.value && newSelectedKeys) {
//             tableRef.value.setSelectedRows(newSelectedKeys.map((id: any) =>
//                 props.data.find(row => row.id === id) // 根据 ID 找到对应的行数据
//             ));
//         }
//     },
//     { immediate: true, deep: true }
// );
defineExpose({
    tableRef
})
// 监听 tableData 的变化
const handleSelect = (selection: any) => {

    emit('update:selected-keys', selection.map((row: any) => row.id))
}
const select = (selection: any, row: any) => {
    console.log(row)
}

</script>

<template>

    <div class="gi-table" :class="{ 'gi-table--fullscreen': isFullscreen }">
        <div>
            <slot name="custom-title">
                <div class="gi-table-title">
                    {{ props.title }}
                </div>
            </slot>
        </div>
        <div>
            <slot name="top"></slot>
        </div>
        <el-row :gutter="20" class="content" justify="space-between">
            <el-col :span="width < 1200 ? 24 : 14">
                <slot name="toolbar-left"></slot>
            </el-col>
            <el-col :span="width < 1200 ? 24 : 9" style="flex-wrap: wrap;justify-content: flex-end; gap: 8px; ">
                <div style="display: flex; gap: 8px;justify-content: flex-end;">
                    <slot name="toolbar-right"></slot>

                    <el-tooltip content="刷新" v-if="showRefreshBtn">
                        <el-button icon="Refresh" @click="handleRefresh">
                        </el-button>
                    </el-tooltip>
                    <ColumnSetting v-if="showSizeBtn" v-model:columns="innerColumns" :disabled-keys="disabledColumnKeys"
                        @visible-columns-change="handleVisibleColumnsChange">
                    </ColumnSetting>
                    <el-tooltip content="全屏" v-if="showFullscreenBtn">
                        <el-button @click="isFullscreen = !isFullscreen">
                            <GiSvgIcon :name="isFullscreen ? 'fullscreen-exit' : 'fullscreen'">
                            </GiSvgIcon>
                        </el-button>
                    </el-tooltip>
                </div>
            </el-col>
        </el-row>
        <el-row class="gi-table__toolbar-bottom">
            <slot name="toolbar-bottom"></slot>
        </el-row>
        <div class="body">

            <el-table v-bind="tableProps" :columns="visibleColums" max-height="400px" :data="props.data"
                v-loading="loading" ref="tableRef" @selection-change="handleSelect" @select="select"
                :header-row-style="{ 'background-color': 'red' }">
                <el-table-column label="拖拽" width="60" align="center" v-if="props.draggable.type">
                    <template #default>
                        <!-- 拖拽手柄图标示（↕图标） -->
                        <el-icon class="drag-handle" style="cursor: move">
                            <Sort />
                        </el-icon>
                    </template>
                </el-table-column>
                <el-table-column type="selection" v-if="props.rowSelection !== false"></el-table-column>

                <el-table-column v-for="col in visibleColums" :key="col" :label="col.title" :width="col.width" style=""
                    :fixed="col.fixed" :prop="col.dataIndex" :type="col.dataIndex === 'index' ? 'index' : undefined"
                    :index="col.index || undefined" :show-overflow-tooltip="col.showOverflowTooltip">
                    <template v-if="col.slotName && slots[col.slotName]" #default="scope">
                        <slot :key="col.slotName" :name="col.slotName" v-bind="scope" class="gi-table-slot">
                        </slot>
                    </template>

                    <template v-else-if="col.render" #default="scope">
                        <component :is="col.render(scope.row)"></component>
                    </template>
                </el-table-column>

            </el-table>

        </div>
        <div class="footer">
            <el-pagination v-if="pagination !== false" v-model:current-page="pagination.currentPage"
                v-model:page-size="pagination.pageSize" @size-change="pagination.onPageSizeChange"
                @current-change="pagination.onChange" layout="total, sizes, prev, pager, next"
                :total="pagination.total" />
        </div>
    </div>

</template>

<style lang="scss" scoped>
.content {
    flex-wrap: wrap;
    overflow: hidden;
}

:deep(.el-table__header-wrapper .el-table__header) {
    background: #476593 !important;
    /* 表头背景色（替换为你想要的颜色） */

}

.el-col {
    margin: 5px;
}

.el-row {
    flex-wrap: wrap;

}

.gi-table {
    display: flex;
    flex-direction: column;
    width: 100%;
    background: var(--color-bg-1);
    position: relative;

    &--fullscreen {
        // padding: $padding;
        position: fixed;
        left: 0;
        right: 0;
        top: 0;
        bottom: 0;
        z-index: 1001;
    }
}

.body {
    margin: 10px auto;
    width: 97%;
    border: 1px solid #e3e3e3;
    overflow: auto;
    flex: 1;
    min-height: 400px;
}

.el-table {
    height: 100%;
}


.el-row {
    padding: 5px 20px;
}


.footer {
    display: flex;
    // position: absolute;
    // bottom: 2%;
    // right: 2%;
    height: 40px;
    width: 100%;
    justify-content: flex-end;
}
</style>