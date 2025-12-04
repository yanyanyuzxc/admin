import axios from 'axios';
import type { AxiosError, AxiosInstance, InternalAxiosRequestConfig} from 'axios'
import {getToken} from './auth'
import errorNotification  from './error-notification.ts';
import errorMessage from './error-message.ts'
import {ElMessage} from 'element-plus'
import errorMessagebox from './error-messagebox.ts';
import { useUserStore } from '@/store/modules/user.ts';
import { useTenantStore } from '@/store/modules/tenant.ts';
import type { AxiosRequestConfig, AxiosResponse } from 'axios';
import qs from 'query-string'
interface ICodeMessage {
    [propName: number]: string
  }
  
  const StatusCodeMessage: ICodeMessage = {
    200: '服务器成功返回请求的数据',
    201: '新建或修改数据成功。',
    202: '一个请求已经进入后台排队（异步任务）',
    204: '删除数据成功',
    400: '请求错误(400)',
    401: '未授权，请重新登录(401)',
    403: '拒绝访问(403)',
    404: '请求出错(404)',
    408: '请求超时(408)',
    500: '服务器错误(500)',
    501: '服务未实现(501)',
    502: '网络错误(502)',
    503: '服务不可用(503)',
    504: '网络超时(504)',
  }

  //错误信息的处理
const handleError = (err:string)=>
{
  if(err.length>15){
    return errorNotification({
        title: '错误',
        message:err,
        duration:5*1000
    })
  }
   return  errorMessage({
    title: '错误',
    message:err,
    duration:5*1000
   })
}


// 封装axios请求
const http:AxiosInstance= axios.create({
    baseURL:import.meta.env.VITE_API_PREFIX ?? import.meta.env.VITE_API_BASE_URL,
    timeout: 5000,
})

//添加请求拦截器
http.interceptors.request.use(
    (config:InternalAxiosRequestConfig) => {
      const token : string | null = getToken()
      if (!config.headers) {
        config.headers = config.headers || {};
      }
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }
      const tenantStore = useTenantStore()
      if (tenantStore.tenantEnabled && tenantStore.tenantId) {
        config.headers['X-Tenant-Id'] = tenantStore.tenantId
      }
      return config
    },
    (error) => Promise.reject(error),
  )

//添加响应拦截器
http.interceptors.response.use(
    (response:any) => {
        // 对响应数据做点什么
        const { data } = response
        const {success,code,msg} = data
        if(response.request.responseType === 'blob')
        {
          const contentType = data.type
          if(contentType.includes('application/json'))
          {
            const reader = new FileReader()
            reader.readAsText(data)
            reader.onload = () =>{
              const {success,msg} = JSON.parse(reader.result as string)
              if(!success)
              {
                handleError(msg)
                return Promise.reject(msg)
              }
            }
          }
          else{
            return response
          }
        }
        if(success)
    {
         return response
    }

    //token失效
    if(code === 401 && response.config.url !== '/auth/user/info')
    {
      errorMessagebox({
        message: msg,       // 错误内容
        title: '提示',      // 弹窗标题
        confirmButtonText: 'OK',  // 确认按钮文本
        cancelButtonText: 'Cancel', // 取消按钮文本
        type: 'error'       // 弹窗类型（错误）
      }).then(async () => {
        const userStore = useUserStore()
        await userStore.logoutCallBack()
        return true
      }).catch((error) => {
        // 用户点击“Cancel”或关闭弹窗后的逻辑
        ElMessage.error(`${error}`)
      });
    } else {
      handleError(msg)
    }
    return Promise.reject(new Error(msg || '服务器端错误'))
  },
  (error: AxiosError) => {
    if (!error.response) {
      handleError('网络连接失败，请检查您的网络')
      return Promise.reject(error)
    }
    const status = error.response?.status
    const errorMsg = StatusCodeMessage[status] || '服务器暂时未响应，请刷新页面并重试。若无法解决，请联系管理员'
    handleError(errorMsg)
    return Promise.reject(error)
  },
  
)

//定义数据放回的统一格式
const request = async <T = unknown>(config: InternalAxiosRequestConfig) : Promise<ApiRes<T>> => {
  return http.request<ApiRes<T>>(config)
  .then((response) => {return response.data})
  .catch((error) => {
    return Promise.reject(error)
  })
}

const createRequest = (method:string) => {
  return <T = any>(
    url: string,
    params?: object,
    config?: InternalAxiosRequestConfig
  ): Promise<ApiRes<T>> => {
    const paramKey = method.toLowerCase() === 'get' ? 'params' : 'data';
    const requestConfig = {
      method,
      url,
      [paramKey]: params,
      // 确保 headers 不为 undefined（默认空对象）
      // headers: {},
      ...(method.toLowerCase() === 'get'
        ? {
            paramsSerializer: (obj: any) => qs.stringify(obj),
          }
        : {}),
      // 合并用户传入的 config，若有 headers 会覆盖上面的默认空对象
      ...config,
    };

    return request(requestConfig as InternalAxiosRequestConfig);
  };
};

const requestNative = async <T = unknown>(config: AxiosRequestConfig): Promise<AxiosResponse> => {
  return http.request<T>(config)
    .then((res: AxiosResponse) => res)
    .catch((err: { msg: string }) => Promise.reject(err))
}
const download = (url: string, params?: object, config?: AxiosRequestConfig): Promise<AxiosResponse> => {
  return requestNative({
    method: 'get',
    url,
    responseType: 'blob',
    params,
    paramsSerializer: (obj) => qs.stringify(obj),
    ...config,
  })
}
export default{
  get:createRequest('get'),
  post:createRequest('post'),
  put:createRequest('put'),
  del:createRequest('delete'),
  patch:createRequest('patch'),
  download,
  requestNative,
}