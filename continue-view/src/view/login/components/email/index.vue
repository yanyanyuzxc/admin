<script lang="ts" setup>
import { useDevice } from '@/hooks/index.ts'
import { ref, reactive } from 'vue'
import { ElMessage, type FormItemRule } from 'element-plus'
import { getEmailCaptcha } from '@/apis'
import type { FormInstance, FormRules } from 'element-plus'
import * as Regexp from '@/utils/regexp.ts'
import { useUserStore, useTabStore } from '@/store'
import { useRouter } from 'vue-router'
let router = useRouter()
let tabStore = useTabStore()
let userStore = useUserStore()
const { isDesktop } = useDevice()
defineOptions({
    name: 'Email'
})
const form = reactive({
    email: '',
    captcha: '',
})
const formRef = ref<FormInstance>()
const tenantCode = ref()
const vaildateEmail = (rule: any, value: any, callback: any) => {
    if (!value) {
        callback('请输入邮箱')
    }
    if (!Regexp.Email.test(value)) {
        callback('请输入正确的邮箱')
    }
    else {
        callback()
    }

}
const rules = reactive<FormRules>({
    email: [
        { required: true, message: '请输入邮箱' },
        { validator: vaildateEmail, message: '请输入正确的邮箱' },
    ],
    captcha: [{ required: true, message: '请输入验证码' }],
})

const isShow = ref(false)
const loading = ref(false)
const captchaLoading = ref(false)
const captchaType = ref('login')
const captchaMode = ref('popup')
let btnContent = ref('获取验证码')
let captchaTime = ref(60)
let disabled = ref(false)
//获取验证码
const getCaptcha = async (captchaReq: any) => {
    disabled.value = true
    isShow.value = false
    captchaLoading.value = true
    try {
        // await getEmailCaptcha(form.email, captchaReq)
        let timeId = setTimeout(() => {
            ElMessage.success('仅提供效果演示，实际使用请查看代码取消相关注释')

        }, 1000)
        let timer = window.setInterval(() => {
            captchaTime.value -= 1
            if (captchaTime.value <= 0) {
                clearTimeout(timeId)
                clearInterval(timer)
                btnContent.value = '获取验证码'
                captchaTime.value = 60
            } else {
                btnContent.value = `${captchaTime.value}s后重试`
            }
        }, 1000)
    }
    catch (error) {
        return ElMessage.error('获取验证码失败')
    }
    finally {
        captchaLoading.value = false
    }
}
//触发登录
const handleLogin = async () => {
    const isvalid = await formRef.value?.validate()
    if (!isvalid) return
    try {
        let result = await userStore.emailLogin({ email: form.email, captcha: form.captcha }, tenantCode.value)
        tabStore.reset()
        let { redirect, ...otherQuerys } = router.currentRoute.value.query
        if (redirect) {
            redirect = decodeURIComponent(redirect as string)
            router.push({ path: redirect, query: { ...otherQuerys } })
        }
        else {
            router.push({ path: '/', query: { ...otherQuerys } })
        }
    } catch (error) {
        ElMessage.error('登录失败')
        console.log(error)
    }
}
//触发行为验证码
const onCaptchaClick = async () => {
    if (captchaLoading.value) return
    const isvalid = await formRef.value?.validateField('email')
    if (!isvalid) {
        ElMessage.error('请输入正确的邮箱')
    }
    isShow.value = true
}
</script>

<template>
    <div class="form">

        <p>邮箱登录</p>
        <el-form :model="form" :rules="rules" ref="formRef" v-loading="loading">
            <el-form-item size="large" v-if="isDesktop">
                <el-input placeholder="请输入用户编码(不输入为默认账号)" v-model="tenantCode"></el-input>
            </el-form-item>
            <el-form-item size="large" prop="email">
                <el-input placeholder="请输入账号" v-model="form.email"></el-input>
            </el-form-item>
            <el-form-item size="large" prop="captcha">
                <el-input placeholder="请输入验证码" v-model="form.captcha">
                    <template #append>
                        <el-button type="text" v-loading="captchaLoading" @click="onCaptchaClick"
                            :disabled="disabled">{{ btnContent
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
p {
    font-size: 20px;
    margin-bottom: 20px;
}

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