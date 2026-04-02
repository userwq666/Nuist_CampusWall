# Nuist CampusWall

基于 `Spring Boot + Vue 3` 的校园墙项目（课程期末作业），当前后端已完成主链路闭环，进入前端联调阶段。

## 1. 项目定位
1. 面向校园社区，提供账号、发帖、评论、点赞、个人中心与管理员治理能力。
2. 采用“后端先闭环”策略，保证可演示、可答辩、可扩展。
3. 统一鉴权、统一异常、统一返回格式，降低前后端联调成本。

## 2. 当前完成度（截至 2026-04-03）
### 2.1 用户端接口
1. 账号：`/api/account/register`、`/api/account/login`、`/api/account/my`、`/api/account/my/update`
2. 帖子：`/api/post/create`、`/api/post/page`、`/api/post/notice/page`、`/api/post/my/page`、`/api/post/{id}`、`/api/post/update/{id}`、`/api/post/delete/{id}`
3. 评论：`/api/comment/create`、`/api/comment/page`、`/api/comment/my/page`、`/api/comment/delete/{id}`
4. 点赞：`/api/like/do`、`/api/like/undo`
5. 文件：`/api/file/upload`（支持 `POST/COMMENT/AVATAR`）

### 2.2 管理员接口
1. 鉴权：`/api/admin/ping`
2. 用户治理：`/api/admin/user/page`、`/api/admin/user/enable/{userId}`、`/api/admin/user/disable/{userId}`
3. 帖子治理：`/api/admin/post/page`、`/api/admin/post/detail/{postId}`、`/api/admin/post/enable/{postId}`、`/api/admin/post/disable/{postId}`
4. 评论治理：`/api/admin/comment/page`、`/api/admin/comment/detail/{commentId}`、`/api/admin/comment/enable/{commentId}`、`/api/admin/comment/disable/{commentId}`

### 2.3 通用能力
1. JWT 鉴权：`JwtUtil + JwtAuthInterceptor + UserContext`
2. 统一响应：`Result<T> -> { code, message, data }`
3. 统一异常：`BusinessException + GlobalExceptionHandler`
4. 统一语义：HTTP 状态码 + 业务 `code` 双层表达
5. 文件生命周期：`TEMP -> BOUND -> TEMP(解绑) -> DELETED(定时清理)`

## 3. 技术栈
1. JDK 21
2. Spring Boot 4
3. MyBatis-Plus 3.5
4. MySQL 8
5. JWT（jjwt）
6. Lombok
7. Vue 3

## 4. 回归测试
1. 用例目录：`src/test/http`
2. 当前用例：
   - `account.http`（14）
   - `file.http`（7）
   - `post.http`（17）
   - `comment.http`（13）
   - `like.http`（8）
   - `admin.http`（36）
3. 总计：95 条场景。
4. 建议顺序：`account -> file -> post -> comment -> like -> admin`

## 5. 状态结论
1. 后端主功能闭环已完成并完成核心回归。
2. 接口协议已回切为三段式，便于前端统一处理。
3. 当前可直接进入前端页面联调。