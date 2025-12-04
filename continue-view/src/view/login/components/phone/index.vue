<script lang="ts" setup>
import { useDevice } from '@/hooks/index.ts'
import { ref, reactive } from 'vue'
import { ElMessage, type FormInstance, type FormItemRule } from 'element-plus'
import { useUserStore } from '@/store'
import { useTabStore } from '@/store'
import { useRouter } from 'vue-router'
import type { BehaviorCaptchaReq, } from '@/apis'
import { getSmsCaptcha } from '@/apis'
import { set } from 'lodash'
let router = useRouter()
let userStore = useUserStore()
let tabStore = useTabStore()
const { isDesktop } = useDevice()
defineOptions({
    name: 'Phone'
})
let isShow = ref(false)
let loading = ref(false)
const handleLogin = async () => {
    let isvalid = await formRef.value?.validate()
    if (!isvalid) {
        ElMessage.error('登录失败')
    }
    try {
        //开启loading
        loading.value = true
        //处理数据
        let result = await userStore.phoneLogin(form, tenantCode.value)
        // console.log(result)
        tabStore.reset()
        const { redirect, ...othersQuery } = router.currentRoute.value.query
        if (redirect) {
            const redirectPath = decodeURIComponent(redirect as string)//解码
            router.push(redirectPath)
        }
        else {
            await router.push({
                path: '/',
                query: {
                    ...othersQuery,
                },
            })
        }
        ElMessage.success('欢迎使用')
    } catch (error: any) {
        ElMessage.error((error.message) as string)
        form.captcha = ''
    } finally {
        //关闭loading
        loading.value = false
    }

}
const captchaMode = ref('pop')
const captchaType = ref('blockPuzzle')
const formRef = ref<FormInstance | null>()
const form = reactive({
    phone: '',
    captcha: '',
})
const tenantCode = ref()

//表单验证
const rules: Partial<Record<string, FormItemRule[]>> = {
    phone: [
        // required: true 时，指定 type: 'string'（手机号是字符串）
        { required: true, type: 'string', message: '手机号不能为空', trigger: 'blur' },
        // 格式校验（如正则），也需指定 type: 'string'
        {
            type: 'string',
            pattern: /^1[3-9]\d{9}$/, // 手机号正则
            message: '请输入正确的手机号',
            trigger: 'blur'
        }
    ],
    captcha: [
        // 验证码通常是字符串（如4位数字），指定 type: 'string'
        { required: true, type: 'string', message: '验证码不能为空', trigger: 'blur' },
        // 限制验证码长度（可选）
        { type: 'string', min: 4, max: 4, message: '验证码长度为4位', trigger: 'blur' }
    ],
    tenantCode: [
        // 租户编码通常是字符串，补全 type: 'string'
        { required: false, type: 'string', message: '租户编码不能为空', trigger: 'blur' }
    ]
}
let captchaLoading = ref(false)
const buttonContent = ref('获取验证码')
const captchaTimer = ref()
let captchaTime = ref(59)
const captchaDisable = ref(false)
// 重置验证码
const resetCaptcha = () => {
    window.clearInterval(captchaTimer.value)
    captchaTime.value = 59
    buttonContent.value = '获取验证码'
    captchaDisable.value = false
}
//检验人类
const onCaptcha = async () => {
    if (captchaLoading.value) return
    const isvalid = await formRef.value?.validateField('phone')
    if (!isvalid) {
        ElMessage.error('请输入正确的手机号')
        return
    }
    // captchaLoading.value = true
    isShow.value = true
}
const disabled = ref(true)
let captchaTimeId = ref()
const getCaptcha = async (captchaReq: any) => {
    isShow.value = false
    captchaLoading.value = true
    try {
        // const result = await getSmsCaptcha(form.phone, captchaReq)
        disabled.value = false
        captchaTimeId.value = setTimeout(() => {
            ElMessage.success('仅用于测试,详情见以后')
            captchaLoading.value = false
            buttonContent.value = `${captchaTime.value}秒后重试`
        }, 500)
        captchaTimer.value = setInterval(() => {
            captchaTime.value -= 1
            if (captchaTime.value <= 0) {
                clearInterval(captchaTimer.value)
                clearTimeout(captchaTimeId.value)
                resetCaptcha()
            } else {
                buttonContent.value = `${captchaTime.value}s后重试`
            }
        }, 1000)
        // console.log(captchaLoading.value)
    } catch (error: any) {
        console.log(error)
        resetCaptcha()
    }
    finally {
        // captchaLoading.value = false
    }

}

</script>

<template>
    <div class="form" v-loading.fullscreen.lock="loading">
        <el-form ref="formRef" :model="form" :rules="rules">
            <el-form-item size="large" v-if="isDesktop" prop="tenantCode">
                <el-input placeholder="请输入用户编码(不输入为默认账号)" v-model="tenantCode"></el-input>
            </el-form-item>
            <el-form-item size="large" prop="phone">
                <el-input placeholder="请输入手机号" v-model="form.phone"></el-input>
            </el-form-item>
            <el-form-item size="large" prop="captcha">
                <el-input placeholder="请输入验证码" v-model="form.captcha" :disabled="disabled">
                    <template #append>
                        <el-button type="text" @click="onCaptcha" v-loading="captchaLoading" :disabled="!disabled">{{
                            buttonContent
                            }}</el-button>
                    </template>
                </el-input>
            </el-form-item>
        </el-form>
        <div class="btn" @click="handleLogin">
            <button>登录</button>
        </div>
        <Verify @close="isShow = false" v-show="isShow" :captcha-type="captchaType" :mode="captchaMode"
            :img-size="{ width: '330px', height: '155px' }" @success="getCaptcha"></Verify>
    </div>
</template>

<style lang="scss" scoped>
.btn {
    width: 100%;

    margin: 10px auto;

    button {
        background: rgb(var(--primary-6));
        width: 100%;
        height: 40px;
        color: white;
        border-radius: 7px;
        border: none;
    }
}
</style>