<script lang="ts" setup>
import { AiEditor, type AiEditorOptions } from 'aieditor'
import 'aieditor/dist/style.css'
import { useAppStore } from '@/store'
const props = defineProps<{
    modelValue: string
    options?: AiEditorOptions
}>()
</script>

<template>
    <div class="edit">
        <span>{{ modelValue }}</span>
    </div>
</template>

<style lang="scss" scoped>
.edit {
    border: 1px dashed #ccc;
    height: 100%;
}
</style>