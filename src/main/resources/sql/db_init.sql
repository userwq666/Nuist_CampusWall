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

-- 文件资源表
CREATE TABLE IF NOT EXISTS `file_asset` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文件ID',
    `owner_user_id` BIGINT NOT NULL COMMENT '上传用户ID',
    `file_type` TINYINT NOT NULL COMMENT '文件类型: 1帖子图片, 2评论图片, 3头像',
    `biz_type` TINYINT DEFAULT NULL COMMENT '业务类型: 1帖子, 2评论, 3头像',
    `biz_id` BIGINT DEFAULT NULL COMMENT '业务ID',
    `original_name` VARCHAR(255) NOT NULL COMMENT '原始文件名',
    `object_key` VARCHAR(255) NOT NULL COMMENT '对象存储路径key',
    `url` VARCHAR(500) NOT NULL COMMENT '访问URL',
    `mime_type` VARCHAR(100) NOT NULL COMMENT 'MIME类型',
    `file_ext` VARCHAR(20) DEFAULT NULL COMMENT '文件后缀',
    `file_size` BIGINT NOT NULL COMMENT '文件大小(字节)',
    `sha256` CHAR(64) DEFAULT NULL COMMENT '文件内容摘要',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0临时, 1已绑定, 2已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_file_asset_object_key` (`object_key`),
    KEY `idx_file_asset_owner_status_create_time` (`owner_user_id`, `status`, `create_time`),
    KEY `idx_file_asset_file_type_status_create_time` (`file_type`, `status`, `create_time`),
    KEY `idx_file_asset_biz_type_biz_id` (`biz_type`, `biz_id`),
    KEY `idx_file_asset_sha256` (`sha256`),
    CONSTRAINT `fk_file_asset_owner_user` FOREIGN KEY (`owner_user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `ck_file_asset_type` CHECK (`file_type` IN (1, 2, 3)),
    CONSTRAINT `ck_file_asset_biz_type` CHECK (`biz_type` IS NULL OR `biz_type` IN (1, 2, 3)),
    CONSTRAINT `ck_file_asset_status` CHECK (`status` IN (0, 1, 2)),
    CONSTRAINT `ck_file_asset_bind_pair` CHECK (
        (`biz_type` IS NULL AND `biz_id` IS NULL)
        OR (`biz_type` IS NOT NULL AND `biz_id` IS NOT NULL)
    )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件资源表';
