# Nuist CampusWall

基于 `Spring Boot + Vue 3` 的校园墙课程项目，前后端已闭环，可演示、可答辩。

## 1. 项目定位
1. 面向校园社区场景，提供账户、发帖、评论、点赞、个人中心与管理员治理能力。
2. 采用"后端先闭环、前端后联调"的推进策略。
3. 通过统一鉴权、统一异常、统一返回协议降低联调成本。

## 2. 功能完成度

### 2.1 用户端（前端 + 后端）
| 模块 | 后端接口 | 前端页面 | 状态 |
|------|---------|---------|------|
| 账户注册/登录 | `POST /api/account/register` `POST /api/account/login` | `LoginView` `RegisterView` | ✅ |
| 个人信息 | `GET /api/account/my` `POST /api/account/my/update` | `ProfileEditView`（昵称/邮箱/密码/头像） | ✅ |
| 帖子 | 创建/分页/公告/我的/详情/修改/删除 | `PostListView`（瀑布流+标签+搜索+无限滚动+发布弹窗） `PostDetailView` | ✅ |
| 评论 | 创建/分页/我的/删除 | `PostDetailView`（楼中楼嵌套） | ✅ |
| 点赞 | `POST /api/like/do` `POST /api/like/undo` | `PostDetailView`（点赞/取消） | ✅ |
| 文件上传 | `POST /api/file/upload`（POST/COMMENT/AVATAR） | 帖子发布/评论/头像 | ✅ |

### 2.2 管理端
| 功能 | 后端接口 | 前端页面 | 状态 |
|------|---------|---------|------|
| 管理员鉴权 | `GET /api/admin/ping` | `AdminView`（权限校验） | ✅ |
| 用户治理 | 分页/启用/禁用 | `AdminView`（分页表格+详情弹窗） | ✅ |
| 帖子治理 | 分页/详情/启用/禁用 | `AdminView`（分页表格+详情弹窗） | ✅ |
| 评论治理 | 分页/详情/启用/禁用 | `AdminView`（分页表格+详情弹窗） | ✅ |

### 2.3 通用能力
1. JWT：`JwtUtil + JwtAuthInterceptor + UserContext`
2. 统一返回：`Result<T> = { code, message, data }`
3. 统一异常：`BusinessException + GlobalExceptionHandler`
4. 映射器：`ErrorCodeToHttpStatus`（业务码 -> HTTP 状态）
5. 文件生命周期：`TEMP -> BOUND -> TEMP -> DELETED`
6. 前端路由守卫：`guestOnly / requiresAuth / requiresAdmin`

## 3. 技术栈

### 后端
1. JDK 21
2. Spring Boot 4
3. MyBatis-Plus 3.5
4. MySQL 8
5. JWT（jjwt）

### 前端
1. Vue 3（Composition API）+ Vite
2. Axios（统一请求/响应拦截）
3. Pinia（状态管理：auth store）
4. Vue Router 4（懒加载路由 + 守卫）
5. Element Plus（UI 组件）

## 4. 项目结构

```
Nuist_CampusWall/
├── src/main/java/.../        # 后端：controller / service / mapper / domain / dto / common / security / config
├── src/main/resources/       # 后端：配置 + SQL 初始化脚本
├── src/test/http/            # HTTP 接口测试用例（95 个）
├── doc/                      # 全量项目文档（11 份）
└── frontend/
    ├── src/
    │   ├── api/              # 接口封装（request.js + 模块 api）
    │   ├── components/       # 公共组件（NavBar.vue）
    │   ├── router/           # 路由配置 + 守卫
    │   ├── stores/           # Pinia 状态（auth）
    │   └── views/            # 页面组件
    ├── index.html
    └── vite.config.js
```

## 5. 回归测试
1. 用例目录：`src/test/http`
2. 模块用例：`account/file/post/comment/like/admin`
3. 场景总量：95
4. 断言标准：`HTTP 状态码 + 业务 code`
5. 前端构建：`npm run build`（Vite + Rolldown）

## 6. 当前结论
1. **前后端联调已全部完成**，功能闭环可演示。
2. 协议口径统一为三段式，文档已全量同步。
3. 下一阶段：答辩材料收口（演示脚本、故障预案、功能边界说明）。
