import {defineStore} from "pinia";
/**
 * 使用 defineStore 定义认证相关的状态管理
 * @param {String} id - store 的唯一标识符
 * @param {Object} options - store 配置选项
 * @param {Function} options.state - 返回初始状态的函数
 * @param {Object} options.actions - 包含修改状态方法的对象
 */
export const useAuthStore = defineStore('auth', {
    /**
     * state: 响应式状态数据
     * 所有状态都以函数形式返回，确保每个组件实例获取独立的状态副本
     */
    state: () => ({
        // token 状态：存储用户认证令牌
        // 优先从 localStorage 读取（实现页面刷新后保持登录状态），如果没有则返回空字符串
        token: localStorage.getItem('token') || ''
    }),
    
    /**
     * actions: 包含可以修改状态的方法
     * 支持同步和异步操作，this 指向 store 实例本身
     */
    actions: {
        /**
         * 设置用户登录 token
         * @param {String} token - 后端返回的认证令牌
         * 作用：同时更新 store 中的 token 状态和浏览器的 localStorage
         */
        setToken(token) {
            this.token = token
            localStorage.setItem('token', token)
        },
        /**
         * 清除用户登录 token（用于退出登录）
         * 作用：清空 store 和 localStorage 中的 token 信息
         */
        clearToken() {
            this.token = ''
            localStorage.removeItem('token')
        }
    }
})