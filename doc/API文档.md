# Nuist CampusWall API文档（V1.1）

## 1. 统一返回结构
所有接口统一返回：

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

字段说明：
1. `code`：业务状态码，`0` 表示成功，非 `0` 表示失败。
2. `message`：提示信息。
3. `data`：业务数据，失败时通常为 `null`。

## 2. 账户模块

### 2.1 用户注册
- 路径：`POST /api/account/register`
- 描述：创建新用户账号。

请求体：
```json
{
  "username": "test1001",
  "password": "123456",
  "nickname": "测试用户1001",
  "educationEmail": "test1001@nuist.edu.cn"
}
```

成功响应示例：
```json
{
  "code": 0,
  "message": "success",
  "data": "注册成功"
}
```

失败响应示例（用户名重复）：
```json
{
  "code": 401,
  "message": "用户名已存在",
  "data": null
}
```

失败响应示例（邮箱重复）：
```json
{
  "code": 402,
  "message": "邮箱已存在",
  "data": null
}
```

### 2.2 用户登录
- 路径：`POST /api/account/login`
- 描述：校验账号密码并返回用户基础信息（当前版本未返回 JWT）。

请求体：
```json
{
  "username": "test1",
  "password": "123456"
}
```

成功响应示例：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 6,
    "username": "test1",
    "nickname": "测试用户1001",
    "educationEmail": "test1001@nuist.edu.cn",
    "imageUrl": null,
    "role": "USER",
    "status": "ENABLE"
  }
}
```

失败响应示例（用户名不存在）：
```json
{
  "code": 403,
  "message": "用户名不存在",
  "data": null
}
```

失败响应示例（用户禁用）：
```json
{
  "code": 404,
  "message": "用户已被禁用",
  "data": null
}
```

失败响应示例（密码错误）：
```json
{
  "code": 405,
  "message": "密码错误",
  "data": null
}
```

## 3. 业务状态码（当前）
1. `0`：成功
2. `401`：用户名已存在
3. `402`：邮箱已存在
4. `403`：用户名不存在
5. `404`：用户已被禁用
6. `405`：密码错误
7. `500`：服务器异常（全局异常兜底）

## 4. 待补充接口
1. 获取当前用户 `GET /api/account/me`
2. JWT 刷新/退出登录接口（可选）
3. 帖子模块接口
4. 评论模块接口
5. 点赞模块接口
