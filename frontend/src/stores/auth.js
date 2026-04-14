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
        token: localStorage.getItem('token') || '',
        // user 状态：存储用户信息
        userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null')
    }),


    /**
     * getters: 获取状态数据的计算属性
     * 支持同步和异步操作，this 指向 store 状态数据对象
     */
    getters: {
        // 判断用户是否已登录（通过检查 token 是否存在）
        isLogin: (state) => !!state.token,
        // 判断用户是否为管理员（检查 userInfo 中的 role 字段）
        isAdmin: (state) => state.userInfo?.role === 'ADMIN'
    },

    /**
     * actions: 包含可以修改状态的方法
     * 支持同步和异步操作，this 指向 store 实例本身
     */
    actions: {
        setLogin(token, userInfo){
            this.token = token
            this.userInfo = userInfo
            localStorage.setItem('token', token)
            localStorage.setItem('userInfo', JSON.stringify(userInfo))
        },

        setToken(token){
            this.token = token
            localStorage.setItem('token', token)
        },

        clearToken(){
            this.token = ''
           this.userInfo = null
            localStorage.removeItem('token')
            localStorage.removeItem('userInfo')
        }
    }
})