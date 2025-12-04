import { defineStore } from "pinia";
import { reactive , toRefs,computed ,watch} from "vue";
import { getSettings } from '@/config/setting'
import { generate, getRgbStr } from '@arco-design/color'
import { type BasicConfig, listSiteOptionDict } from '@/apis/system'
const setupStore = () =>{
   // App配置
   const settingConfig = reactive({ ...getSettings() }) as App.AppSettings
    // 设置左侧菜单折叠状态
  const setMenuCollapse = (collapsed: boolean) => {
    settingConfig.menuCollapse = collapsed
  }
        // 深色菜单主题色变量
        const themeCSSVar = computed<Record<string, string>>(() => {
          const obj: Record<string, string> = {}
          const list = generate(settingConfig.themeColor, { list: true, dark: true }) as string[]
          list.forEach((color, index) => {
            obj[`--primary-${index + 1}`] = getRgbStr(color)
          })
          return obj
        })

    // 设置主题色
    const setThemeColor = (color: string) => {
      if (!color) return
      settingConfig.themeColor = color
      const list = generate(settingConfig.themeColor, { list: true, dark: settingConfig.theme === 'dark' }) as string[]
      list.forEach((color, index) => {
        let rgbStr = color
        if(!color.startsWith('rgb')){
           rgbStr = getRgbStr(color)
        }
        document.body.style.setProperty(`--primary-${index + 1}`, rgbStr)
      })
    }

    // const setThemeColor = (color:string) =>{
    //   if (!color) return
    //   settingConfig.themeColor = color
    //   //生成相应主题的颜色列表
    //   const colorList = generate(settingConfig.themeColor,{
    //     list:true,//以数组的形式返回颜色列表
    //     dark:settingConfig.theme === 'dark'//是否为暗黑主题
    //   })
    //   //设置颜色变量
    //   colorList.forEach((color:string,index:number)=>{
    //     const rgbStr = getRgbStr(color)//获取每个颜色的rgb值
    //     document.body.style.setProperty(`--primary-${index+1}`,rgbStr)
    //   })
    // }
  // 切换主题 暗黑模式|简白模式
 //改变主题色
  const toggleTheme = (dark: boolean) => {
    if (dark) {
      settingConfig.theme = 'dark'
      document.body.setAttribute('arco-theme', 'dark')
    } else {
      settingConfig.theme = 'light'
      document.body.removeAttribute('arco-theme')
    }
    setThemeColor(settingConfig.themeColor)
  }
    // 系统配置配置
    const siteConfig = reactive({}) as BasicConfig
  //修改系统配置
  const setSiteConfig  = (config:BasicConfig) =>{
   Object.assign(siteConfig,config)
   document.title = config.SITE_TITLE || ''
   document.querySelector('link[rel="shortcut icon"]')?.setAttribute('href', config.SITE_FAVICON || '/favicon.ico')
  }
  // const toggleTheme =(dark:boolean)=>{
  //   if(dark){
  //     settingConfig.theme = 'dark'
  //     document.body.setAttribute('arco-theme','dark')
  //   }
  //   else{
  //     settingConfig.theme = 'light'
  //     document.body.removeAttribute('arco-theme')
  //   }
  //   setThemeColor(settingConfig.themeColor)
  

  //   }
  // 监听 色弱模式 和 哀悼模式
watch([
  () => settingConfig.enableMourningMode,
  () => settingConfig.enableColorWeaknessMode,
], ([mourningMode, colorWeaknessMode]) => {
  const filters = [] as string[]
  if (mourningMode) {
    filters.push('grayscale(100%)')
  }
  if (colorWeaknessMode) {
    filters.push('invert(80%)')
  }
  // 如果没有任何滤镜条件，移除 `filter` 样式
  if (filters.length === 0) {
    document.documentElement.style.removeProperty('filter')
  } else {
    document.documentElement.style.setProperty('filter', filters.join(' '))
  }
}, {
  immediate: true,
})
    return {
        ...toRefs(settingConfig),
         setMenuCollapse,
         toggleTheme,
         setThemeColor,
         setSiteConfig,
    }
}
export const useAppStore = defineStore("app", setupStore,{persist: true});