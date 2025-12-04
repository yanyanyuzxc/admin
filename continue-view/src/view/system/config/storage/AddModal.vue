<script lang="ts" setup>
import { addStorage, getStorage, updateStorage } from '@/apis/system/storage'
import { type ColumnItem, GiForm } from '@/components/GiForm'
import { ElMessage } from 'element-plus'
import { useResetReactive } from '@/hooks'
import { useDict } from '@/hooks/app'
import { encryptByRsa } from '@/utils/encrypt'
const dataId = ref('')
const visible = ref(false)
const isUpdate = computed(() => !!dataId.value)
const { storage_type_enum } = useDict('storage_type_enum')
const storageType = ref('')
const Add = (type: string) => {
    reset()
    dataId.value = ''
    form.type = type
    visible.value = true

    storageType.value = storage_type_enum.value.find((item) => item.value === type)?.label || '本地存储'
}
// 修改
const onUpdate = async (id: string) => {
    reset()
    dataId.value = id
    const { data } = await getStorage(id)
    Object.assign(form, data)
    storageType.value = storage_type_enum.value.find((item) => item.value === form.type)?.label || '本地存储'
    visible.value = true
}
const [form, resetForm] = useResetReactive({
    type: 2,
    isDefault: false,
    sort: 999,
    status: 2,
})
const emit = defineEmits<{
    (e: 'save-success'): () => void
}>()
const formRef = ref<InstanceType<typeof GiForm>>();
const columns: ColumnItem[] = reactive([
    {
        label: '名称',
        field: 'name',
        type: 'input',
        span: 24,
        props: {
            maxLength: 100,
        },
        required: true,
    },
    {
        label: '编码',
        field: 'code',
        type: 'input',
        span: 24,
        props: {
            maxLength: 30,
        },
        required: true,
        disabled: () => isUpdate.value,
    },
    {
        label: 'Access Key',
        field: 'accessKey',
        type: 'input',
        span: 24,
        required: true,
        show: () => form.type === 2,
    },
    {
        label: 'Secret Key',
        field: 'secretKey',
        type: 'input',
        span: 24,
        required: () => !isUpdate.value,
        show: () => form.type === 2,
    },
    {
        label: 'Endpoint',
        field: 'endpoint',
        type: 'input',
        span: 24,
        required: true,
        show: () => form.type === 2,
    },
    {
        label: 'Bucket',
        field: 'bucketName',
        type: 'input',
        span: 24,
        required: true,
        show: () => form.type === 2,
    },
    {
        label: '域名',
        field: 'domain',
        type: 'input',
        span: 24,
        required: false,
        show: () => form.type === 2,
    },
    {
        label: '存储路径',
        field: 'bucketName',
        type: 'input',
        span: 24,
        required: true,
        show: () => form.type === 1,
    },
    {
        label: '访问路径',
        field: 'domain',
        type: 'input',
        span: 24,
        required: true,
        show: () => form.type === 1,
    },
    {
        label: '排序',
        field: 'sort',
        type: 'input-number',
        span: 24,
        props: {
            min: 1,
            mode: 'button',
        },
    },
    {
        label: '描述',
        field: 'description',
        type: 'input',
        components: { type: 'textarea' },
        span: 24,
    },
    {
        label: '状态',
        field: 'status',
        type: 'switch',
        span: 24,
        props: {
            type: 'round',
            checkedValue: 1,
            uncheckedValue: 2,
            checkedText: '启用',
            uncheckedText: '禁用',
        },
    },
])
const save = async () => {

    try {
        const isInvalid = await formRef.value?.formRef?.validate()
        if (!isInvalid) return false
        if (isUpdate.value) {
            await updateStorage({
                ...form,
                secretKey: form.type === 2 && form.secretKey ? encryptByRsa(form.secretKey) || '' : null,
            }, dataId.value)
            ElMessage.success('修改成功')
        } else {
            await addStorage({
                ...form,
                secretKey: form.type === 2 ? encryptByRsa(form.secretKey) || '' : form.secretKey,
            })
            ElMessage.success('新增成功')
        }
        emit('save-success')
        return true
    } catch (error) {
        return false
    }
}
// 重置
const reset = () => {
    formRef.value?.formRef?.resetFields()
    resetForm()
}
// 取消
const handleCancel = () => {
    visible.value = false

}

defineExpose({
    Add, onUpdate
})
</script>
<template>
    <el-dialog v-model="visible" width="450px">
        <template #header>
            <div class="header">
                <span>新增配置</span>
            </div>
        </template>
        <template #default>
            <GiForm ref="formRef" v-model="form" :columns="columns">
                <template #secretKey>
                    <el-input v-model="form.secretKey"
                        :placeholder="isUpdate ? '保持 Secret Key 为空将不更改' : '请输入 Secret Key'" />
                </template>
            </GiForm>
        </template>
        <template #footer>
            <el-button type="primary" @click="save">保存</el-button>
            <el-button @click="handleCancel">取消</el-button>
        </template>
    </el-dialog>
</template>

<style lang="scss" scoped>
.header {
    @include flex-center;
}
</style>