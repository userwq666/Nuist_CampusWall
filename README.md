# Nuist CampusWall

基于 Spring Boot 的校园内容发布与互动平台（后端）。

## 项目现状（2026-03-27）
当前已完成：账户、JWT 鉴权、帖子、评论、点赞主链路，支持“我的”视图与软删除。

## 已实现功能
### 1. 账户
1. `POST /api/account/register`
2. `POST /api/account/login`
3. `GET /api/account/my`

### 2. 帖子
1. `POST /api/post/create`
2. `GET /api/post/page`（公开分页，仅 `ENABLE`）
3. `GET /api/post/my/page`（我的分页）
4. `GET /api/post/{id}`（详情）
5. `POST /api/post/update/{id}`（修改我的帖子）
6. `POST /api/post/delete/{id}`（删除我的帖子，软删除）

### 3. 评论
1. `POST /api/comment/create`
2. `GET /api/comment/page`（公开分页，仅 `ENABLE`）
3. `GET /api/comment/my/page`（我的分页）
4. `POST /api/comment/delete/{id}`（删除我的评论，软删除）

### 4. 点赞
1. `POST /api/like/do`
2. `POST /api/like/undo`
3. 支持目标：`POST` / `COMMENT`
4. 防重复点赞（唯一键 + 业务校验）

### 5. 通用
1. `Result<T>` 统一返回
2. `BusinessException` + `GlobalExceptionHandler`
3. `ErrorCode` 常量化

## 当前错误码（摘要）
`401~407, 408, 409, 410, 411, 412, 413, 422, 500`

## 启动
```bash
mvn spring-boot:run
```

## HTTP 用例
1. `src/test/http/account.http`
2. `src/test/http/post.http`
3. `src/test/http/comment.http`
4. `src/test/http/like.http`
5. `src/test/http/http-client.env.json`

## 下一阶段
1. 管理员权限拦截与 `/api/admin/**`
2. 用户/帖子/评论管理接口
3. 文档终版收口与答辩材料
