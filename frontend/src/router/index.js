import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/account/LoginView.vue'
import RegisterView from '../views/account/RegisterView.vue'
import PostListView from '../views/post/PostListView.vue'
import PostDetailView from "../views/post/PostDetailView.vue";
import AdminView from "../views/admin/AdminView.vue";
import { useAuthStore } from '../stores/auth'


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
    { path: '/login', component: LoginView ,meta: { guestOnly: true }},
    // 注册页面路由
    { path: '/register', component: RegisterView ,meta: { guestOnly: true }},
    // 帖子列表页面路由
    { path: '/post', component: PostListView},
    // 帖子详情页面路由
    { path: '/post/:id', component: PostDetailView ,meta: { requiresAuth: true }},
    // 管理页面路由
    { path: '/admin', component: AdminView ,meta: { requiresAuth: true, requiresAdmin: true }}
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

router.beforeEach((to, from, next) => {
    const authStore = useAuthStore()

    // 如果访问的页面不需要登录，但当前用户已登录，则跳转到帖子列表页面
    if (to.meta.guestOnly && authStore.isLogin) {
        next('/post')
        return
    }

    // 如果访问的页面需要登录，但当前用户没有登录，则跳转到登录页面
    if (to.meta.requiresAuth && !authStore.isLogin) {
        next('/login')
        return
    }

    // 如果当前用户不是管理员，并且访问的是管理员页面，则跳转到帖子列表页面
    if (to.meta.requiresAdmin && !authStore.isAdmin) {
        next('/post')
        return
    }

    next()
})


// 导出路由实例，供 main.js 使用
export default router
