<template>
    <div class="layout">
        <div class="menu">
            <Menu></Menu>
        </div>

    </div>

</template>
<script lang="ts" setup>
import { useRouteStore } from '@/store/modules/route'
import Menu from '../menu/index.vue'
import Logo from '../logo/index.vue'
import { ref, watch, nextTick } from 'vue'

import { useRoute, useRouter } from 'vue-router'
import { type RouteRecordRaw } from 'vue-router'
import { filterTree } from '@/utils/index'
import { cloneDeep } from 'lodash'
import { searchTree } from 'xe-utils'

let routeStore = useRouteStore()
let route = useRoute()
// 过滤是菜单的路由
const cloneRoutes = cloneDeep(routeStore.routes) as RouteRecordRaw[]
const menuRoutes = filterTree(cloneRoutes, (i) => i.meta?.hidden === false)

</script>
<style lang="scss" scoped>
.layout {
    height: 100%;
    border-right: 1px solid #e0dfdf;
}

.logo {
    height: $tabber-height;
}

.menu {
    height: calc(100% - #{$tabber-height});
}
</style>