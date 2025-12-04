import {ref,toRefs} from 'vue'
import { listCommonDict } from '@/apis/system'
import { useDictStore } from '@/store'

//定义一个promies函数，防止重复请求
const pendingRequests = new Map<string, Promise<any>>()

export const useDict = (...codes: string[]) => {
    //实列化仓库
    const dictStore = useDictStore()
    //定义一个字典列表
    const dictData = ref<Record<string,App.DictItem[]>>({})

    //遍历codes，获取相应的字典列表
    codes.forEach(async(code: string) => {
        //如果字典列表已经存在，则直接返回
        dictData.value[code] = []
        const cached = dictStore.getDict(code)
        if (cached) {
            dictData.value[code] = cached
            return
        }
        if(!pendingRequests.has(code)){
            const request = listCommonDict(code)
            .then(({data})=>{
                // console.log(data)
                dictData.value[code] = data
                return data
            })
            .catch((error) => {
                console.error(error)
                return []
            })
            .finally(()=>{
                pendingRequests.delete(code)
            })
            pendingRequests.set(code, request)
        }
        pendingRequests.get(code)!.then((data) => {
            dictData.value[code] = data
        })
      
    })
    return toRefs(dictData.value)
}
