<script lang="ts" setup>
import { useTabStore } from "@/store";
import { computed } from "vue";
let tabStore = useTabStore();
const cacheList = computed(() => tabStore.cacheList as string[])
//router-view会暴露子路由的component和route对象，通过route.fullPath判断是否需要缓存
</script>

<template>
    <div>
        <router-view v-slot="{ Component, route }">
            <keep-alive :include="(cacheList)">
                <component :is="Component" :key="route.fullPath" />
            </keep-alive>
        </router-view>
    </div>
</template>

<style lang="scss" scoped></style>