<script lang="ts" setup>
import Logo from "../logo/index.vue";
import { useRouteStore, useAppStore, } from "@/store";
import MenuItem from "./MenuItem.vue";
import { useRouter, useRoute, type RouteRecordRaw } from "vue-router";
import { computed, defineProps, h } from "vue";
import type { CSSProperties } from 'vue'
import { useDevice } from '@/hooks'
let routeStore = useRouteStore();
const { isDesktop } = useDevice()
const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
defineOptions({
    name: 'Menu'
})
const props = withDefaults(defineProps<Props>(), {})
interface Props {
    menus?: RouteRecordRaw[]
    menuStyle?: CSSProperties
}
// 菜单垂直模式/水平模式
const mode = computed(() => {
    if (!['left', 'mix'].includes(appStore.layout)) {
        return 'horizontal'
    } else {
        return 'vertical'
    }
})
// 菜单数据

const sidebarRoutes = computed(() => (props.menus ? props.menus : routeStore.routes))
</script>

<template>
    <div class="layout">
        <div class="menu">

            <el-menu :mode="'vertical'" :default-active="route.path" class="menu-item" :collapse="appStore.menuCollapse"
                router :class="{ 'collapse-menu': !isDesktop ? false : appStore.menuCollapse }">
                <MenuItem v-for="item in sidebarRoutes" :key="item.path" :item="item">
                </MenuItem>

            </el-menu>


        </div>

    </div>

</template>

<style lang="scss" scoped>
.layout {
    height: 100%;
    width: 100%;
}

.menu {
    height: 100%;
    width: 100%;
    overflow: hidden;
}

::v-deep .menu-item {
    border: 0 !important;
    width: 100%;
    background: var(--color-bg-1);
    /* 设置菜单项悬停背景色 */
    --el-menu-hover-bg-color: var(--color-fill-2);

}



::v-deep .el-menu-item {
    // margin-top: 10px;
    height: 40px;
    margin: 5px;
    border-radius: 10px;
    color: var(--color-text-2)
}

::v-deep .el-menu-item.is-active {
    background: var(--color-fill-2);
    color: rgba(var(--primary-6));
}
</style>