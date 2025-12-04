//定义导入vue组件的类型
type ImportVueFileType = typeof import('*.vue')
type ImportVueFileFnType = () => Promise<ImportVueFileType>
type Recordable<T = any> = Record<string, T>


//定义导入的vue组件列表
const moduleFiles = import.meta.glob<ImportVueFileType>('@/view/**/*.vue')

//定义异步导入的vue组件列表函数

export const asyncRouteModules = Object.entries(moduleFiles).reduce(
    (routes,[url,importFn])=>{
     if(!/\/(view\/login|components)\//.test(url))
     {
        const path = url.replace('/src/view/','').replace('.vue','')
        routes[path] =  importFn
     }
        return routes
    },
{} as Record<string,ImportVueFileFnType>)