# Nuist CampusWall

基于 Spring Boot 的校园信息发布与互动平台（后端）。

## 项目概览
当前项目已完成账户模块的注册与登录主链路，并已建立统一异常与统一返回规范，可作为后续 JWT 鉴权、帖子与评论模块的基础。

## 当前已实现
1. `POST /api/account/register`
2. `POST /api/account/login`
3. 用户名查重（MyBatis-Plus `selectCount`）
4. 邮箱查重（MyBatis-Plus `selectCount`）
5. 密码 BCrypt 加密入库
6. 登录密码校验（`BCryptPasswordEncoder.matches`）
7. 登录返回 `LoginVO`（不返回密码）
8. 统一返回体 `Result<T>`
9. 全局异常处理 `GlobalExceptionHandler`
10. 错误码规范：
   - 401（用户名已存在）
   - 402（邮箱已存在）
   - 403（用户名不存在）
   - 404（用户已被禁用）
   - 405（密码错误）

## 技术栈
1. Java 17
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
│  └─ GlobalExceptionHandler.java
├─ controller/account
├─ service/account
├─ mapper/user
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
1. JWT 签发与解析
2. 鉴权拦截（放行 register/login）
3. `/api/account/me` 当前用户接口
4. 帖子模块（发布/列表/详情）
5. 评论模块（发布/回复/列表）
6. 点赞模块（帖子与评论点赞/取消）

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
