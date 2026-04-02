import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './style.css'
import App from './App.vue'
import router from './router'

// 创建 Vue 应用实例
const app = createApp(App)

// 使用 Pinia 状态管理（必须先于 router 使用，因为某些路由守卫可能需要访问 store）
app.use(createPinia())

// 使用路由插件，使整个应用可以使用 vue-router
app.use(router)

// 使用 Element Plus UI 组件库
app.use(ElementPlus)

// 将应用挂载到 HTML 页面中的 id 为 'app' 的元素上
app.mount('#app')

