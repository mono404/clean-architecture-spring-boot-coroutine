#
************************************************************
# Sequel Ace SQL dump
# Version 20094
#
# https://sequel-ace.com/
# https://github.com/Sequel-Ace/Sequel-Ace
#
# Host: 10.251.63.114 (MySQL 9.3.0)
# Database: backend
# Generation Time: 2025-08-20 08:41:11 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
SET NAMES utf8mb4;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE='NO_AUTO_VALUE_ON_ZERO', SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


#
Dump of table board_post_count
# ------------------------------------------------------------

DROP TABLE IF EXISTS `board_post_count`;

CREATE TABLE `board_post_count`
(
    `board_id`   bigint   NOT NULL,
    `post_count` bigint   NOT NULL,
    `created_at` datetime NOT NULL,
    PRIMARY KEY (`board_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



#
Dump of table campsite
# ------------------------------------------------------------

DROP TABLE IF EXISTS `campsite`;

CREATE TABLE `campsite`
(
    `campsite_id`                int NOT NULL AUTO_INCREMENT,
    `fclty_nm`                   varchar(255)   DEFAULT NULL,
    `ctgry_one_nm`               varchar(255)   DEFAULT NULL,
    `ctgry_two_nm`               varchar(255)   DEFAULT NULL,
    `ctgry_three_nm`             varchar(255)   DEFAULT NULL,
    `ctprvn_nm`                  varchar(255)   DEFAULT NULL,
    `signgu_nm`                  varchar(255)   DEFAULT NULL,
    `legaldong_nm`               varchar(255)   DEFAULT NULL,
    `li_nm`                      varchar(255)   DEFAULT NULL,
    `lnbr_no`                    varchar(255)   DEFAULT NULL,
    `road_nm`                    varchar(255)   DEFAULT NULL,
    `buld_no`                    varchar(255)   DEFAULT NULL,
    `lc_la`                      decimal(11, 9) DEFAULT NULL,
    `lc_lo`                      decimal(12, 9) DEFAULT NULL,
    `coord`                      point GENERATED ALWAYS AS (st_srid(point(`lc_lo`, `lc_la`), 4326)) STORED NOT NULL,
    `zip_no`                     varchar(255)   DEFAULT NULL,
    `rdnmadr_nm`                 varchar(255)   DEFAULT NULL,
    `lnm_addr`                   varchar(255)   DEFAULT NULL,
    `tel_no`                     varchar(255)   DEFAULT NULL,
    `hmpg_url`                   varchar(255)   DEFAULT NULL,
    `manage_mby_nm`              varchar(255)   DEFAULT NULL,
    `workday_oper_at`            varchar(255)   DEFAULT NULL,
    `wkend_oper_at`              varchar(255)   DEFAULT NULL,
    `spr_oper_at`                varchar(255)   DEFAULT NULL,
    `sumr_oper_at`               varchar(255)   DEFAULT NULL,
    `fall_oper_at`               varchar(255)   DEFAULT NULL,
    `wnt_oper_at`                varchar(255)   DEFAULT NULL,
    `elect_provd_at`             varchar(255)   DEFAULT NULL,
    `hwater_provd_at`            varchar(255)   DEFAULT NULL,
    `wifi_hold_at`               varchar(255)   DEFAULT NULL,
    `fwood_sle_at`               varchar(255)   DEFAULT NULL,
    `wlk_road_at`                varchar(255)   DEFAULT NULL,
    `waterpark_hold_at`          varchar(255)   DEFAULT NULL,
    `ply_fclty_hold_at`          varchar(255)   DEFAULT NULL,
    `mart_hold_at`               varchar(255)   DEFAULT NULL,
    `toilet_co`                  int            DEFAULT NULL,
    `shwerrm_co`                 int            DEFAULT NULL,
    `sink_co`                    int            DEFAULT NULL,
    `fetgs_co`                   int            DEFAULT NULL,
    `cfr_fshng_fclty_at`         varchar(255)   DEFAULT NULL,
    `cfr_wlk_road_fclty_at`      varchar(255)   DEFAULT NULL,
    `cfr_beach_fclty_at`         varchar(255)   DEFAULT NULL,
    `cfr_prize_lsr_fclty_at`     varchar(255)   DEFAULT NULL,
    `cfr_vall_fclty_at`          varchar(255)   DEFAULT NULL,
    `cfr_river_fclty_at`         varchar(255)   DEFAULT NULL,
    `cfr_pool_fclty_at`          varchar(255)   DEFAULT NULL,
    `cfr_ynbgs_exprn_fclty_at`   varchar(255)   DEFAULT NULL,
    `cfr_fafv_exprn_fclty_at`    varchar(255)   DEFAULT NULL,
    `cfr_child_ply_fclty_at`     varchar(255)   DEFAULT NULL,
    `glamping_bed_hold_at`       varchar(255)   DEFAULT NULL,
    `glamping_tv_hold_at`        varchar(255)   DEFAULT NULL,
    `glamping_fridge_hold_at`    varchar(255)   DEFAULT NULL,
    `glamping_wifi_hold_at`      varchar(255)   DEFAULT NULL,
    `glamping_in_toilet_hold_at` varchar(255)   DEFAULT NULL,
    `glamping_ac_hold_at`        varchar(255)   DEFAULT NULL,
    `glamping_heater_hold_at`    varchar(255)   DEFAULT NULL,
    `glamping_ckng_tool_hold_at` varchar(255)   DEFAULT NULL,
    `etc_fclty_dc`               text,
    `fclty_intrcn_cn`            text,
    `last_updt_de`               date           DEFAULT NULL,
    PRIMARY KEY (`campsite_id`),
    SPATIAL KEY `idx_campsite_coord` (`coord`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



#
Dump of table comment
# ------------------------------------------------------------

DROP TABLE IF EXISTS `comment`;

CREATE TABLE `comment`
(
    `comment_id`        bigint unsigned NOT NULL,
    `content`           varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
    `post_id`           bigint                                                         NOT NULL,
    `parent_comment_id` bigint                                                         NOT NULL,
    `deleted`           tinyint(1) NOT NULL,
    `created_at`        datetime                                                       NOT NULL,
    `updated_at`        datetime                                                       NOT NULL,
    `member_id`         bigint                                                       NOT NULL,
    `nickname`          varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
    `profile_image_url` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
    PRIMARY KEY (`comment_id`),
    KEY                 `idx_post_id_parent_comment_id_comment_id` (`post_id`,`parent_comment_id`,`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



#
Dump of table comment_v2
# ------------------------------------------------------------

DROP TABLE IF EXISTS `comment_v2`;

CREATE TABLE `comment_v2`
(
    `comment_id`        bigint                                                       NOT NULL,
    `content`           varchar(3000)                                                NOT NULL,
    `post_id`           bigint                                                       NOT NULL,
    `path`              varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin        NOT NULL,
    `deleted`           tinyint(1) NOT NULL,
    `created_at`        datetime                                                     NOT NULL,
    `updated_at`        datetime                                                     NOT NULL,
    `member_id`         bigint                                                       NOT NULL,
    `nickname`          varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
    `profile_image_url` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
    PRIMARY KEY (`comment_id`),
    UNIQUE KEY `idx_post_id_path` (`post_id`,`path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



#
Dump of table dead_letter_queue
# ------------------------------------------------------------

DROP TABLE IF EXISTS `dead_letter_queue`;

CREATE TABLE `dead_letter_queue`
(
    `dead_letter_queue_id` bigint       NOT NULL,
    `event_type`           varchar(100) NOT NULL,
    `aggregate_id`         varchar(100) NOT NULL,
    `payload_json`         json         NOT NULL,
    `attempts`             int          NOT NULL,
    `last_error`           text,
    `moved_at`             datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PRIMARY KEY (`dead_letter_queue_id`),
    KEY                    `idx_dlq_moved_at` (`moved_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



#
Dump of table fcm_token
# ------------------------------------------------------------

DROP TABLE IF EXISTS `fcm_token`;

CREATE TABLE `fcm_token`
(
    `fcm_token_id` bigint       NOT NULL,
    `member_id`    bigint       NOT NULL,
    `fcm_token`    varchar(255) NOT NULL,
    `device_id`    varchar(200) NOT NULL,
    `created_at`   timestamp    NOT NULL,
    `updated_at`   timestamp    NOT NULL,
    PRIMARY KEY (`fcm_token_id`),
    UNIQUE KEY `member_id` (`member_id`,`device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



#
Dump of table media_file
# ------------------------------------------------------------

DROP TABLE IF EXISTS `media_file`;

CREATE TABLE `media_file`
(
    `media_file_id` bigint unsigned NOT NULL,
    `owner_type`    varchar(50)                                                   NOT NULL,
    `owner_id`      bigint                                                        NOT NULL,
    `url`           varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
    `media_type`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
    `size`          bigint   DEFAULT NULL,
    `created_at`    datetime DEFAULT NULL,
    PRIMARY KEY (`media_file_id`),
    KEY             `idx_owner_type_id` (`owner_type`,`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



#
Dump of table member
# ------------------------------------------------------------

DROP TABLE IF EXISTS `member`;

CREATE TABLE `member`
(
    `member_id`         bigint unsigned NOT NULL,
    `provider_id`       varchar(30)                                                   DEFAULT NULL,
    `provider`          varchar(10)                                                   DEFAULT NULL,
    `nickname`          varchar(80)                                                   DEFAULT NULL,
    `profile_image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
    `role`              varchar(10)                                                   DEFAULT NULL,
    `created_at`        datetime NOT NULL,
    `updated_at`        datetime NOT NULL,
    PRIMARY KEY (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



#
Dump of table notification
# ------------------------------------------------------------

DROP TABLE IF EXISTS `notification`;

CREATE TABLE `notification`
(
    `notification_id`          bigint   NOT NULL,
    `member_id`                bigint   NOT NULL,
    `notification_template_id` bigint   NOT NULL,
    `is_read`                  tinyint(1) DEFAULT '0',
    `created_at`               datetime NOT NULL,
    PRIMARY KEY (`notification_id`),
    KEY                        `notification_template_id` (`notification_template_id`),
    CONSTRAINT `notification_ibfk_1` FOREIGN KEY (`notification_template_id`) REFERENCES `notification_template` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



#
Dump of table notification_template
# ------------------------------------------------------------

DROP TABLE IF EXISTS `notification_template`;

CREATE TABLE `notification_template`
(
    `id`         bigint        NOT NULL,
    `type`       varchar(50)   NOT NULL,
    `title`      varchar(255)  NOT NULL,
    `body`       varchar(1000) NOT NULL,
    `metadata`   json DEFAULT NULL,
    `ttl_days`   int  DEFAULT '3',
    `created_at` datetime NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



#
Dump of table outbox
# ------------------------------------------------------------

DROP TABLE IF EXISTS `outbox`;

CREATE TABLE `outbox`
(
    `outbox_id`           bigint       NOT NULL,
    `event_type`          varchar(100) NOT NULL,
    `aggregate_id`        varchar(100) NOT NULL,
    `payload_json`        json         NOT NULL,
    `status`              enum('PENDING','PROCESSING','DONE','FAILED') NOT NULL DEFAULT 'PENDING',
    `attempts`            int          NOT NULL DEFAULT '0',
    `next_attempt_at`     datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `processing_deadline` datetime(3) DEFAULT NULL,
    `last_error`          text,
    `created_at`          datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `updated_at`          datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PRIMARY KEY (`outbox_id`),
    KEY                   `idx_outbox_status_next_attempt` (`status`,`next_attempt_at`),
    KEY                   `idx_outbox_status_deadln` (`status`,`processing_deadline`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



#
Dump of table post
# ------------------------------------------------------------

DROP TABLE IF EXISTS `post`;

CREATE TABLE `post`
(
    `post_id`           bigint unsigned NOT NULL,
    `title`             varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL,
    `content`           varchar(9000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
    `board_id`          bigint                                                         NOT NULL,
    `created_at`        datetime                                                       NOT NULL,
    `updated_at`        datetime                                                       NOT NULL,
    `member_id`         bigint                                                         NOT NULL,
    `nickname`          varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci   NOT NULL,
    `profile_image_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
    PRIMARY KEY (`post_id`),
    KEY                 `idx_board_id_post_id` (`board_id`,`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



#
Dump of table post_comment_count
# ------------------------------------------------------------

DROP TABLE IF EXISTS `post_comment_count`;

CREATE TABLE `post_comment_count`
(
    `post_id`       bigint   NOT NULL,
    `comment_count` bigint   NOT NULL,
    `created_at`    datetime NOT NULL,
    PRIMARY KEY (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



#
Dump of table post_like
# ------------------------------------------------------------

DROP TABLE IF EXISTS `post_like`;

CREATE TABLE `post_like`
(
    `post_like_id` bigint   NOT NULL,
    `post_id`      bigint   NOT NULL,
    `member_id`    bigint   NOT NULL,
    `created_at`   datetime NOT NULL,
    PRIMARY KEY (`post_like_id`),
    UNIQUE KEY `idx_post_id_member_id` (`post_id`,`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



#
Dump of table post_like_count
# ------------------------------------------------------------

DROP TABLE IF EXISTS `post_like_count`;

CREATE TABLE `post_like_count`
(
    `post_id`    bigint NOT NULL,
    `like_count` bigint NOT NULL,
    `version`    bigint NOT NULL,
    PRIMARY KEY (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



#
Dump of table post_view_count
# ------------------------------------------------------------

DROP TABLE IF EXISTS `post_view_count`;

CREATE TABLE `post_view_count`
(
    `post_id`    bigint NOT NULL,
    `view_count` bigint NOT NULL,
    PRIMARY KEY (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



#
Dump of table refresh_token
# ------------------------------------------------------------

DROP TABLE IF EXISTS `refresh_token`;

CREATE TABLE `refresh_token`
(
    `refresh_token_id` bigint unsigned NOT NULL,
    `member_id`        bigint                                                         DEFAULT NULL,
    `device_id`        varchar(50)                                                    DEFAULT NULL,
    `refresh_token`    varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
    `expires_at`       datetime NOT NULL,
    `created_at`       datetime NOT NULL,
    PRIMARY KEY (`refresh_token_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



#
Dump of table search_index
# ------------------------------------------------------------

DROP TABLE IF EXISTS `search_index`;

CREATE TABLE `search_index`
(
    `search_index_id`   bigint                                                       NOT NULL,
    `domain_id`         int                                                          NOT NULL,
    `board_id`          int                                                          NOT NULL,
    `title`             text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
    `content`           text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
    `comment`           text,
    `created_at`        datetime                                                     NOT NULL,
    `updated_at`        datetime                                                     NOT NULL,
    `member_id`         bigint                                                       NOT NULL,
    `nickname`          varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
    `profile_image_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
    PRIMARY KEY (`search_index_id`),
    FULLTEXT KEY `ft_all` (`title`,`content`,`comment`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
