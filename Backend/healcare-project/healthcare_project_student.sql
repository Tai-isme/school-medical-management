-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: healthcare_project
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
INSERT INTO `student` VALUES (204,'Nguyễn Văn 1','2012-01-01','Nam','Con trai',1,3),(205,'Trần Thị 2','2011-09-15','Nữ','Con gái',1,7),(206,'Lê Hoàng 3','2012-03-22','Nam','Con trai',2,2),(207,'Phạm Minh 4','2011-12-10','Nam','Con trai',1,9),(208,'Hoàng Thị 5','2012-07-18','Nữ','Con gái',3,4),(209,'Vũ Đức 6','2011-05-30','Nam','Con trai',2,1),(210,'Đặng Lan 7','2012-11-25','Nữ','Con gái',1,6),(211,'Bùi Văn 8','2011-08-14','Nam','Con trai',3,8),(212,'Ngô Thị 9','2012-02-28','Nữ','Con gái',2,5),(213,'Dương Hữu 10','2011-10-12','Nam','Con trai',1,10),(214,'Lý Thị 11','2012-06-05','Nữ','Con gái',3,3),(215,'Trịnh Văn 12','2011-04-17','Nam','Con trai',2,7),(216,'Võ Minh 13','2012-09-23','Nam','Con trai',1,1),(217,'Phan Thị 14','2011-07-08','Nữ','Con gái',3,4),(218,'Tạ Đức 15','2012-12-31','Nam','Con trai',2,9),(219,'Mai Lan 16','2011-03-15','Nữ','Con gái',1,2),(220,'Chu Văn 17','2012-08-07','Nam','Con trai',3,6),(221,'Đỗ Thị 18','2011-11-20','Nữ','Con gái',2,8),(222,'Cao Hữu 19','2012-04-03','Nam','Con trai',1,5),(223,'Lâm Thị 20','2011-06-26','Nữ','Con gái',3,10),(224,'Đinh Văn 21','2012-01-14','Nam','Con trai',2,3),(225,'Hồ Minh 22','2011-05-29','Nam','Con trai',1,7),(226,'Kiều Thị 23','2012-10-11','Nữ','Con gái',3,1),(227,'Lưu Đức 24','2011-12-24','Nam','Con trai',2,4),(228,'Mạch Lan 25','2012-07-16','Nữ','Con gái',1,9),(229,'Ninh Văn 26','2011-02-09','Nam','Con trai',3,2),(230,'Ông Thị 27','2012-09-01','Nữ','Con gái',2,6),(231,'Quách Hữu 28','2011-04-25','Nam','Con trai',1,8),(232,'Rồng Thị 29','2012-11-18','Nữ','Con gái',3,5),(233,'Sử Văn 30','2011-08-02','Nam','Con trai',2,10),(234,'Thái Minh 31','2012-03-27','Nam','Con trai',1,3),(235,'Ưng Thị 32','2011-06-19','Nữ','Con gái',3,7),(236,'Văn Đức 33','2012-12-05','Nam','Con trai',2,1),(237,'Xa Lan 34','2011-01-28','Nữ','Con gái',1,4),(238,'Yên Văn 35','2012-05-21','Nam','Con trai',3,9),(239,'Âu Thị 36','2011-10-13','Nữ','Con gái',2,2),(240,'Ấu Hữu 37','2012-08-30','Nam','Con trai',1,6),(241,'Bạch Thị 38','2011-03-06','Nữ','Con gái',3,8),(242,'Cung Văn 39','2012-11-22','Nam','Con trai',2,5),(243,'Đàm Minh 40','2011-07-15','Nam','Con trai',1,10),(244,'Én Thị 41','2012-02-12','Nữ','Con gái',3,3),(245,'Phùng Đức 42','2011-09-28','Nam','Con trai',2,7),(246,'Giang Lan 43','2012-06-14','Nữ','Con gái',1,1),(247,'Hà Văn 44','2011-12-07','Nam','Con trai',3,4),(248,'Ỉn Thị 45','2012-04-26','Nữ','Con gái',2,9),(249,'Kha Hữu 46','2011-11-11','Nam','Con trai',1,2),(250,'Long Thị 47','2012-08-04','Nữ','Con gái',3,6),(251,'Mã Văn 48','2011-05-18','Nam','Con trai',2,8),(252,'Nông Minh 49','2012-01-31','Nam','Con trai',1,5),(253,'Ô Thị 50','2011-10-24','Nữ','Con gái',3,10),(254,'Pù Đức 51','2012-07-09','Nam','Con trai',2,3),(255,'Quế Lan 52','2011-04-02','Nữ','Con gái',1,7),(256,'Sơn Văn 53','2012-12-18','Nam','Con trai',3,1),(257,'Tôn Thị 54','2011-06-11','Nữ','Con gái',2,4),(258,'Ung Hữu 55','2012-03-05','Nam','Con trai',1,9),(259,'Vương Thị 56','2011-09-21','Nữ','Con gái',3,2),(260,'Xương Văn 57','2012-11-08','Nam','Con trai',2,6),(261,'Yết Minh 58','2011-02-14','Nam','Con trai',1,8),(262,'Ấn Thị 59','2012-08-27','Nữ','Con gái',3,5),(263,'Bảo Đức 60','2011-05-12','Nam','Con trai',2,10),(264,'Cam Lan 61','2012-01-25','Nữ','Con gái',1,3),(265,'Đan Văn 62','2011-10-17','Nam','Con trai',3,7),(266,'Êm Thị 63','2012-06-03','Nữ','Con gái',2,1),(267,'Phong Hữu 64','2011-12-20','Nam','Con trai',1,4),(268,'Gôn Thị 65','2012-09-06','Nữ','Con gái',3,9),(269,'Hoan Văn 66','2011-03-30','Nam','Con trai',2,2),(270,'Ít Minh 67','2012-11-15','Nam','Con trai',1,6),(271,'Kỳ Thị 68','2011-07-29','Nữ','Con gái',3,8),(272,'Lục Đức 69','2012-04-11','Nam','Con trai',2,5),(273,'Mạnh Lan 70','2011-01-04','Nữ','Con gái',1,10),(274,'Nghiêm Văn 71','2012-08-20','Nam','Con trai',3,3),(275,'Ôn Thị 72','2011-06-05','Nữ','Con gái',2,7),(276,'Pháp Hữu 73','2012-02-22','Nam','Con trai',1,1),(277,'Quang Thị 74','2011-11-14','Nữ','Con gái',3,4),(278,'Rạng Văn 75','2012-07-31','Nam','Con trai',2,9),(279,'Sinh Minh 76','2011-04-16','Nam','Con trai',1,2),(280,'Thành Thị 77','2012-12-03','Nữ','Con gái',3,6),(281,'Ước Đức 78','2011-08-19','Nam','Con trai',2,8),(282,'Vân Lan 79','2012-05-07','Nữ','Con gái',1,5),(283,'Xuân Văn 80','2011-01-23','Nam','Con trai',3,10),(284,'Ý Thị 81','2012-10-10','Nữ','Con gái',2,3),(285,'Ảnh Hữu 82','2011-07-03','Nam','Con trai',1,7),(286,'Bích Thị 83','2012-03-19','Nữ','Con gái',3,1),(287,'Cầm Văn 84','2011-12-11','Nam','Con trai',2,4),(288,'Đào Minh 85','2012-09-24','Nam','Con trai',1,9),(289,'Ê Thị 86','2011-05-08','Nữ','Con gái',3,2),(290,'Phúc Đức 87','2012-01-20','Nam','Con trai',2,6),(291,'Gia Lan 88','2011-10-06','Nữ','Con gái',1,8),(292,'Hạnh Văn 89','2012-06-28','Nam','Con trai',3,5),(293,'Ích Thị 90','2011-02-11','Nữ','Con gái',2,10),(294,'Kiên Hữu 91','2012-11-27','Nam','Con trai',1,3),(295,'Lợi Thị 92','2011-08-13','Nữ','Con gái',3,7),(296,'Minh Văn 93','2012-04-29','Nam','Con trai',2,1),(297,'Nghĩa Minh 94','2011-01-15','Nam','Con trai',1,4),(298,'Ơn Thị 95','2012-10-02','Nữ','Con gái',3,9),(299,'Phước Đức 96','2011-06-18','Nam','Con trai',2,2),(300,'Quyền Lan 97','2012-02-05','Nữ','Con gái',1,6),(301,'Sáng Văn 98','2011-09-22','Nam','Con trai',3,8),(302,'Thịnh Thị 99','2012-12-14','Nữ','Con gái',2,5),(303,'Uyển Hữu 100','2011-04-07','Nam','Con trai',1,10);
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

-- Dump completed on 2025-06-09  8:03:42
