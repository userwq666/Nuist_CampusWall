import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'


const routes = [
    { path: '/', redirect: '/post' },
    { path: '/login', component: () => import('../views/account/LoginView.vue'), meta: { guestOnly: true } },
    { path: '/register', component: () => import('../views/account/RegisterView.vue'), meta: { guestOnly: true } },
    { path: '/post', component: () => import('../views/post/PostListView.vue') },
    { path: '/post/:id', component: () => import('../views/post/PostDetailView.vue'), meta: { requiresAuth: true } },
    { path: '/profile', component: () => import('../views/account/ProfileEditView.vue'), meta: { requiresAuth: true } },
    { path: '/admin', component: () => import('../views/admin/AdminView.vue'), meta: { requiresAuth: true, requiresAdmin: true } }
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
