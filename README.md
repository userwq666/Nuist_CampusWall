# Nuist CampusWall

基于 Spring Boot 的校园内容发布与互动平台（后端）。

## 1. 项目概览
本项目是期末课程作业，采用前后端分离架构。当前后端已完成“账户 + JWT 鉴权 + 帖子 + 评论”的核心基础链路，已具备继续接入 Vue 前端联调的条件。

## 2. 当前已实现功能
### 2.1 账户模块
1. `POST /api/account/register` 用户注册
2. `POST /api/account/login` 用户登录
3. `GET /api/account/me` 获取当前登录用户
4. 注册查重（用户名、教育邮箱）
5. 密码 BCrypt 加密存储
6. 登录密码 BCrypt 校验

### 2.2 JWT 鉴权
1. 登录成功签发 JWT
2. `JwtAuthInterceptor` 拦截 `/api/**`
3. 放行 `/api/account/register`、`/api/account/login`
4. 鉴权通过后将 `userId` 放入 `UserContext`

### 2.3 帖子模块
1. `POST /api/post/create` 发布帖子
2. `GET /api/post/page` 帖子分页查询
3. `GET /api/post/{id}` 帖子详情
4. DTO 参数校验（`@Valid` + `@NotBlank/@Size/@Min/@Max`）

### 2.4 评论模块
1. `POST /api/comment/create` 发表评论/回复
2. `GET /api/comment/page` 评论分页查询（按 `postId`）
3. 评论发布时校验目标帖子是否存在

### 2.5 通用能力
1. 统一返回体 `Result<T>`
2. 统一业务异常 `BusinessException`
3. 全局异常处理 `GlobalExceptionHandler`
4. 错误码常量化 `ErrorCode`（401~409、422、500）

## 3. 技术栈（与代码一致）
1. Java 21
2. Spring Boot 4.0.3
3. MyBatis-Plus 3.5.15（Boot4 starter）
4. MySQL 8
5. JJWT 0.12.7
6. spring-security-crypto（仅用于 BCrypt）
7. Jakarta Validation

## 4. 目录结构（核心）
```text
src/main/java/com/nuist_campuswall
├── common          # Result / ErrorCode / Exception
├── config          # MVC、MP、启动检查
├── controller      # account / post / comment
├── domain          # user / post / comment / like / enums
├── dto             # account、post、comment、common
├── mapper          # user / post / comment
├── security        # JwtUtil / JwtAuthInterceptor / UserContext
└── service         # 业务层

src/main/resources
├── application.properties
└── sql
    ├── db_init.sql
    └── db_seed.sql

src/test/http
├── account.http
├── post.http
├── comment.http
└── http-client.env.json
```

## 5. 快速启动
### 5.1 配置数据库
修改 `src/main/resources/application.properties`：
1. `spring.datasource.url`
2. `spring.datasource.username`
3. `spring.datasource.password`

项目启动时会自动执行：
1. `classpath:sql/db_init.sql`
2. `classpath:sql/db_seed.sql`

### 5.2 启动后端
```bash
mvn spring-boot:run
```

默认端口：`8080`

### 5.3 HTTP 接口测试
1. 打开 `src/test/http/http-client.env.json`
2. 在 `dev` 环境配置 `host`、`token`
3. 按顺序执行：
   - `account.http`（注册/登录/me）
   - `post.http`（发帖/分页/详情）
   - `comment.http`（评论创建/分页）

## 6. 数据模型说明（摘要）
1. `user`：用户表
2. `post`：帖子表（含 `like_count`）
3. `comment`：评论表（扁平楼层 + 回复字段）
4. `like`：点赞行为记录表（唯一键防重复）

关键约束：
1. `comment.reply_to_comment_id` 与 `comment.reply_to_user_id` 必须同时为空或同时非空
2. `like` 表唯一键：`(user_id, target_type, target_id)`

## 7. 下一阶段计划
1. 完成点赞模块（帖子/评论点赞与取消）
2. 补齐管理员鉴权与管理接口（用户/帖子/评论状态流转）
3. 优化账号模块参数校验（注册/登录 DTO 增强）
4. 输出接口联调清单，进入 Vue 前端联调

## 8. 项目文档
1. `doc/需求说明.md`
2. `doc/架构说明.md`
3. `doc/API文档.md`
4. `doc/错误码说明.md`
5. `doc/数据字典.md`
6. `doc/测试记录.md`
7. `doc/开发日志.md`
8. `doc/里程碑计划.md`
9. `doc/部署说明.md`
10. `doc/文档索引.md`
