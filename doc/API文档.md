# Nuist CampusWall API 文档（V3.3）

## 1. 统一返回格式（三段式）
```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```
说明：
1. 成功：`code=0`。
2. 失败：`code!=0`，`message` 为错误说明。
3. 协议层仍使用 HTTP 状态码表达错误大类。

## 2. 鉴权说明
1. 受保护接口统一要求请求头：`Authorization: Bearer <token>`。
2. 放行接口：
   - `POST /api/account/register`
   - `POST /api/account/login`
3. 管理员接口前缀：`/api/admin/**`，要求角色 `ADMIN`。

## 3. 核心接口
### 3.1 account
1. `POST /api/account/register`
2. `POST /api/account/login`
3. `GET /api/account/my`
4. `POST /api/account/my/update`

### 3.2 file
1. `POST /api/file/upload?fileType=POST`
2. `POST /api/file/upload?fileType=COMMENT`
3. `POST /api/file/upload?fileType=AVATAR`

### 3.3 post
1. `POST /api/post/create`
2. `GET /api/post/page`
3. `GET /api/post/notice/page`
4. `GET /api/post/my/page`
5. `GET /api/post/{id}`
6. `POST /api/post/update/{id}`
7. `POST /api/post/delete/{id}`

### 3.4 comment
1. `POST /api/comment/create`
2. `GET /api/comment/page`
3. `GET /api/comment/my/page`
4. `POST /api/comment/delete/{id}`

### 3.5 like
1. `POST /api/like/do`
2. `POST /api/like/undo`

### 3.6 admin
1. `GET /api/admin/ping`
2. `GET /api/admin/user/page`
3. `POST /api/admin/user/enable/{userId}`
4. `POST /api/admin/user/disable/{userId}`
5. `GET /api/admin/post/page`
6. `GET /api/admin/post/detail/{postId}`
7. `POST /api/admin/post/enable/{postId}`
8. `POST /api/admin/post/disable/{postId}`
9. `GET /api/admin/comment/page`
10. `GET /api/admin/comment/detail/{commentId}`
11. `POST /api/admin/comment/enable/{commentId}`
12. `POST /api/admin/comment/disable/{commentId}`

## 4. 推荐联调断言
1. 先看 HTTP 状态码（200/401/403/404/409/413/415/422/500）。
2. 再看响应体 `code`（例如 `0/406/412/409/410/417`）。
3. 页面提示优先使用 `message`。

## 5. 回归用例对应
1. `src/test/http/account.http`
2. `src/test/http/file.http`
3. `src/test/http/post.http`
4. `src/test/http/comment.http`
5. `src/test/http/like.http`
6. `src/test/http/admin.http`