<template>

    <span v-if="!dictItem"></span>
    <span v-else-if="!dictItem.extra">{{ dictItem.label }}</span>
    <el-tag v-else-if="dictItem.extra === 'primary'" color="arcoblue">{{ dictItem.label }}</el-tag>
    <el-tag v-else-if="dictItem.extra === 'success'" color="green">{{ dictItem.label }}</el-tag>
    <el-tag v-else-if="dictItem.extra === 'warning'" color="orangered">{{ dictItem.label }}</el-tag>
    <el-tag v-else-if="dictItem.extra === 'error'" color="red">{{ dictItem.label }}</el-tag>
    <el-tag v-else-if="dictItem.extra === 'default'" color="gray">{{ dictItem.label }}</el-tag>
</template>

<script setup lang="ts">
import type { LabelValueState } from '@/type/global'

export interface GiCellTagType {
    dict: LabelValueState[] | any[]
    value: number | string
}


defineOptions({ name: 'GiCellTag' })
const props = withDefaults(defineProps<Partial<GiCellTagType>>(), {
    dict: [{
        label: '',
        value: '',
    }],
    value: '',
})

const dictItem = computed((): LabelValueState => {
    try {
        return props.dict.find(
            (d) => d.value === String(props.value) || d.value === Number(props.value),
        ) || { label: '', value: '' }
    } catch (error) {
        return { label: '', value: '' }
    }
})
</script>

<style scoped lang="scss"></style>