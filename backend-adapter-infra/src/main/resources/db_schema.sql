# ************************************************************
# Sequel Ace SQL dump
# Version 20092
#
# https://sequel-ace.com/
# https://github.com/Sequel-Ace/Sequel-Ace
#
# Host: 10.251.63.114 (MySQL 9.2.0)
# Database: backend
# Generation Time: 2025-06-09 06:16:29 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
SET NAMES utf8mb4;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE='NO_AUTO_VALUE_ON_ZERO', SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table board_post_count
# ------------------------------------------------------------

CREATE TABLE `board_post_count` (
  `board_id` bigint NOT NULL,
  `post_count` bigint NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`board_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# Dump of table comment
# ------------------------------------------------------------

CREATE TABLE `comment` (
  `comment_id` bigint unsigned NOT NULL,
  `content` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `post_id` bigint NOT NULL,
  `parent_comment_id` bigint NOT NULL,
  `writer_id` bigint NOT NULL,
  `deleted` tinyint(1) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`comment_id`),
  KEY `idx_article_id_parent_comment_id_comment_id` (`post_id`,`parent_comment_id`,`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# Dump of table comment_v2
# ------------------------------------------------------------

CREATE TABLE `comment_v2` (
  `comment_id` bigint NOT NULL,
  `content` varchar(3000) NOT NULL,
  `post_id` bigint NOT NULL,
  `writer_id` bigint NOT NULL,
  `path` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `deleted` tinyint(1) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`comment_id`),
  UNIQUE KEY `idx_article_id_path` (`post_id`,`path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# Dump of table member
# ------------------------------------------------------------

CREATE TABLE `member` (
  `member_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `provider_id` varchar(30) DEFAULT NULL,
  `provider` varchar(10) DEFAULT NULL,
  `nickname` varchar(80) DEFAULT NULL,
  `profile_image_url` varchar(100) DEFAULT NULL,
  `role` varchar(10) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# Dump of table outbox
# ------------------------------------------------------------

CREATE TABLE `outbox` (
  `outbox_id` bigint NOT NULL,
  `shard_key` bigint NOT NULL,
  `event_type` varchar(100) NOT NULL,
  `payload` varchar(5000) NOT NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`outbox_id`),
  KEY `idx_shard_key_created_at` (`shard_key`,`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# Dump of table post
# ------------------------------------------------------------

CREATE TABLE `post` (
  `post_id` bigint unsigned NOT NULL,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `board_id` bigint NOT NULL,
  `writer_id` bigint NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`post_id`),
  KEY `idx_board_id_article_id` (`board_id`,`post_id` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# Dump of table post_comment_count
# ------------------------------------------------------------

CREATE TABLE `post_comment_count` (
  `post_id` bigint NOT NULL,
  `comment_count` bigint NOT NULL,
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# Dump of table post_like
# ------------------------------------------------------------

CREATE TABLE `post_like` (
  `post_like_id` bigint NOT NULL,
  `post_id` bigint NOT NULL,
  `member_id` bigint NOT NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`post_like_id`),
  UNIQUE KEY `idx_article_id_user_id` (`post_id`,`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# Dump of table post_like_count
# ------------------------------------------------------------

CREATE TABLE `post_like_count` (
  `post_id` bigint NOT NULL,
  `like_count` bigint NOT NULL,
  `version` bigint NOT NULL,
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# Dump of table post_view_count
# ------------------------------------------------------------

CREATE TABLE `post_view_count` (
  `post_id` bigint NOT NULL,
  `view_count` bigint NOT NULL,
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# Dump of table refresh_token
# ------------------------------------------------------------

CREATE TABLE `refresh_token` (
  `refresh_token_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `member_id` bigint DEFAULT NULL,
  `device_id` varchar(50) DEFAULT NULL,
  `refresh_token` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `expires_at` datetime DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`refresh_token_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
