<template>
    <el-dialog v-model="visible" title="Warning" align-center width="1000">
        <template #header>
            <span>分配角色</span>
        </template>
        <template #default>
            <UserSelect :roleId="dataId" @select-user="handleSelectUser" ref="UserSelectRef"
                v-model:value="selectedData" v-if="visible" />
        </template>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="reset">Cancel</el-button>
                <el-button type="primary" @click="save">
                    Confirm
                </el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { assignToUsers } from '@/apis/system/role'
import UserSelect from '@/components/UserSelect/index.vue'
import { ElMessage } from 'element-plus'
const visible = ref(false)
const dataId = ref('')
const open = async (id: string) => {
    dataId.value = id
    selectedData.value = []
    visible.value = true
}
const selectedData = ref<string[]>([])
const handleSelectUser = (rows: string[]) => {
    selectedData.value = rows
}
const UserSelectRef = ref<InstanceType<typeof UserSelect>>()
const emit = defineEmits<{ (e: 'save-success'): void }>()
// 重置
const reset = () => {
    dataId.value = ''
    selectedData.value = []
    UserSelectRef.value?.onClearSelected()
    visible.value = false
}
// 保存
const save = async () => {
    try {
        const isInvalid = selectedData.value.length === 0
        if (isInvalid) {
            ElMessage.warning('请选择用户')
            return false
        }
        await assignToUsers(dataId.value, selectedData.value)
        reset()
        emit('save-success')

        ElMessage.success('分配成功')
        return true
    } catch (error) {
        return false
    }
}
defineExpose({
    open
})
</script>
<style lang="scss" scoped></style>