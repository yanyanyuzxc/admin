<script lang="ts" setup>
import router from '@/router'
import { useWindowSize } from '@vueuse/core'
import { ElMessage } from 'element-plus'
import NProgress from 'nprogress'
import { encryptByRsa } from '@/utils/encrypt'
import { useUserStore } from '@/store'
import { type ColumnItem, GiForm } from '@/components/GiForm'
import { useResetReactive } from '@/hooks'
import * as Regexp from '@/utils/regexp'
import { type BehaviorCaptchaReq, getEmailCaptcha, getSmsCaptcha, updateUserEmail, updateUserPassword, updateUserPhone } from '@/apis'
import { ElMessageBox } from 'element-plus'
const { width } = useWindowSize()
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)
const visible = ref(false)
const verifyType = ref()
const title = computed(
  () => `修改${verifyType.value === 'phone' ? '手机号' : verifyType.value === 'email' ? '邮箱' : '密码'}`,
)
const formRef = ref<InstanceType<typeof GiForm>>()

const [form, resetForm] = useResetReactive({
  phone: '',
  email: '',
  captcha: '',
  oldPassword: '',
  newPassword: '',
  rePassword: '',
})
const columns: ColumnItem[] = reactive([
  {
    label: '手机号',
    field: 'phone',
    type: 'input',
    span: 24,
    props: {
      showWordLimit: false,
    },
    rules: [
      { required: true, message: '请输入手机号' },
      { match: Regexp.Phone, message: '请输入正确的手机号' },
    ],
    hide: () => {
      return verifyType.value !== 'phone'
    },
  },
  {
    label: '邮箱',
    field: 'email',
    type: 'input',
    span: 24,
    rules: [
      { required: true, message: '请输入邮箱' },
      { match: Regexp.Email, message: '请输入正确的邮箱' },
    ],
    hide: () => {
      return verifyType.value !== 'email'
    },
  },
  {
    label: '验证码',
    field: 'captcha',
    type: 'input',
    span: 24,
    rules: [{ required: true, message: '请输入验证码' }],
    hide: () => {
      return !['phone', 'email'].includes(verifyType.value)
    },
  },
  {
    label: '当前密码',
    field: 'oldPassword',
    type: 'input',
    span: 24,
    rules: [{ required: true, message: '请输入当前密码' }],
    hide: () => {
      return !userInfo.value.pwdResetTime
    },
  },
  {
    label: '新密码',
    field: 'newPassword',
    type: 'input',
    span: 24,
    rules: [
      { required: true, message: '请输入新密码' },
      {
        validator: (rule: any, value: any, callback: any) => {
          if (value === form.oldPassword) {
            callback('新密码不能与当前密码相同')
          } else {
            callback()
          }
        },
      },
    ],
    hide: () => {
      return verifyType.value !== 'password'
    },
  },
  {
    label: '确认新密码',
    field: 'rePassword',
    type: 'input',
    span: 24,
    props: {
      placeholder: '请再次输入新密码',
    },
    rules: [
      { required: true, message: '请再次输入新密码' },
      {
        validator: (rule: any, value: any, callback: any) => {
          if (value !== form.newPassword) {
            callback('两次输入的密码不一致')
          } else {
            callback()
          }
        },
      },
    ],
    hide: () => {
      return verifyType.value !== 'password'
    },
  },
])
const VerifyRef = ref<InstanceType<any>>()
const captchaType = ref('blockPuzzle')
const captchaMode = ref('pop')
const captchaLoading = ref(false)
const captchaTimer = ref()
const captchaTime = ref(60)
const captchaBtnName = ref('获取验证码')
const captchaDisable = ref(false)
const onCaptcha = async () => {
  if (captchaLoading.value) return
  const isInvalid = formRef.value?.formRef?.validateField(verifyType.value === 'phone' ? 'phone' : 'email')
  if (!isInvalid) ElMessage.error('请正确填写信息')
  // 重置行为参数
  VerifyRef.value.refresh()
  VerifyRef.value.show()
}
// 获取验证码
const getCaptcha = async (captchaReq: BehaviorCaptchaReq) => {

  // 发送验证码
  try {
    VerifyRef.value?.hide()
    captchaLoading.value = true
    captchaBtnName.value = '发送中...'
    if (verifyType.value === 'phone') {
      await getSmsCaptcha(form.phone, captchaReq)
    } else if (verifyType.value === 'email') {
      await getEmailCaptcha(form.email, captchaReq)
    }
    captchaLoading.value = false
    captchaDisable.value = true
    captchaBtnName.value = `获取验证码(${(captchaTime.value -= 1)}s)`
    ElMessage.success('发送成功')
    // Message.success('仅提供效果演示，实际使用请查看代码取消相关注释')
    captchaTimer.value = window.setInterval(() => {
      captchaTime.value -= 1
      captchaBtnName.value = `获取验证码(${captchaTime.value}s)`
      if (captchaTime.value <= 0) {
        resetCaptcha()
      }
    }, 1000)
  } catch (error) {
    resetCaptcha()
  } finally {
    captchaLoading.value = false
  }
}

// 重置验证码
const resetCaptcha = () => {
  window.clearInterval(captchaTimer.value)
  captchaTime.value = 60
  captchaBtnName.value = '获取验证码'
  captchaDisable.value = false
}
// 重置
const reset = () => {
  formRef.value?.formRef?.resetFields()
  resetForm()
  resetCaptcha()
}
// 打开弹框
const open = (type: string) => {
  verifyType.value = type
  visible.value = true
}
// 保存
const save = async () => {
  console.log(encryptByRsa(form.oldPassword))
  try {
    const isInvalid = formRef.value?.formRef?.validate()
    if (!isInvalid) return
    if (verifyType.value === 'phone') {
      await updateUserPhone({
        phone: form.phone,
        captcha: form.captcha,
        oldPassword: encryptByRsa(form.oldPassword) as string,
      })
      ElMessage.success('修改成功')
    } else if (verifyType.value === 'email') {

      await updateUserEmail({
        email: form.email,
        captcha: form.captcha,
        oldPassword: encryptByRsa(form.oldPassword) as string,
      })

      ElMessage.success('修改成功')
    } else if (verifyType.value === 'password') {
      if (form.newPassword !== form.rePassword) {
        ElMessage.error('两次输入的密码不一致')
        return false
      }
      if (form.oldPassword === form.newPassword) {
        ElMessage.error('新密码不能与当前密码相同')
        return false
      }
      await updateUserPassword({
        oldPassword: encryptByRsa(form.oldPassword) as string || '',
        newPassword: encryptByRsa(form.newPassword) as string || '',
      })

      ElMessage.success('修改成功')
      ElMessageBox.confirm(
        '密码修改成功，是否退出登录？',
        '提示',
        {
          confirmButtonText: '确认',

          type: 'warning',
        },
      ).then(async () => {
        userStore.logout()
        await router.replace('/login')
      })
    }
    visible.value = false
  }
  catch (error) {
    ElMessage.error('保存失败')
  }
}

defineExpose({ open })
</script>

<template>
  <el-dialog v-model="visible" :title="title" :mask-closable="false" :esc-to-close="false"
    :width="width >= 500 ? 500 : '100%'" draggable>
    <GiForm ref="formRef" v-model="form" :columns="columns">
      <template #captcha>
        <el-space>
          <el-input v-model="form.captcha" placeholder="请输入验证码" style="width: 260px" />
          <el-button class="captcha-btn" :loading="captchaLoading" :disabled="captchaDisable" @click="onCaptcha">
            {{ captchaBtnName }}
          </el-button>
        </el-space>
      </template>
    </GiForm>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false">Cancel</el-button>
        <el-button type="primary" @click="save">
          Confirm
        </el-button>
      </div>
    </template>
    <Verify ref="VerifyRef" :captcha-type="captchaType" :mode="captchaMode"
      :img-size="{ width: '330px', height: '155px' }" @success="getCaptcha" />
  </el-dialog>
</template>

<style lang="scss" scoped></style>