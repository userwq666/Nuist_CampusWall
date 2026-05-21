<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-left">
        <div class="auth-illustration">
          <div class="auth-logo">校园墙</div>
          <p class="auth-slogan">分享校园的每一刻</p>
        </div>
      </div>
      <div class="auth-right">
        <h2 class="auth-title">欢迎回来</h2>
        <p class="auth-subtitle">登录你的账号</p>
        <el-form :model="form" class="auth-form" @submit.prevent="onLogin">
          <el-form-item>
            <el-input v-model="form.username" placeholder="用户名" :prefix-icon="User" size="large" />
          </el-form-item>
          <el-form-item>
            <el-input v-model="form.password" placeholder="密码" type="password" :prefix-icon="Lock" size="large" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" class="auth-btn" :loading="loading" native-type="submit">登录</el-button>
          </el-form-item>
        </el-form>
        <div class="auth-footer">
          没有账号？<router-link to="/register">去注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { useRouter } from 'vue-router'
import { loginApi } from '../../api/account'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const form = reactive({ username: '', password: '' })
const loading = ref(false)
const authStore = useAuthStore()
const router = useRouter()

const onLogin = async () => {
  if (!form.username || !form.password) { return }
  loading.value = true
  try {
    const res = await loginApi(form)
    const { token, userInfo } = res.data
    authStore.setLogin(token, userInfo)
    if (userInfo?.role === 'ADMIN') router.push('/admin')
    else router.push('/post')
  } catch (e) {
    ElMessage.error(e.message || '登录失败')
  } finally { loading.value = false }
}
</script>

<style scoped>
.auth-page {
  min-height: calc(100vh - 56px);
  display: flex; align-items: center; justify-content: center;
  background: linear-gradient(135deg, #FFF8F5 0%, #FFF0EC 50%, #FFE8E0 100%);
  padding: 20px;
}
.auth-card {
  display: flex; max-width: 800px; width: 100%;
  background: white; border-radius: 20px;
  box-shadow: var(--shadow-lg); overflow: hidden;
}
.auth-left {
  flex: 1; background: linear-gradient(135deg, #FF6B4A, #FF8A6F);
  display: flex; align-items: center; justify-content: center;
  padding: 40px; min-height: 400px;
}
.auth-illustration { text-align: center; color: white; }
.auth-logo { font-size: 48px; font-weight: 700; margin-bottom: 12px; letter-spacing: -1px; }
.auth-slogan { font-size: 16px; opacity: 0.9; }
.auth-right { flex: 1; padding: 48px 40px; display: flex; flex-direction: column; justify-content: center; }
.auth-title { font-size: 28px; font-weight: 600; color: var(--text-primary); margin-bottom: 4px; }
.auth-subtitle { font-size: 14px; color: var(--text-secondary); margin-bottom: 32px; }
.auth-form { width: 100%; }
.auth-btn { width: 100%; height: 48px; font-size: 16px; border-radius: 12px; }
.auth-footer { text-align: center; font-size: 14px; color: var(--text-secondary); margin-top: 16px; }
.auth-footer a { color: var(--primary); font-weight: 500; margin-left: 4px; }
@media (max-width: 640px) {
  .auth-left { display: none; }
  .auth-right { padding: 32px 24px; }
}
</style>
