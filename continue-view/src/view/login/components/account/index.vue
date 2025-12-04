<script lang="ts" setup>
import { useDevice } from '@/hooks/index.ts'
import { useStorage } from '@vueuse/core'
import type { FormInstance, FormRules } from 'element-plus/es/components/form'
import { ElMessage } from 'element-plus'
import { getImageCaptcha } from '@/apis/common'
import { reactive, ref, onBeforeUnmount, onMounted } from 'vue'
import { useUserStore } from '@/store'
import { useRouter } from 'vue-router'
import { useTabStore } from '@/store'

const { isDesktop } = useDevice()
let userStore = useUserStore()
// let tenantStore = useTenantStore()
let tabsStore = useTabStore()
const tenantCode = ref('')
const formRef = ref<FormInstance>()
import { encryptByRsa } from '@/utils/encrypt'
let router = useRouter()
defineOptions({
    name: 'Account'
})
//定义登录接口数据的类型
interface LoginConfig {
    rememberMe: boolean;
    username: string;
    password: string;
}
const loginConfig = useStorage<LoginConfig>('login-config', {
    rememberMe: true,
    username: 'admin', // 演示默认值
    password: 'admin123', // 演示默认值
    // username: debug ? 'admin' : '', // 演示默认值
    // password: debug ? 'admin123' : '', // 演示默认值
})

const form = reactive({
    username: loginConfig.value.username,
    password: loginConfig.value.password,
    captcha: '',
    uuid: '',
    expired: false,
})
// 验证码过期定时器
let timer: any = null
const startTimer = (expireTime: number, curTime = Date.now()) => {
    if (timer) {
        clearTimeout(timer)
    }
    const remainingTime = expireTime - curTime
    if (remainingTime <= 0) {
        form.expired = true
        return
    }
    timer = setTimeout(() => {
        form.expired = true
    }, remainingTime)
}
// 组件销毁时清理定时器
onBeforeUnmount(() => {
    if (timer) {
        clearTimeout(timer)
    }
})
// 是否启用验证码
const isCaptchaEnabled = ref(true)
// 验证码图片
const captchaImgBase64 = ref()
// 获取验证码
const getCaptcha = () => {
    getImageCaptcha().then((res) => {
        const { uuid, img, expireTime, isEnabled } = res.data
        isCaptchaEnabled.value = isEnabled
        captchaImgBase64.value = img
        form.uuid = uuid
        startTimer(expireTime, Number(res.data.timestamp))
    })
}
interface RuleForm {
    username: string;
    password: string;
    captcha: string;
}
//校验规则
const rules = reactive<FormRules<RuleForm>>({
    username: [
        { required: true, message: '请输入账号', trigger: 'blur' },

    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
    ],
    captcha: [
        { required: true, message: '请输入验证码', trigger: 'blur' },
        { min: 4, max: 4, message: '验证码长度为 4', trigger: 'blur' },
    ],
})
//提交登录
let loading = ref(false)
const handleLogin = async () => {
    try {
        const isInvalid = await formRef.value?.validate()
        if (!isInvalid) return
        loading.value = true
        await userStore.accountLogin({
            username: form.username,
            password: encryptByRsa(form.password) || '',//对密码进行加密
            captcha: form.captcha,
            uuid: form.uuid,
        }, tenantCode.value)
        // console.log(result)
        //如果路径中存在参数，跳转到参数对应的页面
        tabsStore.reset()
        const { redirect, ...othersQuery } = router.currentRoute.value.query
        const { rememberMe } = loginConfig.value
        loginConfig.value.username = rememberMe ? form.username : ''
        loginConfig.value.password = rememberMe ? form.password : ''
        if (redirect) {
            const redirectPath = decodeURIComponent(redirect as string)
            await router.push(redirectPath)
        } else {
            loading.value = false
            await router.push({
                path: '/',
                query: {
                    ...othersQuery,
                },
            })
        }
        ElMessage.success('欢迎使用')
    }
    catch (error: any) {
        ElMessage.error('请正确输入相关信息')
    }
    finally {
        loading.value = false
    }
}
onMounted(() => {
    getCaptcha()
})
</script>

<template>
    <div class="form" v-loading="loading">
        <el-form ref="formRef" :model="form" :rules="rules">
            <el-form-item size="large" v-if="isDesktop" v-model="tenantCode">
                <el-input placeholder="请输入用户编码(不输入为默认账号)"></el-input>
            </el-form-item>
            <el-form-item size="large" prop="username">
                <el-input v-model="form.username" placeholder="请输入账号"></el-input>
            </el-form-item>
            <el-form-item size="large" prop="password">
                <el-input v-model="form.password" type='password' placeholder="请输入密码" show-password></el-input>
            </el-form-item>
            <el-form-item size="large" prop="captcha">
                <el-input placeholder="请输入验证码" v-model="form.captcha">
                    <template #append>
                        <div @click="getCaptcha" class="captcha">
                            <img :src="captchaImgBase64" class="captcha" v-show="form.expired" />
                            <p v-show="!form.expired">已过期，请刷新</p>
                        </div>
                    </template>
                </el-input>
            </el-form-item>
        </el-form>
        <div class="remember">
            <el-checkbox label="记住我" value="Value 1" v-model="loginConfig.rememberMe" />
            <p style="margin-top:10px">忘记密码</p>
        </div>
        <div class="btn" @click="handleLogin">
            <button>登录</button>
        </div>
    </div>
</template>

<style lang="scss" scoped>
::v-deep .el-input-group__append,
::v-deep .el-input-group__prepend {
    padding: 0px;
    background: transparent;
    border: none;
    /* 生效 */
}

.remember {
    width: 100%;
    color: rgb(var(--primary-6));
    display: flex;
    justify-content: space-between;
}

p {
    height: 18px;
    line-height: 18px;

}

.captcha {
    width: 100%;
    height: 100%;
    max-height: 30px;
    min-width: 80px;
    display: flex;
    align-items: center;
    justify-content: center;

    img {
        height: 100%;
        width: 100%;
        object-fit: contain;
        flex-shrink: 1;
    }
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