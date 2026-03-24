# Nuist CampusWall API 文档（V2.0）

## 1. 统一返回结构
```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

字段说明：
1. `code`：业务码，`0` 成功，非 `0` 失败
2. `message`：提示信息
3. `data`：业务数据，失败通常为 `null`

---

## 2. 账户模块

### 2.1 注册
- 方法与路径：`POST /api/account/register`
- 鉴权：不需要 token

请求体：
```json
{
  "username": "test1",
  "password": "123456",
  "nickname": "测试用户1001",
  "educationEmail": "test1001@nuist.edu.cn"
}
```

成功响应：
```json
{
  "code": 0,
  "message": "success",
  "data": "注册成功"
}
```

失败响应（示例）：
1. 用户名重复：`code=401`
2. 邮箱重复：`code=402`

### 2.2 登录
- 方法与路径：`POST /api/account/login`
- 鉴权：不需要 token

请求体：
```json
{
  "username": "test1",
  "password": "123456"
}
```

成功响应（`LoginRespVO`）：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.xxx.yyy",
    "userInfo": {
      "id": 2,
      "username": "test1",
      "nickname": "测试用户1001",
      "educationEmail": "test1001@nuist.edu.cn",
      "imageUrl": null,
      "role": "USER",
      "status": "ENABLE"
    }
  }
}
```

失败响应（示例）：
1. 用户名不存在：`code=403`
2. 用户禁用：`code=404`
3. 密码错误：`code=405`

### 2.3 当前用户信息
- 方法与路径：`GET /api/account/me`
- 鉴权：需要 token
- 请求头：`Authorization: Bearer <token>`

成功响应：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 2,
    "username": "test1",
    "nickname": "测试用户1001",
    "educationEmail": "test1001@nuist.edu.cn",
    "imageUrl": null,
    "role": "USER",
    "status": "ENABLE"
  }
}
```

失败响应（示例）：
1. 未登录/缺 token：`code=406`
2. token 无效或过期：`code=407`
3. token 中用户不存在：`code=408`

---

## 3. 帖子模块

### 3.1 创建帖子
- 方法与路径：`POST /api/post/create`
- 鉴权：需要 token

请求体：
```json
{
  "title": "第一条帖子",
  "content": "这是帖子正文",
  "imageUrl": null
}
```

成功响应：
```json
{
  "code": 0,
  "message": "success",
  "data": "帖子创建成功"
}
```

失败响应（示例）：
1. 未登录：`code=406`
2. 参数校验失败：`code=422`

### 3.2 帖子分页
- 方法与路径：`GET /api/post/page?pageNum=1&pageSize=5`
- 鉴权：需要 token

成功响应（`PageResult<PostVO>`）：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "total": 5,
    "records": [
      {
        "id": 1,
        "userId": 2,
        "title": "第一条帖子",
        "content": "这是帖子正文",
        "imageUrl": null,
        "likeCount": 0,
        "createTime": "2026-03-23T22:31:16"
      }
    ]
  }
}
```

失败响应（示例）：
1. 分页参数非法：`code=422`

### 3.3 帖子详情
- 方法与路径：`GET /api/post/{id}`
- 鉴权：需要 token

成功响应：`data` 为 `PostVO`

失败响应（示例）：
1. 帖子不存在：`code=409`

---

## 4. 评论模块

### 4.1 创建评论
- 方法与路径：`POST /api/comment/create`
- 鉴权：需要 token

请求体（顶层评论）：
```json
{
  "postId": 1,
  "replyToCommentId": null,
  "replyToUserId": null,
  "content": "这是第一条评论",
  "imageUrl": null
}
```

请求体（回复评论）：
```json
{
  "postId": 1,
  "replyToCommentId": 10,
  "replyToUserId": 2,
  "content": "回复一下",
  "imageUrl": null
}
```

成功响应：
```json
{
  "code": 0,
  "message": "success",
  "data": "评论创建成功"
}
```

失败响应（示例）：
1. 未登录：`code=406`
2. 目标帖子不存在：`code=409`
3. 参数校验失败：`code=422`

### 4.2 评论分页
- 方法与路径：`GET /api/comment/page?postId=1&pageNum=1&pageSize=5`
- 鉴权：需要 token

成功响应（`PageResult<CommentVO>`）：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "total": 2,
    "records": [
      {
        "id": 1,
        "userId": 2,
        "postId": 1,
        "replyToCommentId": null,
        "replyToUserId": null,
        "content": "这是第一条评论",
        "imageUrl": null,
        "likeCount": 0,
        "createTime": "2026-03-25T10:00:00"
      }
    ]
  }
}
```

失败响应（示例）：
1. 分页参数非法：`code=422`

---

## 5. 错误码一览（摘要）
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
11. `422`：参数校验失败
12. `500`：服务器异常

详细见：`doc/错误码说明.md`
