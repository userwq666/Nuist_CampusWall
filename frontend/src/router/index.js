import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/account/LoginView.vue'
import RegisterView from '../views/account/RegisterView.vue'
import PostListView from '../views/post/PostListView.vue'

/**
 * 路由规则配置数组
 * 每个路由对象包含：
 * - path: URL 路径
 * - component: 对应的 Vue 组件
 * - redirect: 重定向目标（可选）
 */
const routes = [
    // 默认重定向：访问根路径 '/' 时自动跳转到 '/post' 帖子页面
    { path: '/', redirect: '/post' },
    // 登录页面路由
    { path: '/login', component: LoginView },
    // 注册页面路由
    { path: '/register', component: RegisterView },
    // 帖子列表页面路由
    { path: '/post', component: PostListView }
]

/**
 * 创建路由实例
 * @param {Object} config - 路由配置对象
 * @param {Object} config.history - 历史模式配置（这里使用 HTML5 History 模式）
 * @param {Array} config.routes - 路由规则数组
 */
const router = createRouter({
    history: createWebHistory(),
    routes
})

// 导出路由实例，供 main.js 使用
export default router
