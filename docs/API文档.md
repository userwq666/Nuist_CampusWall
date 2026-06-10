# Nuist CampusWall API 文档（V5.1）

最后同步：2026-06-10

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

（其余接口列表保持不变，见原文档）
