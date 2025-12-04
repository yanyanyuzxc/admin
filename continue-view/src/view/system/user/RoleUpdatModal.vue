<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { type ColumnItem } from '@/components/GiForm/src/type'
import { useResetReactive } from '@/hooks'
import { useRole } from '@/hooks/app'
import { getUser, updateUserRole } from '@/apis/system'
import { ElMessage } from 'element-plus'
const formRef = ref()
const { roleList, getRoleList } = useRole()
const [form, resetForm] = useResetReactive({})
const columns: ColumnItem[] = reactive([
    {
        label: '角色',
        field: 'roleIds',
        type: 'select',
        style: {
            width: "1000px !important", // 临时添加 !important 测试优先级
        },
        required: true,
        props: {
            options: roleList,
            multiple: true,
            allowClear: true,
            allowSearch: { retainInputValue: true },
        },
    },
])
const dialog = ref(false)
const dataId = ref('')
const reset = () => {
    resetForm()
}
// 初始化
const open = async (id: string) => {
    reset()
    dataId.value = id
    if (!roleList.value.length) {
        await getRoleList()
    }
    const { data } = await getUser(id)
    Object.assign(form, data)
    dialog.value = true
}
// 保存
const save = async () => {
    try {
        const isInvalid = await formRef.value?.formRef?.validate()
        if (!isInvalid) return false
        await updateUserRole({ roleIds: form.roleIds }, dataId.value)
        console.log(form.roleIds)
        ElMessage.success('分配成功')
        dialog.value = false
        emit('save-success')
        return true
    } catch (error) {
        return false
    }
}
const emit = defineEmits<{
    (e: 'save-success'): void
}>()
defineExpose({ open })
</script>

<template>
    <el-dialog v-model="dialog">
        <template #title>分配角色</template>
        <template #default>
            <GiForm :columns="columns" v-model="form" ref="formRef"></GiForm>
        </template>
        <template #footer>
            <el-button type="primary" @click="save">确定</el-button>
            <el-button @click="dialog = false">取消</el-button>
        </template>
    </el-dialog>

</template>

<style lang="scss" scoped></style>