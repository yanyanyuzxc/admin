import { defineStore } from 'pinia'
import { type RouteLocationNormalized, type RouteRecordName, useRouter } from 'vue-router'
import _XEUtils_ from 'xe-utils'
import {ref,nextTick} from 'vue'
import type { TabPaneName } from 'element-plus'
import { useRouteStore } from '@/store/modules/route'
import path from 'path'
export const useTabStore = defineStore('tab', ()  => {
    const router = useRouter()
    const tabList = ref<RouteLocationNormalized[]>([]) // 保存页签tab的数组
    const cacheList = ref<RouteRecordName[]>([]) // keep-alive缓存的数组，元素是组件名

   //添加一个标签
    const addTabItem = (item: RouteLocationNormalized) => {
        const index = tabList.value.findIndex((i) => i.path === item.path)
        if (index >= 0) {
          tabList.value[index].fullPath !== item.fullPath && (tabList.value[index] = item)
        } else {
          if (item.meta?.showInTabs ?? true) {
            tabList.value.push(item)
          }
        }
      }
      const addCacheItem = (item: RouteLocationNormalized) => {
        if(!item.name) return
        if (cacheList.value.includes(item.name)) return // 已经存在
        if (item.meta?.keepAlive) {
            cacheList.value.push(item.name)
          }
        
      }
        // 重置
  const reset = () => {
    clearTabList()
    clearCacheList()
  }
      //进行初始化
      const init = () => {
        if (tabList.value.some((i) => !i?.meta.affix)) return
        reset()
      }
      //删除一个标签
      const deleteTabItem = (path: string) => {
        const index = tabList.value.findIndex((item) => item.path === path && !item.meta?.affix)
        if (index < 0) return
        // 如果是当前激活的标签，则激活上一个标签
        const isActive = router.currentRoute.value.path === tabList.value[index].path
        tabList.value.splice(index, 1)
        if (isActive) {
          const lastObj = tabList.value[tabList.value.length - 1]
          router.push(lastObj.fullPath || lastObj.path)
        }
      }
        // 删除一个缓存页
  const deleteCacheItem = (name: RouteRecordName) => {
    const index = cacheList.value.findIndex((i) => i === name)
    if (index >= 0) {
      cacheList.value.splice(index, 1)
    }
  }
       // 关闭当前
  const closeCurrent = (path: TabPaneName) => {
     path = path as string
    const item = tabList.value.find((i) => i.path === path)
    item?.name && deleteCacheItem(item.name)
    deleteTabItem(path)
  }
  //页面刷新
  const reloadFlag = ref(true)
  const reloadPage = () =>{
    const route = router.currentRoute.value
    deleteCacheItem(route.name as string) // 修复点击刷新图标，无法重新触发生命周期钩子函数问题
    reloadFlag.value = false
    nextTick(() => {
      reloadFlag.value = true
      addCacheItem(route)
    })
  }
    // 关闭左侧
   const closeLeft = (path: string) => {
    const index = tabList.value.findIndex((i)=> i.path === path)
    if (index < 0) return
    const leftTabs = tabList.value.slice(0, index).filter(item => !item.meta?.affix)
    leftTabs.forEach((item) => {
      deleteTabItem(item.path)
      item?.name && deleteCacheItem(item.name)
    })
   }
  
    // 关闭右侧
    const closeRight = (path: string) => {
      const index = tabList.value.findIndex((i) => i.path === path)
      if (index < 0) return
      const arr = tabList.value.filter((i, n) => n > index)
      arr.forEach((item) => {
        deleteTabItem(item.path)
        item?.name && deleteCacheItem(item.name)
      })
    }
      // 关闭其他
  const closeOther = (path: string) => {
    const arr = tabList.value.filter((i) => i.path !== path)
    arr.forEach((item) => {
      deleteTabItem(item.path)
      item?.name && deleteCacheItem(item.name)
    })
  }
  // 清空标签列表
   // 清空页签
   const clearTabList = () => {
    const routeStore = useRouteStore()
    const arr: RouteLocationNormalized[] = []
    _XEUtils_.eachTree(routeStore.routes, (item) => {
      if (item.meta?.affix ?? false) {
        arr.push(item as unknown as RouteLocationNormalized)
      }
    })
    console.log(arr)
    tabList.value = arr
  }
  // 清空缓存列表
  const clearCacheList = () => {
    cacheList.value = []
  }
    // 关闭全部
    const closeAll = () => {
      clearTabList()
      clearCacheList()
      router.push({ path: '/' })
    }
    return {
        tabList,
        cacheList,
        addTabItem,
        init,
        closeOther,
        closeAll,
        deleteTabItem,
        closeCurrent,
        addCacheItem,
        reset,
        reloadPage,
        closeLeft,
        closeRight
    }
},

{ persist: true })