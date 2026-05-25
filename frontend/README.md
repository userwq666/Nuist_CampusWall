# Nuist CampusWall Frontend

前端技术栈：`Vue 3 + Vite + Axios + Pinia + Vue Router + Element Plus`。

## 1. 启动方式
```powershell
cd D:\javacode\javaweb\Nuist_CampusWall\frontend
npm install
npm run dev
```

构建命令：
```powershell
npm run build
```

## 2. 当前页面
| 路径 | 组件 | 守卫 | 说明 |
|------|------|------|------|
| `/login` | LoginView | guestOnly | 登录 |
| `/register` | RegisterView | guestOnly | 注册 |
| `/post` | PostListView | 开放 | 帖子瀑布流 |
| `/post/:id` | PostDetailView | requiresAuth | 详情+评论 |
| `/profile` | ProfileEditView | requiresAuth | 个人信息修改 |
| `/admin` | AdminView | requiresAdmin | 管理后台 |

## 3. 项目结构
```
frontend/
├── src/
│   ├── api/          # 接口封装（request.js + 模块化 api）
│   ├── components/   # 公共组件（NavBar.vue）
│   ├── router/       # 路由 + 守卫
│   ├── stores/       # Pinia 状态（auth store）
│   └── views/        # 页面（account / post / admin）
├── doc/              # 前端文档（6 份）
├── index.html
└── vite.config.js
```

## 4. 文档入口
请先阅读：`frontend/doc/文档索引.md`
