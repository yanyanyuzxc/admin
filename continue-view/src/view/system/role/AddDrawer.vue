<script lang="ts" setup>
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { useWindowSize } from '@vueuse/core'
import type { GiForm } from '@/components/GiForm'
import { type FormInstance, Message, type TreeNodeData } from '@arco-design/web-vue'
import { useResetReactive } from '@/hooks'
import { useDept, useDict } from '@/hooks/app'
import { addRole, getRole, updateRole } from '@/apis'
import { ElMessage } from 'element-plus'

const visible = ref(false)
const isUpdate = computed(() => !!dataId.value)
const { data_scope_enum } = useDict('data_scope_enum')
// console.log(data_scope_enum)
const { deptList, getDeptList } = useDept()
const [form, resetForm] = useResetReactive({
    deptCheckStrictly: false,
    sort: 999,
    dataScope: 4,
})
const dataId = ref('')
const formRef = ref<FormInstance>()
const deptTreeRef = ref()
//是否展开
const isDeptExpanded = ref(true)
const allKeys = ref<(string | number)[]>([])
const onUpdate = async (id: string) => {
    reset()

    if (deptList.value.length === 0) {
        await getDeptList()
        allKeys.value = getAllNodeIds(deptList.value)

    }
    dataId.value = id
    const { data } = await getRole(id)
    const originalDeptCheckStrictly = data.deptCheckStrictly;
    data.deptCheckStrictly = false
    Object.assign(form, data)
    const count = ref(0);

    // 替换原有的 forEach 逻辑
    // 3. 回显节点：用 Promise 包裹，确保所有节点的 setChecked 都执行完
    await new Promise((resolve) => {
        if (!data.deptIds || data.deptIds.length === 0) {
            resolve(null);
            return;
        }
        // 记录已回显的节点数量，全部完成后 resolve
        let count = 0;
        data.deptIds.forEach((node) => {
            nextTick(() => {
                if (deptTreeRef.value) {
                    deptTreeRef.value.setChecked(node, true, true);
                }
                count++;
                if (count === data.deptIds.length) {
                    resolve(null); // 所有节点回显完成
                }
            });
        });
    });

    // 4. 关键：节点回显完后，恢复 form 的联动状态（改对目标！）
    form.deptCheckStrictly = originalDeptCheckStrictly;
    visible.value = true
}
const reset = () => {
    formRef.value?.resetFields()
    form.deptCheckStrictly = false;
    // 4. 可选：重置全选和展开状态，避免残留
    // 5. 重置树的选中状态（如果树已存在）
    if (deptTreeRef.value) {
        deptTreeRef.value.setCheckedKeys([]);

        // deptTreeRef.value.expandAll(true);
    }
    resetForm()
}
const onAdd = async () => {
    reset()
    if (!deptList.value.length) {
        await getDeptList()
    }
    dataId.value = ''
    visible.value = true
}
const emit = defineEmits<{
    (e: 'save-success'): void
}>()
// 获取所有选中的部门
const getDeptAllCheckedKeys = () => {
    if (!deptTreeRef.value) {
        return []
    }
    // 获取目前被选中的部门
    const checkedNodes = deptTreeRef.value?.getCheckedNodes()
    const checkedKeys = checkedNodes.map((item: TreeNodeData) => item.key)
    // 获取半选中的部门
    const halfCheckedNodes = deptTreeRef.value?.getHalfCheckedNodes()
    const halfCheckedKeys = halfCheckedNodes.map((item: TreeNodeData) => item.key)
    checkedKeys.unshift(...halfCheckedKeys)
    return checkedKeys
}
const confirm = async () => {
    const isInvalid = await formRef.value?.validate()
    if (!isInvalid) return ElMessage.error('请检查表单内容')
    form.deptIds = getDeptAllCheckedKeys()
    try {
        if (isUpdate.value) {
            await updateRole(form, dataId.value)

            ElMessage.success('修改成功')
        } else {
            await addRole(form)
            ElMessage.success('新增成功')
        }
        emit('save-success')
        visible.value = false
        return true
    } catch (err: any) {
        ElMessage.error(err)
    }
}

const title = computed(() => (isUpdate.value ? '修改角色' : '新增角色'))
defineExpose({
    visible,
    onUpdate,
    onAdd
})

// “展开/折叠” checkbox 的回调函数
const onExpanded = async () => {
    if (!deptTreeRef.value) return;
    await nextTick(); // 确保树已渲染
    // 调用 expandAll，传入 isDeptExpanded 的值（true 展开，false 折叠）

    deptTreeRef.value.expandAll(isDeptExpanded.value)
};
//获取所有数组的id
// 递归获取所有节点的 key（根据你的数据结构调整，假设节点唯一标识为 id）
const getAllNodeIds = (nodes: any[]): (string | number)[] => {
    let ids: (string | number)[] = [];
    nodes.forEach(node => {
        ids.push(node.key); // 收集当前节点的 id
        if (node.children && node.children.length) {
            ids = [...ids, ...getAllNodeIds(node.children)]; // 递归收集子节点
        }
    });
    return ids;
};

// 全选/全不选
const isDeptCheckAll = computed(() => {
    if (!deptTreeRef.value || allKeys.value.length === 0) return false;
    // 获取完全选中的节点 key
    if (form.deptCheckStrictly) {
        const halfCheckedKeys = deptTreeRef.value.getHalfCheckedKeys();
        if (halfCheckedKeys.length > 0) {
            return false;
        }
    }
    const halfCheckedKeys = deptTreeRef.value.getHalfCheckedKeys();
    if (halfCheckedKeys.length > 0) {
        return false;
    }

    const checkedKeys = deptTreeRef.value.getCheckedKeys();

    return checkedKeys.length === allKeys.value.length;
})
// 全选/取消全选的回调
const onCheckAll = async () => {
    if (!deptTreeRef.value) return;
    // 等待树渲染完成
    await nextTick();
    if (!isDeptCheckAll.value) {
        // 全选：获取所有节点的 id，设置为选中
        // console.log(allKeys.value)
        deptTreeRef.value.setCheckedKeys(allKeys.value, true)

    } else {
        // 取消全选：清空选中的 key
        deptTreeRef.value.setCheckedKeys([]);
    }
};
watch(
    () => form.dataScope,
    async (newDataScope) => {
        if (newDataScope === 5) {
            // 等待 1 个 nextTick：确保 el-tree 因 v-if 渲染完成并挂载
            await nextTick();

            // 确认树实例已存在
            if (deptTreeRef.value) {
                // 方法 1：用 getNodes() 获取所有节点（返回完整节点数组）


                // 方法 2：如需只拿 key，可结合之前的 getAllNodeIds 函数
                allKeys.value = getAllNodeIds(deptList.value);


            }
        }
    },
    { deep: true }
);
const quit = () => {
    reset()
    visible.value = false
}

const rules: FormInstance['rules'] = {
    name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
    code: [{ required: true, message: '请输入编码', trigger: 'blur' }],
    dataScope: [{ required: true, message: '请选择数据权限', trigger: 'change' }],
}

</script>

<template>
    <el-drawer v-model="visible" :title="title">
        <el-form :rules="rules" ref="formRef" :model="form">
            <el-form-item label="名称" prop="name">
                <el-input v-model="form.name" placeholder="请输入名称" maxlength="30" show-word-limit />
            </el-form-item>
            <el-form-item label="编码" prop="code">
                <el-input v-model="form.code" placeholder="请输入编码" maxlength="30" show-word-limit :disabled="isUpdate" />
            </el-form-item>
            <el-form-item label="排序" prop="sort">
                <el-input-number v-model="form.sort" placeholder="请输入排序" :min="1" mode="button" />
            </el-form-item>
            <el-form-item label="描述" prop="description">
                <el-input type="textarea" v-model="form.description" placeholder="请输入描述" show-word-limit
                    :maxlength="200" :auto-size="{ minRows: 3, maxRows: 5 }" />
            </el-form-item>
            <el-form-item hide-label prop="dataScope">
                <el-select v-model="form.dataScope" :options="data_scope_enum" placeholder="请选择数据权限"
                    :disabled="form.isSystem" />
            </el-form-item>
            <el-form-item v-if="form.dataScope === 5" hide-label :disabled="form.isSystem">
                <el-space>
                    <el-checkbox v-model="isDeptExpanded" @change="onExpanded">展开/折叠</el-checkbox>
                    <el-checkbox v-model="isDeptCheckAll" @change="onCheckAll()">全选/全不选</el-checkbox>
                    <el-checkbox v-model="form.deptCheckStrictly">父子联动</el-checkbox>
                </el-space>
                <div>
                    <el-tree ref="deptTreeRef" :data="deptList" :check-strictly="!form.deptCheckStrictly"
                        default-expand-all show-checkbox :node-key="'key'" />
                </div>
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="quit">取消</el-button>
            <el-button class="confirm-btn" @click="confirm">确定</el-button>
        </template>
    </el-drawer>
</template>

<style lang="scss" scoped>
.confirm-btn {
    background: $color-theme;
    color: white;
}
</style>