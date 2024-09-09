import { createApp } from 'vue'
import './style.css'
import App from './App.vue'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'

import router from '@/router' // 导入路由转发器

// 创建应用实例 
const app = createApp(App);
// 使用路由转发器
app.use(router);

app.use(ElementPlus, {
  locale: zhCn,
});


//挂载到 #app
app.mount('#app');
