<template>
    <el-container>
        <el-aside :class="{ 'collapsed': appStore.menuCollapse, 'color': appStore.menuDark }">
            <div class="logo">
                <Logo :collapse="appStore.menuCollapse" />
            </div>
            <div class="menu" :class="{ 'color': appStore.menuDark }">
                <Menu></Menu>
            </div>
            <Ad />
        </el-aside>

        <el-container class="container">

            <el-header :class="{ 'collapsed': appStore.menuCollapse }">
                <div class="top" :class="{ 'color': !appStore.tab }">
                    <el-row style="width: 97%;">
                        <el-col :span="12" style="display: flex; align-items: center;">
                            <Fold></Fold>
                            <Breadcrumb></Breadcrumb>
                        </el-col>
                        <el-col :span="12">
                            <Header></Header>
                        </el-col>
                    </el-row>
                </div>
                <div class="right" v-show="appStore.tab">
                    <Tab></Tab>
                </div>
            </el-header>
            <el-main :class="{ 'collapsed': appStore.menuCollapse }">
                <Main />
            </el-main>

            <el-footer>

            </el-footer>
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

</script>

<style lang="scss" scoped>
.el-container * {
    transition: all 0.3s ease-in-out;
}

.el-container {
    height: 100vh;
    width: 100%;
    background-color: rgba(245, 247, 253);
    align-items: stretch;
    overflow: hidden;

    .el-aside {
        width: $asider-width;
        height: 100%;
        border-right: 1px solid var(--color-border);
        background-color: var(--color-bg-1) !important;
        transition: all 0.3s ease-in-out;
        display: flex;
        flex-direction: column;
        overflow: hidden;

        color: var(--color-text-1) !important;

        &.color {
            background-color: rgba(0, 21, 41);
            // color: white !important;
            color: white !important;
        }

        .logo {
            height: 5%;

        }

        .menu {
            // flex: 1;
            // overflow-y: auto;
            overflow-x: hidden;
            background-color: var(--color-bg-1) !important;
            // padding-top: 10px;
            min-width: 0;
            flex-shrink: 1;
            height: 80%;
            transition: all 0.3s ease-in-out;

            &.color {
                background-color: rgba(0, 21, 41) !important;
            }

        }

        &.collapsed {
            width: $collapse-width;
        }
    }

    .container {
        height: 100vh;


        .el-header {
            height: 13%;
            width:cacl(100% - #{$asider-width});
            padding: 0;
            margin: 0;
            color: var(--color-text-1);
            background: var(--color-bg-1) !important;
            border-bottom: 1px solid var(--color-bg-1);

            .top {
                display: flex;
                width: 100%;
                height: 60%;
                justify-content: space-between;
                align-items: center;
                padding: 0 10px;
                border-bottom: 1px solid rgb(64, 64, 66);
                overflow: hidden;

                &.color {
                    height: 100%;
                }

                .el-menu {
                    background: var(--color-bg-1) !important;
                    border: 0 !important;
                }

                .breadcrumb {
                    width: 400px;
                    overflow: hidden;
                    white-space: nowrap;
                }
            }

            // .right {
            //     height: 40%;
            // }
        }
    }
}
</style>