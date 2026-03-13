# 南信大校园墙 (Nuist CampusWall)

一个基于 Spring Boot 的校园社交信息发布平台。

## 项目简介

南信大校园墙是一个面向南京信息工程大学师生的校园信息交流平台，
为校内用户提供便捷的信息发布与浏览服务。

## 技术栈

- **开发语言**: Java 17
- **核心框架**: Spring Boot 4.0.3
- **数据库**: MySQL
- **其他依赖**: 
  - spring-boot-starter-webmvc
  - mysql-connector-java
  - Lombok
  - spring-boot-devtools

## 快速开始

### 环境要求

- JDK 17
- MySQL 数据库

### 启动方式

```bash
# 使用 Maven Wrapper 启动
./mvnw spring-boot:run

# 或打包后运行
java -jar Nuist_CampusWall.jar
```

## 项目结构

```
Nuist_CampusWall/
├── src/main/java/com/nuist_campuswall/
│   ├── config/           # 配置类
│   ├── domain/           # 领域模型
│   │   ├── post/        # 帖子模块
│   │   ├── comment/     # 评论模块
│   │   └── user/        # 用户模块
│   └── NuistCampusWallApplication.java  # 主启动类
├── src/main/resources/
│   ├── application.properties  # 配置文件
│   └── sql/                   # SQL 脚本
└── doc/                     # 项目文档
```

## 数据库设计

- **数据库 ER 图（Visio）**: [`src/main/resources/sql/数据库 ER 图.vsdx`](src/main/resources/sql/数据库 ER 图.vsdx)
- **数据库初始化脚本**: [`src/main/resources/sql/db_init.sql`](src/main/resources/sql/db_init.sql)
- **测试数据脚本**: [`src/main/resources/sql/db_seed.sql`](src/main/resources/sql/db_seed.sql)

## 开发日志

详见 [开发日志](doc/开发日志.md)

## 需求说明

详见 [需求说明](doc/需求说明.md)
