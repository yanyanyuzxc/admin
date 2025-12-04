<template>
    <el-container class="box">
        <el-aside :class="{ 'collapsed': appStore.menuCollapse }">
            <div class="logo">
                <Logo :collapse="appStore.menuCollapse" />
            </div>
            <div class="menu" :class="{ 'color': appStore.menuDark }">
                <Menu :menus="leftMenus"></Menu>
            </div>
            <!-- <div class="ad" style=" width:100%;">
                <img src="@/assets/images/ad.png" alt="" style="width:100%; height:100%;
                ">
            </div> -->
            <!-- <Ad /> -->
        </el-aside>

        <el-container class="container">

            <el-header :class="{ 'collapsed': appStore.menuCollapse }">
                <div class="top" :class="{ 'color': !appStore.tab }">
                    <fold></fold>
                    <el-menu mode="horizontal" :default-active="activeMenu" class="el-menu">
                        <el-menu-item v-for="item in topMenus" :key="item.path" class="menu" :index="item.path"
                            @click="handleClick(item.path)">
                            <template #title>
                                <GiSvgIcon :name="item.meta?.icon" size="24"></GiSvgIcon>
                                <!-- 将p标签的样式颜色绑定到color -->
                                <p ref="tag" style="margin-left:10px;color: var(--color-text-5)">{{
                                    item.meta?.title }}</p>
                                <!-- <Icon :title="item.meta?.title" :icon="item.meta?.icon"></Icon> -->
                            </template>
                        </el-menu-item>
                    </el-menu>
                    <Header></Header>
                </div>
                <div class="right" v-show="appStore.tab">
                    <Tab></Tab>
                </div>
            </el-header>
            <el-main :class="{ 'collapsed': appStore.menuCollapse, 'color': appStore.theme === 'dark' }">
                <Main />
            </el-main>

            <!-- <el-footer>

            </el-footer> -->
        </el-container>
    </el-container>

</template>
<script lang="ts" setup>
import Ad from './ad.vue'
import Main from "./main/index.vue";
import Fold from "./header/fold.vue";
import { useRouteStore } from '@/store/modules/route'
import { useAppStore } from '@/store/modules/app'
import Menu from './menu/index.vue'
import Logo from './logo/index.vue'
import Header from './header/index.vue'
import Tab from './tab/index.vue'
import { ref, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { type RouteRecordRaw } from 'vue-router'
import Icon from "@/layout/header/components/index.vue"
import { filterTree } from '@/utils/index'
import { cloneDeep } from 'lodash'
import { searchTree } from 'xe-utils'
import { isExternal } from "@/utils/validate";
let routeStore = useRouteStore()
let route = useRoute()
let router = useRouter()
let appStore = useAppStore()
let defaultActive = ref('/')
// 过滤是菜单的路由
const cloneRoutes = cloneDeep(routeStore.routes) as RouteRecordRaw[]
// console.log(cloneRoutes)
const menuRoutes = filterTree(cloneRoutes, (i) => i.meta?.hidden === false)

// 克隆是菜单的路由
const cloneMenuRoutes: RouteRecordRaw[] = cloneDeep(menuRoutes)
// 左侧的菜单
const leftMenus = ref<RouteRecordRaw[]>([])
// 当前激活的菜单
const activeMenu = ref<string>()
// 获取左侧菜单
const getLeftMenus = async (key: string) => {
    const arr = searchTree(cloneMenuRoutes, (i) => i.path === key, { children: 'children' })
    const rootPath = arr.length ? arr[0].path : ''
    const obj = cloneMenuRoutes.find((i) => i.path === rootPath)
    activeMenu.value = obj ? obj.path : ''
    leftMenus.value = obj ? (obj.children as RouteRecordRaw[]) : []
}
// console.log(leftMenus.value)
// 顶部一级菜单
const topMenus = ref<RouteRecordRaw[]>([])
topMenus.value = JSON.parse(JSON.stringify(menuRoutes))
watch(
    () => route.path,
    (newPath) => {
        nextTick(() => {
            getLeftMenus(newPath)
        })
    },
    { immediate: true },
)
const handleClick = async (key: string) => {
    if (isExternal(key)) {
        window.open(key)
        return
    }
    await getLeftMenus(key)
    const obj = topMenus.value.find((i) => i.path === key)
    if (obj && obj.redirect === 'noRedirect') return
    router.push({ path: key })
}
</script>

<style lang="scss" scoped>
.el-container * {
    transition: all 0.3s ease-in-out;
    scrollbar-width: none;
}

.el-main {
    padding: 0px;
    flex: 1;
    background-color: rgb(245, 247, 253);


    &.color {
        background-color: var(--color-bg-2);
    }
}

.box {
    height: 100vh;
    width: 100%;
    // background-color: rgb(245, 247, 253);

    // background-color: var(--color-bg-2);
    align-items: stretch;
    overflow: hidden;


    .el-aside {
        width: $asider-width;
        height: 100%;
        border-right: 1px solid var(--color-border);
        background-color: var(--color-bg-1);
        transition: all 0.3s ease-in-out;
        display: flex;
        flex-direction: column;
        overflow: hidden;
        color: var(--color-text-2);

        .logo {
            height: 5%;

        }

        .menu {
            overflow-x: hidden;
            background-color: var(--color-bg-1) !important;
            min-width: 0;
            margin-top: 20px;
            flex-shrink: 1;
            height: 80%;
            transition: all 0.3s ease-in-out;

            // &.color {
            //     background-color: rgb(27, 28, 28) !important;
            // }

        }

        &.collapsed {
            width: $collapse-width;
        }
    }

    .container {
        height: 100vh;



        .el-header {
            height: auto;
            padding: 0;
            margin: 0;
            color: var(--color-text-1);

            background: var(--color-bg-1) !important;

            border-bottom: 1px solid var(--color-bg-1);
            transition: all 0.3s ease-in-out;

            .top {
                display: flex;
                padding: 0 10px;
                height: 57%;
                justify-content: space-between;
                align-items: center;
                border-bottom: 1px solid rgb(64, 64, 66);
                overflow: hidden;
                transition: all 0.3s ease-in-out;

                &.color {
                    height: 100%;
                }

                .el-menu {
                    // background: var(--color-bg-1) !important;
                    transition: all 0.3s ease-in-out;
                    border: 0 !important;
                    // overflow: hidden;
                    max-width: 60%;
                    --el-menu-bg-color: var(--color-bg-1);

                    /* 设置菜单文字颜色 */
                    --el-menu-text-color: var(--color-text-2);

                    /* 设置活动菜单项文本颜色 */
                    --el-menu-active-color: rgba(var(--primary-6));

                    /* 设置菜单项悬停背景色 */
                    --el-menu-hover-bg-color: #f1f3f4;
                }
            }

        }
    }
}
</style>