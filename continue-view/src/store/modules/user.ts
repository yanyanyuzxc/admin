import {defineStore} from 'pinia'
import { reactive,computed, ref } from 'vue'
import {getToken,clearToken,setToken} from '@/utils/auth'
import { resetHasRouteFlag } from '@/router/guard'
import { resetRouter } from '@/router'
import type { AccountLoginReq, PhoneLoginReq, UserInfo, EmailLoginReq } from '@/apis'
import {AuthTypeConstants,logout as logoutApi,accountLogin as accountLoginApi,
  phoneLogin as phoneLoginApi, emailLogin as emailLoginApi, getUserInfo as getUserInfoApi} from '@/apis'
import {useTenantStore} from '@/store/'

export const useUserStore = defineStore('user', ()=>{

const userInfo = reactive<UserInfo>({
  id: '',
  username: '',
  nickname: '',
  gender: 0,
  email: '',
  phone: '',
  avatar: '',
  pwdResetTime: '',
  pwdExpired: false,
  registrationDate: '',
  deptName: '',
  roles: [],
  roleNames: [],
  permissions: [],
})
const tenantStore = useTenantStore()
const nickname = computed(() => userInfo.nickname)
const username = computed(() => userInfo.username)
const avatar = computed(() => userInfo.avatar)
const token = ref(getToken() || '')
const pwdExpiredShow = ref<boolean>(true)
const roles = ref<string[]>([]) // 当前用户角色
const permissions = ref<string[]>([]) // 当前角色权限标识集合
//账号登录方法
const accountLogin = async(req: AccountLoginReq,tenantCode?: string) =>{
  const res = await accountLoginApi({ ...req, clientId: import.meta.env.VITE_CLIENT_ID, authType: AuthTypeConstants.ACCOUNT }, tenantCode)
  setToken(res.data.token)
  tenantStore.setTenantId(res.data.tenantId)
  token.value = res.data.token
}
//手机登录
const phoneLogin = async(req:PhoneLoginReq,tenantCode?: string) => {
  const res = await phoneLoginApi({...req, clientId: import.meta.env.VITE_CLIENT_ID, authType: AuthTypeConstants.PHONE }, tenantCode)
  setToken(res.data.token)
  tenantStore.setTenantId(res.data.tenantId)
  token.value = res.data.token
}
 // 邮箱登录
 const emailLogin = async (req: EmailLoginReq, tenantCode?: string) => {
  const res = await emailLoginApi({ ...req, clientId: import.meta.env.VITE_CLIENT_ID, authType: AuthTypeConstants.EMAIL }, tenantCode)
  setToken(res.data.token)
  tenantStore.setTenantId(res.data.tenantId)
  token.value = res.data.token
}
        // 重置token
  const resetToken = () => {
    token.value = ''
    clearToken()
    resetHasRouteFlag()
  }
 // 退出登录回调
 const logoutCallBack = async () => {
    roles.value = []
    permissions.value = []
    pwdExpiredShow.value = true
    resetToken()
    resetRouter()
    // tenantStore.resetTenantId()
  }

// 退出登录
const logout = async () => {
    try {
      await logoutApi()
      await logoutCallBack()
      return true
    } catch (error) {
      return false
    }
  }
  //获取用户的相关信息
  const getInfo = async () =>{
    const res = await getUserInfoApi()
    Object.assign(userInfo,res.data)
    userInfo.avatar = res.data.avatar
    if(res.data.roles && res.data.roles.length){
      roles.value = res.data.roles
      permissions.value = res.data.permissions
    }
  }
  return {
    logoutCallBack,
    accountLogin,
    phoneLogin,
    emailLogin,
    getInfo,
    userInfo,
    nickname,
    username,
    avatar,
    token,
    roles,
    permissions,
    pwdExpiredShow,
    logout
  }
}
,{persist: true})