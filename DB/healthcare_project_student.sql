-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: healthcare_project
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student` (
  `student_id` int NOT NULL AUTO_INCREMENT,
  `avatar_url` varchar(255) DEFAULT NULL,
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
) ENGINE=InnoDB AUTO_INCREMENT=346 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
INSERT INTO `student` VALUES (205,NULL,'Trần Thị 2','2011-09-15','Nữ','Con gái',1,7),(206,NULL,'Lê Hoàng 3','2012-03-22','Nam','Con trai',2,2),(207,NULL,'Phạm Minh 4','2011-12-10','Nam','Con trai',1,9),(208,NULL,'Hoàng Thị 5','2012-07-18','Nữ','Con gái',3,4),(209,NULL,'Vũ Đức 6','2011-05-30','Nam','Con trai',2,10),(210,NULL,'Đặng Lan 7','2012-11-25','Nữ','Con gái',1,6),(211,NULL,'Bùi Văn 8','2011-08-14','Nam','Con trai',3,8),(212,NULL,'Ngô Thị 9','2012-02-28','Nữ','Con gái',2,5),(213,NULL,'Dương Hữu 10','2011-10-12','Nam','Con trai',1,10),(214,NULL,'Lý Thị 11','2012-06-05','Nữ','Con gái',3,3),(215,NULL,'Trịnh Văn 12','2011-04-17','Nam','Con trai',2,7),(216,NULL,'Võ Minh 13','2012-09-23','Nam','Con trai',1,1),(217,NULL,'Phan Thị 14','2011-07-08','Nữ','Con gái',3,4),(218,NULL,'Tạ Đức 15','2012-12-31','Nam','Con trai',2,9),(219,NULL,'Mai Lan 16','2011-03-15','Nữ','Con gái',1,2),(220,NULL,'Chu Văn 17','2012-08-07','Nam','Con trai',3,6),(221,NULL,'Đỗ Thị 18','2011-11-20','Nữ','Con gái',2,8),(222,NULL,'Cao Hữu 19','2012-04-03','Nam','Con trai',1,5),(223,NULL,'Lâm Thị 20','2011-06-26','Nữ','Con gái',3,10),(224,NULL,'Đinh Văn 21','2012-01-14','Nam','Con trai',2,3),(225,NULL,'Hồ Minh 22','2011-05-29','Nam','Con trai',1,7),(226,NULL,'Kiều Thị 23','2012-10-11','Nữ','Con gái',3,1),(227,NULL,'Lưu Đức 24','2011-12-24','Nam','Con trai',2,4),(228,NULL,'Mạch Lan 25','2012-07-16','Nữ','Con gái',1,9),(229,NULL,'Ninh Văn 26','2011-02-09','Nam','Con trai',3,2),(230,NULL,'Ông Thị 27','2012-09-01','Nữ','Con gái',2,6),(231,NULL,'Quách Hữu 28','2011-04-25','Nam','Con trai',1,8),(232,NULL,'Rồng Thị 29','2012-11-18','Nữ','Con gái',3,5),(233,NULL,'Sử Văn 30','2011-08-02','Nam','Con trai',2,10),(234,NULL,'Thái Minh 31','2012-03-27','Nam','Con trai',1,3),(235,NULL,'Ưng Thị 32','2011-06-19','Nữ','Con gái',3,7),(236,NULL,'Văn Đức 33','2012-12-05','Nam','Con trai',2,1),(237,NULL,'Xa Lan 34','2011-01-28','Nữ','Con gái',1,4),(238,NULL,'Yên Văn 35','2012-05-21','Nam','Con trai',3,9),(239,NULL,'Âu Thị 36','2011-10-13','Nữ','Con gái',2,2),(240,NULL,'Ấu Hữu 37','2012-08-30','Nam','Con trai',1,6),(241,NULL,'Bạch Thị 38','2011-03-06','Nữ','Con gái',3,8),(242,NULL,'Cung Văn 39','2012-11-22','Nam','Con trai',2,5),(243,NULL,'Đàm Minh 40','2011-07-15','Nam','Con trai',1,10),(244,NULL,'Én Thị 41','2012-02-12','Nữ','Con gái',3,3),(245,NULL,'Phùng Đức 42','2011-09-28','Nam','Con trai',2,7),(246,NULL,'Giang Lan 43','2012-06-14','Nữ','Con gái',1,1),(247,NULL,'Hà Văn 44','2011-12-07','Nam','Con trai',3,4),(248,NULL,'Ỉn Thị 45','2012-04-26','Nữ','Con gái',2,9),(249,NULL,'Kha Hữu 46','2011-11-11','Nam','Con trai',1,2),(250,NULL,'Long Thị 47','2012-08-04','Nữ','Con gái',3,6),(251,NULL,'Mã Văn 48','2011-05-18','Nam','Con trai',2,8),(252,NULL,'Nông Minh 49','2012-01-31','Nam','Con trai',1,5),(253,NULL,'Ô Thị 50','2011-10-24','Nữ','Con gái',3,10),(254,NULL,'Pù Đức 51','2012-07-09','Nam','Con trai',2,3),(255,NULL,'Quế Lan 52','2011-04-02','Nữ','Con gái',1,7),(256,NULL,'Sơn Văn 53','2012-12-18','Nam','Con trai',3,1),(257,NULL,'Tôn Thị 54','2011-06-11','Nữ','Con gái',2,4),(258,NULL,'Ung Hữu 55','2012-03-05','Nam','Con trai',1,9),(259,NULL,'Vương Thị 56','2011-09-21','Nữ','Con gái',3,2),(260,NULL,'Xương Văn 57','2012-11-08','Nam','Con trai',2,6),(261,NULL,'Yết Minh 58','2011-02-14','Nam','Con trai',1,8),(262,NULL,'Ấn Thị 59','2012-08-27','Nữ','Con gái',3,5),(263,NULL,'Bảo Đức 60','2011-05-12','Nam','Con trai',2,10),(264,NULL,'Cam Lan 61','2012-01-25','Nữ','Con gái',1,3),(265,NULL,'Đan Văn 62','2011-10-17','Nam','Con trai',3,7),(266,NULL,'Êm Thị 63','2012-06-03','Nữ','Con gái',2,1),(267,NULL,'Phong Hữu 64','2011-12-20','Nam','Con trai',1,4),(268,NULL,'Gôn Thị 65','2012-09-06','Nữ','Con gái',3,9),(269,NULL,'Hoan Văn 66','2011-03-30','Nam','Con trai',2,2),(270,NULL,'Ít Minh 67','2012-11-15','Nam','Con trai',1,6),(271,NULL,'Kỳ Thị 68','2011-07-29','Nữ','Con gái',3,8),(272,NULL,'Lục Đức 69','2012-04-11','Nam','Con trai',2,5),(273,NULL,'Mạnh Lan 70','2011-01-04','Nữ','Con gái',1,10),(274,NULL,'Nghiêm Văn 71','2012-08-20','Nam','Con trai',3,3),(275,NULL,'Ôn Thị 72','2011-06-05','Nữ','Con gái',2,7),(276,NULL,'Pháp Hữu 73','2012-02-22','Nam','Con trai',1,1),(277,NULL,'Quang Thị 74','2011-11-14','Nữ','Con gái',3,4),(278,NULL,'Rạng Văn 75','2012-07-31','Nam','Con trai',2,9),(279,NULL,'Sinh Minh 76','2011-04-16','Nam','Con trai',1,2),(280,NULL,'Thành Thị 77','2012-12-03','Nữ','Con gái',3,6),(281,NULL,'Ước Đức 78','2011-08-19','Nam','Con trai',2,8),(282,NULL,'Vân Lan 79','2012-05-07','Nữ','Con gái',1,5),(283,NULL,'Xuân Văn 80','2011-01-23','Nam','Con trai',3,10),(284,NULL,'Ý Thị 81','2012-10-10','Nữ','Con gái',2,3),(285,NULL,'Ảnh Hữu 82','2011-07-03','Nam','Con trai',1,7),(286,NULL,'Bích Thị 83','2012-03-19','Nữ','Con gái',3,1),(287,NULL,'Cầm Văn 84','2011-12-11','Nam','Con trai',2,4),(288,NULL,'Đào Minh 85','2012-09-24','Nam','Con trai',1,9),(289,NULL,'Ê Thị 86','2011-05-08','Nữ','Con gái',3,2),(290,NULL,'Phúc Đức 87','2012-01-20','Nam','Con trai',2,6),(291,NULL,'Gia Lan 88','2011-10-06','Nữ','Con gái',1,8),(292,NULL,'Hạnh Văn 89','2012-06-28','Nam','Con trai',3,5),(293,NULL,'Ích Thị 90','2011-02-11','Nữ','Con gái',2,10),(294,NULL,'Kiên Hữu 91','2012-11-27','Nam','Con trai',1,3),(295,NULL,'Lợi Thị 92','2011-08-13','Nữ','Con gái',3,7),(296,NULL,'Minh Văn 93','2012-04-29','Nam','Con trai',2,1),(297,NULL,'Nghĩa Minh 94','2011-01-15','Nam','Con trai',1,4),(298,NULL,'Ơn Thị 95','2012-10-02','Nữ','Con gái',3,9),(299,NULL,'Phước Đức 96','2011-06-18','Nam','Con trai',2,2),(300,NULL,'Quyền Lan 97','2012-02-05','Nữ','Con gái',1,6),(301,NULL,'Sáng Văn 98','2011-09-22','Nam','Con trai',3,8),(302,NULL,'Thịnh Thị 99','2012-12-14','Nữ','Con gái',2,5),(303,NULL,'Uyển Hữu 100','2011-02-11','Nam','Con trai',1,10),(304,NULL,'Nguyễn Minh Khang','2014-03-15','Nam','Con trai',4,11),(305,NULL,'Lê Hoàng Bảo','2015-07-22','Nam','Con trai',4,12),(306,NULL,'Trần Nhật Minh','2013-11-30','Nam','Con trai',4,13),(307,NULL,'Phạm Anh Tú','2014-09-12','Nam','Con trai',4,14),(308,NULL,'Hoàng Gia Huy','2016-01-25','Nam','Con trai',4,15),(309,NULL,'Đỗ Trung Kiên','2013-06-10','Nam','Con trai',4,16),(310,NULL,'Ngô Minh Quân','2014-12-05','Nam','Con trai',4,17),(311,NULL,'Vũ Bảo Long','2015-08-14','Nam','Con trai',4,18),(312,NULL,'Mai Tuấn Khải','2014-04-02','Nam','Con trai',4,19),(313,NULL,'Bùi Quốc Anh','2013-10-21','Nam','Con trai',4,20),(314,NULL,'Nguyễn Ngọc Lan','2014-02-17','Nữ','Con gái',4,21),(315,NULL,'Lê Khánh Linh','2013-09-28','Nữ','Con gái',4,22),(316,NULL,'Trần Diệu Anh','2015-05-19','Nữ','Con gái',4,23),(317,NULL,'Phạm Thảo Nhi','2016-03-08','Nữ','Con gái',4,24),(318,NULL,'Hoàng Ánh Tuyết','2014-07-03','Nữ','Con gái',4,25),(319,NULL,'Đỗ Hà My','2013-01-11','Nữ','Con gái',4,26),(320,NULL,'Ngô Phương Thảo','2015-06-27','Nữ','Con gái',4,27),(321,NULL,'Vũ Minh Châu','2014-12-14','Nữ','Con gái',4,28),(322,NULL,'Mai Thanh Hà','2013-03-09','Nữ','Con gái',4,29),(323,NULL,'Bùi Trúc Mai','2015-10-26','Nữ','Con gái',4,30),(324,NULL,'Nguyễn Hữu Phúc','2014-05-30','Nam','Con trai',4,31),(325,NULL,'Lê Minh Khôi','2015-01-13','Nam','Con trai',4,32),(326,NULL,'Trần Nhật Hào','2016-04-07','Nam','Con trai',4,33),(327,NULL,'Phạm Quang Vinh','2013-08-18','Nam','Con trai',4,34),(328,NULL,'Hoàng Thiên Bảo','2014-10-05','Nam','Con trai',4,35),(329,NULL,'Đỗ Tuấn Minh','2015-09-16','Nam','Con trai',4,36),(330,NULL,'Ngô Khánh Duy','2016-02-04','Nam','Con trai',4,37),(331,NULL,'Vũ Hữu Đức','2013-11-23','Nam','Con trai',4,38),(332,NULL,'Mai Hưng Thịnh','2014-06-06','Nam','Con trai',4,39),(333,NULL,'Bùi Công Thành','2015-07-29','Nam','Con trai',4,40),(334,NULL,'Nguyễn Gia Hân','2014-01-09','Nữ','Con gái',4,41),(335,NULL,'Lê Bảo Ngọc','2013-12-22','Nữ','Con gái',4,42),(336,NULL,'Trần Thiên Kim','2015-03-18','Nữ','Con gái',4,43),(337,NULL,'Phạm Nhã Uyên','2016-08-01','Nữ','Con gái',4,44),(338,NULL,'Hoàng Minh Anh','2013-04-25','Nữ','Con gái',4,45),(339,NULL,'Đỗ Yến Nhi','2014-09-19','Nữ','Con gái',4,46),(340,NULL,'Ngô Hà Vy','2015-06-30','Nữ','Con gái',4,47),(341,NULL,'Vũ Phương Anh','2016-05-11','Nữ','Con gái',4,48),(342,NULL,'Mai Khánh Chi','2013-07-08','Nữ','Con gái',4,49),(345,NULL,'123','2012-01-01','Nam','Con trai',NULL,55);
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-07-03  1:13:56
