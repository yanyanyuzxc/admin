<script lang="ts" setup>
import { toRefs } from 'vue'
import Unknown from '@/assets/images/avatar/unknown.png'
import * as Regexp from '@/utils/regexp'
interface Props {
    src?: string
    name?: string
    text?: string
    color?: string
    size?: string | number
    alt?: string
    trigger?: boolean
}
/**
 * 英文开头：取传入字符串的前面两个字符，例如：Charles => Ch
 * 超过 4 位：取字符串第一个字符，例如：系统管理员 => 系
 * 其他：取传入字符串的最后两个字符，例如：张三 => 张三；王多鱼：多鱼
 */
const avatarName = computed(() => {
    const name = props.name
    if (!name) return ''
    if (name[0].match(Regexp.OnlyEn)) {
        const nameArr = name.split(' ')
        if (nameArr.length > 1 && nameArr[1][0].match(Regexp.OnlyEn)) return `${nameArr[0][0]}${nameArr[1][0]}`
        return name.substring(0, 2)
    }
    if (name.length > 4) return name[0]
    return name.substring(name.length - 2, name.length)
})
const props = withDefaults(defineProps<Props>(), {
    src: '',
    name: '',
    size: 30,
    alt: 'avatar',
    trigger: false,
})
const { src, name, size } = toRefs(props)
const x = (size.value) as number / 5
const y = (size.value) as number * 0.8
const offset = [-x, y]
</script>

<template>
    <el-badge :offset="offset" :style="{ width: `${size}px`, height: `${size}px` }">
        <el-avatar v-if="src" :size="size" class="avatar">
            <img :src="src" :alt="alt" />
        </el-avatar>
        <el-avatar v-else-if="name || text" :style="{ width: `${size}px`, height: `${size}px` }">

            <span v-if="name">{{ avatarName }}</span>

            <span v-else>{{ text }}</span>
        </el-avatar>
        <el-avatar v-else :size="size" :style="{ width: `${size}px`, height: `${size}px` }">
            <!-- <img :src="Unknown" :alt="alt" /> -->
            <slot name="trigger-icon"></slot>
        </el-avatar>
        <template #content v-if="trigger">
            <slot name="trigger-icon"></slot>
        </template>
    </el-badge>
</template>

<style lang="scss" scoped>
:deep(.el-badge__content--danger) {
    background: skyblue;
    color: $color-theme;
    z-index: 999;
    width: 37px;
    height: 37px;
    padding: 0px;
    border-radius: 50%;
    border: none;
}

:deep(.el-badge) {
    border-radius: 50%;
    background: transparent;
}

.el-avatar {
    background: rgb(145, 206, 243) !important;
}

span {
    font-size: 1.8em;
    background: rgb(145, 206, 243) !important;
}
</style>