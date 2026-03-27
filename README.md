# Nuist CampusWall

基于 Spring Boot + MyBatis-Plus 的校园墙后端项目（课程期末作业）。

## 1. 项目目标
1. 面向校园场景，提供用户注册登录、发帖、评论、点赞、个人中心能力。
2. 后端先行完成接口闭环，前端后续按小红书风格逐步接入。
3. 当前阶段重点是“稳定可演示 + 文档可答辩 + 代码可扩展”。

## 2. 当前完成度（截至 2026-03-28）
### 2.1 账户模块
1. `POST /api/account/register`
2. `POST /api/account/login`
3. `GET /api/account/my`

### 2.2 帖子模块
1. `POST /api/post/create`
2. `GET /api/post/page`（公开帖子）
3. `GET /api/post/notice/page`（公告帖子）
4. `GET /api/post/my/page`
5. `GET /api/post/{id}`
6. `POST /api/post/update/{id}`
7. `POST /api/post/delete/{id}`

### 2.3 评论模块
1. `POST /api/comment/create`
2. `GET /api/comment/page`
3. `GET /api/comment/my/page`
4. `POST /api/comment/delete/{id}`

### 2.4 点赞模块
1. `POST /api/like/do`
2. `POST /api/like/undo`

### 2.5 管理员模块（当前已落地基础）
1. `GET /api/admin/ping`
2. `GET /api/admin/user/page`

### 2.6 通用能力
1. 统一返回体 `Result<T>`
2. `BusinessException` + `GlobalExceptionHandler`
3. JWT 鉴权拦截器 + `UserContext`
4. 统一错误码 `ErrorCode`

## 3. 关键配置
1. `app.admin-user-id=1`（管理员用户ID，用于公告与公开帖子过滤）
2. `jwt.secret`、`jwt.expire-seconds`、`jwt.issuer`

## 4. 接口回归文件
1. `src/test/http/account.http`
2. `src/test/http/post.http`
3. `src/test/http/comment.http`
4. `src/test/http/like.http`
5. `src/test/http/admin.http`
6. `src/test/http/http-client.env.json`

## 5. 下一步计划（主线）
1. 管理员用户启用/禁用接口
2. 管理员帖子/评论管理接口
3. 文档终版与答辩材料收口