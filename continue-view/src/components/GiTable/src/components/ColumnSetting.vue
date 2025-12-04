<script lang="ts" setup>
import { VueDraggable } from 'vue-draggable-plus'
import { defineProps, computed, ref, watch, onMounted } from 'vue'
import type { TableColumnData } from '@arco-design/web-vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
let route = useRoute()
interface ColumnItem {
    title: string
    key: string
    dataIndex?: string
    show: boolean
    disabled: boolean
    fixed?: 'left' | 'right'
    width?: number
}

const props = defineProps<{
    columns: TableColumnData[]
    disabledKeys: string[]
    tableId?: string
}>()
const visible = ref(false)
const localColumns = ref<ColumnItem[]>([])
// 初始化列表和原始列配置
// 初始化列表和原始列配置
// 保存原始列配置
const originalColumns = ref<TableColumnData[]>([])

const transformColumns = (columns = props.columns) => {
    if (!columns || columns.length === 0) {
        return []
    }
    //按照固定位置排序
    const leftColums: ColumnItem[] = []
    const centerColums: ColumnItem[] = []
    const rightColums: ColumnItem[] = []
    columns.forEach((column) => {
        const key = column.dataIndex || (typeof column.title === 'string' ? column.title : '')
        const item: ColumnItem = {
            key,
            dataIndex: column.dataIndex,
            title: (typeof column.title === 'string' ? column.title : String(key)),
            show: column.show ?? true,
            disabled: props.disabledKeys.includes(key),
            fixed: typeof column.fixed === 'boolean' ? 'left' : column.fixed as 'left' | 'right' | undefined,
            width: column.width as number,
        }
        if (item.fixed === 'left') {
            leftColums.push(item)
        } else if (item.fixed === 'right') {
            rightColums.push(item)
        } else {
            centerColums.push(item)
        }

    })
    return [...leftColums, ...centerColums, ...rightColums]
}
const handleOpen = () => {
    if (!localColumns.value.length) {
        localColumns.value = transformColumns()
        // console.log(localColumns.value)
    }


}
// 更新列变化
const emitColumnsChange = () => {
    const updatedColumns = props.columns.map((originColumn) => {
        const key = originColumn.dataIndex || (typeof originColumn.title === 'string' ? originColumn.title : '')
        const item = localColumns.value.find((column) => column.key === key)
        if (item) {
            return {
                ...originColumn,
                show: item.show,
                fixed: item.fixed,
                width: item.width,
            }
        }
    })
    // 根据拖拽后的顺序重新排序列
    const sortedColumns = sortColumnsByLocalOrder(updatedColumns)
    emit('update:columns', sortedColumns)
}
//提取列排序逻辑，主要是拖拽后排序
const sortColumnsByLocalOrder = (columns: TableColumnData[]) => {
    const keyOrderMap = new Map(
        localColumns.value.map((item, index) => [item.key, index])

    )//这里建立columns中每一列的顺序映射，方便后续排序
    return columns.sort((a, b) => {
        const aIndex = a.dataIndex || (typeof a.title === 'string' ? a.title : '')
        const bIndex = b.dataIndex || (typeof b.title === 'string' ? b.title : '')
        const orderA = keyOrderMap.get(aIndex) ?? 999
        const orderB = keyOrderMap.get(bIndex) ?? 999
        return orderA - orderB

    })
}
// 监听本地列变化，发出可见列变化事件
watch([localColumns], () => {
    if (localColumns.value.length > 0) {
        emit('visible-columns-change', visibleColumns.value)
    }
}, { immediate: true, deep: true })
const visibleColumns = computed(() => {
    //show=false的列不显示
    const showColumns = props.columns.filter((column) => {
        const key = column.dataIndex || (typeof column.title === 'string' ? column.title : '')
        const item = localColumns.value.find((column) => column.key === key)
        return item?.show !== false
    }).map((column) => {
        const key = column.dataIndex || (typeof column.title === 'string' ? column.title : '')
        const localCol = localColumns.value.find((col) => col.key === key)
        if (localCol) {
            return {
                ...column,
                fixed: localCol.fixed,
                width: localCol.width,
            }
        }
        return column
    })
    //注意虽然localColums.value是排好的，但是props.columns是原始列，需要根据localColums.value重新排序
    //localColmns.value只是提供顺序，缺少props.colmuns中其他属性，所以需要对props.columns重新排序
    const sortedColumns = sortColumnsByLocalOrder(showColumns)//拖拽后排序
    const leftFixedColumns = sortedColumns.filter((col) => col.fixed === 'left')
    const unfixedColumns = sortedColumns.filter((col) => !col.fixed)
    const rightFixedColumns = sortedColumns.filter((col) => col.fixed === 'right')

    // 返回排序后的列
    return [...leftFixedColumns, ...unfixedColumns, ...rightFixedColumns]
})
// 处理单列变更
const handleColumnChange = (item: ColumnItem) => {
    // 如果是隐藏列，取消固定
    if (!item.show) {
        item.fixed = undefined
    }
    emitColumnsChange()
}
const handleFixedColumn = (item: ColumnItem, fixed: 'left' | 'right') => {
    // 如果是隐藏列，不能固定
    if (!item.show) return

    //键插列是否已经固定到当前位置，是则不重复操作
    const isCurrentlyFixed = item.fixed === fixed

    // 如果当前列已经固定到当前位置，则取消固定
    item.fixed = isCurrentlyFixed ? undefined : fixed
    // 如果被固定但没有宽度，设置默认宽度100
    if (item.fixed && !item.width) {
        item.width = 100
    }

    // 发出列变更事件
    emitColumnsChange()
}

//缓存选中的状态
const selelctedColumns = computed(() => {
    return localColumns.value.filter((item) => !item.disabled)
})
//计算全选状态
const isAllSelected = computed(() => {
    if (selelctedColumns.value.length === 0) return false
    return selelctedColumns.value.every((item) => {
        return item.show
    })
})
//计算半选状态
const isHalfSelected = computed(() => {
    if (!isAllSelected.value && selelctedColumns.value.length > 0)
        return true
    else return false
})
/** 获取存储键 */
const getStorageKey = computed(() => {
    const pathKey = route.path.replace(/\//g, ':')
    return props.tableId
        ? `table-columns-settings-${pathKey}:${props.tableId}`
        : `table-columns-settings-${pathKey}`
})

//save to localstorage
const handleSave = () => {
    if (!getStorageKey.value) return
    try {
        const settings = {
            columns: localColumns.value,
        }
        localStorage.setItem(getStorageKey.value, JSON.stringify(settings))
        visible.value = false
        ElMessage.success('保存成功')
    } catch (e) {
        console.error('Failed to save column settings', e)
        ElMessage.error('保存失败')
    }

}
//reset to original
const handleReset = () => {
    if (!getStorageKey.value) return
    try {
        localStorage.removeItem(getStorageKey.value)
        if (originalColumns.value.length > 0) {
            const ReSetColumns = JSON.parse(JSON.stringify(originalColumns.value))
            //这里不需要排序，因为重置时，原始列的顺序已经是正确的

            // 使用父组件传入的列更新内部状态
            // emit('update:columns', ReSetColumns)

            // 更新本地列配置
            localColumns.value = transformColumns(ReSetColumns)
        } else {
            // 如果没有原始配置，则使用当前props
            localColumns.value = transformColumns()

        }
        // 发送更新事件
        emitColumnsChange()
        ElMessage.success('重置成功')

    } catch (e) {
        console.error('Failed to reset column settings', e)
        ElMessage.error('重置失败')
    }
}
//load from localstorage
const loadSettingsFromStorage = () => {
    try {
        // 如果没有存储键，则不加载
        if (!getStorageKey.value) return
        // 如果存储中没有设置，则不加载
        const settingsJson = localStorage.getItem(getStorageKey.value)
        if (!settingsJson) return false
        // 解析设置
        const settings = JSON.parse(settingsJson)
        if (!settings || !settings.columns || !Array.isArray(settings.columns)) return false

        const columnsMap = new Map(
            props.columns.map(col => [
                col.dataIndex || (typeof col.title === 'string' ? col.title : ''),
                col
            ])
        )

        // 2. 关键：过滤“存储中有但父组件列中没有”的无效列
        const validStoredColumns = settings.columns.filter((item: ColumnItem) => {
            return columnsMap.has(item.key); // 只保留父组件列中存在的列
        })
        localColumns.value = transformColumns(validStoredColumns)
        // 检查是否有新增的列，将它们添加到末尾
        const existingkeys = new Set(localColumns.value.map((item: any) => item.key))
        const newColumns = props.columns.filter((item: any) => {
            const key = item.dataIndex || (typeof item.title === 'string' ? item.title : '')
            return !existingkeys.has(key)

        }).map((item: any) => {
            const key = item.dataIndex || (typeof item.title === 'string' ? item.title : '')
            return {
                key,
                dataIndex: item.dataIndex,
                title: (typeof item.title === 'string' ? item.title : String(key)),
                show: item.show ?? true,
                disabled: props.disabledKeys.includes(key),
                fixed: typeof item.fixed === 'boolean' ? 'left' : item.fixed as 'left' | 'right' | undefined,
                width: item.width as number || 100,
            }
        })
        if (newColumns.length > 0) {
            localColumns.value = [...localColumns.value, ...newColumns]
        }
        // 发送更新事件
        emitColumnsChange()
        ElMessage.success('加载成功')
        return true
    } catch (e) {
        console.error('Failed to load column settings', e)
        ElMessage.error('加载失败')
        return false
    }
}
// 初始化列表和原始列配置
watch(() => props.columns, (newColumns) => {
    if (newColumns && newColumns.length > 0) {
        // 第一次接收到非空列数据时，保存原始配置
        if (originalColumns.value.length === 0) {
            originalColumns.value = JSON.parse(JSON.stringify(newColumns))
        }

        // 如果本地列数组为空，初始化本地列
        if (localColumns.value.length === 0) {
            localColumns.value = transformColumns()
        }
    }
}, { immediate: true })

const emit = defineEmits<{
    (e: 'update:columns', columns: TableColumnData[]): void,
    (e: 'visible-columns-change', columns: TableColumnData[]): void,
}>()
// 处理拖拽结束
const handleDragEnd = () => {
    emitColumnsChange()
}
// 初始化时加载存储设置
onMounted(() => {
    loadSettingsFromStorage()
})
</script>

<template>
    <el-popover width="240px" v-model:visible="visible" content="Bottom Center prompts info" placement="bottom"
        trigger="click">
        <template #reference> <el-button icon="Setting" @click="handleOpen"></el-button></template>
        <template #default>
            <div class="setting-container">
                <div class="gi-table_setting-select-all">
                    <el-checkbox label="全选" size="large" :indeterminate="isHalfSelected" :model-value="isAllSelected" />
                    <el-link @click="handleReset">重置</el-link>
                </div>
                <el-divider></el-divider>
                <VueDraggable v-model="localColumns" class="vue-draggable-list" @end="handleDragEnd">
                    <div v-for="item in localColumns" :key="item.key" class="box-item">
                        <div class="left">
                            <GiSvgIcon name="drag-dot-vertical"></GiSvgIcon>
                            <el-checkbox @change="() => handleColumnChange(item)" v-model="item.show"
                                :disabled="item.disabled">{{ item.title }}</el-checkbox>
                        </div>
                        <div class="gi-table-item-fixed">
                            <el-tooltip>
                                <template #content>
                                    左固定
                                </template>
                                <GiSvgIcon @click="handleFixedColumn(item, 'left')" name="pushpin"
                                    :class="{ 'active': item.fixed === 'left' }">
                                </GiSvgIcon>

                            </el-tooltip>
                            <el-tooltip>
                                <template #content>
                                    右固定
                                </template>
                                <GiSvgIcon name="pushpin" @click="handleFixedColumn(item, 'right')"
                                    style="transform:rotate(270deg);" :class="{ 'active': item.fixed === 'right' }">
                                </GiSvgIcon>
                            </el-tooltip>
                        </div>
                    </div>
                </VueDraggable>
                <el-divider></el-divider>
                <div class="bottom">
                    <el-button type="primary" style="width:100%;" @click="handleSave">保存</el-button>
                </div>
            </div>
        </template>
    </el-popover>
</template>

<style lang="scss" scoped>
.el-popover.el-popper {
    padding: 5px 10px !important;
}

.el-popover {
    // height: 350px;
    // overflow: hidden;

    .vue-draggable-list {
        height: 200px;
        overflow: auto;
    }

    .box-item {
        @include flex-between;
    }
}

.gi-table_setting-select-all {
    @include flex-between;
    height: 5%;
}

.gi-table-item-fixed {
    .active {
        color: $color-theme;
    }
}

.el-divider {
    margin: 10px 0;
}
</style>