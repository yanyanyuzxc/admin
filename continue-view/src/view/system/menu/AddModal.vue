<script lang="ts" setup>
import { ref, computed, nextTick } from 'vue'
import { type ColProps, type FormInstance, Message, type TreeNodeData } from '@arco-design/web-vue'
import { useWindowSize } from '@vueuse/core'
import { mapTree } from 'xe-utils'
import { type MenuResp, addMenu, getMenu, updateMenu } from '@/apis/system/menu'
import { useResetReactive } from '@/hooks'
import { filterTree, transformPathToName } from '@/utils'
import { useComponentPaths } from '@/hooks/modules/useComponentPath.ts'
import { ElMessage } from 'element-plus'
const visible = ref(false)
interface Props {
    menus: MenuResp[]
}
const props = withDefaults(defineProps<Props>(), {
    menus: () => [],
})
//
const menuSelectTree = computed(() => {
    const menus: MenuResp[] = JSON.parse(JSON.stringify(props.menus))
    const data = filterTree(menus, (item) => [1, 2].includes(item.type))
    return mapTree(data, (item) => ({
        key: item.id,
        label: item.title,
        children: item.children,
    }))

})
const [form, resetForm] = useResetReactive({
    type: 1,
    sort: 999,
    isExternal: false,
    isCache: false,
    isHidden: false,
    status: 1,
})
const formRef = ref<FormInstance>()
const rules: FormInstance['rules'] = {
    parentId: [{ required: true, message: '请选择上级菜单', }],
    title: [{ required: true, message: '请输入菜单标题', }],
    path: [{ required: true, message: '请输入路由地址', }],
    name: [{ required: true, message: '请输入组件名称', }],
    component: [{ required: true, message: '请输入组件路径', }],
    permission: [{ required: true, message: '请输入权限标识', }],
}
const colProps: ColProps = { xs: 24, sm: 24, md: 12, lg: 12, xl: 12, xxl: 12 }
// eslint-disable-next-line vue/return-in-computed-property
const formRules = computed(() => {
    if ([1, 2].includes(form.type)) {
        const { title, name, path } = rules
        return { title, name, path } as FormInstance['rules']
    }
    if (form.type === 3) {
        const { parentId, title, permission } = rules
        return { parentId, title, permission } as FormInstance['rules']
    }
})


const { componentOptions } = useComponentPaths()
const componentName = computed(() => transformPathToName(form.path))
// 设置建议组件名
const inputComponentName = () => {
    form.name = componentName.value
}
const Add = (id?: string) => {
    reset()
    dataId.value = ''
    visible.value = true
}
// 修改
const onUpdate = async (id: string) => {
    reset()
    dataId.value = id
    const { data } = await getMenu(id)
    Object.assign(form, data)
    visible.value = true
}
const dataId = ref('')
const isUpdate = computed(() => !!dataId.value)
const title = computed(() => (isUpdate.value ? '修改菜单' : '新增菜单'))
const { width } = useWindowSize()

// 重置
const reset = () => {
    formRef.value?.resetFields()
    dataId.value = ''
    resetForm()
}
const Cancel = () => {
    visible.value = false
}
console.log(form.status)
defineExpose({
    Add, onUpdate
})
// 切换类型清除校验
const onChangeType = async () => {

    await formRef.value?.clearValidate()

}
// 保存
const save = async () => {
    try {
        const isInvalid = await formRef.value?.validate()
        if (!isInvalid) return false
        if (isUpdate.value) {
            await updateMenu(form, dataId.value)
            ElMessage.success('修改成功')
            visible.value = false
            formRef.value?.resetFields()
            emit('save-success')
        } else {
            await addMenu(form)
            ElMessage.success('新增成功')
        }
        emit('save-success')
        return true
    } catch (error) {
        return false
    }
}
const emit = defineEmits<{
    (e: 'save-success'): void
}>()

</script>

<template>
    <el-dialog v-model="visible" width="650">
        <template #header>
            <span style="font-size:1.2em;display:block;text-align: center;">{{ title }}</span>
            <el-divider></el-divider>
        </template>
        <template #default>
            <el-form ref="formRef" :model="form" :rules="formRules" :key="`form-${form.type}`">
                <el-form-item label="菜单类型" prop="type">
                    <el-radio-group v-model="form.type" @change="onChangeType">
                        <el-radio-button :value="1">目录</el-radio-button>
                        <el-radio-button :value="2">菜单</el-radio-button>
                        <el-radio-button :value="3">按钮</el-radio-button>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="上级菜单" prop="parentId">
                    <el-tree-select v-model="form.parentId" placeholder="请选择上级菜单" :data="menuSelectTree"
                        node-key="key" />
                </el-form-item>
                <el-row>
                    <el-col v-bind="colProps">
                        <el-form-item label="菜单标题" prop="title">
                            <el-input v-model="form.title" placeholder="请输入菜单标题" maxlength="30" show-word-limit />
                        </el-form-item>
                    </el-col>
                    <el-col v-bind="colProps">
                        <el-form-item v-if="[1, 2].includes(form.type)" label="菜单图标" label-width="80px" prop="icon">
                            <GiIconSelector v-model="form.icon" />
                        </el-form-item>
                        <el-form-item v-else label="权限标识" prop="permission">
                            <el-input v-model="form.permission" placeholder="system:user:add" show-word-limit
                                maxlength="100" />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col v-bind="colProps">
                        <el-form-item v-if="[1, 2].includes(form.type)" label="路由地址" prop="path">
                            <el-input v-model="form.path" placeholder="请输入路由地址" />
                        </el-form-item>
                    </el-col>
                    <el-col v-bind="colProps">
                        <el-form-item v-if="form.type === 1 || (form.type === 2 && !form.isExternal)" label="重定向"
                            prop="redirect" label-width="80px">
                            <el-input v-model="form.redirect" placeholder="请输入重定向地址" />
                        </el-form-item>
                        <el-form-item v-if="form.type === 2 && form.isExternal" label="组件路径" prop="component ">
                            <el-input v-model="form.component" placeholder="请输入组件路径" />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item v-if="form.type === 2 && !form.isExternal" label="组件路径" prop="component">
                    <el-select v-model="form.component" placeholder="请输入或选择组件路径" :options="componentOptions">

                    </el-select>
                </el-form-item>
                <el-row>
                    <el-col v-bind="colProps">
                        <el-form-item v-if="form.type === 1 || (form.type === 2 && !form.isExternal)" label="组件名称"
                            prop="name">
                            <el-input v-model="form.name" placeholder="请输入组件名称" />
                            <span v-if="componentName">建议组件名称:{{ componentName }}</span>
                        </el-form-item>
                    </el-col>
                    <el-col v-bind="colProps">
                        <el-form-item v-if="form.type === 2" label="权限标识" prop="permission">
                            <el-input v-model="form.permission" placeholder="system:user:add" show-word-limit />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row v-if="[1, 2].includes(form.type)" :gutter="16">
                    <el-col :xs="12" :sm="12" :md="8" :lg="8" :xl="8" :xxl="8">
                        <el-form-item label="是否隐藏" prop="hidden">
                            <el-switch v-model="form.isHidden" />
                        </el-form-item>
                    </el-col>
                    <el-col :xs="12" :sm="12" :md="8" :lg="8" :xl="8" :xxl="8">
                        <el-form-item label="是否缓存" prop="keepAlive">
                            <el-switch v-model="form.isCache" />
                        </el-form-item>
                    </el-col>
                    <el-col :xs="12" :sm="12" :md="8" :lg="8" :xl="8" :xxl="8">
                        <el-form-item v-if="form.type === 2" label="是否外链" prop="isExternalUrl">
                            <el-switch v-model="form.isExternal" />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item label="菜单排序" prop="sort">
                    <el-input-number v-model="form.sort" placeholder="请输入菜单排序" style="width: 150px" />
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
                </el-form-item>
            </el-form>
            <el-divider></el-divider>
        </template>
        <template #footer>
            <div class="dialog-footer">

                <el-button @click="Cancel">Cancel</el-button>
                <el-button type="primary" @click="save">
                    Confirm
                </el-button>
            </div>
        </template>
    </el-dialog>
</template>

<style lang="scss" scoped>
:deep(.el-radio-button.is-active .el-radio-button__original-radio:not(:disabled)+.el-radio-button__inner) {
    background-color: white;
    border-color: white;
    box-shadow: none;
    color: $color-theme
}

:deep(.el-radio-button__inner) {
    background: rgb(242, 243, 245);
    border: none !important;
    box-shadow: none;
    transform: scale(0.9);
}

.el-radio-group {
    // padding: 4px;
    background: rgb(242, 243, 245);
    box-sizing: border-box;

}

.el-divider--horizontal {
    margin: 12px 0 0;

}

.el-form-item {
    margin-right: 10px;
}
</style>