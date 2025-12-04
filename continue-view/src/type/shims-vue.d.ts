/// <reference types="vite/client" />
import { VNode } from 'vue'

declare global {
  namespace JSX {
    // 声明 JSX 元素的类型为 Vue 的 VNode
    interface Element extends VNode {}
    // 声明 JSX 元素属性的类型
    interface IntrinsicElements {
      // 允许任意 HTML 标签，属性为任意类型（根据需要可细化）
      [elem: string]: any
    }
  }
}


declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  // eslint-disable-next-line @typescript-eslint/no-explicit-any, @typescript-eslint/ban-types
  const component: DefineComponent<{}, {}, any>
  export default component
}
// //定义导入的.vue文件类型
// declare module '*vue'{
//     import type { DefineComponent } from 'vue'
//     components: DefineComponent<{}, {}, any>
//     export default components
// }
