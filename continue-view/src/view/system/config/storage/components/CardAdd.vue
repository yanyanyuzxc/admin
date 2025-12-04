<script lang="ts" setup>
import { Plus } from '@element-plus/icons-vue'
const props = defineProps({
    type: {
        type: Number,
        default: 1,
    },
})

import AddModal from '../AddModal.vue'
const AddModalRef = ref<InstanceType<typeof AddModal>>()
const handleAdd = () => {
    AddModalRef.value?.Add(props.type)
}
const emit = defineEmits<{
    (e: 'save-success'): void
}>()

const search = () => {
    emit('save-success')
}

</script>
<template>
    <el-card @click="handleAdd">
        <div class="card-body">
            <el-icon size="25">
                <Plus />
            </el-icon>
            <span>点击创建本地存储</span>
        </div>
    </el-card>
    <AddModal ref="AddModalRef" @save-success="search"> </AddModal>
</template>

<style lang="scss" scoped>
.el-card {
    border: 1px solid #dcdfe6;
    @include flex-center;
    height: 200px;
    max-width: 300px;

    .card-body {
        @include flex-center;
        flex-direction: column;
    }

    span {
        display: inline-block;
        margin-top: 10px;
    }
}
</style>
