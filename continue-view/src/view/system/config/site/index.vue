<script lang="ts" setup>
import { FolderOpened } from '@element-plus/icons-vue'
import {
    listOption,
    type SiteConfig,
    type OptionResp,
    updateOption,
    resetOptionValue
} from '@/apis/system'
import { fileToBase64 } from '@/utils'
import { ElMessage } from 'element-plus'
import { type FileItem, type FormInstance, Modal, type RequestOption } from '@arco-design/web-vue'
import { useResetReactive } from '@/hooks'
import { ElUpload } from 'element-plus';
import { useAppStore } from '@/store';
let appStore = useAppStore();
const [form] = useResetReactive({
    SITE_FAVICON: '',
    SITE_LOGO: '',
    SITE_TITLE: '',
    SITE_COPYRIGHT: '',
})
const rules = {
    SITE_NAME: [
        { required: true, message: '请输入站点名称', trigger: 'blur' }, { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
    ],
    SITE_TITLE: [
        { required: true, message: '请输入站点标题', trigger: 'blur' }, { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
    ],
    SITE_DESC: [
        { required: true, message: '请输入站点描述', trigger: 'blur' }, { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
    ],
    SITE_KEYWORDS: [
        { required: true, message: '请输入站点关键词', trigger: 'blur' }, { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
    ],
    SITE_LOGO: [
        { required: true, message: '请上传站点Logo', trigger: 'blur' }
    ],
    SITE_FAVICON: [
        { required: true, message: '请上传站点favicon', trigger: 'blur' }
    ],
}
const siteConfig = ref<SiteConfig>(
    {
        SITE_FAVICON: { id: '', name: '', code: 'SITE_FAVICON', value: '', description: '' },
        SITE_LOGO: { id: '', name: '', code: 'SITE_LOGO', value: '', description: '' },
        SITE_TITLE: { id: '', name: '', code: 'SITE_TITLE', value: '', description: '' },
        SITE_DESCRIPTION: { id: '', name: '', code: 'SITE_TITLE', value: '', description: '' },
        SITE_COPYRIGHT: { id: '', name: '', code: 'SITE_TITLE', value: '', description: '' },
        SITE_BEIAN: { id: '', name: '', code: 'SITE_TITLE', value: '', description: '' },
    }
)
const formRef = ref<any>()
const faviconFile = ref<FileItem>({ uid: '-1' })
const logoFile = ref<FileItem>({ uid: '-2' })
// 重置
const reset = () => {
    formRef.value?.resetFields()
    form.SITE_FAVICON = siteConfig.value?.SITE_FAVICON?.value || ''
    form.SITE_LOGO = siteConfig.value?.SITE_LOGO?.value || ''
    form.SITE_TITLE = siteConfig.value?.SITE_TITLE?.value || ''
    form.SITE_DESCRIPTION = siteConfig.value?.SITE_DESCRIPTION?.value || ''
    form.SITE_COPYRIGHT = siteConfig.value?.SITE_COPYRIGHT?.value || ''
    form.SITE_BEIAN = siteConfig.value?.SITE_BEIAN?.value || ''
    faviconFile.value.url = siteConfig.value?.SITE_FAVICON?.value
    logoFile.value.url = siteConfig.value?.SITE_LOGO?.value
}
const queryForm = reactive({
    category: 'SITE',
})
// 查询列表数据
const defaultSiteConfig: SiteConfig = {
    SITE_FAVICON: {} as OptionResp,
    SITE_LOGO: {} as OptionResp,
    SITE_TITLE: {} as OptionResp,
    SITE_DESCRIPTION: {} as OptionResp,
    SITE_COPYRIGHT: { id: '', name: '', code: 'SITE_COPYRIGHT', value: '', description: '' },
    SITE_BEIAN: { id: '', name: '', code: 'SITE_BEIAN', value: '', description: '' },
};
const getDataList = async () => {

    const { data } = await listOption(queryForm)

    siteConfig.value = data.reduce((obj: SiteConfig, option: OptionResp) => {
        obj[option.code] = { ...option }
        return obj
    }, { ...defaultSiteConfig })
    handleCancel()


}
const isUpdate = ref(false)
const handleCancel = () => {
    reset()
    isUpdate.value = false
}
//上传favicon
const handleUploadFavicon = (options: any) => {
    //为上传添加中断
    const controller = new AbortController();
    (async function requestWrap() {
        const { onProgress, onError, onSuccess, file, name = 'file' } = options
        onProgress(20)
        if (!file) {
            return
        }
        else {
            fileToBase64(file).then((res) => {
                onSuccess()//只是告诉组件上传成功，准备相应的样式处理，比如loading
                form.SITE_FAVICON = res
                faviconFile.value = {
                    uid: file.uid, // 用文件的唯一标识
                    url: res, // Base64 作为图片 URL
                    status: 'success', // 标记成功状态
                };

            })
                .catch((err) => {
                    onError(err)
                })
        }
    })()
    return {
        abort: () => {
            controller.abort()
        }
    }
}

// 恢复默认
const handleResetValue = async () => {
    await resetOptionValue(queryForm)
    ElMessage.success('恢复成功')
    await getDataList()
    appStore.setSiteConfig(form)
}
const handleUploadLogo = () => { }
const handleChangeLogo = () => { }
// 保存
const handleSave = async () => {
    const isInvalid = await formRef.value?.validate()
    type SiteConfigKey = keyof typeof siteConfig.value;
    if (!isInvalid) return false
    await updateOption(
        //entries 遍历对象，返回数组，数组的每一项是一个数组，包含两个元素，第一个元素是键，第二个元素是值,将键值对变为数组

        Object.entries(form).map(([key, value]: [SiteConfigKey, any]) => {
            return {
                id: siteConfig.value?.[key]?.id || '', // 可选链 + 空值兜底，双重保险
                code: key,
                value: value || ''
            };
        }),
    )
    appStore.setSiteConfig(form)
    await getDataList()
    ElMessage.success('保存成功')
}
onMounted(() => {
    getDataList()
})
</script>

<template>
    <el-form ref="formRef" :model="form" :rules="rules" layout="vertical" class="form">
        <el-form-item class="image-item">
            <template #default>
                <div class="content">
                    <span class="title">{{ siteConfig.SITE_LOGO.name || '站点Logo' }}</span><br />
                    <span class="description">{{ siteConfig.SITE_LOGO.description || '上传站点展示Logo' }}</span>
                    <el-upload :file-list="logoFile ? [logoFile] : []" accept="image/*" :show-file-list="false"
                        :http-request="handleUploadLogo" :on-change="handleChangeLogo">
                        <template #default>
                            <div :class="`arco-upload-list-item${logoFile && logoFile.status === 'error' ? ' arco-upload-list-item-error' : ''
                                }`">
                                <div v-if="logoFile && logoFile.url" class="logo">
                                    <img :src="logoFile.url" alt="Logo" />
                                    <div v-if="isUpdate" class="arco-upload-list-picture-mask logo">
                                        <el-icon :size="20">
                                            <Edit />
                                        </el-icon>
                                    </div>
                                </div>
                                <div v-else class="arco-upload-picture-card logo">
                                    <div class="arco-upload-picture-card-text">
                                        <GiSvgIcon name="upload"></GiSvgIcon>
                                    </div>
                                </div>
                            </div>
                        </template>
                    </el-upload>
                </div>
            </template>
        </el-form-item>
        <el-form-item class="image-item">

            <template #default>
                <div class="avatar">
                    <span class="title">{{ siteConfig.SITE_FAVICON.name }}</span><br />
                    <span class="description">{{ siteConfig.SITE_FAVICON.description }}
                    </span>
                    <el-upload :file-list="faviconFile ? [faviconFile] : []" accept="image/*" :show-file-list="false"
                        :http-request="handleUploadFavicon">
                        <template #trigger>
                            <div :class="`arco-upload-list-item${faviconFile && faviconFile.status === 'error' ? ' arco-upload-list-item-error' : ''
                                }`">
                                <div v-if="faviconFile && faviconFile.url" class="favicon">
                                    <img :src="faviconFile.url" alt="favicon" />
                                    <div v-if="isUpdate" class="arco-upload-list-picture-mask favicon">
                                        <IconEdit />
                                    </div>
                                </div>
                                <div v-else class="arco-upload-picture-card favicon">
                                    <div class="arco-upload-picture-card-text">
                                        <icon-upload />
                                    </div>
                                </div>
                            </div>
                        </template>
                    </el-upload>
                </div>
            </template>

        </el-form-item>
        <el-form-item>
            <el-space>
                <el-button icon="FolderOpened" @click="handleSave">
                    保存
                </el-button>
                <el-button icon="FolderOpened" @click="handleResetValue">
                    重置
                </el-button>
            </el-space>
        </el-form-item>
    </el-form>

</template>

<style lang="scss" scoped>
.el-form {
    padding: 20px;
}

.title {
    font-size: 18px;
}

.description {
    font-size: 13px;
    color: #7c7b7b;
}

.logo,
.favicon {
    width: 50px;
    height: 50px;

    img {
        width: 100%;
        height: 100%;
    }

}
</style>