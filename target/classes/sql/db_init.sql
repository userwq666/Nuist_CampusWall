USE campuswall;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '登录账号',
    `password` VARCHAR(100) NOT NULL COMMENT '加密后的密码',
    `nickname` VARCHAR(50) NOT NULL COMMENT '昵称',
    `education_email` VARCHAR(100) NOT NULL COMMENT '教育邮箱',
    `image_url` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `role` TINYINT NOT NULL DEFAULT 0 COMMENT '角色: 0用户, 1管理员',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1启用, 0禁用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_education_email` (`education_email`),
    KEY `idx_user_status_create_time` (`status`, `create_time`),
    KEY `idx_user_role_status` (`role`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 帖子表
CREATE TABLE IF NOT EXISTS `post` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '帖子ID',
    `user_id` BIGINT NOT NULL COMMENT '作者用户ID',
    `title` VARCHAR(100) NOT NULL COMMENT '标题',
    `content` TEXT NOT NULL COMMENT '内容',
    `image_url` VARCHAR(255) DEFAULT NULL COMMENT '图片URL',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1显示, 0隐藏',
    `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY `idx_post_user_id` (`user_id`),
    KEY `idx_post_status_create_time` (`status`, `create_time`),
    KEY `idx_post_user_status_create_time` (`user_id`, `status`, `create_time`),
    CONSTRAINT `fk_post_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子表';

-- 评论表（扁平楼层 + 回复）
CREATE TABLE IF NOT EXISTS `comment` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    `user_id` BIGINT NOT NULL COMMENT '评论用户ID',
    `post_id` BIGINT NOT NULL COMMENT '目标帖子ID',
    `reply_to_comment_id` BIGINT DEFAULT NULL COMMENT '被回复评论ID',
    `reply_to_user_id` BIGINT DEFAULT NULL COMMENT '被回复用户ID',
    `content` VARCHAR(500) NOT NULL COMMENT '评论内容',
    `image_url` VARCHAR(255) DEFAULT NULL COMMENT '图片URL',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1显示, 0隐藏',
    `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY `idx_comment_user_id` (`user_id`),
    KEY `idx_comment_post_id` (`post_id`),
    KEY `idx_comment_reply_to_comment_id` (`reply_to_comment_id`),
    KEY `idx_comment_reply_to_user_id` (`reply_to_user_id`),
    KEY `idx_comment_status_create_time` (`status`, `create_time`),
    KEY `idx_comment_post_status_create_time` (`post_id`, `status`, `create_time`),
    KEY `idx_comment_user_status_create_time` (`user_id`, `status`, `create_time`),
    CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_comment_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`),
    CONSTRAINT `fk_comment_reply_to_comment` FOREIGN KEY (`reply_to_comment_id`) REFERENCES `comment` (`id`),
    CONSTRAINT `fk_comment_reply_to_user` FOREIGN KEY (`reply_to_user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `ck_comment_reply_pair` CHECK (
        (`reply_to_comment_id` IS NULL AND `reply_to_user_id` IS NULL)
        OR (`reply_to_comment_id` IS NOT NULL AND `reply_to_user_id` IS NOT NULL)
    )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 点赞记录表
CREATE TABLE IF NOT EXISTS `like` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '点赞记录ID',
    `user_id` BIGINT NOT NULL COMMENT '点赞用户ID',
    `target_type` TINYINT NOT NULL COMMENT '目标类型: 1帖子, 2评论',
    `target_id` BIGINT NOT NULL COMMENT '目标ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_user_target` (`user_id`, `target_type`, `target_id`),
    KEY `idx_target` (`target_type`, `target_id`),
    KEY `idx_like_user_create_time` (`user_id`, `create_time`),
    CONSTRAINT `fk_user_like_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞记录表';