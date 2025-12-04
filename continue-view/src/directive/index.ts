import type { App } from 'vue'
import hasPerm from './permission/hasPerm'
export default{
    install(app: App) {
        app.directive('hasPerm', hasPerm)
    }
}