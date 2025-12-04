import {defineStore} from 'pinia'
import {ref} from 'vue'
const setupStore = () =>{
    const tenantEnabled = ref<boolean>(false)
    const tenantId = ref<string>()
  
    const setTenantEnable = (status: boolean) => {
      tenantEnabled.value = status
    }
    const setTenantId = (id: string) => {
      tenantId.value = id
    }
    return {
      tenantEnabled,
      tenantId,
      setTenantEnable,
      setTenantId
    }
}
export const useTenantStore = defineStore('tenant', setupStore,{persist:true})