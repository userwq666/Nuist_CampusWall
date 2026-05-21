<template>
  <header class="navbar">
    <div class="navbar-inner">
      <div class="navbar-left">
        <router-link to="/post" class="logo"><span class="logo-nuist">NUIST</span> 校园墙</router-link>
      </div>
      <div class="navbar-center">
        <el-input v-model="searchText" placeholder="搜索帖子..." :prefix-icon="Search" class="search-input" clearable @keyup.enter="doSearch" @clear="doClearSearch" />
      </div>
      <div class="navbar-right">
        <template v-if="isLoggedIn">
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="user-trigger">
              <el-avatar :size="32" :src="currentUser?.imageUrl || undefined">{{ currentUser?.nickname?.charAt(0) || 'U' }}</el-avatar>
              <span class="user-name">{{ currentUser?.nickname || '用户' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile"><el-icon><User /></el-icon>个人资料</el-dropdown-item>
                <el-dropdown-item command="myPosts"><el-icon><Document /></el-icon>我的帖子</el-dropdown-item>
                <el-dropdown-item v-if="isAdmin" command="admin"><el-icon><Setting /></el-icon>管理后台</el-dropdown-item>
                <el-dropdown-item divided command="logout"><el-icon><SwitchButton /></el-icon>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <router-link to="/login" class="nav-btn login-btn">登录</router-link>
          <router-link to="/register" class="nav-btn register-btn">注册</router-link>
        </template>
      </div>
      <button class="mobile-menu-btn" @click="showMobileMenu = !showMobileMenu"><el-icon><Menu /></el-icon></button>
    </div>
    <transition name="slide">
      <div v-if="showMobileMenu" class="mobile-menu">
        <template v-if="isLoggedIn">
          <div class="mobile-user-info">
            <el-avatar :size="40" :src="currentUser?.imageUrl || undefined" />
            <span>{{ currentUser?.nickname }}</span>
          </div>
          <router-link to="/post" class="mobile-menu-item" @click="showMobileMenu=false"><el-icon><HomeFilled /></el-icon>首页</router-link>
          <router-link to="/profile" class="mobile-menu-item" @click="showMobileMenu=false"><el-icon><User /></el-icon>个人资料</router-link>
          <router-link to="/post?tab=my" class="mobile-menu-item" @click="showMobileMenu=false"><el-icon><Document /></el-icon>我的帖子</router-link>
          <router-link v-if="isAdmin" to="/admin" class="mobile-menu-item" @click="showMobileMenu=false"><el-icon><Setting /></el-icon>管理后台</router-link>
          <div class="mobile-menu-item" @click="handleLogout"><el-icon><SwitchButton /></el-icon>退出登录</div>
        </template>
        <template v-else>
          <router-link to="/login" class="mobile-menu-item" @click="showMobileMenu=false">登录</router-link>
          <router-link to="/register" class="mobile-menu-item" @click="showMobileMenu=false">注册</router-link>
        </template>
      </div>
    </transition>
  </header>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuth } from '../composables/useAuth'
import { Search, ArrowDown, Document, Setting, SwitchButton, Menu, HomeFilled, User } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const { isLoggedIn, currentUser, isAdmin, logout } = useAuth()
const searchText = ref('')
const showMobileMenu = ref(false)

function handleCommand(command) {
  if (command === 'logout') handleLogout()
  else if (command === 'profile') router.push('/profile')
  else if (command === 'admin') router.push('/admin')
  else if (command === 'myPosts') router.push('/post?tab=my')
}
function doSearch() {
  const q = searchText.value.trim()
  if (q) {
    router.push({ path: '/post', query: { search: q } })
  }
}
function doClearSearch() {
  if (route.path === '/post') {
    router.push({ path: '/post' })
  }
}
function handleLogout() { showMobileMenu.value = false; logout() }
</script>

<style scoped>
.navbar {
  position: fixed; top: 0; left: 0; right: 0;
  height: var(--nav-height);
  background: rgba(255,255,255,0.96);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--border);
  z-index: 1000;
}
.navbar-inner {
  max-width: 1200px; margin: 0 auto; height: 100%;
  display: flex; align-items: center;
  padding: 0 20px; gap: 24px;
}
.navbar-left { flex-shrink: 0; }
.logo { font-size: 22px; font-weight: 700; color: var(--text-primary); letter-spacing: -0.5px; }
.logo-nuist { color: var(--primary); font-weight: 800; letter-spacing: 1px; }
.navbar-center { flex: 1; max-width: 400px; }
.search-input { --el-input-border-radius: 20px; --el-input-bg-color: #F5F5F5; }
.search-input :deep(.el-input__wrapper) { background: #F5F5F5; box-shadow: none !important; }
.search-input :deep(.el-input__inner) { font-size: 14px; }
.navbar-right { flex-shrink: 0; display: flex; align-items: center; gap: 12px; }
.user-trigger {
  display: flex; align-items: center; gap: 8px; cursor: pointer;
  padding: 4px 8px; border-radius: 20px; transition: background 0.2s;
}
.user-trigger:hover { background: var(--primary-light); }
.user-name { font-size: 14px; font-weight: 500; color: var(--text-primary); }
.nav-btn { padding: 8px 20px; border-radius: 20px; font-size: 14px; font-weight: 500; transition: all 0.2s; }
.login-btn { color: var(--primary); border: 1px solid var(--primary); }
.login-btn:hover { background: var(--primary-light); }
.register-btn { background: var(--primary); color: white; }
.register-btn:hover { background: var(--primary-hover); }
.mobile-menu-btn { display: none; background: none; border: none; font-size: 24px; cursor: pointer; color: var(--text-primary); padding: 4px; }
.mobile-menu { position: absolute; top: var(--nav-height); left: 0; right: 0; background: white; border-bottom: 1px solid var(--border); padding: 12px 16px; box-shadow: var(--shadow-md); }
.mobile-user-info { display: flex; align-items: center; gap: 12px; padding: 12px 0; border-bottom: 1px solid var(--border); margin-bottom: 8px; font-weight: 500; }
.mobile-menu-item { display: flex; align-items: center; gap: 10px; padding: 12px 0; font-size: 15px; cursor: pointer; color: var(--text-primary); border-bottom: 1px solid #f5f5f5; }
.mobile-menu-item:last-child { border-bottom: none; }
.slide-enter-active, .slide-leave-active { transition: all 0.25s ease; }
.slide-enter-from, .slide-leave-to { opacity: 0; transform: translateY(-10px); }
@media (max-width: 768px) {
  .navbar-center, .navbar-right { display: none; }
  .mobile-menu-btn { display: flex; align-items: center; margin-left: auto; }
}
@media (min-width: 769px) { .mobile-menu { display: none !important; } }
</style>
