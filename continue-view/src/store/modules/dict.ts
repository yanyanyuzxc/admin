import { defineStore } from 'pinia'
import { ref } from 'vue'
const storeSetup = () => {
  const dictData = ref<Record<string, App.DictItem[]>>({})

  // 设置字典
  const setDict = (code: string, items: App.DictItem[]) => {
    if (code) {
      dictData.value[code] = items
    }
    }
    // 获取字典
    const getDict = (code: string) => {
        if(!code) return null
        return dictData.value[code] || null
    }

    //删除字典
    const deleteDict = (code: string) => {  
        if(!code || !(code in dictData.value)){
            return false
        }
        delete dictData.value[code]
        return true
    }

    // 清空字典
    const clearDict = () => {
        dictData.value = {}
    }

    return {
        setDict,
        getDict,
        deleteDict,
        clearDict
    }

}
export const useDictStore = defineStore('dict', storeSetup,{persist: true})