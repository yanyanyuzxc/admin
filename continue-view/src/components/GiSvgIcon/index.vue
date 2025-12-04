<template>
    <svg aria-hidden="true" :class="svgClass" v-bind="$attrs"
        :style="{ color, fill: color, width: iconSize, height: iconSize }">
        <use :xlink:href="`${prefix}${name}`"></use>

    </svg>
</template>

<script setup lang="ts">
import { computed } from 'vue'
defineOptions({ name: 'GiSvgIcon' })
interface Props {
    prefix?: string
    name: string
    color?: string
    size?: string | number
}
const props = withDefaults(defineProps<Props>(), {
    prefix: '#icon-',
    name: '',
    color: '',
    size: 20,
})



// 判断传入的值，是否带有单位，如果没有，就默认用px单位
const getUnitValue = (value: string | number): string | number => {
    return /(px|em|rem|%)$/.test(value.toString()) ? value : `${value}px`
}

const iconSize = computed<string | number>(() => {
    return getUnitValue(props.size)
})

// const iconName = computed(() => `#icon-${props.name}`)

const svgClass = computed(() => {
    if (props.name) return `svg-icon icon-${props.name}`
    return 'svg-icon'
})
</script>

<style scoped lang="scss">
.svg-icon {
    width: auto;
    height: auto;
    // fill: currentColor;
    vertical-align: middle;
    flex-shrink: 0;
}
</style>