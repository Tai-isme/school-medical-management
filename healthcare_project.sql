-- Adminer 5.3.0 MySQL 8.0.42 dump

SET NAMES utf8;
SET time_zone = '+00:00';
SET foreign_key_checks = 0;
SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';

SET NAMES utf8mb4;

DROP TABLE IF EXISTS `blacklisted_tokens`;
CREATE TABLE `blacklisted_tokens` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `token` varchar(255) NOT NULL,
  `expiry_date` datetime NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `token` (`token`),
  KEY `fk_blacklisted_user` (`user_id`),
  CONSTRAINT `fk_blacklisted_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `class`;
CREATE TABLE `class` (
  `class_id` int NOT NULL AUTO_INCREMENT,
  `teacher_name` varchar(100) DEFAULT NULL,
  `class_name` varchar(20) DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  PRIMARY KEY (`class_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `class` (`class_id`, `teacher_name`, `class_name`, `quantity`) VALUES
(1,	'Nguyen Van A',	'1A',	30),
(2,	'Tran Thi B',	'2B',	28),
(3,	'Le Van C',	'3C',	35);

DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback` (
  `feedback_id` int NOT NULL AUTO_INCREMENT,
  `satisfaction` varchar(20) DEFAULT NULL,
  `comment` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `parent_id` int DEFAULT NULL,
  `nurse_id` int DEFAULT NULL,
  `vaccine_result_id` int DEFAULT NULL,
  `health_result_id` int DEFAULT NULL,
  `response_nurse` text,
  `status` varchar(20) DEFAULT 'NOT_REPLIED',
  PRIMARY KEY (`feedback_id`),
  KEY `parent_id` (`parent_id`),
  KEY `nurse_id` (`nurse_id`),
  KEY `vaccine_result_id` (`vaccine_result_id`),
  KEY `health_result_id` (`health_result_id`),
  CONSTRAINT `feedback_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `feedback_ibfk_2` FOREIGN KEY (`nurse_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `feedback_ibfk_3` FOREIGN KEY (`vaccine_result_id`) REFERENCES `vaccine_result` (`vaccine_result_id`) ON DELETE CASCADE,
  CONSTRAINT `feedback_ibfk_4` FOREIGN KEY (`health_result_id`) REFERENCES `health_check_result` (`health_result_id`) ON DELETE CASCADE,
  CONSTRAINT `feedback_chk_1` CHECK ((`satisfaction` in (_utf8mb4'SATISFIED',_utf8mb4'UNSATISFIED'))),
  CONSTRAINT `feedback_chk_2` CHECK ((`status` in (_utf8mb4'NOT_REPLIED',_utf8mb4'REPLIED')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `health_check_form`;
CREATE TABLE `health_check_form` (
  `health_check_form_id` int NOT NULL AUTO_INCREMENT,
  `health_check_id` int DEFAULT NULL,
  `student_id` int DEFAULT NULL,
  `parent_id` int DEFAULT NULL,
  `nurse_id` int DEFAULT NULL,
  `form_date` date DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `commit` tinyint(1) DEFAULT NULL,
  `status` varchar(10) DEFAULT 'draft',
  PRIMARY KEY (`health_check_form_id`),
  KEY `health_check_id` (`health_check_id`),
  KEY `student_id` (`student_id`),
  KEY `parent_id` (`parent_id`),
  KEY `nurse_id` (`nurse_id`),
  CONSTRAINT `health_check_form_ibfk_1` FOREIGN KEY (`health_check_id`) REFERENCES `health_check_program` (`health_check_id`) ON DELETE CASCADE,
  CONSTRAINT `health_check_form_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`) ON DELETE CASCADE,
  CONSTRAINT `health_check_form_ibfk_3` FOREIGN KEY (`parent_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `health_check_form_ibfk_4` FOREIGN KEY (`nurse_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `health_check_form_chk_1` CHECK ((`status` in (_utf8mb4'DRAFT',_utf8mb4'SENT')))
) ENGINE=InnoDB AUTO_INCREMENT=388 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `health_check_form` (`health_check_form_id`, `health_check_id`, `student_id`, `parent_id`, `nurse_id`, `form_date`, `notes`, `commit`, `status`) VALUES
(386,	7,	209,	10,	9,	'2025-06-18',	NULL,	NULL,	'SENT'),
(387,	7,	216,	1,	9,	'2025-06-18',	NULL,	NULL,	'DRAFT');

DROP TABLE IF EXISTS `health_check_program`;
CREATE TABLE `health_check_program` (
  `health_check_id` int NOT NULL AUTO_INCREMENT,
  `health_check_name` varchar(100) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `status` varchar(50) DEFAULT 'NOT_STARTED',
  `note` varchar(255) DEFAULT NULL,
  `admin_id` int DEFAULT NULL,
  PRIMARY KEY (`health_check_id`),
  KEY `admin_id` (`admin_id`),
  CONSTRAINT `health_check_program_ibfk_1` FOREIGN KEY (`admin_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `health_check_program_chk_1` CHECK ((`status` in (_utf8mb4'ON_GOING',_utf8mb4'COMPLETED',_utf8mb4'NOT_STARTED')))
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `health_check_program` (`health_check_id`, `health_check_name`, `description`, `start_date`, `end_date`, `status`, `note`, `admin_id`) VALUES
(7,	'Health Check - Semester 1',	'Kiểm tra sức khỏe đầu năm học 2025',	'2025-09-01',	'2025-09-10',	'NOT_STARTED',	'12345',	1),
(8,	'Health Check - Midterm',	'Kiểm tra sức khỏe giữa học kỳ',	'2025-11-01',	'2025-11-05',	'ON_GOING',	'Tập trung vào mắt và cột sống',	2),
(9,	'Health Check - Final',	'Kiểm tra sức khỏe cuối năm học',	'2025-12-10',	'2025-12-20',	'COMPLETED',	'Đã hoàn thành kiểm tra toàn trường',	1);

DROP TABLE IF EXISTS `health_check_result`;
CREATE TABLE `health_check_result` (
  `health_result_id` int NOT NULL AUTO_INCREMENT,
  `diagnosis` varchar(255) DEFAULT NULL,
  `level` varchar(50) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `health_check_form_id` int DEFAULT NULL,
  PRIMARY KEY (`health_result_id`),
  KEY `health_check_form_id` (`health_check_form_id`),
  CONSTRAINT `health_check_result_ibfk_1` FOREIGN KEY (`health_check_form_id`) REFERENCES `health_check_form` (`health_check_form_id`) ON DELETE CASCADE,
  CONSTRAINT `health_check_result_chk_1` CHECK ((`level` in (_utf8mb4'GOOD',_utf8mb4'FAIR',_utf8mb4'AVERAGE',_utf8mb4'POOR')))
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `medical_event`;
CREATE TABLE `medical_event` (
  `event_id` int NOT NULL AUTO_INCREMENT,
  `type_event` varchar(100) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `student_id` int DEFAULT NULL,
  `nurse_id` int DEFAULT NULL,
  PRIMARY KEY (`event_id`),
  KEY `student_id` (`student_id`),
  KEY `nurse_id` (`nurse_id`),
  CONSTRAINT `medical_event_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`) ON DELETE CASCADE,
  CONSTRAINT `medical_event_ibfk_2` FOREIGN KEY (`nurse_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `medical_event` (`event_id`, `type_event`, `date`, `description`, `student_id`, `nurse_id`) VALUES
(6,	'Ngất xỉu trong lớp',	'2025-06-17',	'Học sinh ngất xỉu khi đang học môn Thể dục, đã được sơ cứu và chuyển đến phòng y tế.',	209,	9),
(7,	'Ngất xỉu trong lớp',	'2025-06-17',	'Học sinh ngất xỉu khi đang học môn Thể dục, đã được sơ cứu và chuyển đến phòng y tế.',	216,	9),
(8,	'Ngất xỉu trong lớp',	'2025-06-17',	'Học sinh ngất xỉu khi đang học môn Thể dục, đã được sơ cứu và chuyển đến phòng y tế.',	226,	9);

DROP TABLE IF EXISTS `medical_record`;
CREATE TABLE `medical_record` (
  `record_id` int NOT NULL AUTO_INCREMENT,
  `student_id` int DEFAULT NULL,
  `allergies` varchar(255) DEFAULT NULL,
  `chronic_disease` varchar(255) DEFAULT NULL,
  `treatment_history` varchar(255) DEFAULT NULL,
  `vision` varchar(50) DEFAULT NULL,
  `hearing` varchar(50) DEFAULT NULL,
  `weight` float DEFAULT NULL,
  `height` float DEFAULT NULL,
  `last_update` datetime DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`record_id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `medical_record_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `medical_record` (`record_id`, `student_id`, `allergies`, `chronic_disease`, `treatment_history`, `vision`, `hearing`, `weight`, `height`, `last_update`, `note`) VALUES
(7,	205,	'Pollen',	'Asthma',	'Used inhaler for 3 years',	'Normal',	'Normal',	45.5,	150,	'2025-06-08 16:18:24',	'Student is in good condition'),
(8,	225,	'string',	'string',	'string',	'Unknown',	'',	1,	30,	'2025-06-08 16:18:57',	'string'),
(10,	213,	'2',	'2',	'2',	'2',	'2',	2,	2,	'2025-06-08 16:18:57',	'2');

DROP TABLE IF EXISTS `medical_request`;
CREATE TABLE `medical_request` (
  `request_id` int NOT NULL AUTO_INCREMENT,
  `request_name` varchar(100) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `status` varchar(50) DEFAULT 'PROCESSING',
  `student_id` int DEFAULT NULL,
  `parent_id` int DEFAULT NULL,
  PRIMARY KEY (`request_id`),
  KEY `student_id` (`student_id`),
  KEY `parent_id` (`parent_id`),
  CONSTRAINT `medical_request_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`) ON DELETE CASCADE,
  CONSTRAINT `medical_request_ibfk_2` FOREIGN KEY (`parent_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `chk_status` CHECK ((`status` in (_utf8mb4'SUBMITTED',_utf8mb4'COMPLETED',_utf8mb4'PROCESSING',_utf8mb4'CANCELLED')))
) ENGINE=InnoDB AUTO_INCREMENT=150793 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `medical_request` (`request_id`, `request_name`, `date`, `note`, `status`, `student_id`, `parent_id`) VALUES
(150792,	'Yêu cầu cấp phát thuốc',	'2025-06-18',	'Học sinh bị sốt nhẹ, cần uống thuốc theo đơn',	'PROCESSING',	209,	1);

DROP TABLE IF EXISTS `medical_request_detail`;
CREATE TABLE `medical_request_detail` (
  `detail_id` int NOT NULL AUTO_INCREMENT,
  `medicine_name` varchar(100) DEFAULT NULL,
  `dosage` varchar(100) DEFAULT NULL,
  `time` varchar(200) DEFAULT NULL,
  `request_id` int DEFAULT NULL,
  PRIMARY KEY (`detail_id`),
  KEY `request_id` (`request_id`),
  CONSTRAINT `medical_request_detail_ibfk_1` FOREIGN KEY (`request_id`) REFERENCES `medical_request` (`request_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=150796 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `medical_request_detail` (`detail_id`, `medicine_name`, `dosage`, `time`, `request_id`) VALUES
(150794,	'Paracetamol 500mg',	'1 viên',	'Sáng sau ăn',	150792),
(150795,	'Vitamin C',	'1 viên',	'Chiều sau ăn',	150792);

DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `title` varchar(255) NOT NULL,
  `content` text,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_notification_user` (`user_id`),
  CONSTRAINT `fk_notification_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=133 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `notification` (`id`, `user_id`, `title`, `content`, `created_at`) VALUES
(128,	10,	'Thông báo chương trình tiêm vaccine cho học sinh Lâm Thị 20',	'Bạn có phiếu thông báo tiêm chủng vaccine mới cần xác nhận.',	'2025-06-16 08:05:59'),
(129,	10,	'Thông báo chương trình tiêm vaccine cho học sinh Sử Văn 30',	'Bạn có phiếu thông báo tiêm chủng vaccine mới cần xác nhận.',	'2025-06-16 08:05:59'),
(130,	10,	'Thông báo chương trình tiêm vaccine cho học sinh Đàm Minh 40',	'Bạn có phiếu thông báo tiêm chủng vaccine mới cần xác nhận.',	'2025-06-16 08:05:59'),
(131,	1,	'Thông báo chương trình khám sức khỏe định kỳ',	'Bạn có phiếu thông báo khám sức khỏe định kỳ mới cần xác nhận.',	'2025-06-18 16:19:46'),
(132,	1,	'Thông báo chương trình khám sức khỏe định kỳ',	'Bạn có phiếu thông báo khám sức khỏe định kỳ mới cần xác nhận.',	'2025-06-18 16:20:28');

DROP TABLE IF EXISTS `refresh_token`;
CREATE TABLE `refresh_token` (
  `id` int NOT NULL AUTO_INCREMENT,
  `refresh_token` varchar(255) NOT NULL,
  `expiry_date` datetime NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `refresh_token` (`refresh_token`),
  UNIQUE KEY `user_id` (`user_id`),
  CONSTRAINT `fk_refresh_token_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `refresh_token` (`id`, `refresh_token`, `expiry_date`, `created_at`, `updated_at`, `user_id`) VALUES
(2,	'88b3dca1-7bb9-4496-8939-c1a32c4cd708',	'2025-06-18 02:55:34',	'2025-06-07 21:30:59',	'2025-06-11 02:55:34',	1),
(3,	'eaf1b115-3e89-4a9f-99c3-e54182513b74',	'2025-06-18 03:41:46',	'2025-06-07 21:31:29',	'2025-06-11 03:41:46',	2),
(4,	'76a09f59-ee59-4a56-bc40-02ddef71b7dc',	'2025-06-18 02:57:39',	'2025-06-07 21:32:02',	'2025-06-11 02:57:39',	3),
(5,	'6b730114-918e-4654-ac05-56be5e9167ac',	'2025-06-18 02:58:04',	'2025-06-07 21:32:44',	'2025-06-11 02:58:04',	4),
(6,	'8161c7e2-0318-4cc1-b2ca-fc0c0e5bb011',	'2025-06-18 03:00:03',	'2025-06-07 21:33:12',	'2025-06-11 03:00:03',	5),
(7,	'2cc987b5-3514-4432-a81a-60a1d0e57f02',	'2025-06-18 03:00:30',	'2025-06-07 21:33:35',	'2025-06-11 03:00:30',	6),
(8,	'b73a8475-85d5-454a-98dc-8bf70941b530',	'2025-06-18 03:00:53',	'2025-06-07 21:33:59',	'2025-06-11 03:00:53',	7),
(9,	'4e2a3984-4586-4a40-9464-64bab3fae516',	'2025-06-19 07:42:43',	'2025-06-07 21:34:25',	'2025-06-12 07:42:43',	8),
(10,	'62046d5f-4c4f-4566-a458-f4652a931302',	'2025-06-25 16:06:04',	'2025-06-07 21:34:51',	'2025-06-18 16:06:04',	9),
(11,	'09e7d6c9-14d4-4d6e-9e89-289573729a5b',	'2025-06-25 17:40:25',	'2025-06-07 21:35:16',	'2025-06-18 17:40:25',	10);

DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `student_id` int NOT NULL AUTO_INCREMENT,
  `student_name` varchar(100) NOT NULL,
  `dob` date DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `relationship` varchar(50) DEFAULT NULL,
  `class_id` int DEFAULT NULL,
  `parent_id` int DEFAULT NULL,
  PRIMARY KEY (`student_id`),
  KEY `class_id` (`class_id`),
  KEY `parent_id` (`parent_id`),
  CONSTRAINT `student_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `class` (`class_id`) ON DELETE CASCADE,
  CONSTRAINT `student_ibfk_2` FOREIGN KEY (`parent_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=304 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `student` (`student_id`, `student_name`, `dob`, `gender`, `relationship`, `class_id`, `parent_id`) VALUES
(204,	'Nguyễn Văn 1',	'2012-01-01',	'Nam',	'Con trai',	1,	3),
(205,	'Trần Thị 2',	'2011-09-15',	'Nữ',	'Con gái',	1,	7),
(206,	'Lê Hoàng 3',	'2012-03-22',	'Nam',	'Con trai',	2,	2),
(207,	'Phạm Minh 4',	'2011-12-10',	'Nam',	'Con trai',	1,	9),
(208,	'Hoàng Thị 5',	'2012-07-18',	'Nữ',	'Con gái',	3,	4),
(209,	'Vũ Đức 6',	'2011-05-30',	'Nam',	'Con trai',	2,	10),
(210,	'Đặng Lan 7',	'2012-11-25',	'Nữ',	'Con gái',	1,	6),
(211,	'Bùi Văn 8',	'2011-08-14',	'Nam',	'Con trai',	3,	8),
(212,	'Ngô Thị 9',	'2012-02-28',	'Nữ',	'Con gái',	2,	5),
(213,	'Dương Hữu 10',	'2011-10-12',	'Nam',	'Con trai',	1,	10),
(214,	'Lý Thị 11',	'2012-06-05',	'Nữ',	'Con gái',	3,	3),
(215,	'Trịnh Văn 12',	'2011-04-17',	'Nam',	'Con trai',	2,	7),
(216,	'Võ Minh 13',	'2012-09-23',	'Nam',	'Con trai',	1,	1),
(217,	'Phan Thị 14',	'2011-07-08',	'Nữ',	'Con gái',	3,	4),
(218,	'Tạ Đức 15',	'2012-12-31',	'Nam',	'Con trai',	2,	9),
(219,	'Mai Lan 16',	'2011-03-15',	'Nữ',	'Con gái',	1,	2),
(220,	'Chu Văn 17',	'2012-08-07',	'Nam',	'Con trai',	3,	6),
(221,	'Đỗ Thị 18',	'2011-11-20',	'Nữ',	'Con gái',	2,	8),
(222,	'Cao Hữu 19',	'2012-04-03',	'Nam',	'Con trai',	1,	5),
(223,	'Lâm Thị 20',	'2011-06-26',	'Nữ',	'Con gái',	3,	10),
(224,	'Đinh Văn 21',	'2012-01-14',	'Nam',	'Con trai',	2,	3),
(225,	'Hồ Minh 22',	'2011-05-29',	'Nam',	'Con trai',	1,	7),
(226,	'Kiều Thị 23',	'2012-10-11',	'Nữ',	'Con gái',	3,	1),
(227,	'Lưu Đức 24',	'2011-12-24',	'Nam',	'Con trai',	2,	4),
(228,	'Mạch Lan 25',	'2012-07-16',	'Nữ',	'Con gái',	1,	9),
(229,	'Ninh Văn 26',	'2011-02-09',	'Nam',	'Con trai',	3,	2),
(230,	'Ông Thị 27',	'2012-09-01',	'Nữ',	'Con gái',	2,	6),
(231,	'Quách Hữu 28',	'2011-04-25',	'Nam',	'Con trai',	1,	8),
(232,	'Rồng Thị 29',	'2012-11-18',	'Nữ',	'Con gái',	3,	5),
(233,	'Sử Văn 30',	'2011-08-02',	'Nam',	'Con trai',	2,	10),
(234,	'Thái Minh 31',	'2012-03-27',	'Nam',	'Con trai',	1,	3),
(235,	'Ưng Thị 32',	'2011-06-19',	'Nữ',	'Con gái',	3,	7),
(236,	'Văn Đức 33',	'2012-12-05',	'Nam',	'Con trai',	2,	1),
(237,	'Xa Lan 34',	'2011-01-28',	'Nữ',	'Con gái',	1,	4),
(238,	'Yên Văn 35',	'2012-05-21',	'Nam',	'Con trai',	3,	9),
(239,	'Âu Thị 36',	'2011-10-13',	'Nữ',	'Con gái',	2,	2),
(240,	'Ấu Hữu 37',	'2012-08-30',	'Nam',	'Con trai',	1,	6),
(241,	'Bạch Thị 38',	'2011-03-06',	'Nữ',	'Con gái',	3,	8),
(242,	'Cung Văn 39',	'2012-11-22',	'Nam',	'Con trai',	2,	5),
(243,	'Đàm Minh 40',	'2011-07-15',	'Nam',	'Con trai',	1,	10),
(244,	'Én Thị 41',	'2012-02-12',	'Nữ',	'Con gái',	3,	3),
(245,	'Phùng Đức 42',	'2011-09-28',	'Nam',	'Con trai',	2,	7),
(246,	'Giang Lan 43',	'2012-06-14',	'Nữ',	'Con gái',	1,	1),
(247,	'Hà Văn 44',	'2011-12-07',	'Nam',	'Con trai',	3,	4),
(248,	'Ỉn Thị 45',	'2012-04-26',	'Nữ',	'Con gái',	2,	9),
(249,	'Kha Hữu 46',	'2011-11-11',	'Nam',	'Con trai',	1,	2),
(250,	'Long Thị 47',	'2012-08-04',	'Nữ',	'Con gái',	3,	6),
(251,	'Mã Văn 48',	'2011-05-18',	'Nam',	'Con trai',	2,	8),
(252,	'Nông Minh 49',	'2012-01-31',	'Nam',	'Con trai',	1,	5),
(253,	'Ô Thị 50',	'2011-10-24',	'Nữ',	'Con gái',	3,	10),
(254,	'Pù Đức 51',	'2012-07-09',	'Nam',	'Con trai',	2,	3),
(255,	'Quế Lan 52',	'2011-04-02',	'Nữ',	'Con gái',	1,	7),
(256,	'Sơn Văn 53',	'2012-12-18',	'Nam',	'Con trai',	3,	1),
(257,	'Tôn Thị 54',	'2011-06-11',	'Nữ',	'Con gái',	2,	4),
(258,	'Ung Hữu 55',	'2012-03-05',	'Nam',	'Con trai',	1,	9),
(259,	'Vương Thị 56',	'2011-09-21',	'Nữ',	'Con gái',	3,	2),
(260,	'Xương Văn 57',	'2012-11-08',	'Nam',	'Con trai',	2,	6),
(261,	'Yết Minh 58',	'2011-02-14',	'Nam',	'Con trai',	1,	8),
(262,	'Ấn Thị 59',	'2012-08-27',	'Nữ',	'Con gái',	3,	5),
(263,	'Bảo Đức 60',	'2011-05-12',	'Nam',	'Con trai',	2,	10),
(264,	'Cam Lan 61',	'2012-01-25',	'Nữ',	'Con gái',	1,	3),
(265,	'Đan Văn 62',	'2011-10-17',	'Nam',	'Con trai',	3,	7),
(266,	'Êm Thị 63',	'2012-06-03',	'Nữ',	'Con gái',	2,	1),
(267,	'Phong Hữu 64',	'2011-12-20',	'Nam',	'Con trai',	1,	4),
(268,	'Gôn Thị 65',	'2012-09-06',	'Nữ',	'Con gái',	3,	9),
(269,	'Hoan Văn 66',	'2011-03-30',	'Nam',	'Con trai',	2,	2),
(270,	'Ít Minh 67',	'2012-11-15',	'Nam',	'Con trai',	1,	6),
(271,	'Kỳ Thị 68',	'2011-07-29',	'Nữ',	'Con gái',	3,	8),
(272,	'Lục Đức 69',	'2012-04-11',	'Nam',	'Con trai',	2,	5),
(273,	'Mạnh Lan 70',	'2011-01-04',	'Nữ',	'Con gái',	1,	10),
(274,	'Nghiêm Văn 71',	'2012-08-20',	'Nam',	'Con trai',	3,	3),
(275,	'Ôn Thị 72',	'2011-06-05',	'Nữ',	'Con gái',	2,	7),
(276,	'Pháp Hữu 73',	'2012-02-22',	'Nam',	'Con trai',	1,	1),
(277,	'Quang Thị 74',	'2011-11-14',	'Nữ',	'Con gái',	3,	4),
(278,	'Rạng Văn 75',	'2012-07-31',	'Nam',	'Con trai',	2,	9),
(279,	'Sinh Minh 76',	'2011-04-16',	'Nam',	'Con trai',	1,	2),
(280,	'Thành Thị 77',	'2012-12-03',	'Nữ',	'Con gái',	3,	6),
(281,	'Ước Đức 78',	'2011-08-19',	'Nam',	'Con trai',	2,	8),
(282,	'Vân Lan 79',	'2012-05-07',	'Nữ',	'Con gái',	1,	5),
(283,	'Xuân Văn 80',	'2011-01-23',	'Nam',	'Con trai',	3,	10),
(284,	'Ý Thị 81',	'2012-10-10',	'Nữ',	'Con gái',	2,	3),
(285,	'Ảnh Hữu 82',	'2011-07-03',	'Nam',	'Con trai',	1,	7),
(286,	'Bích Thị 83',	'2012-03-19',	'Nữ',	'Con gái',	3,	1),
(287,	'Cầm Văn 84',	'2011-12-11',	'Nam',	'Con trai',	2,	4),
(288,	'Đào Minh 85',	'2012-09-24',	'Nam',	'Con trai',	1,	9),
(289,	'Ê Thị 86',	'2011-05-08',	'Nữ',	'Con gái',	3,	2),
(290,	'Phúc Đức 87',	'2012-01-20',	'Nam',	'Con trai',	2,	6),
(291,	'Gia Lan 88',	'2011-10-06',	'Nữ',	'Con gái',	1,	8),
(292,	'Hạnh Văn 89',	'2012-06-28',	'Nam',	'Con trai',	3,	5),
(293,	'Ích Thị 90',	'2011-02-11',	'Nữ',	'Con gái',	2,	10),
(294,	'Kiên Hữu 91',	'2012-11-27',	'Nam',	'Con trai',	1,	3),
(295,	'Lợi Thị 92',	'2011-08-13',	'Nữ',	'Con gái',	3,	7),
(296,	'Minh Văn 93',	'2012-04-29',	'Nam',	'Con trai',	2,	1),
(297,	'Nghĩa Minh 94',	'2011-01-15',	'Nam',	'Con trai',	1,	4),
(298,	'Ơn Thị 95',	'2012-10-02',	'Nữ',	'Con gái',	3,	9),
(299,	'Phước Đức 96',	'2011-06-18',	'Nam',	'Con trai',	2,	2),
(300,	'Quyền Lan 97',	'2012-02-05',	'Nữ',	'Con gái',	1,	6),
(301,	'Sáng Văn 98',	'2011-09-22',	'Nam',	'Con trai',	3,	8),
(302,	'Thịnh Thị 99',	'2012-12-14',	'Nữ',	'Con gái',	2,	5),
(303,	'Uyển Hữu 100',	'2011-02-11',	'Nam',	'Con trai',	1,	10);

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `full_name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `address` varchar(200) DEFAULT NULL,
  `role` varchar(50) DEFAULT 'PARENT',
  PRIMARY KEY (`user_id`),
  CONSTRAINT `users_chk_1` CHECK ((`role` in (_utf8mb4'PARENT',_utf8mb4'NURSE',_utf8mb4'ADMIN')))
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `users` (`user_id`, `full_name`, `email`, `password`, `phone`, `address`, `role`) VALUES
(1,	'Parent 1',	'',	'',	'0707333797',	'HCM',	'PARENT'),
(2,	'Parent 2',	'',	'',	'0797687276',	'HCM',	'PARENT'),
(3,	'Parent 3',	'',	'',	'0389617970',	'HCM',	'PARENT'),
(4,	'Parent 4',	'',	'',	'0767988879',	'HCM',	'PARENT'),
(5,	'Parent 5',	'',	'',	'0938824674',	'HCM',	'PARENT'),
(6,	'Parent 6',	'',	'',	'0913538249',	'HCM',	'PARENT'),
(7,	'Parent 7',	'',	'',	'0898158174',	'HCM',	'PARENT'),
(8,	'Parent 8',	'nguyenhonghieutai7a9@gmail.com',	'',	'',	'HCM',	'ADMIN'),
(9,	'Parent 9',	'tainhhse182011@fpt.edu.vn',	'',	'',	'HCM',	'NURSE'),
(10,	'Parent 10',	'nguyenhonghieutai10cba6@gmail.com',	'',	'',	'HCM',	'PARENT');

DROP TABLE IF EXISTS `vaccine_form`;
CREATE TABLE `vaccine_form` (
  `vaccine_form_id` int NOT NULL AUTO_INCREMENT,
  `vaccine_id` int DEFAULT NULL,
  `student_id` int DEFAULT NULL,
  `parent_id` int DEFAULT NULL,
  `nurse_id` int DEFAULT NULL,
  `form_date` date DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `commit` tinyint(1) DEFAULT NULL,
  `status` varchar(10) DEFAULT 'draft',
  PRIMARY KEY (`vaccine_form_id`),
  KEY `vaccine_id` (`vaccine_id`),
  KEY `student_id` (`student_id`),
  KEY `parent_id` (`parent_id`),
  KEY `nurse_id` (`nurse_id`),
  CONSTRAINT `vaccine_form_ibfk_1` FOREIGN KEY (`vaccine_id`) REFERENCES `vaccine_program` (`vaccine_id`) ON DELETE CASCADE,
  CONSTRAINT `vaccine_form_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`) ON DELETE CASCADE,
  CONSTRAINT `vaccine_form_ibfk_3` FOREIGN KEY (`parent_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `vaccine_form_ibfk_4` FOREIGN KEY (`nurse_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `chk_vaccine_form_status` CHECK ((`status` in (_utf8mb4'DRAFT',_utf8mb4'SENT')))
) ENGINE=InnoDB AUTO_INCREMENT=301 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `vaccine_history`;
CREATE TABLE `vaccine_history` (
  `vaccine_history_id` int NOT NULL AUTO_INCREMENT,
  `vaccine_name` varchar(100) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `record_id` int DEFAULT NULL,
  `vaccine_id` int DEFAULT NULL,
  PRIMARY KEY (`vaccine_history_id`),
  KEY `record_id` (`record_id`),
  KEY `fk_vaccine_history_vaccine_program` (`vaccine_id`),
  CONSTRAINT `fk_vaccine_history_vaccine_program` FOREIGN KEY (`vaccine_id`) REFERENCES `vaccine_program` (`vaccine_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `vaccine_history_ibfk_1` FOREIGN KEY (`record_id`) REFERENCES `medical_record` (`record_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `vaccine_history` (`vaccine_history_id`, `vaccine_name`, `note`, `record_id`, `vaccine_id`) VALUES
(13,	'Hepatitis B',	'First dose at 12 months',	7,	NULL),
(14,	'Hepatitis B',	'Completed 3-dose series',	7,	NULL);

DROP TABLE IF EXISTS `vaccine_program`;
CREATE TABLE `vaccine_program` (
  `vaccine_id` int NOT NULL AUTO_INCREMENT,
  `vaccine_name` varchar(100) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `vaccine_date` date DEFAULT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'NOT_STARTED',
  `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `admin_id` int DEFAULT NULL,
  PRIMARY KEY (`vaccine_id`),
  KEY `admin_id` (`admin_id`),
  CONSTRAINT `vaccine_program_ibfk_1` FOREIGN KEY (`admin_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `chk_vaccine_status` CHECK ((`status` in (_utf8mb4'ON_GOING',_utf8mb4'COMPLETED',_utf8mb4'NOT_STARTED')))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `vaccine_program` (`vaccine_id`, `vaccine_name`, `description`, `vaccine_date`, `status`, `note`, `admin_id`) VALUES
(1,	'Hepatitis B',	'Protects against Hepatitis B virus',	'2025-07-01',	'NOT_STARTED',	'Administered at birth',	10);

DROP TABLE IF EXISTS `vaccine_result`;
CREATE TABLE `vaccine_result` (
  `vaccine_result_id` int NOT NULL AUTO_INCREMENT,
  `status_health` varchar(255) DEFAULT NULL,
  `result_note` varchar(255) DEFAULT NULL,
  `reaction` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `vaccine_form_id` int DEFAULT NULL,
  PRIMARY KEY (`vaccine_result_id`),
  KEY `vaccine_form_id` (`vaccine_form_id`),
  CONSTRAINT `vaccine_result_ibfk_1` FOREIGN KEY (`vaccine_form_id`) REFERENCES `vaccine_form` (`vaccine_form_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- 2025-06-18 10:50:41 UTC
