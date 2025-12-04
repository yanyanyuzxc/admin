import type * as T from './type'
import http from '@/utils/http'
import { AxiosHeaders } from 'axios'; // 导入 AxiosHeaders 类export type * from './type'

const BASE_URL = '/auth'
const Api  = {
  LOGIN_URL : `${BASE_URL}/login` // 注意：模板字符串在枚举中需 TypeScript 4.1+ 支持
}
export function logout() {
    return 
  }
  
const login = (req: T.AccountLoginReq | T.PhoneLoginReq | T.EmailLoginReq, tenantCode?: string) => {
 // 使用 AxiosHeaders 构造函数创建符合类型的请求头
 const headers = new AxiosHeaders();
  if (tenantCode) {
    headers.set('X-Tenant-Code', tenantCode);
  }
  return http.post<T.LoginResp>(Api.LOGIN_URL, req, {
    headers,
  })
}

/** @desc 账号登录 */
export function accountLogin(req: T.AccountLoginReq, tenantCode?: string) {
  return login(req, tenantCode)
}

/** @desc 手机号登录 */
export function phoneLogin(req: T.PhoneLoginReq, tenantCode?: string) {
  return login(req, tenantCode)
}
/** @desc 邮箱登录 */
export function emailLogin(req: T.EmailLoginReq, tenantCode?: string) {
  return login(req, tenantCode)
}
/** @desc 三方账号登录授权 */
export function socialAuth(source: string) {
  return http.get<T.SocialAuthAuthorizeResp>(`${BASE_URL}/${source}`)
}
/** @desc 获取用户信息 */
export const getUserInfo = () => {
  return http.get<T.UserInfo>(`${BASE_URL}/user/info`)
}
/** @desc 获取路由信息 */
export const getUserRoute = () => {
  return http.get<T.RouteItem[]>(`${BASE_URL}/user/route`)
}