<script lang="ts" setup>
import { ArrowLeft } from '@element-plus/icons-vue'
import { useTabStore } from '@/store'
import AiEditor from './components/index.vue'
import { useResetReactive } from '@/hooks'
import { type ColumnItem, GiForm } from '@/components/GiForm'
import { useDict } from '@/hooks/app'
import type { LabelValueState } from '@/type/global'
import { listUserDict } from '@/apis/system'
import { addNotice, getNotice, updateNotice } from '@/apis/system/notice'
import { ElMessage } from 'element-plus'
let router = useRouter()
let route = useRoute()
let tabStore = useTabStore()
const [form, resetForm] = useResetReactive({
    title: '',
    type: '',
    content: '',
    noticeScope: 1,
    noticeMethods: [1],
    isTiming: false,
    publishTime: undefined,
    isTop: false,
    status: 1,
})
const userList = ref<LabelValueState[]>([])
const onOpenSelectUser = () => { }
const { notice_type, notice_scope_enum, notice_method_enum } = useDict('notice_type', 'notice_scope_enum', 'notice_method_enum')
const columns: ColumnItem[] = reactive([
    {
        label: '标题',
        field: 'title',
        type: 'input',
        span: 24,
        props: {
            maxLength: 150,
            showWordLimit: true,
        },
        rules: [{ required: true, message: '请输入标题' }],
    },
    {
        label: '分类',
        field: 'type',
        type: 'select',

        props: {
            options: notice_type,
        },
        rules: [{ required: true, message: '请选择分类' }],
    },
    {
        label: '通知范围',
        field: 'noticeScope',
        type: 'radio-group',
        disabled: () => {
            return form.status === 3
        },
        props: {
            options: notice_scope_enum,
        },
        rules: [{ required: true, message: '请选择通知范围' }],
    },
    {
        label: '指定用户',
        field: 'noticeUsers',
        type: 'select',
        hide: () => {
            return form.noticeScope === 1
        },
        rules: [{ required: true, message: '请选择指定用户' }],
    },
    {
        label: '通知方式',
        field: 'noticeMethods',
        type: 'checkbox',
        disabled: () => {
            return form.status === 3
        },
    },
    {
        label: '定时发布',
        field: 'isTiming',
        type: 'switch',
        disabled: () => {
            return form.status === 3
        },
        props: {
            type: 'round',
            checkedValue: true,
            uncheckedValue: false,
            checkedText: '是',
            uncheckedText: '否',
        },
    },
    {
        label: '发布时间',
        field: 'publishTime',
        type: 'date-picker',
        hide: () => {
            return !form.isTiming
        },
        required: true,
        props: {
            showTime: true,
            placeholder: '请选择发布时间',
        },
    },
    {
        label: '置顶',
        field: 'isTop',
        type: 'switch',
        props: {
            type: 'round',
            checkedValue: true,
            uncheckedValue: false,
            checkedText: '是',
            uncheckedText: '否',
        },
    },
])
const onBack = () => {
    tabStore.closeCurrent(route.path)
    router.push('/system/notice')
}
const { id, type } = route.query
const isUpdate = computed(() => type === 'update')
onMounted(async () => {
    // 当id存在与type为update时，执行修改操作
    if (id && isUpdate.value) {
        await onUpdate(id as string)
    }
    // 获取所有用户
    const { data } = await listUserDict()
    userList.value = data.map((item) => ({ ...item, value: `${item.value}` }))

})
const formRef = ref<InstanceType<typeof GiForm>>()
// 修改
const onUpdate = async (id: string) => {
    resetForm()
    const res = await getNotice(id)
    Object.assign(form, res.data)
}
const save = async (isDraft: boolean) => {
    const isInvalid = await formRef.value?.formRef.validate()
    if (!isInvalid) ElMessage.error('请检查表单内容')
    try {
        form.noticeUsers = form.noticeScope === 1 ? [] : form.noticeUsers
        console.log(form.noticeMethods)
        form.status = isDraft ? 1 : 3
        if (isUpdate.value) {
            await updateNotice(form, id as string)
            ElMessage.success('修改成功')
        } else {
            await addNotice(form)
            ElMessage.success('新增成功')
        }
        onBack()
        return true
    } catch (error: any) {
        ElMessage.error(error)
    }
}
</script>

<template>
    <div class="container">
        <div class="header">
            <el-page-header :icon="ArrowLeft" title="通知公告" @back="onBack">
                <template #content>
                    <span> 新增 </span>
                </template>
                <template #extra>
                    <el-space>
                        <el-button @click="onBack" icon="Close">

                            <template #default>取消</template>
                        </el-button>
                        <el-button v-if="!isUpdate || (isUpdate && form.status !== 3)" type="primary"
                            @click="save(true)">

                            <template #default>
                                <GiSvgIcon name="save" size="15"></GiSvgIcon>草稿
                            </template>
                        </el-button>
                        <el-button type="primary" @click="save(false)">

                            <template #default>
                                <GiSvgIcon name="send" size="15"></GiSvgIcon>{{ isUpdate && form.status === 3 ? '保存' :
                                    '发布'
                                }}
                            </template>
                        </el-button>
                    </el-space>
                </template>
            </el-page-header>
        </div>

        <div class="content">
            <GiForm ref="formRef" v-model="form" :columns="columns">
                <template #noticeUsers>
                    <el-space>
                        <el-select v-model="form.noticeUsers" :options="userList" style="width:200px" />
                        <el-tooltip content="选择用户">
                            <el-button @click="onOpenSelectUser" icon="Plus">

                            </el-button>
                        </el-tooltip>
                    </el-space>
                </template>
                <template #noticeMethods>
                    <el-checkbox-group v-model="form.noticeMethods" :options="notice_method_enum" />
                </template>
            </GiForm>
            <div style="flex: 1;">
                <AiEditor v-model="form.content" />
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
.container {
    display: flex;
    flex-direction: column;
    height: 100%;

}

.header {
    background-color: white;
    height: 5%;
    padding: 20px;
    margin-top: -13px;
    margin-left: -14px;
    margin-right: -15px;

    span {
        font-size: 16px;
    }
}

.content {
    margin-top: 20px;
    padding: 10px;
    background: white;
}

:deep(.el-radio-group) {
    background-color: white !important;
}

.el-radio {
    background-color: white !important;
}
</style>