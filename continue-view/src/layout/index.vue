<script lang="ts" setup>
import LayoutMix from './layoutMix.vue'
import Layoutdefault from './layoutDefault.vue';
import { useAppStore } from '@/store';
const appStore = useAppStore();
</script>

<template>
    <LayoutMix v-if="appStore.layout === 'mix'"></LayoutMix>
    <Layoutdefault v-else></Layoutdefault>
</template>

<style lang="scss" scoped></style>