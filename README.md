# Nuist CampusWall

基于 Spring Boot + MyBatis-Plus 的校园墙后端项目（课程期末作业）。

## 1. 项目目标
1. 面向校园场景，提供用户注册登录、发帖、评论、点赞、个人中心能力。
2. 后端先行完成接口闭环，前端后续按小红书风格逐步接入。
3. 当前阶段重点是“稳定可演示 + 文档可答辩 + 代码可扩展”。

## 2. 当前完成度（截至 2026-03-27）
### 2.1 账户模块
1. `POST /api/account/register` 注册（查重 + BCrypt 加密）。
2. `POST /api/account/login` 登录（密码校验 + JWT 签发）。
3. `GET /api/account/my` 获取当前登录用户。

### 2.2 帖子模块
1. `POST /api/post/create` 发布帖子。
2. `GET /api/post/page` 公开帖子分页（仅 `ENABLE`）。
3. `GET /api/post/my/page` 我的帖子分页。
4. `GET /api/post/{id}` 帖子详情。
5. `POST /api/post/update/{id}` 修改我的帖子。
6. `POST /api/post/delete/{id}` 删除我的帖子（软删除）。

### 2.3 评论模块
1. `POST /api/comment/create` 发布评论/回复。
2. `GET /api/comment/page` 评论分页（按帖子）。
3. `GET /api/comment/my/page` 我的评论分页。
4. `POST /api/comment/delete/{id}` 删除我的评论（软删除）。

### 2.4 点赞模块
1. `POST /api/like/do` 点赞。
2. `POST /api/like/undo` 取消点赞。
3. 支持目标：帖子、评论。
4. 具备防重复点赞和计数维护。

### 2.5 通用能力
1. 统一返回体 `Result<T>`。
2. `BusinessException` + `GlobalExceptionHandler`。
3. JWT 鉴权拦截器与用户上下文。
4. 统一错误码 `ErrorCode`。

## 3. 技术栈
1. Java 21
2. Spring Boot 4.0.3
3. MyBatis-Plus 3.5.15
4. MySQL 8.x
5. JWT（jjwt 0.12.7）
6. Lombok + Validation

## 4. 项目结构
```text
src/main/java/com/nuist_campuswall
├─ common        # 统一返回、异常、错误码
├─ config        # WebMvc、MyBatis-Plus配置
├─ controller    # 接口层（account/post/comment/like）
├─ domain        # 实体类
├─ dto           # 入参DTO
├─ mapper        # 数据访问层
├─ security      # JWT工具、拦截器、用户上下文
└─ service       # 业务层

src/main/resources/sql
├─ db_init.sql   # 全量建表脚本（含索引）
└─ db_seed.sql   # 初始化数据
```

## 5. 快速启动
1. 配置 `src/main/resources/application.properties` 的数据库连接与 JWT 参数。
2. 确认本地 MySQL 已创建数据库 `campuswall`。
3. 启动：

```bash
mvn spring-boot:run
```

## 6. 接口回归文件
1. `src/test/http/account.http`
2. `src/test/http/post.http`
3. `src/test/http/comment.http`
4. `src/test/http/like.http`
5. `src/test/http/http-client.env.json`

## 7. 下一步计划（主线）
1. 管理员权限拦截（基于 JWT 的 role）。
2. 管理员用户/帖子/评论管理接口。
3. 文档终版与答辩材料收口。
