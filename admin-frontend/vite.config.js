import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5174,
    // 配置代理,将/api开头的请求代理到http://localhost:8080 实现跨域
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (url) => url.replace(/^\/api/, '/api/v1')
      }
    }
  },
  resolve: {
    alias: {
      // 配置别名
      '@': '/src'
    }
  }
})
