<template>
  <div>
    <h2>登录页</h2>

    <input v-model="form.username" placeholder="用户名"/>
    <input v-model="form.password" placeholder="密码" type="password"/>
    <button @click="onLogin">登录</button>
  </div>
</template>

<script setup>
import {reactive} from 'vue'
import {useAuthStore} from "../../stores/auth.js";
import {useRouter} from "vue-router";
import {loginApi} from "../../api/account.js";

// 表单数据
const form = reactive({
  username: '',
  password: ''
})

const authStore = useAuthStore()
const router = useRouter()

// 登录方法
const onLogin = async () => {
  if (!form.username || !form.password) {
    alert('请输入用户名和密码')
    return
  }

  try {
    const res = await loginApi(form)
    const {token, userInfo} = res.data
    authStore.setLogin(token, userInfo)
    alert('登录成功')
    if (userInfo?.role === 'ADMIN') {
      router.push('/admin')
    } else {
      router.push('/post')
    }
  } catch (e) {
    alert(e.message || '登录失败')
  }
}
</script>
