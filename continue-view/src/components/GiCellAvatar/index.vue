<script lang="ts" setup>
import { computed } from 'vue'
import { randomSaturatedColor } from '@/utils'
const props = defineProps({
    name: {
        type: String,
    },
    avatar: {
        type: String,
    }
})
const nickname = computed(() => {
    return props.name?.substring(0, 2)
})
const blackGroundColor = computed(() => {
    return randomSaturatedColor()
})
</script>

<template>
    <el-space>
        <span :style="{ backgroundColor: blackGroundColor, borderRadius: '50%' }" class="avatar">
            {{ nickname }}
        </span>
        <span>{{ props.name }}</span>
    </el-space>
</template>

<style lang="scss" scoped>
.avatar {
    font-size: 0.7em;
    display: inline-block;
    @include flex-center;
    width: 25px;
    height: 25px;
    color: white;
    line-height: 25px;
}
</style>