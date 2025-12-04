import { ref } from 'vue'
import type { TreeNodeData } from '@arco-design/web-vue'
import { listDeptDictTree } from '@/apis/system'
import { mapTree } from 'xe-utils'
/** 部门模块 */
export function useDept(options?: { onSuccess?: () => void }) {
  const loading = ref(false)
  const deptList = ref<TreeNodeData[]>([])

  const getDeptList = async (name?: string) => {
    try {
      loading.value = true
      const res = await listDeptDictTree({ description: name })
      deptList.value = res.data
      deptList.value = mapTree(deptList.value,(item:any)=> {
           item.label = item.title
           item.value = item.key
           return item
      })
      
      options?.onSuccess && options.onSuccess()
    } finally {
      loading.value = false
    }
  }
  return { deptList, getDeptList, loading }
}
