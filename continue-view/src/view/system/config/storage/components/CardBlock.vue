<script lang="ts" setup>
import { ArrowDown } from '@element-plus/icons-vue'
import AddModal from '../AddModal.vue'
import has from '@/utils/has'
import { type StorageResp, deleteStorage, setDefaultStorage, updateStorageStatus } from '@/apis/system'
import { useDict } from '@/hooks/app'
import { ElMessageBox, ElMessage } from 'element-plus'
interface Props {
    loading: boolean
    data: StorageResp
}

const props = withDefaults(defineProps<Props>(), {})
// 修改
const AddModalRef = ref<InstanceType<typeof AddModal>>()
const onUpdate = (record: StorageResp) => {
    AddModalRef.value?.onUpdate(record.id)
}
const emit = defineEmits<{
    (e: 'save-success'): void
}>()

const search = () => {
    emit('save-success')
}
// 删除
const onDelete = (record: StorageResp) => {
    ElMessageBox.confirm(
        `是否确定删除存储「${record.name}(${record.code})」？`,
        'Warning',
        {
            confirmButtonText: 'OK',
            cancelButtonText: 'Cancel',
            type: 'warning',
        }
    )
        .then(async () => {
            try {
                const res = await deleteStorage(record.id)
                if (res.success) {
                    ElMessage.success('删除成功')
                    search()
                }
                return res.success
            } catch (error) {
                return false
            }
        },)
        .catch(() => {
            ElMessage({
                type: 'info',
                message: 'Delete canceled',
            })
        })

}
const { storage_type_enum } = useDict('storage_type_enum')
const storageType = computed(() => {
    return storage_type_enum.value.find((item) => item.value === props.data.type as unknown as string)?.label || '本地存储'
})

const status = ref(props.data.status)
// 更新状态
const onUpdateStatus = async (): Promise<boolean> => {
    switchLoading.value = true; // 开始加载，禁用开关
    const currentStatus = status.value; // 记录当前状态（用于取消时还原）
    console.log(currentStatus);
    const tip = currentStatus === 2 ? '启用' : '禁用';

    try {
        // 用 await 简化回调，避免嵌套
        await ElMessageBox.confirm(
            `是否确定${tip}${storageType.value}「${props.data.name}(${props.data.code})」？`,
            '提示',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
            }
        );

        // 调用接口更新状态
        const res = await updateStorageStatus(
            { status: currentStatus === 2 ? 1 : 2 },
            props.data.id
        );

        if (res.success) {
            ElMessage.success(`${tip}成功`);
            search();
            return true; // 允许开关切换
        } else {
            ElMessage.error(`${tip}失败`);
            return false; // 阻止开关切换
        }
    } catch (error) {
        // 取消确认时还原状态
        status.value = currentStatus;
        ElMessage.info('操作取消');
        return false;
    } finally {
        switchLoading.value = false; // 结束加载
    }
};
const switchLoading = ref(false)
</script>

<template>
    <el-card>

        <div class="title">
            <span>{{ data.name }} ({{ data.code }})</span>

            <el-dropdown class="more-btn">
                <el-icon class="el-icon--right">
                    <arrow-down />
                </el-icon>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item :disabled="data.isDefault || data.status === 2">
                            <GiSvgIcon name="check-circle" size="15"></GiSvgIcon>

                            设为默认
                        </el-dropdown-item>
                        <el-dropdown-item @click="onUpdate(data)">
                            <GiSvgIcon name="edit" size="15"></GiSvgIcon>
                            修改
                        </el-dropdown-item>
                        <el-dropdown-item @click="onDelete(data)" :disabled="data.isDefault">
                            <GiSvgIcon name="delete" size="15"></GiSvgIcon>
                            删除
                        </el-dropdown-item>

                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </div>
        <div class="time">{{ data.createTime }}</div>
        <div class="body">
            <slot name="content"></slot>
        </div>
        <div class="extra">

            <el-switch v-model="status" :disabled="!has.hasPermOr(['system:storage:updateStatus']) || data.isDefault"
                :title="data.isDefault ? '不允许禁用默认存储' : ''" :loading="switchLoading" :active-value="1"
                :inactive-value="2" :before-change="onUpdateStatus" />
        </div>
    </el-card>
    <AddModal ref="AddModalRef" />
</template>

<style lang="scss" scoped>
.el-card {
    height: 200px;
    max-width: 300px;
}

.more-btn {
    display: none;

    cursor: pointer; // 鼠标移上去显示手型
}

// 2. 核心修复：卡片 hover 时，显示按钮（选择器是“卡片 hover → 按钮”）
.el-card:hover .more-btn {
    display: block;
}

.title {
    @include flex-between;
}

.time {
    margin-top: 5px;
    font-size: 0.9em;
    opacity: 0.6;
}

.body {
    margin-top: 15px;
}
</style>