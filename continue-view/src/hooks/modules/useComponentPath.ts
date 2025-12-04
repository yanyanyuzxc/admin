import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
interface ComponentOption {
    label: string
    value: string
  }
  
  export const useComponentPaths =  () => {
    const componentOptions = ref<ComponentOption[]>([])
  
    const loadComponentPaths = async () => {
      try {
        const modules = import.meta.glob('@/view/**/index.vue')
        const paths = Object.keys(modules)
        componentOptions.value = paths.map((path) => {
          // 格式转化
          path = path.replace('/src/view/', '')
          const label = `@view/${path}`
          const value = path.split('.vue')[0]
          return { label, value }
        })
      } catch (error) {
        ElMessage.error('加载组件路径失败')
        console.error('加载组件路径失败:', error)
      }
    }
  

      onMounted(async() => {
        await loadComponentPaths()
      })

  
    return {
      componentOptions,
    }
  }