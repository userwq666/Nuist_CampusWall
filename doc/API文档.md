# Nuist CampusWall API 文档（V2.1）

## 1. 统一返回结构
```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

## 2. 账户模块
### 2.1 注册
- `POST /api/account/register`

### 2.2 登录
- `POST /api/account/login`
- 成功返回 `token + userInfo`

### 2.3 当前用户
- `GET /api/account/me`
- Header: `Authorization: Bearer <token>`

## 3. 帖子模块
### 3.1 创建帖子
- `POST /api/post/create`

### 3.2 帖子分页
- `GET /api/post/page?pageNum=1&pageSize=5`

### 3.3 帖子详情
- `GET /api/post/{id}`

## 4. 评论模块
### 4.1 创建评论
- `POST /api/comment/create`

### 4.2 评论分页
- `GET /api/comment/page?postId=1&pageNum=1&pageSize=5`

## 5. 点赞模块
### 5.1 点赞
- 方法与路径：`POST /api/like/do`
- 鉴权：需要 token

请求体：
```json
{
  "targetType": "POST",
  "targetId": 1
}
```

说明：
1. `targetType` 支持 `POST` / `COMMENT`
2. 重复点赞返回 `code=411`

成功响应：
```json
{
  "code": 0,
  "message": "success",
  "data": "点赞成功"
}
```

### 5.2 取消点赞
- 方法与路径：`POST /api/like/undo`
- 鉴权：需要 token

请求体：
```json
{
  "targetType": "POST",
  "targetId": 1
}
```

成功响应：
```json
{
  "code": 0,
  "message": "success",
  "data": "取消点赞成功"
}
```

失败响应（示例）：
1. 未登录：`code=406`
2. 未点赞取消：`code=422`
3. 目标不存在：`POST -> 409`，`COMMENT -> 410`

## 6. 错误码一览
1. `0`：成功
2. `401`：用户名已存在
3. `402`：邮箱已存在
4. `403`：用户名不存在
5. `404`：用户已被禁用
6. `405`：密码错误
7. `406`：未登录或 token 缺失
8. `407`：token 无效或已过期
9. `408`：用户不存在
10. `409`：帖子不存在
11. `410`：评论不存在
12. `411`：点赞已存在（重复点赞）
13. `422`：参数校验失败或业务参数错误
14. `500`：服务器异常

## 7. HTTP 用例文件
1. `src/test/http/account.http`
2. `src/test/http/post.http`
3. `src/test/http/comment.http`
4. `src/test/http/like.http`
