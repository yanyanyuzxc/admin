<script lang="ts" setup>
import { ArrowRight } from '@element-plus/icons-vue'
import { useRouteStore } from '@/store';
import { cloneDeep } from 'lodash-es'
import { ref, onMounted, watchEffect } from 'vue'
import { findTree } from 'xe-utils'
import { useRouter, useRoute } from 'vue-router'
import { type RouteRecordRaw } from 'vue-router'
import type { RouteLocationMatched } from 'vue-router'
let routeStore = useRouteStore()
let router = useRouter()
let route = useRoute()
let breadcrumbList = ref<RouteLocationMatched[]>([])
let home: RouteLocationMatched | null = null
//缓存首页数据
const getHome = () => {
    if (!home) {
        const cloneRoutes = cloneDeep(routeStore.routes) as RouteLocationMatched[]
        //寻找首页节点
        const obj = findTree(cloneRoutes, (item) => item.path === '/dashboard/workplace')
        home = obj.item
    }
}
function getBreadcrumbList() {
    getHome()
    const cloneRoutes = cloneDeep(routeStore.routes) as RouteLocationMatched[]
    const obj = findTree(cloneRoutes, (i) => i.path === route.path)
    // 获取当前节点的所有上级节点集合，包含当前节点
    const arr = obj ? obj.nodes.filter((item) => item.meta && item.meta.title && item.meta.breadcrumb !== false) : []
    if (home) {
        breadcrumbList.value = [home, ...arr]
    }
}
getBreadcrumbList()

watchEffect(() => {
    if (route.path.startsWith('/redirect/')) return
    getBreadcrumbList()
})
</script>

<template>
    <el-breadcrumb :separator-icon="ArrowRight" class="my-breadcrumb">
        <el-breadcrumb-item v-for="(item, index) in breadcrumbList" :key="item.path + index" v-show="item.meta.title"
            :to="item.path">
            <!-- <el-icon style="vertical-align: middle">
                <component :is="item.meta.icon"></component>
            </el-icon> -->
            <template #default>
                <span style="margin: 0 5px; vertical-align: middle" class="title"
                    :class="{ 'is-active': index === breadcrumbList.length - 1 }">
                    {{ item.meta.title }}
                </span>
            </template>

        </el-breadcrumb-item>
    </el-breadcrumb>
</template>

<style lang="scss" scoped>
.el-breadcrumb {
    display: flex;
    transition: color 0.3s ease;

    .title {
        color: var(--color-text-1);
        font-weight: 400;

        &:hover {
            color: rgba(var(--primary-6))
        }

        &.is-active {
            color: inherit;
        }

        // &:last-child:hover {
        //     color: inherit !important;
        // }
    }





}
</style>