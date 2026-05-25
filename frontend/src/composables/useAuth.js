import { computed } from 'vue'
import { useAuthStore } from '../stores/auth'
import { useRouter } from 'vue-router'

export function useAuth() {
    const authStore = useAuthStore()
    const router = useRouter()

    const isLoggedIn = computed(() => authStore.isLogin)
    const currentUser = computed(() => authStore.userInfo)
    const isAdmin = computed(() => authStore.isAdmin)

    function logout() {
        authStore.clearToken()
        router.push('/post')
    }

    return { isLoggedIn, currentUser, isAdmin, logout }
}
