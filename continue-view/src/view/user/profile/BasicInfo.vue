<script lang="ts" setup>
import { useWindowSize } from '@vueuse/core'
import { type FileItem } from '@arco-design/web-vue'
import { useUserStore } from '@/store'
import { VueCropper } from 'vue-cropper'
import { uploadAvatar } from '@/apis/system'
import 'vue-cropper/dist/index.css'
import BasicInfoUpdateModal from './BasicInfoUpdateModal.vue'
import getAvatar from '@/utils/avatar.ts'
import { ElMessage } from 'element-plus'
const { width } = useWindowSize()
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)
const fileRef = ref<File | null>(null)
const avatar = {
    uid: '-2',
    name: 'avatar.png',
    url: userInfo.value.avatar,
}
const avatarList = ref<FileItem[]>([avatar])
const options = reactive({
    img: '',
    autoCrop: true,
    autoCropWidth: 160,
    autoCropHeight: 160,
    fixedBox: true,
    fixed: true,
    full: false,
    centerBox: true,
    canMove: true,
    outputSize: 1,
    outputType: 'png',
})
// 打开裁剪框
const visible = ref(false)
const onBeforeUpload = (file: File): boolean => {

    fileRef.value = file
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => {
        options.img = reader.result as string
    }
    visible.value = true
    return false
}


const reset = () => { }
const previews: any = ref({})
const previewStyle: any = ref({})
// 实时预览
const handleRealTime = (data: any) => {
    console.log(data)
    previewStyle.value = {
        width: `${data.w}px`,
        height: `${data.h}px`,
        overflow: 'hidden',
        margin: '0',
        zoom: 100 / data.h,
        borderRadius: '50%',
    }
    previews.value = data
}
const cropperRef = ref()
//上交头像
const handleUpload = async () => {
    cropperRef.value.getCropBlob((data: any) => {
        const formData = new FormData()
        formData.append('avatarFile', data, fileRef.value?.name)
        uploadAvatar(formData).then((res) => {
            userInfo.value.avatar = res.data.avatar
            avatarList.value[0].url = getAvatar(res.data.avatar, undefined)
            reset()
            ElMessage.success('更新成功')
        })
    })
    visible.value = false
}
// 修改基本信息
const BasicInfoUpdateModalRef = ref<InstanceType<typeof BasicInfoUpdateModal> | null>(null)
const onUpdate = async () => {
    BasicInfoUpdateModalRef.value?.onUpdate()
}
</script>

<template>
    <el-card>
        <template #header>
            <div class="title"><span>基本信息</span></div>
        </template>
        <template #default>
            <div class="message">
                <div class="avatar">
                    <el-upload :file-list="avatarList" accept="image/*" :show-file-list="false" list-type="picture-card"
                        :before-upload="onBeforeUpload">
                        <template #trigger>
                            <Avatar :src="avatarList[0].url" :name="userStore.nickname" :size="100" trigger>
                                <template #trigger-icon>
                                    <GiSvgIcon name="camera" size="20" />
                                </template>
                            </Avatar>
                        </template>
                    </el-upload>
                </div>
                <div class="userInfo">
                    <el-space>
                        <span class="name">{{ userInfo.nickname }}</span>

                        <GiSvgIcon name="man" size="15" v-if="userInfo.gender === 1"></GiSvgIcon>
                        <GiSvgIcon name="woman" size="15" v-else-if="userInfo.gender === 2"></GiSvgIcon>
                        <GiSvgIcon name="edit" size="15" @click="onUpdate"></GiSvgIcon>
                    </el-space>
                </div>
                <div class="id">
                    <GiSvgIcon name="id" :size="16" />
                    <span>{{ userInfo.id }}</span>
                </div>
            </div>
        </template>
        <template #footer>
            <div class="footer">
                <el-descriptions :column="4" size="large">
                    <el-descriptions-item :span="4">
                        <template #label>
                            <GiSvgIcon name="user" /><span style="margin-left: 5px">用户名</span>
                        </template>
                        {{ userInfo.username }}
                    </el-descriptions-item>
                    <el-descriptions-item :span="4">
                        <template #label>
                            <GiSvgIcon name="phone-color" /><span style="margin-left: 5px">手机</span>
                        </template>
                        {{ userInfo.phone || '暂无' }}
                    </el-descriptions-item>
                    <el-descriptions-item :span="4">
                        <template #label>
                            <GiSvgIcon name="email" /><span style="margin-left: 5px">邮箱</span>
                        </template>
                        {{ userInfo.email || '暂无' }}
                    </el-descriptions-item>
                    <el-descriptions-item :span="4">
                        <template #label>
                            <GiSvgIcon name="mind-mapping" /><span style="margin-left: 5px">部门</span>
                        </template>
                        {{ userInfo.deptName }}
                    </el-descriptions-item>
                    <el-descriptions-item :span="4">
                        <template #label>
                            <GiSvgIcon name="user-group" /><span style="margin-left: 5px">角色</span>
                        </template>
                        {{ userInfo.roleNames.join(' · ') }}
                    </el-descriptions-item>
                </el-descriptions>
            </div>
            <el-divider></el-divider>
            <div class="bottom">注册于 {{ userInfo.registrationDate }}</div>
        </template>

    </el-card>
    <el-dialog v-model="visible" title="上传头像" :width="width >= 400 ? 400 : '100%'" draggable @close="reset">
        <el-row>
            <el-col :span="14" style="width: 200px; height: 200px">
                <VueCropper mode="cover" ref="cropperRef" :img="options.img" :info="true" :auto-crop="options.autoCrop"
                    :auto-crop-width="options.autoCropWidth" :auto-crop-height="options.autoCropHeight"
                    :fixed-box="options.fixedBox" :fixed="options.fixed" :full="options.full"
                    :center-box="options.centerBox" :can-move="options.canMove" :output-type="options.outputType"
                    :output-size="options.outputSize" @real-time="handleRealTime" />
            </el-col>
            <el-col :span="10" style="display: flex; justify-content: center">
                <div :style="previewStyle">
                    <div :style="previews.div">
                        <img :src="previews.url" :style="previews.img" alt="" />
                    </div>
                </div>
            </el-col>
        </el-row>
        <div style="text-align: center; padding-top: 30px">
            <el-space>
                <el-button type="primary" @click="handleUpload">确定</el-button>
                <el-button @click="reset">取消</el-button>
            </el-space>
        </div>

    </el-dialog>
    <BasicInfoUpdateModal ref="BasicInfoUpdateModalRef" />
</template>

<style lang="scss" scoped>
:deep(.el-card__footer) {
    border: none;
}

:deep(.el-upload--picture-card) {
    background: var(--color-bg-1);
    border: none;
}

.el-card {
    height: 80vh;

    .title {
        font-size: 18px;
    }

    .message {
        @include flex-center;

        flex-direction: column;

        .userInfo {
            margin: 20px auto;

            .name {
                font-size: 1.7em;
            }
        }
    }
}

.bottom {
    width: 100%;
    @include flex-center;
}

.footer {
    padding: 10px;
}
</style>