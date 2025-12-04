
import { values } from 'lodash'
import { browse, mapTree } from 'xe-utils'
import { isExternal } from '@/utils/validate'
import { camelCase, upperFirst } from 'lodash-es'
/**
  @desc 深拷贝 */
  export const deepClone = (data: any) => {
    if (typeof data !== 'object' || data == null) return '不是对象'
    const newData: any = Array.isArray(data) ? [] : {}
    for (const key in data) {
      newData[key] = typeof data[key] === 'object' ? deepClone(data[key]) : data[key]
    }
    return newData
  }
/** @desc 是否是h5环境 */
export const isMobile = () => {
  return browse().isMobile
}
  /**
 * @desc 过滤树
 * @param { values } 数组
 */
type FilterTree = <T extends { children?: T[] }>(
    array: T[],
    iterate: (item: T, index?: number, items?: T[]) => boolean
  ) => T[]
  export const filterTree: FilterTree = (values, fn) => {
    const arr = values.filter(fn)
    const data = mapTree(arr, (item) => {
      if (item.children && item.children.length) {
        item.children = item.children.filter(fn)
      }
      return item
    })
    return data
  }
  // * @description 动态路由 path 转 name
  // * @demo /system => System
  // * @demo /system/menu => SystemMenu
  // * @demo /data-manage/detail => DataManageDetail
  // */
export const transformPathToName = (path: string) => {
  if (!path) return ''
  if (isExternal(path)) return ''
  return upperFirst(camelCase(path))
}
//  export const transformPathToName = (path: string) =>{
//   if(!path) return ''
//   if(isExternal(path)) return ''
//   return upperFirst(camelCase(path))  //camelCase 转换成驼峰命名
//  }
/** @desc 问候 */
export function goodTimeText() {
  const time = new Date()
  const hour = time.getHours()
  return hour < 9 ? '早上好' : hour <= 11 ? '上午好' : hour <= 13 ? '中午好' : hour <= 18 ? '下午好' : '晚上好'
}

/** @desc 随机高饱和度颜色（HSL 模式，可控性更强） */
export const randomSaturatedColor = () => {
  const hue = Math.floor(Math.random() * 360); // 色相 0-360（全光谱）
  const saturation = 70 + Math.floor(Math.random() * 30); // 饱和度 70%-100%（鲜艳）
  const lightness = 40 + Math.floor(Math.random() * 20); // 亮度 40%-60%（避免过暗过亮）
  return `hsl(${hue}, ${saturation}%, ${lightness}%)`;
};
/** @desc 格式化文件大小 */
export const formatFileSize = (fileSize: number) => {
  if (fileSize == null || fileSize === 0) {
    return '0 Bytes'
  }
  const unitArr = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
  let index = 0
  const srcSize = Number.parseFloat(fileSize.toString())
  index = Math.floor(Math.log(srcSize) / Math.log(1024))
  const size = srcSize / 1024 ** index
  return `${size.toFixed(2)} ${unitArr[index]}`
}
export const fileToBase64 = (file: File): Promise<string> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => {
      if (reader.result) {
        resolve(reader.result.toString())
      } else {
        reject(new Error('文件转base64失败'))
      }
    }
    reader.onerror = (error) => {
      reject(error)
    }
    reader.readAsDataURL(file)
  })
}
