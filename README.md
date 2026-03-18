# Nuist CampusWall

基于 Spring Boot 的校园信息发布与互动平台（后端）。

## 项目概览
当前项目已完成账户模块注册、登录、JWT 鉴权基础闭环，并已建立统一异常与统一返回规范，可作为后续帖子、评论与点赞模块开发基础。

## 当前已实现
1. `POST /api/account/register`
2. `POST /api/account/login`
3. `GET /api/account/me`（从 token 获取当前用户）
4. 用户名查重（MyBatis-Plus `selectCount`）
5. 邮箱查重（MyBatis-Plus `selectCount`）
6. 密码 BCrypt 加密入库
7. 登录密码校验（`BCryptPasswordEncoder.matches`）
8. 登录返回 `LoginRespVO`（`token + userInfo`）
9. JWT 签发与解析（`JwtUtil`）
10. 鉴权拦截（`JwtAuthInterceptor` + `WebMvcConfig`）
11. 统一返回体 `Result<T>`
12. 全局异常处理 `GlobalExceptionHandler`
13. 错误码常量化（`ErrorCode`，401~408、500）

## 技术栈
1. Java 21
2. Spring Boot 4.0.3
3. MyBatis-Plus `mybatis-plus-spring-boot4-starter` 3.5.15
4. MySQL 8
5. Lombok
6. `spring-security-crypto`（仅用于密码加密）

## 项目结构（核心）
```text
src/main/java/com/nuist_campuswall
├─ common
│  ├─ Result.java
│  ├─ BusinessException.java
│  ├─ ErrorCode.java
│  └─ GlobalExceptionHandler.java
├─ config
│  ├─ initCheck.java
│  └─ WebMvcConfig.java
├─ controller/account
├─ service/account
├─ mapper/user
├─ security
│  ├─ JwtUtil.java
│  ├─ JwtAuthInterceptor.java
│  └─ UserContext.java
├─ domain
│  ├─ user
│  ├─ post
│  ├─ comment
│  ├─ like
│  └─ enums
└─ NuistCampusWallApplication.java

src/main/resources
├─ application.properties
└─ sql
   ├─ db_init.sql
   ├─ db_seed.sql
   └─ ER.vsdx
```

## 接口快速测试
测试文件位置：[src/test/http/account.http](src/test/http/account.http)

推荐使用 IntelliJ HTTP Client 直接运行。

## 数据库说明
当前 `db_init.sql` 约定：
1. `user`：用户信息
2. `post`：帖子（含 `like_count`）
3. `comment`：评论（扁平楼层 + 回复字段）
4. `like`：点赞记录（唯一约束防重复点赞）

重点约束：
1. `comment.reply_to_comment_id` 与 `comment.reply_to_user_id` 必须同空或同非空
2. `like` 表唯一键 `(user_id, target_type, target_id)` 防重复点赞

## 运行方式
```bash
mvn spring-boot:run
```

## 下一阶段计划
1. 鉴权完善（业务码与 HTTP 状态映射）
2. 账户模块收口（`LoginVO` 映射复用、参数校验注解）
3. 帖子模块（发布/列表/详情）
4. 评论模块（发布/回复/列表）
5. 点赞模块（帖子与评论点赞/取消）

## 文档目录
1. [需求说明](doc/需求说明.md)
2. [开发日志](doc/开发日志.md)
3. [数据字典](doc/数据字典.md)
4. [API 文档](doc/API文档.md)
5. [部署说明](doc/部署说明.md)
6. [测试记录](doc/测试记录.md)
7. [架构说明](doc/架构说明.md)
8. [错误码说明](doc/错误码说明.md)
9. [里程碑计划](doc/里程碑计划.md)
