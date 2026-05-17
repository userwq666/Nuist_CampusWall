<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-left">
        <div class="auth-illustration">
          <div class="auth-logo">校园墙</div>
          <p class="auth-slogan">加入校园社区</p>
        </div>
      </div>
      <div class="auth-right">
        <h2 class="auth-title">创建账号</h2>
        <p class="auth-subtitle">注册一个新账户</p>
        <el-form :model="form" class="auth-form" @submit.prevent="onRegister">
          <el-form-item><el-input v-model="form.username" placeholder="用户名" :prefix-icon="User" size="large" /></el-form-item>
          <el-form-item><el-input v-model="form.nickname" placeholder="昵称" :prefix-icon="EditPen" size="large" /></el-form-item>
          <el-form-item><el-input v-model="form.educationEmail" placeholder="教育邮箱（xxx@xxx.edu）" :prefix-icon="Message" size="large" /></el-form-item>
          <el-form-item><el-input v-model="form.password" placeholder="密码" type="password" :prefix-icon="Lock" size="large" show-password /></el-form-item>
          <el-form-item><el-input v-model="form.confirmPassword" placeholder="确认密码" type="password" :prefix-icon="Lock" size="large" show-password /></el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" class="auth-btn" :loading="loading" native-type="submit">注册</el-button>
          </el-form-item>
        </el-form>
        <div class="auth-footer">
          已有账号？<router-link to="/login">去登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { registerApi } from '../../api/account'
import { User, Lock, EditPen, Message } from '@element-plus/icons-vue'

const router = useRouter()
const form = reactive({ username: '', password: '', confirmPassword: '', nickname: '', educationEmail: '' })
const loading = ref(false)

const onRegister = async () => {
  if (!form.username || !form.password || !form.nickname || !form.educationEmail) { alert('请完整填写注册信息'); return }
  if (form.username.length < 3) { alert('用户名长度至少为 3 个字符'); return }
  if (form.username.length > 20) { alert('用户名长度不能超过 20 个字符'); return }
  if (form.username.match(/[^a-zA-Z0-9]/)) { alert('用户名只能包含字母和数字'); return }
  if (form.password.length < 6) { alert('密码长度至少为 6 个字符'); return }
  if (form.password !== form.confirmPassword) { alert('两次密码不一致'); return }
  const emailRegex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*\.edu(\.[A-Za-z0-9-]+)*$/i
  if (!emailRegex.test(form.educationEmail)) { alert('请使用教育邮箱（域名需包含 .edu）'); return }
  loading.value = true
  try {
    await registerApi({ username: form.username.trim(), password: form.password, nickname: form.nickname.trim(), educationEmail: form.educationEmail.trim() })
    alert('注册成功，请登录')
    router.push('/login')
  } catch (e) { alert(e.message || '注册失败') }
  finally { loading.value = false }
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
.auth-subtitle { font-size: 14px; color: var(--text-secondary); margin-bottom: 24px; }
.auth-form { width: 100%; }
.auth-btn { width: 100%; height: 48px; font-size: 16px; border-radius: 12px; margin-top: 8px; }
.auth-footer { text-align: center; font-size: 14px; color: var(--text-secondary); margin-top: 16px; }
.auth-footer a { color: var(--primary); font-weight: 500; margin-left: 4px; }
@media (max-width: 640px) {
  .auth-left { display: none; }
  .auth-right { padding: 32px 24px; }
}
</style>
