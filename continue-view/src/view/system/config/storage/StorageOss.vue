<script lang="ts" setup>
import CardAdd from './components/CardAdd.vue'
import CardBlock from './components/CardBlock.vue'
import type { StorageResp } from '@/apis'
import has from '@/utils/has'
defineProps({
    loading: {
        type: Boolean,
        default: false,
    },
    data: {
        type: Array<StorageResp>,
        default: () => [],
    },
})

const emit = defineEmits<{
    (e: 'save-success'): void
}>()

const search = () => {
    emit('save-success')
}
</script>

<template>
    <div class="list-wrap">
        <el-row class="list-row" :gutter="24">
            <el-col v-if="has.hasPermOr(['system:storage:create'])" :xs="24" :sm="24" :md="12" :lg="8" :xl="8" :xxl="6"
                class="list-col" style="min-height: 162px">
                <CardAdd :type="1" @save-success="search" />
            </el-col>
            <el-empty v-if="!data.length && !has.hasPermOr(['system:storage:create'])" />
            <el-col v-for="item in data" :key="item.id" :xs="24" :sm="24" :md="12" :lg="8" :xl="8" :xxl="6"
                class="list-col" style="min-height: 162px">
                <CardBlock :loading="loading" :data="item" @save-success="search">
                    <template #content>

                        <el-descriptions :column="1">
                            <el-descriptions-item label="存储路径">{{ item.bucketName }}</el-descriptions-item>
                            <el-descriptions-item label="访问路径">{{ item.domain }}</el-descriptions-item>
                        </el-descriptions>
                    </template>
                </CardBlock>
            </el-col>
        </el-row>
    </div>
</template>

<style lang="scss" scoped></style>