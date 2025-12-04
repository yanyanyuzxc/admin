import type { Directive, DirectiveBinding } from 'vue'
import { useUserStore } from '@/store'
const checkPeimission = (el: HTMLElement, binding: DirectiveBinding) =>{
    const userStore = useUserStore()
     const { value } = binding
     //定义超级权限
     const all_permission = '*:*:*'

     //检查value的格式
     if(value && Array.isArray(value) && value.length){
        const permissionValue:string[] = value
        //检查权限
       const hasPermission = userStore.permissions.some(perm => {
        return perm === all_permission || permissionValue.includes(perm)

       })
       if(!hasPermission){
        el.parentNode && el.parentNode.removeChild(el)
       }
     }else{
        throw new Error('v-has-perm value must be an array and not empty')
     }

}

const directive :Directive = {
    mounted(el: HTMLElement, binding: DirectiveBinding) {
        checkPeimission(el, binding)
    },
    updated(el: HTMLElement, binding: DirectiveBinding) {
        checkPeimission(el, binding)
    }
}

export default directive