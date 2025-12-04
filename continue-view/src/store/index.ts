import {createPinia} from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'


export * from './modules/route'
export * from './modules/tab'
export * from './modules/app'
export * from './modules/user'
export * from './modules/tenant'
export * from './modules/dict'
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

export default pinia