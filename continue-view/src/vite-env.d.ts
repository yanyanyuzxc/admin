/// <reference types="vite/client" />
declare module 'virtual:svg-icons-register' {
    // 这里不需要导出任何东西，因为这个模块是用于副作用的（比如自动注册图标）
  }
  interface Window {
    getComputedStyle:any
  }

declare module '@arco-design/color'