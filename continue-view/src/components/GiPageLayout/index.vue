<template>
    <el-row class="page-layout">
        <!-- 左侧列：添加基础样式，确保内容可见 -->
        <el-col v-show="!isCollapsed" :sm="10" :md="7" :lg="4" :xl="5" :xxl="5" class="left-col" v-if="slots.left">
            <div class="gi-page-layout__left">
                <slot name="left"></slot>
            </div>
        </el-col>
        <!-- 分割线：添加宽度和背景，避免看不见 -->
        <div class="divider" :class="{ 'is-collapsed': isCollapsed || !isDesktop }" v-if="slots.left">
            <div class="icon" @click="toggleCollapsed" :style="isCollapsed ? 'left:0px' : 'left:-12px'">
                <ArrowRight v-if="isCollapsed" style="width: 1em; height: 1em;" />
                <ArrowLeft v-else style="width: 1em; height: 1em;" />
            </div>
        </div>
        <!-- 右侧列：添加基础样式 -->
        <el-col class="right-col" :sm="isCollapsed ? 24 : slots.left ? 14 : 24"
            :md="isCollapsed ? 24 : slots.left ? 17 : 24" :lg="isCollapsed ? 24 : slots.left ? 20 : 24"
            :xl="isCollapsed ? 24 : slots.left ? 19 : 24" :xxl="isCollapsed ? 24 : slots.left ? 19 : 24">
            <div class="header" v-if="slots.header">
                <slot name="header"></slot>
            </div>
            <div class="body">
                <slot></slot>
            </div>
        </el-col>
    </el-row>
</template>
<script setup lang='ts'>
import { ref } from 'vue'
import type { ColProps } from '@arco-design/web-vue'
import type { CSSProperties } from 'vue'
import { useDevice } from '@/hooks'
import { useSlots } from 'vue'
import { useWindowSize } from '@vueuse/core'
import { sl } from 'element-plus/es/locale/index.mjs'
const isCollapsed = ref(false)
const { width } = useWindowSize()
const toggleCollapsed = () => {
    isCollapsed.value = !isCollapsed.value
}
const { isDesktop } = useDevice()
const slots = useSlots()
defineSlots<{
    left: () => void;    // 声明 left 插槽（无参数）
    header: () => void;  // 声明 header 插槽（无参数）
    default: () => void; // 声明默认插槽（无参数）
}>();
defineOptions({ name: 'GiPageLayout' })
</script>

<style scoped lang="less">
.page-layout {
    height: 100%;
    display: flex;
    overflow: hidden;
    box-sizing: border-box;
    flex-wrap: nowrap;
    background-color: var(--color-bg-1);
}

.divider {
    min-width: 1px; // 分割线宽度
    background: #eee; // 分割线颜色
    height: 100%; // 分割线高度，与内容区对齐
    position: relative;

    &.is-collapsed {
        width: 0; // 收起时分割线宽度为0，隐藏右侧内容区
    }

    .icon {
        position: absolute;
        top: 50%;
        transform: translateY(-50%);
        display: flex;
        justify-content: center;
        align-items: center;
        z-index: 999;
        border: 1px solid var(--color-border-2);
        box-sizing: border-box;
        background-color: var(--color-bg-1);
        cursor: pointer;
        width: 24px;
        height: 24px;
        font-size: 16px;
        border-radius: 50%;
        left: -12px;
        overflow: hidden;
        box-shadow: 0 2px 3px rgba(0, 0, 0, 0.1);

    }
}

.right-col {
    flex: 1;
    height: 100%;


}
</style>