# Nuist CampusWall API 文档（V4.0）

## 1. 统一返回格式
```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

规则：
1. `code=0`：业务成功。
2. `code!=0`：业务失败。
3. 失败场景同时配合 HTTP 状态码表达错误类别。

## 2. 鉴权规则
1. 受保护接口要求：`Authorization: Bearer <token>`。
2. 放行接口：
   - `POST /api/account/register`
   - `POST /api/account/login`
   - `GET /api/post/page`
   - `GET /api/post/notice/page`
3. 管理员接口前缀：`/api/admin/**`，要求 `Role.ADMIN`。

## 3. 用户端接口
### 3.1 account
1. `POST /api/account/register`
2. `POST /api/account/login`
3. `GET /api/account/my`
4. `POST /api/account/my/update`

### 3.2 file
1. `POST /api/file/upload?fileType=POST`
2. `POST /api/file/upload?fileType=COMMENT`
3. `POST /api/file/upload?fileType=AVATAR`
- 请求类型：`multipart/form-data`，字段名 `file`
- 返回 `data` 为文件 ID（`Long`）

### 3.3 post
1. `POST /api/post/create` — 请求体：`{ title, content, fileID }`
2. `GET /api/post/page?pageNum=1&pageSize=5`
3. `GET /api/post/notice/page?pageNum=1&pageSize=5`
4. `GET /api/post/my/page?pageNum=1&pageSize=5`
5. `GET /api/post/{id}`
6. `POST /api/post/update/{id}` — 请求体：`{ title, content, fileId }`
7. `POST /api/post/delete/{id}`

### 3.4 comment
1. `POST /api/comment/create` — 请求体：`{ postId, content, replyToCommentId?, replyToUserId?, fileId? }`
2. `GET /api/comment/page?postId=xx&pageNum=1&pageSize=5`
3. `GET /api/comment/my/page?pageNum=1&pageSize=5`
4. `POST /api/comment/delete/{id}`

### 3.5 like
1. `POST /api/like/do` — 请求体：`{ targetType: "POST"|"COMMENT", targetId }`
2. `POST /api/like/undo` — 同上

## 4. 管理端接口
1. `GET /api/admin/ping` — 鉴权探针
2. `GET /api/admin/user/page?pageNum=1&pageSize=10`
3. `POST /api/admin/user/enable/{userId}`
4. `POST /api/admin/user/disable/{userId}`
5. `GET /api/admin/post/page?pageNum=1&pageSize=10`
6. `GET /api/admin/post/detail/{postId}`
7. `POST /api/admin/post/enable/{postId}`
8. `POST /api/admin/post/disable/{postId}`
9. `GET /api/admin/comment/page?pageNum=1&pageSize=10`
10. `GET /api/admin/comment/detail/{commentId}`
11. `POST /api/admin/comment/enable/{commentId}`
12. `POST /api/admin/comment/disable/{commentId}`

## 5. 字段注意
1. `post/create` 用 `fileID`（大写 ID），`post/update` 用 `fileId`（小写 d）。
2. `comment/create` 用 `fileId`（小写 d）。

## 6. 联调断言建议
1. 先断言 HTTP 状态码。
2. 再断言 `code`。
3. 页面提示优先显示 `message`。
