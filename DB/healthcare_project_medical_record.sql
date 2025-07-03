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
-- Table structure for table `medical_record`
--

DROP TABLE IF EXISTS `medical_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medical_record`
--

LOCK TABLES `medical_record` WRITE;
/*!40000 ALTER TABLE `medical_record` DISABLE KEYS */;
INSERT INTO `medical_record` VALUES (7,209,'Pollen','Asthma','Used inhaler for 3 years','Normal','Normal',45.5,150,'2025-06-08 16:18:24','Student is in good condition'),(8,225,'string','string','string','Unknown','',1,30,'2025-06-08 16:18:57','string'),(10,213,'2','2','2','2','2',2,2,'2025-06-08 16:18:57','2'),(11,304,'Không','Không','Không','10/10','Bình thường',25.5,130,'2025-06-23 14:07:29','Khám định kỳ'),(12,305,'Hải sản','Không','Viêm họng','9/10','Bình thường',27,132,'2025-06-23 14:07:29','Cần theo dõi dị ứng'),(13,306,'Không','Hen suyễn','Điều trị định kỳ','10/10','Tốt',30.2,135.5,'2025-06-23 14:07:29','Đang điều trị hen'),(14,307,'Bụi','Không','Không','10/10','Bình thường',28.7,134,'2025-06-23 14:07:29','Thỉnh thoảng dị ứng nhẹ'),(15,308,'Không','Không','Cảm cúm','10/10','Tốt',26.8,131,'2025-06-23 14:07:29','Đã khỏi bệnh'),(16,309,'Không','Không','Không','10/10','Bình thường',29.5,136,'2025-06-23 14:07:29','Khỏe mạnh'),(17,310,'Không','Viêm da cơ địa','Đã điều trị','9/10','Bình thường',27.3,133,'2025-06-23 14:07:29','Da nhạy cảm'),(18,311,'Trứng','Không','Không','10/10','Bình thường',25,128,'2025-06-23 14:07:29','Dị ứng nhẹ'),(19,312,'Không','Không','Không','10/10','Tốt',31,137,'2025-06-23 14:07:29','Phát triển tốt'),(20,313,'Không','Không','Không','10/10','Bình thường',28,132.5,'2025-06-23 14:07:29','Không có vấn đề'),(21,314,'Không','Không','Không','10/10','Bình thường',26.2,129,'2025-06-23 14:07:29','Khám định kỳ'),(22,315,'Không','Không','Đau bụng nhẹ','10/10','Bình thường',27.8,130,'2025-06-23 14:07:29','Tiêu hóa yếu'),(23,316,'Không','Không','Không','10/10','Tốt',29.1,134,'2025-06-23 14:07:29','Bình thường'),(24,317,'Không','Không','Không','10/10','Bình thường',30,135,'2025-06-23 14:07:29','Khỏe'),(25,318,'Không','Không','Không','10/10','Bình thường',28.5,132,'2025-06-23 14:07:29','Bình thường'),(26,319,'Không','Không','Không','10/10','Tốt',26.9,130,'2025-06-23 14:07:29','Khỏe mạnh'),(27,320,'Sữa bò','Không','Không','9/10','Bình thường',27,129.5,'2025-06-23 14:07:29','Theo dõi dị ứng sữa'),(28,321,'Không','Không','Không','10/10','Tốt',28.6,131,'2025-06-23 14:07:29','Bình thường'),(29,322,'Không','Không','Không','10/10','Bình thường',30.4,136,'2025-06-23 14:07:29','Khám định kỳ'),(30,323,'Không','Không','Không','10/10','Tốt',29.7,133,'2025-06-23 14:07:29','Không có vấn đề'),(31,324,'Không','Không','Không','10/10','Tốt',25.3,127,'2025-06-23 14:07:29','Bình thường'),(32,325,'Không','Không','Không','10/10','Bình thường',28.1,132,'2025-06-23 14:07:29','Bình thường'),(33,326,'Không','Không','Không','10/10','Tốt',29,134,'2025-06-23 14:07:29','Khám sức khỏe học kỳ'),(34,327,'Không','Không','Không','10/10','Bình thường',26.7,130,'2025-06-23 14:07:29','Không có ghi chú'),(35,328,'Không','Không','Không','10/10','Tốt',30,135.5,'2025-06-23 14:07:29','Phát triển tốt'),(36,329,'Không','Không','Không','10/10','Bình thường',27.5,130.5,'2025-06-23 14:07:29','Khám định kỳ'),(37,330,'Không','Không','Không','10/10','Tốt',28.9,133,'2025-06-23 14:07:29','Bình thường'),(38,331,'Không','Không','Không','10/10','Tốt',31.1,137,'2025-06-23 14:07:29','Phát triển tốt'),(39,332,'Không','Không','Không','10/10','Bình thường',27.4,129,'2025-06-23 14:07:29','Khám sức khỏe học sinh'),(40,333,'Không','Không','Không','10/10','Tốt',29.8,134,'2025-06-23 14:07:29','Không có vấn đề'),(41,334,'Không','Không','Không','10/10','Bình thường',28.3,132,'2025-06-23 14:07:29','Khám định kỳ'),(42,335,'Không','Không','Không','10/10','Tốt',26,128,'2025-06-23 14:07:29','Bình thường'),(43,336,'Không','Không','Không','10/10','Tốt',30.5,136,'2025-06-23 14:07:29','Phát triển tốt'),(44,337,'Không','Không','Không','10/10','Bình thường',27.6,130.5,'2025-06-23 14:07:29','Không có vấn đề'),(45,338,'Không','Không','Không','10/10','Tốt',29.2,133,'2025-06-23 14:07:29','Khám định kỳ'),(46,339,'Không','Không','Không','10/10','Bình thường',30.7,135,'2025-06-23 14:07:29','Khỏe mạnh'),(47,340,'Không','Không','Không','10/10','Tốt',28.4,131,'2025-06-23 14:07:29','Bình thường'),(48,341,'Không','Không','Không','10/10','Tốt',29.9,134.5,'2025-06-23 14:07:29','Phát triển tốt'),(49,342,'Không','Không','Không','10/10','Bình thường',26.5,128,'2025-06-23 14:07:29','Khám định kỳ'),(52,345,'Dị ứng hải sản','Không','Đã từng điều trị viêm họng năm 2022','Trái: 9/10, Phải: 10/10','Bình thường',35,135,'2025-07-01 22:08:08','Sức khỏe ổn định. Cần theo dõi dị ứng khi ăn bán trú.'),(53,223,'','','','21','e222',13213,222,'2025-07-03 00:01:53',''),(54,233,'','','','22','22',222,222,'2025-07-03 00:28:56','');
/*!40000 ALTER TABLE `medical_record` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-07-03  1:13:55
