import { defineStore } from 'pinia'
import { getUserRoute } from '@/apis'
import { type RouteItem } from '@/apis'
import { mapTree, toTreeArray } from 'xe-utils'
import { cloneDeep, omit } from 'lodash-es'
import { ref } from 'vue'
import type { RouteRecordRaw } from 'vue-router'
import { transformPathToName } from '@/utils/index.ts'
import { asyncRouteModules } from '@/router/asyncModules.ts'
import { constantRoutes, systemRoutes } from '@/router/route'
// let storeSetup = [...constantRoutes, ...systemRoutes]
//判断路由层级是否大于2
export const isMultipRoute =(route:RouteRecordRaw) =>{
  if(route.children && route.children.length > 0){
    return route.children.some((item)=>item.children && item.children.length > 0)?? false
  }
}
//吧三级路由集以上降级为二级路由
export const  flatMultiLevelRoutes =(routes:RouteRecordRaw[]) =>{
  return cloneDeep(routes.map((item)=>{
    if(!isMultipRoute(item)){
      return item
    }
    return {
      ...item,
      children:toTreeArray(item.children).map((item)=>omit(item,['children']))
    }
  }))
}
//配置基础
const layoutComponentMap = {
  Layout: () => import('@/layout/index.vue'),
  ParentView: () => import('@/components/ParentView/index.vue'),
}

/** 将component由字符串转成真正的模块 */
const transformComponentView = (component: string) => {
  return layoutComponentMap[component as keyof typeof layoutComponentMap] || asyncRouteModules[component]
}
/**
 * @description 前端来做排序、格式化
 * @params {menus} 后端返回的路由数据，已经根据当前用户角色过滤掉了没权限的路由
 * 1. 对后端返回的路由数据进行排序，格式化
 * 2. 同时将component由字符串转成真正的模块
 */
const formatAsyncRoutes =(menus: RouteItem[]) =>{
  //判断路由是否为空
  if(!menus || menus.length === 0){return}
  //对后端返回的路由数据进行排序
  const pathMap = new Map()
  return mapTree(menus, (item) => {
    pathMap.set(item.path, item.path)
    if(item.children && item.children.length > 0)
    {
      item.children.sort((a, b) => (a?.sort ?? 0) - (b?.sort ?? 0))

    }
    // 部分子菜单，例如：通知公告新增、查看详情，需要选中其父菜单
  if (item.parentId && item.type === 2 && item.permission) {
      item.activeMenu = pathMap.get(item.parentId)
    }
    return {
      path: item.path,
      name: item.name ?? transformPathToName(item.path),//将路径转化为name
      component: transformComponentView(item.component),
      redirect: item.redirect,
      meta: {
        title: item.title,
        hidden: item.isHidden,
        keepAlive: item.isCache,
        icon: item.icon,
        showInTabs: item.showInTabs,
        activeMenu: item.activeMenu,
      },
    } 
  }) as unknown as RouteRecordRaw[]
  
}
export const useRouteStore = defineStore('route', ()  => {
      const route = ref<RouteRecordRaw[]>([])
      const asyncRoutes = ref<RouteRecordRaw[]>([])
      //合并路由
      const setRoutes = (routes: RouteRecordRaw[]) => {
        route.value = [...constantRoutes, ...systemRoutes, ...routes].sort((a,b)=>(a.meta?.sort??0) - (b.meta?.sort??0))
        asyncRoutes.value = routes
      }

      //产生路由
      const generateRoutes = async () =>{
        //获取后端路由数据
          const {data} = await getUserRoute()
          // 格式化路由
          const asyncRoute = formatAsyncRoutes(data)
          //降级为二级路由
          const cloneRoutes = cloneDeep(asyncRoute)
          const flatRoutes = flatMultiLevelRoutes(cloneRoutes as RouteRecordRaw[])
          //合并路由
          setRoutes(asyncRoute as RouteRecordRaw[])
          return flatRoutes
      }
      return { 
          routes: route ,
          generateRoutes}
}, 
{ persist: true })
