# 前端小红书风格改版设计文档

## 概述
将 Nuist CampusWall 前端改造为类似小红书的视觉风格：温暖米白+珊瑚橙色调、顶部固定导航栏、瀑布流卡片布局。

## 技术栈
Vue 3.5 + Vite 8 + Element Plus 2.13 + Pinia 3 + Vue Router 4
保持现有后端 API 不变

## 页面清单
### 1. 全局布局
NavBar.vue + App.vue + style.css

### 2. 登录/注册页面
居中卡片布局，Element Plus 表单

### 3. 帖子列表页（瀑布流）
Tab 切换 + CSS 三列瀑布流 + 滚动加载更多

### 4. 帖子详情页
作者信息、点赞、评论互动

### 5. 管理后台
侧边栏菜单 + el-table 管理

## API 层
comment.js / like.js / file.js / admin.js

## 约束
不动后端代码、所有页面处理 loading/error/empty 状态、响应式适配
