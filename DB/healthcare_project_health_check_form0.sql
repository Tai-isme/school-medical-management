CREATE DATABASE  IF NOT EXISTS `healthcare_project` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `healthcare_project`;
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
-- Table structure for table `health_check_form`
--

DROP TABLE IF EXISTS `health_check_form`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `health_check_form` (
  `health_check_form_id` int NOT NULL AUTO_INCREMENT,
  `health_check_id` int DEFAULT NULL,
  `student_id` int DEFAULT NULL,
  `parent_id` int DEFAULT NULL,
  `form_date` date DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `commit` tinyint(1) DEFAULT NULL,
  `status` varchar(10) DEFAULT 'draft',
  PRIMARY KEY (`health_check_form_id`),
  KEY `health_check_id` (`health_check_id`),
  KEY `student_id` (`student_id`),
  KEY `parent_id` (`parent_id`),
  CONSTRAINT `health_check_form_ibfk_1` FOREIGN KEY (`health_check_id`) REFERENCES `health_check_program` (`health_check_id`) ON DELETE CASCADE,
  CONSTRAINT `health_check_form_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`) ON DELETE CASCADE,
  CONSTRAINT `health_check_form_ibfk_3` FOREIGN KEY (`parent_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `health_check_form_chk_1` CHECK ((`status` in (_utf8mb4'DRAFT',_utf8mb4'SENT')))
) ENGINE=InnoDB AUTO_INCREMENT=1043 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `health_check_form`
--

LOCK TABLES `health_check_form` WRITE;
/*!40000 ALTER TABLE `health_check_form` DISABLE KEYS */;
INSERT INTO `health_check_form` VALUES (1001,7,209,10,'2025-06-01','Không vấn đề',1,'SENT'),(1002,8,206,2,'2025-06-01','Thấy mệt',1,'SENT'),(1003,7,207,9,'2025-06-15','Thấy mệt nhiều',1,'SENT'),(1004,9,304,11,'2025-06-01','Không có ghi chú đặc biệt',1,'DRAFT'),(1005,9,305,12,'2025-06-01','Đã từng dị ứng nhẹ',0,'SENT'),(1006,9,306,13,'2025-06-01','Khỏe mạnh',1,'DRAFT'),(1007,9,307,14,'2025-06-01','Không có vấn đề gì',0,'SENT'),(1008,9,308,15,'2025-06-01','Đã từng khám tai mũi họng',1,'DRAFT'),(1009,9,309,16,'2025-06-02','Phản hồi tốt',0,'SENT'),(1010,9,310,17,'2025-06-02','Phát triển bình thường',1,'DRAFT'),(1011,9,311,18,'2025-06-02','Không có bệnh nền',0,'SENT'),(1012,9,312,19,'2025-06-02','Dị ứng nhẹ với trứng',1,'DRAFT'),(1013,9,313,20,'2025-06-02','Không có vấn đề gì',0,'SENT'),(1014,9,314,21,'2025-06-03','Khám định kỳ',1,'DRAFT'),(1015,9,315,22,'2025-06-03','Cần theo dõi thêm',0,'SENT'),(1016,9,316,23,'2025-06-03','Không có ghi chú',1,'DRAFT'),(1017,9,317,24,'2025-06-03','Khỏe mạnh',0,'SENT'),(1018,9,318,25,'2025-06-03','Có tiền sử dị ứng',1,'DRAFT'),(1019,9,319,26,'2025-06-04','Cần kiểm tra thêm',0,'SENT'),(1020,9,320,27,'2025-06-04','Không có vấn đề gì',1,'DRAFT'),(1021,9,321,28,'2025-06-04','Được tiêm đầy đủ',0,'SENT'),(1022,9,322,29,'2025-06-04','Phản hồi tích cực',1,'DRAFT'),(1023,9,323,30,'2025-06-04','Không có ghi chú',0,'SENT'),(1024,9,324,31,'2025-06-05','Tình trạng ổn định',1,'DRAFT'),(1025,9,325,32,'2025-06-05','Khỏe mạnh',0,'SENT'),(1026,9,326,33,'2025-06-05','Đã tiêm phòng đầy đủ',1,'DRAFT'),(1027,9,327,34,'2025-06-05','Cần theo dõi cân nặng',0,'SENT'),(1028,9,328,35,'2025-06-05','Bé phát triển tốt',1,'DRAFT'),(1029,9,329,36,'2025-06-06','Không có bệnh nền',0,'SENT'),(1030,9,330,37,'2025-06-06','Cần đo lại thị lực',1,'DRAFT'),(1031,9,331,38,'2025-06-06','Khỏe mạnh',0,'SENT'),(1032,9,332,39,'2025-06-06','Không có phản hồi',1,'DRAFT'),(1033,9,333,40,'2025-06-06','Được phụ huynh đồng ý',0,'SENT'),(1034,9,334,41,'2025-06-07','Không có vấn đề gì',1,'DRAFT'),(1035,9,335,42,'2025-06-07','Có dấu hiệu ho nhẹ',0,'SENT'),(1036,9,336,43,'2025-06-07','Được xác nhận từ phụ huynh',1,'DRAFT'),(1037,9,337,44,'2025-06-07','Cần theo dõi dị ứng',0,'SENT'),(1038,9,338,45,'2025-06-08','Không có ghi chú',1,'DRAFT'),(1039,9,339,46,'2025-06-08','Bé ăn uống kém',0,'SENT'),(1040,9,340,47,'2025-06-08','Cần bổ sung vitamin',1,'DRAFT'),(1041,9,341,48,'2025-06-08','Không có vấn đề gì',0,'SENT'),(1042,9,342,49,'2025-06-09','Tiêm phòng đầy đủ',1,'DRAFT');
/*!40000 ALTER TABLE `health_check_form` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-07-14  2:27:02
