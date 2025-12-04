import NProgress from 'nprogress'
import type {Router, RouteRecordRaw} from 'vue-router'
import {useUserStore} from '@/store/modules/user'
import { getToken } from '@/utils/auth'
import { ElMessage } from 'element-plus'
import { isHttp } from '@/utils/validate'
import {useRouteStore} from '@/store/modules/route'
import 'nprogress/nprogress.css'
NProgress.configure({
    easing: 'ease', // 动画方式
    speed: 500, // 递增进度条的速度
    showSpinner: false, // 是否显示圆圈加载
    trickleSpeed: 200, // 自动递增间隔
    minimum: 0.3, // 初始化时的最小百分比

  })
  let hasRouteFlag = false
export const resetHasRouteFlag = () => {
  hasRouteFlag = false
}
/** 免登录白名单 */
const whiteList = ['/login', '/social/callback', '/pwdExpired']
//初始化路由守卫
export const setupRouterGuard = (router: Router) => {
   router.beforeEach(async(to,from,next)=>{
      NProgress.start()
      const userStore = useUserStore()
      const routeStore =useRouteStore()
    
      if(getToken()){
        if(to.path === '/login')
        {
          next('/')
        }
        else{
         
          if (!hasRouteFlag) {
            try
            { 
              await userStore.getInfo()
              if (userStore.userInfo.pwdExpired && to.path !== '/pwdExpired') {
               ElMessage.warning('密码已过期，请修改密码')
               next('/pwdExpired')
              }
            const accessRoutes = await routeStore.generateRoutes()
            accessRoutes.forEach((route) => {
              if (!isHttp(route.path)) {
                router.addRoute(route as RouteRecordRaw)
              }
            })
            hasRouteFlag = true // 标记为已添加
            next({ ...to, replace: true }) // 重新触发一次导航
            }catch(error){
              await userStore.logoutCallBack()
              next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
            }
          }
           else {
            next() // 已添加过路由，直接放行
          }
        }
        
      }
      //没有token
      else{
         if(whiteList.includes(to.path))  {
          next()
         }
         else{
          next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
         }
    
      }
   })
   router.onError(() => {
    NProgress.done()
  })

  router.afterEach(() => {
    NProgress.done()
  })
}