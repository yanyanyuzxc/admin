<script lang="ts" setup>
import { ref, reactive, computed } from 'vue'
import { type ColumnItem, GiForm } from '@/components/GiForm'
import { GenderList } from '@/constant/common'
import { useDept, useRole } from '@/hooks/app'
import { encryptByRsa } from '@/utils/encrypt'
import { Message, type TreeNodeData } from '@arco-design/web-vue'
import { useWindowSize } from '@vueuse/core'
import type { Gender, Status } from '@/type/global'
import { useResetReactive } from '@/hooks'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { addUser, getUser, updateUser } from '@/apis/system/user'
const drawer = ref(false)
const dataId = ref('')
const isUpdate = computed(() => !!dataId.value)// 判断是否为空
const title = computed(() => (isUpdate.value ? '修改用户' : '新增用户'))
const { roleList, getRoleList } = useRole()
const { deptList, getDeptList } = useDept()
const columns: ColumnItem[] = reactive([
    {
        label: '昵称',
        field: 'nickname',
        type: 'input',
        span: 24,
        required: true,
        props: {
            maxlength: 30,
        },
    },
    {
        label: '用户名',
        field: 'username',
        type: 'input',
        span: 24,
        required: true,
        props: {
            maxlength: 64,
        },
    },
    {
        label: '密码',
        field: 'password',
        type: 'input',
        span: 24,
        required: true,
        props: {
            maxlength: '32',
            type: 'password'
        },
        hide: () => isUpdate.value,
    },
    {
        label: '手机号码',
        field: 'phone',
        type: 'input',
        span: 24,
        props: {
            maxlength: 11,
            type: 'password'
        },
    },
    {
        label: '邮箱',
        field: 'email',
        type: 'input',
        span: 24,
        props: {
            maxlength: 255,
        },
    },
    {
        label: '性别',
        field: 'gender',
        type: 'radio-group',
        span: 24,
        props: {
            options: GenderList,
        },
    },
    {
        label: '所属部门',
        field: 'deptId',
        type: 'tree-select',
        span: 24,
        required: true,
        props: {
            data: deptList,
            allowClear: true,
            allowSearch: true,
            fallbackOption: false,
            filterTreeNode(searchKey: string, nodeData: TreeNodeData) {
                if (nodeData.title) {
                    return nodeData.title.toLowerCase().includes(searchKey.toLowerCase())
                }
                return false
            },
        },
    },
    {
        label: '角色',
        field: 'roleIds',
        type: 'select',
        span: 24,
        required: true,
        props: {
            options: roleList,
            multiple: true,
            allowClear: true,
            allowSearch: true,
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
const { width } = useWindowSize()
const [form, resetForm] = useResetReactive({
    gender: 1 as Gender,
    status: 1 as Status,
})
// 保存
const formRef = ref<InstanceType<typeof GiForm>>()
const save = async () => {
    const rawPassword = form.password
    try {
        const isInvalid = await formRef.value?.formRef?.validate()
        if (!isInvalid) {
            return false
        }
        if (isUpdate.value) {
            await updateUser(form, dataId.value)
            ElMessage.success('修改成功')
        } else {
            if (rawPassword) {
                form.password = encryptByRsa(rawPassword) || ''
            }
            await addUser(form)
            ElMessage.success('新增成功')
        }
        emit('save-success')
        drawer.value = false
        return true
    } catch (error) {
        form.password = rawPassword
        return false
    }
}
// 重置
const reset = () => {
    //   formRef.value?.formRef?.resetFields()
    resetForm()
}
// 新增
const onAdd = async () => {
    drawer.value = true
    reset()
    if (!deptList.value.length) {
        await getDeptList()
        // console.log(deptList.value)
    }
    if (!roleList.value.length) {
        await getRoleList()
        // console.log(roleList.value)
    }
    dataId.value = ''

}
const onEdit = async (id: any) => {
    reset()
    dataId.value = id
    if (!deptList.value.length) {
        await getDeptList()
    }
    if (!roleList.value.length) {
        await getRoleList()
    }
    const { data } = await getUser(id)
    Object.assign(form, data)
    drawer.value = true

}
defineExpose({
    onAdd, onEdit
})
const emit = defineEmits<{
    (e: 'save-success'): void
}>()
</script>

<template>
    <el-drawer v-model="drawer" :title="title" show-close>
        <GiForm v-model="form" :columns="columns" ref="formRef"> </GiForm>
        <template #footer>
            <el-button type="primary" size="small">取消</el-button>
            <el-button type="primary" size="small" @click="save">确定</el-button>
        </template>
    </el-drawer>
</template>

<style lang="scss" scoped></style>