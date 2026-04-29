<template>
  <div>
    <h2>注册页</h2>

    <input v-model="form.username" placeholder="用户名" />
    <input v-model="form.password" placeholder="密码" type="password" />
    <input v-model="form.confirmPassword" placeholder="确认密码" type="password" />
    <input v-model="form.nickname" placeholder="昵称" />
    <input v-model="form.educationEmail" placeholder="教育邮箱" />

    <button @click="back">返回</button>
    <button @click="onRegister">注册</button>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { registerApi } from '../../api/account'

const router = useRouter()

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  educationEmail: ''
})

const back = () => {
  router.push('/login')
}

const onRegister = async () => {
  if (!form.username || !form.password || !form.nickname || !form.educationEmail) {
    alert('请完整填写注册信息')
    return
  }

  if (form.username.length < 3) {
    alert('用户名长度至少为 3 个字符')
    return
  }

  if (form.username.length > 20) {
    alert('用户名长度不能超过 20 个字符')
    return
  }

  if (form.username.match(/[^a-zA-Z0-9]/)) {
    alert('用户名只能包含字母和数字')
    return
  }

  if (form.password.length < 6) {
    alert('密码长度至少为 6 个字符')
    return
  }

  if (form.password !== form.confirmPassword) {
    alert('两次密码不一致')
    return
  }

  const eduEmailRegex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*\.edu(\.[A-Za-z0-9-]+)*$/i
  if (!eduEmailRegex.test(form.educationEmail)) {
    alert('请使用教育邮箱（域名需包含 .edu）')
    return
  }

  const payload = {
    username: form.username.trim(),
    password: form.password,
    nickname: form.nickname.trim(),
    educationEmail: form.educationEmail.trim()
  }

  try {
    await registerApi(payload)
    alert('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    alert(e.message || '注册失败')
  }
}
</script>
