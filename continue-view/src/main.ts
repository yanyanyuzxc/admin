import { createApp } from 'vue'
import App from './App.vue'
import ElmentPlus from 'element-plus'
import 'element-plus/dist/index.css'
import '@/styles/arco-ui/index.less'
import router from './router/index.ts'
import directive from './directive/index.ts'

import pinia from './store'
// main.ts
import '@/styles/index.scss'
// 支持SVG
import 'virtual:svg-icons-register'
import '@/styles/icon.scss'
// 如果您正在使用CDN引入，请删除下面一行。
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import GiSvgIcon from '@/components/GiSvgIcon/index.vue'

const app = createApp(App)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
app.component('GiSvgIcon', GiSvgIcon)

app.use(router)
app.use(pinia)
app.use(directive)

app.use(ElmentPlus)
app.mount('#app')

