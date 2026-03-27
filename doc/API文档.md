# Nuist CampusWall API 文档（V2.3）

## 1. 统一返回
```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

## 2. 账户接口
### 2.1 注册
- `POST /api/account/register`

请求体：
```json
{
  "username": "test1",
  "password": "123456",
  "nickname": "测试用户",
  "educationEmail": "test1@nuist.edu.cn"
}
```

### 2.2 登录
- `POST /api/account/login`

成功响应 `data`：
```json
{
  "token": "jwt-token",
  "userInfo": {
    "id": 1,
    "username": "test1",
    "nickname": "测试用户",
    "educationEmail": "test1@nuist.edu.cn",
    "imageUrl": null,
    "role": "USER",
    "status": "ENABLE"
  }
}
```

### 2.3 当前用户
- `GET /api/account/my`
- Header: `Authorization: Bearer <token>`

## 3. 帖子接口
1. `POST /api/post/create`
2. `GET /api/post/page?pageNum=1&pageSize=5`
3. `GET /api/post/my/page?pageNum=1&pageSize=5`
4. `GET /api/post/{id}`
5. `POST /api/post/update/{id}`
6. `POST /api/post/delete/{id}`

## 4. 评论接口
1. `POST /api/comment/create`
2. `GET /api/comment/page?postId=1&pageNum=1&pageSize=5`
3. `GET /api/comment/my/page?pageNum=1&pageSize=5`
4. `POST /api/comment/delete/{id}`

## 5. 点赞接口
1. `POST /api/like/do`
2. `POST /api/like/undo`

请求体：
```json
{
  "targetType": "POST",
  "targetId": 1
}
```

## 6. 错误码（业务）
1. `401` 用户名已存在
2. `402` 邮箱已存在
3. `403` 用户名不存在
4. `404` 用户被禁用
5. `405` 密码错误
6. `406` 未登录或 token 缺失
7. `407` token 无效/过期
8. `408` token 对应用户不存在
9. `409` 帖子不存在
10. `410` 评论不存在
11. `411` 重复点赞
12. `412` 无权限
13. `413` 帖子状态错误
14. `422` 参数错误
15. `500` 服务器异常
