<script lang="ts" setup>
import { computed, ref, watch, reactive } from 'vue'
import type { FormInstance, GridProps } from '@arco-design/web-vue'
import { omit } from 'lodash-es'
import type { ColumnItem } from './type'
import type * as A from '@arco-design/web-vue'
import type { RangePickerInstance } from '@arco-design/web-vue';
// 导入 Arco 组件（如果是全局注册可省略）
// 定义表单项的基础类型（后续再扩展）


interface Props {
    modelValue: any
    layout?: FormInstance['layout']
    size?: FormInstance['size']
    labelColProps?: FormInstance['labelColProps']
    wrapperColProps?: FormInstance['wrapperColProps']
    labelAlign?: FormInstance['labelAlign']
    disabled?: FormInstance['disabled']
    rules?: FormInstance['rules']
    autoLabelWidth?: FormInstance['autoLabelWidth']
    id?: FormInstance['id']
    scrollToFirstError?: FormInstance['scrollToFirstError']
    // 额外自定义属性
    columns: ColumnItem[]
    gridProps?: number
    span?: number
    search?: boolean // 搜索模式
    defaultCollapsed?: boolean // 折叠按钮默认折叠
    searchBtnText?: string // 搜索按钮文字
    hideFoldBtn?: boolean // 隐藏展开收起按钮，在表单项少的时候手动隐藏
    suffix?: boolean
}

// Props 默认值
const props = withDefaults(defineProps<Props>(), {
    search: false,
    span: 8,
    searchBtnText: '搜索',
    hideFoldBtn: false,
    suffix: true,
})

/** 静态配置 */
const STATIC_PROPS = new Map<ColumnItem['type'], Partial<ColumnItem['props']>>([
    ['input', { allowClear: true, maxLength: 255, showWordLimit: !props.search }],
    ['input-password', { allowClear: true, showWordLimit: !props.search }],
    ['textarea', { allowClear: false, maxLength: 200, showWordLimit: !props.search, autoSize: { minRows: 3, maxRows: 5 } }],
    ['input-tag', { allowClear: true }],
    ['mention', { allowClear: true }],
    ['select', { allowClear: true }],
    ['tree-select', { allowClear: true }],
    ['cascader', { allowClear: true }],
    ['date-picker', { allowClear: true }],
    ['time-picker', { allowClear: true }],
])
/** 数据字典 */
const dicData: Record<string, any> = reactive({})
/** 获取组件默认占位 */
const getPlaceholder = (item: ColumnItem) => {
    if (!item.type) return undefined
    if (['input', 'input-number', 'input-password', 'textarea', 'input-tag', 'mention'].includes(item.type)) {
        return `请输入${item.label}`
    }
    if (['select', 'tree-select', 'cascader'].includes(item.type)) {
        return `请选择${item.label}`
    }
    if (['date-picker'].includes(item.type)) {
        return '请选择日期'
    }
    if (['time-picker'].includes(item.type)) {
        return '请选择时间'
    }
    return undefined
}
/** 获取选项数据 */
const getOptions = (item: ColumnItem): any[] | undefined => {
    if (!item.type) return undefined
    /** 需要选项数据的组件类型 */
    const arr = ['select', 'tree-select', 'cascader', 'radio-group', 'checkbox-group']
    if (arr.includes(item.type)) {
        return dicData[item.field] || []
    }
    return undefined
}
/** 获取组件绑定属性 */
const getComponentBindProps = (item: ColumnItem) => {

    return {
        ...STATIC_PROPS.get(item.type) || {},
        placeholder: getPlaceholder(item),
        options: getOptions(item),
        ...item.props,
        type: item.components?.type || item.type,
    }
}
const emit = defineEmits<{
    (e: 'update:modelValue', value: any): void
    (e: 'search'): void
    (e: 'reset'): void
}>()
/** 表单数据更新  */
const updateValue = (value: any, field: string) => {
    emit('update:modelValue', Object.assign(props.modelValue, { [field]: value }))
}

/** 表单项校验规则 */
const getFormItemRules = (item: ColumnItem) => {
    if (isRequired(item)) {
        const defaultProps = getComponentBindProps(item)
        return [{ required: true, message: defaultProps.placeholder || `请输入${item.label}` }, ...(Array.isArray(item.rules) ? item.rules : [])]
    }
    return item.rules
}
/** 必填项 */
const isRequired = (item: ColumnItem) => {
    if (typeof item.required === 'boolean') return item.required
    if (typeof item.required === 'function') {
        return item.required(props.modelValue)
    }
}
const defaultSpan = {
    xs: 24, sm: 8, xxl: 8
}

const formRef = ref<FormInstance>()
defineExpose({ formRef })
// 脚本中定义工具函数：提取响应式属性

const getResponsiveSpan = (key: 'xs' | 'sm' | 'xxl'): number | undefined => {
    return defaultSpan[key] || undefined;
};

/** 显示表单项 */
const isShow = (item: ColumnItem) => {
    if (typeof item.show === 'boolean') return item.show
    if (typeof item.show === 'function') {
        return item.show(props.modelValue)
    }
}

/** 隐藏表单项 */
const isHide = (item: ColumnItem) => {
    if (item.hide === undefined) return false
    if (typeof item.hide === 'boolean') return item.hide
    if (typeof item.hide === 'function') {

        return item.hide(props.modelValue)
    }
}

/** 禁用表单项 */
const isDisabled = (item: ColumnItem) => {
    if (item.disabled === undefined) return false
    if (typeof item.disabled === 'boolean') return item.disabled
    if (typeof item.disabled === 'function') {

        return item.disabled(props.modelValue)
    }
}
//过滤出显示的列
const filteredColumns = computed(() => {
    return props.columns.filter(item => {
        // 复用原来的显示逻辑：item.show 存在则用 isShow，否则用 !isHide
        if (item.show !== undefined) {
            return isShow(item); // 保留需要显示的
        } else {
            return !isHide(item); // 保留需要显示的
        }
    });
});
</script>


<template>
    <el-form ref="formRef" :model="modelValue" :inline="props.search ? true : false" :size="size" label-width="auto"
        label-position="right">
        <el-row>
            <el-col v-for="item in filteredColumns" :key="item.field" v-bind="item.span ? { span: item.span } : {
                xs: getResponsiveSpan('xs'),
                sm: getResponsiveSpan('sm'),
                xxl: getResponsiveSpan('xxl')
            }">
                <el-form-item :label="item.label" :rules="getFormItemRules(item)" class="top-item" :prop="item.field">
                    <slot v-if="!['group-title'].includes(item.type || '')" :name="item.field"
                        v-bind="{ disabled: isDisabled(item) }">
                        <div class="form-item">
                            <template v-if="item.type === 'date-picker'">
                                <DateRangePicker v-bind="item.props" :model-value="modelValue[item.field]"
                                    @update:model-value="updateValue($event, item.field)"
                                    :style="item?.style || { width: '100%' }">
                                </DateRangePicker>
                            </template>

                            <component v-else :is="`el-${item.type}`" :style="item?.style || { width: '100%' }"
                                v-bind="getComponentBindProps(item)" :model-value="modelValue[item.field]"
                                @update:model-value="updateValue($event, item.field)" default-expand-all>
                            </component>
                        </div>
                    </slot>
                </el-form-item>
            </el-col>
        </el-row>
    </el-form>
    <div class="bottom" v-if="props.search">
        <slot name="suffix">
            <el-space>
                <div class="suffix">
                    <el-button icon="Search" type="primary" @click="emit('search')">
                        查询
                    </el-button>
                    <el-button icon="refresh" @click="emit('reset')">
                        重置
                    </el-button>
                    <el-button :icon="1 === 1 ? 'ArrowUp' : 'ArrowDown'">
                        展开/收起
                    </el-button>
                </div>
            </el-space>
        </slot>
    </div>

</template>

<style lang="scss" scoped>
.el-form {

    width: 100%;

}

.el-form-item {
    margin: 10px;
}

.form-item {
    padding: 0px;
    margin: 0px;
    width: 100%;
    @include flex-center;

    .el-input {
        --el-input-inner-height: 30px;
    }

}

.bottom {
    width: 90%;
    display: flex;
    justify-content: flex-end;
    align-items: center;
    margin-bottom: 10px;
}



::v-deep .el-select--large .el-select__wrapper {
    min-height: 30px;
    padding: 5px auto;

    height: 34px;
}

::v-deep .el-input {
    width: 300px;
    height: 34px;
}

::v-deep .el-date-range-picker {

    height: 34px;
}
</style>
