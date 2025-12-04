<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { type ColumnItem } from '@/components/GiForm/src/type'
import { useResetReactive } from '@/hooks'
import { encryptByRsa } from '@/utils/encrypt'
import { resetUserPwd } from '@/apis/system'
import { ElMessage } from 'element-plus'
const formRef = ref()

const [form, resetForm] = useResetReactive({})
const columns: ColumnItem[] = reactive([
    {
        label: '密码', field: 'newPassword', type: 'input', components: {
            type: 'password'
        }, span: 24, required: true
    },
])
const dialog = ref(false)
const reset = () => {
    formRef.value?.formRef?.resetFields()//重置表单的校验状态
    resetForm()//重置表单数据
}
const open = (id: string) => {
    dialog.value = true

    dataId.value = id

    reset()
}
const emit = defineEmits<{
    (e: 'save-success'): void
}>()
const dataId = ref<string>('')
const save = async () => {
    try {
        const isInvalid = await formRef.value?.formRef?.validate()
        if (!isInvalid) return false

        await resetUserPwd({ newPassword: encryptByRsa(form.newPassword) || '' }, dataId.value)
        ElMessage.success('重置成功')
        dialog.value = false
        return true
    } catch (error) {
        return false
    }
}

defineExpose({ open })
</script>

<template>
    <el-dialog v-model="dialog">
        <template #title>重置密码</template>
        <template #default>
            <GiForm :columns="columns" v-model="form" ref="formRef"></GiForm>
        </template>
        <template #footer>
            <el-button type="primary" @click="save">重置</el-button>
            <el-button @click="dialog = false">取消</el-button>
        </template>
    </el-dialog>

</template>

<style lang="scss" scoped></style>