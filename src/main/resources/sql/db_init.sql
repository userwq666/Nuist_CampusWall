USE campuswall;

-- user table
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户 ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '登录用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '加密密码',
    `nickname` VARCHAR(50) NOT NULL COMMENT '昵称',
    `education_email` VARCHAR(100) NOT NULL COMMENT '教育邮箱',
    `image_url` VARCHAR(255) DEFAULT NULL COMMENT '头像链接',
    `role` TINYINT NOT NULL DEFAULT 0 COMMENT '角色：0 普通用户，1 管理员',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1 启用，0 禁用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_education_email` (`education_email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- post table
CREATE TABLE IF NOT EXISTS `post` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '帖子 ID',
    `user_id` BIGINT NOT NULL COMMENT '作者用户 ID',
    `title` VARCHAR(100) NOT NULL COMMENT '标题',
    `content` TEXT NOT NULL COMMENT '内容',
    `image_url` VARCHAR(255) DEFAULT NULL COMMENT '图片链接',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1 显示，0 隐藏',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY `idx_post_user_id` (`user_id`),
    CONSTRAINT `fk_post_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子表';

-- comment table
CREATE TABLE IF NOT EXISTS `comment` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论 ID',
    `user_id` BIGINT NOT NULL COMMENT '评论用户 ID',
    `post_id` BIGINT NOT NULL COMMENT '目标帖子 ID',
    `content` VARCHAR(500) NOT NULL COMMENT '评论内容',
    `image_url` VARCHAR(255) DEFAULT NULL COMMENT '图片链接',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1 显示，0 隐藏',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY `idx_comment_user_id` (`user_id`),
    KEY `idx_comment_post_id` (`post_id`),
    CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_comment_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';
