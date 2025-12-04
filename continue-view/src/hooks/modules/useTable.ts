import type { TableData, TableInstance } from '@arco-design/web-vue'
import { Message, Modal } from '@arco-design/web-vue'
import { computed, ref } from 'vue'
import type { Options as paginationOptions } from './usePagination.ts'
import { useBreakpoint, usePagination } from '@/hooks'
import { ElMessage, ElMessageBox } from 'element-plus'
interface Options <T,U>{
    formatResult?: (data: T[]) => U[]
    onSuccess?: () => void
    immediate?: boolean
    rowKey?: keyof T
    paginationOption?: paginationOptions
}
interface PaginationParams { page: number, size: number }
type Api<T> = (params: PaginationParams) => Promise<ApiRes<PageRes<T[]>>> | Promise<ApiRes<T[]>>
export const useTable = <T extends U, U =T>(api: Api<T>, options?: Options<T, U>) => {
      const {formatResult,onSuccess,immediate,rowKey} = options || {}
      const { pagination, setTotal } = usePagination(() => getTableData(), options?.paginationOption)
      const loading = ref(false)
      const tableData= ref<U[]>([])
        // 是否立即触发
  const isImmediate = immediate ?? true
   // 多选
   const selectedKeys = ref<(string | number)[]>([])
     // 查询
  const search = () => {
    selectedKeys.value = []   
    pagination.onChange(1)
  }

  //删除
  const handleDelete = async <T>(
    deleteApi: () => Promise<ApiRes<T>>,
    options?: { title?: string, content?: string, successTip?: string, showModal?: boolean, multiple?: boolean },
  ): Promise<boolean | undefined> =>{
    const onDelete = async () => {
      try{
         const res = await deleteApi()
         if (res.success) {
          //计算新的总页数
          const deleteNum =options?.multiple? selectedKeys.value.length : 1
          const totalPage = Math.ceil((pagination.total - deleteNum) / pagination.pageSize)
          // 修正当前页码
          if (pagination.current > totalPage) {
            pagination.current = totalPage > 0 ? totalPage : 1
          }
          options?.multiple && (selectedKeys.value = [])
          ElMessage.success(options?.successTip || '删除成功')
          await getTableData()
        }
        return res.success
      }catch(err:any){
        ElMessage.error(err.message)
        return false
      }
    }
    const flag = options?.showModal ?? true // 是否显示对话框
    if (!flag) {
      return onDelete()
    }
    ElMessageBox.confirm(
      // options?.content ? options.content : '是否确定删除该条数据？', // 内容：有则用自定义，无则用默认
      // options?.title ? options.title : '提示', // 标题：有则用自定义，无则用默认
    `${options?.content ? options.content : '是否确定删除该条数据？'}`,
    `${options?.title ? options.title : '提示'}`,
      {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning',
      }
    )
      .then(() => {
          onDelete()
      })
      .catch(() => {
       ElMessage.warning('Delete canceled')
      })
  }
  const select: TableInstance['onSelect'] = (rowKeys:any) => {
   
    if (Array.isArray(rowKeys)) {
      selectedKeys.value = rowKeys.map((item:any) => item.id)
      
    }
    
  }
  isImmediate && getTableData()

      async function getTableData() {
        try {
          loading.value = true
          
          const res = await api({ page: pagination.current, size: pagination.pageSize })
         
          const data = !Array.isArray(res.data) ? res.data.list : res.data
          
          tableData.value = formatResult ? formatResult(data) : data
       
          const total = !Array.isArray(res.data) ? res.data.total : data.length
           
          setTotal(total)
          onSuccess && onSuccess()
        } finally {
          loading.value = false
        }
      }
     return {
         tableData,
         getTableData,
         loading,
         pagination,
         search,
         handleDelete,
         selectedKeys,
         select
     }
}