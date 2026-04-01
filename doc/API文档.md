# Nuist CampusWall API 文档（V3.1）

## 1. 统一返回格式
```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

## 2. 鉴权说明
1. 受保护接口统一要求请求头：`Authorization: Bearer <token>`。
2. 放行接口：
   - `POST /api/account/register`
   - `POST /api/account/login`
3. 管理员接口前缀：`/api/admin/**`，要求角色 `ADMIN`。

## 3. 用户端接口
### 3.1 account
1. `POST /api/account/register`
2. `POST /api/account/login`
3. `GET /api/account/my`
4. `POST /api/account/my/update`

`/api/account/my/update` 关键字段：
1. `nickname`
2. `educationEmail`
3. `oldPassword + newPassword`
4. `fileID`（头像文件绑定，推荐）

### 3.2 file
1. `POST /api/file/upload?fileType=POST`
2. `POST /api/file/upload?fileType=COMMENT`
3. `POST /api/file/upload?fileType=AVATAR`

说明：
1. 仅允许图片格式：`jpg/jpeg/png/webp/gif`。
2. 业务限制：`app.file.max-image-size=5MB`。
3. Spring multipart 上限需高于业务上限。

### 3.3 post
1. `POST /api/post/create`
2. `GET /api/post/page?pageNum=1&pageSize=5`
3. `GET /api/post/notice/page?pageNum=1&pageSize=5`
4. `GET /api/post/my/page?pageNum=1&pageSize=5`
5. `GET /api/post/{id}`
6. `POST /api/post/update/{id}`
7. `POST /api/post/delete/{id}`

说明：
1. `/post/page` 返回普通帖子（排除管理员公告）。
2. `/post/notice/page` 返回管理员公告。
3. 发帖/改帖可传 `fileID/fileId` 绑定图片。

### 3.4 comment
1. `POST /api/comment/create`
2. `GET /api/comment/page?postId=1&pageNum=1&pageSize=5`
3. `GET /api/comment/my/page?pageNum=1&pageSize=5`
4. `POST /api/comment/delete/{id}`

说明：
1. 创建评论可传 `fileId` 绑定评论图片。
2. 评论采用“扁平楼层 + 回复字段”模型。

### 3.5 like
1. `POST /api/like/do`
2. `POST /api/like/undo`

## 4. 管理员接口
### 4.1 基础鉴权
1. `GET /api/admin/ping`

### 4.2 用户治理
1. `GET /api/admin/user/page?pageNum=1&pageSize=5`
2. `GET /api/admin/user/page?pageNum=1&pageSize=5&status=ENABLE`
3. `POST /api/admin/user/enable/{userId}`
4. `POST /api/admin/user/disable/{userId}`

### 4.3 帖子治理
1. `GET /api/admin/post/page?pageNum=1&pageSize=5`
2. `GET /api/admin/post/page?pageNum=1&pageSize=5&userId=2`
3. `GET /api/admin/post/page?pageNum=1&pageSize=5&status=ENABLE`
4. `GET /api/admin/post/detail/{postId}`
5. `POST /api/admin/post/enable/{postId}`
6. `POST /api/admin/post/disable/{postId}`

### 4.4 评论治理
1. `GET /api/admin/comment/page?pageNum=1&pageSize=5`
2. `GET /api/admin/comment/page?pageNum=1&pageSize=5&postId=1`
3. `GET /api/admin/comment/page?pageNum=1&pageSize=5&userId=1`
4. `GET /api/admin/comment/page?pageNum=1&pageSize=5&status=ENABLE`
5. `GET /api/admin/comment/detail/{commentId}`
6. `POST /api/admin/comment/enable/{commentId}`
7. `POST /api/admin/comment/disable/{commentId}`

## 5. 主要错误码
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
14. `414` 文件不存在
15. `415` 文件类型错误
16. `416` 文件大小错误
17. `417` 文件状态错误
18. `418` 文件上传失败
19. `419` 文件删除失败
20. `422` 参数错误
21. `500` 服务器异常

## 6. 回归用例对应
1. `src/test/http/account.http`
2. `src/test/http/file.http`
3. `src/test/http/post.http`
4. `src/test/http/comment.http`
5. `src/test/http/like.http`
6. `src/test/http/admin.http`
