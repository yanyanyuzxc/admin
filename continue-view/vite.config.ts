
import path from 'path'
import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import createAutoImport from './config/plugins/auto-import'
import Components from 'unplugin-vue-components/vite'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// https://vite.dev/config/
export default defineConfig(({ command, mode }) =>{
  const env = loadEnv(mode, process.cwd()) as ImportMetaEnv
 return{
  plugins: [
    vue(),
   
     AutoImport({
    resolvers: [ElementPlusResolver()],
  }),
  createAutoImport(),
  Components({
    resolvers: [ElementPlusResolver()],
  }),

  createSvgIconsPlugin({
    iconDirs: [path.resolve(process.cwd(), './src/assets/icons')],
    symbolId: 'icon-[name]',
    }),
  ],
  css: {
    preprocessorOptions: {
      scss: {
        // javascriptEnabled: true,
        additionalData: `@import "@/styles/reset.scss";`
      },
    },
  },
  server: {
    // 服务启动时是否自动打开浏览器
    open: true,
    // 本地跨域代理 -> 代理到服务器的接口地址
    proxy: {
      [env.VITE_API_PREFIX]: {
        target: env.VITE_API_BASE_URL, // 后台服务器地址
        changeOrigin: true, // 是否允许不同源
        secure: false, // 支持https
        rewrite: (path) => path.replace(new RegExp(`^${env.VITE_API_PREFIX}`), ''),
      },
    },
  },
    // 以 envPrefix 开头的环境变量会通过 import.meta.env 暴露在你的客户端源码中。
  envPrefix: ['VITE', 'FILE'],
resolve: {
 
  alias: {
  '@': path.resolve(__dirname, 'src'),
  
  }
   
  
}
 }
}
)
