<script lang="ts" setup>
import Icon from "../header/components/index.vue"
import { useAppStore } from "@/store";
import type { RouteRecordRaw } from 'vue-router'
import { ref } from 'vue'
let appStore = useAppStore()
defineOptions({ name: 'MenuItem' })
const props = withDefaults(defineProps<Props>(), {})
interface Props {
    item: RouteRecordRaw
}
// 如果hidden: false那么代表这个路由项显示在左侧菜单栏中
// 如果props.item的子项children只有一个hidden: false的子元素, 那么onlyOneChild就表示这个子元素
const onlyOneChild = ref<RouteRecordRaw | null>(null)
const isOneShowingChild = ref(false)

const handleFunction = () => {
    const children = props.item.children?.length ? props.item.children : []
    // 判断是否只有一个显示的子项
    const showingChildren = children.filter((i: any) => i.meta?.hidden === false)
    if (showingChildren.length) {
        // 保存子项最后一个hidden: false的元素
        onlyOneChild.value = showingChildren[showingChildren.length - 1]
    }

    // 当只有一个要显示子路由时, 默认显示该子路由器
    if (showingChildren.length === 1) {
        isOneShowingChild.value = true
    }

    // 如果没有要显示的子路由, 则显示父路由
    if (showingChildren.length === 0) {
        onlyOneChild.value = { ...props.item, meta: { ...props.item.meta, noShowingChildren: true } } as any
        isOneShowingChild.value = true
    }
}

handleFunction()
</script>
<!-- :class="{ 'collapse-menu': appStore.menuCollapse }" -->
<template>
    <template v-if="!item.meta?.hidden">
        <el-menu-item v-if="
            isOneShowingChild
            && (!onlyOneChild?.children || onlyOneChild?.meta?.noShowingChildren)
            && !item?.meta?.alwaysShow
        " :index="onlyOneChild?.path">
            <Icon :icon="onlyOneChild?.meta?.icon" :size="24"></Icon>
            <template #title>

                <span style="margin-left:10px; font-size:14px">{{ onlyOneChild?.meta?.title }}</span>
            </template>
        </el-menu-item>


        <el-sub-menu v-else :index="item.path" class="submenu">

            <template #title>
                <Icon :icon="item.meta?.icon" :size="24"></Icon>
                <span style="margin-left:10px; font-size:14px">{{ item.meta?.title }}</span>
            </template>

            <MenuItem v-for="child in item.children" :key="child.path" :item="child">
            </MenuItem>
        </el-sub-menu>
    </template>
</template>
<style lang="scss" scoped>
.collapse-menu {
    display: flex;
    flex-direction: column;

    align-items: center;
    justify-content: center;
}
</style>