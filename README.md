# Nuist CampusWall

基于 `Spring Boot + Vue 3` 的校园墙课程项目，当前后端主链路已闭环，前端进入联调阶段。

## 1. 项目定位
1. 面向校园社区场景，提供账户、发帖、评论、点赞、个人中心与管理员治理能力。
2. 采用“后端先闭环、前端后联调”的推进策略。
3. 通过统一鉴权、统一异常、统一返回协议降低联调成本。

## 2. 当前完成度（截至 2026-04-03）
### 2.1 用户端
1. 账户：`/api/account/register`、`/api/account/login`、`/api/account/my`、`/api/account/my/update`
2. 帖子：创建、公开分页、公告分页、我的分页、详情、修改、删除
3. 评论：创建、分页、我的分页、删除
4. 点赞：`/api/like/do`、`/api/like/undo`
5. 文件：`/api/file/upload`（`POST/COMMENT/AVATAR`）

### 2.2 管理端
1. 管理员鉴权：`/api/admin/ping`
2. 用户治理：分页、启用、禁用
3. 帖子治理：分页、详情、启用、禁用
4. 评论治理：分页、详情、启用、禁用

### 2.3 通用能力
1. JWT：`JwtUtil + JwtAuthInterceptor + UserContext`
2. 统一返回：`Result<T> = { code, message, data }`
3. 统一异常：`BusinessException + GlobalExceptionHandler`
4. 映射器：`ErrorCodeToHttpStatus`（业务码 -> HTTP 状态）
5. 文件生命周期：`TEMP -> BOUND -> TEMP -> DELETED`

## 3. 技术栈
1. JDK 21
2. Spring Boot 4
3. MyBatis-Plus 3.5
4. MySQL 8
5. JWT（jjwt）
6. Vue 3 + Vite + Axios + Pinia + Vue Router + Element Plus

## 4. 回归测试
1. 用例目录：`src/test/http`
2. 模块用例：`account/file/post/comment/like/admin`
3. 场景总量：95
4. 断言标准：`HTTP 状态码 + 业务 code`

## 5. 当前结论
1. 后端可交付、可演示、可答辩。
2. 协议口径已统一为三段式并完成文档同步。
3. 下一阶段主线：前端页面联调与答辩材料收口。