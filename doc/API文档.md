# Nuist CampusWall API 文档（V2.5）

## 1. 统一返回
```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

## 2. 用户端接口
### 2.1 account
1. `POST /api/account/register`
2. `POST /api/account/login`
3. `GET /api/account/my`

### 2.2 post
1. `POST /api/post/create`
2. `GET /api/post/page?pageNum=1&pageSize=5`
3. `GET /api/post/notice/page?pageNum=1&pageSize=5`
4. `GET /api/post/my/page?pageNum=1&pageSize=5`
5. `GET /api/post/{id}`
6. `POST /api/post/update/{id}`
7. `POST /api/post/delete/{id}`

说明：
1. `/post/page` 返回普通帖子（排除管理员公告）。
2. `/post/notice/page` 返回管理员公告帖子。

### 2.3 comment
1. `POST /api/comment/create`
2. `GET /api/comment/page?postId=1&pageNum=1&pageSize=5`
3. `GET /api/comment/my/page?pageNum=1&pageSize=5`
4. `POST /api/comment/delete/{id}`

### 2.4 like
1. `POST /api/like/do`
2. `POST /api/like/undo`

## 3. 管理员接口（当前阶段）
1. `GET /api/admin/ping`
2. `GET /api/admin/user/page?pageNum=1&pageSize=5`
3. `GET /api/admin/user/page?pageNum=1&pageSize=5&status=ENABLE`

## 4. 错误码（业务）
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