import { createRouter, createWebHistory } from 'vue-router'
import {constantRoutes,systemRoutes} from './route'
import { setupRouterGuard } from './guard'
let router = createRouter({
  history: createWebHistory(),
  routes: [...constantRoutes,...systemRoutes]
})
export function resetRouter() {
  // try {
  //   const routeStore = useRouteStore()
  //   routeStore.asyncRoutes.forEach((route) => {
  //     const { name } = route
  //     if (name) {
  //       router.hasRoute(name) && router.removeRoute(name)
  //     }
  //   })
  // } catch (error) {
  //   // 强制刷新浏览器也行，只是交互体验不是很好
  //   window.location.reload()
  // }
}
setupRouterGuard(router)
export default router