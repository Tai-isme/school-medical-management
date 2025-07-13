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
-- Table structure for table `medical_request`
--

DROP TABLE IF EXISTS `medical_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medical_request` (
  `request_id` int NOT NULL AUTO_INCREMENT,
  `request_name` varchar(100) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `note` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `status` varchar(50) DEFAULT 'PROCESSING',
  `student_id` int DEFAULT NULL,
  `parent_id` int DEFAULT NULL,
  `reason_rejected` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`request_id`),
  KEY `student_id` (`student_id`),
  KEY `parent_id` (`parent_id`),
  CONSTRAINT `medical_request_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`) ON DELETE CASCADE,
  CONSTRAINT `medical_request_ibfk_2` FOREIGN KEY (`parent_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `chk_status` CHECK ((`status` in (_utf8mb4'SUBMITTED',_utf8mb4'COMPLETED',_utf8mb4'PROCESSING',_utf8mb4'CANCELLED')))
) ENGINE=InnoDB AUTO_INCREMENT=150809 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medical_request`
--

LOCK TABLES `medical_request` WRITE;
/*!40000 ALTER TABLE `medical_request` DISABLE KEYS */;
INSERT INTO `medical_request` VALUES (150792,'Yêu cầu cấp phát thuốc','2025-06-18','Học sinh bị sốt nhẹ, cần uống thuốc theo đơn','SUBMITTED',209,1,NULL),(150793,'Yêu cầu phát thuốc','2025-06-10','Bé ho và chảy mũi','PROCESSING',304,11,NULL),(150794,'Khám tai mũi họng','2025-06-11','Có dấu hiệu viêm mũi','SUBMITTED',305,12,NULL),(150795,'Khám sốt nhẹ','2025-06-12','Bé bị sốt nhẹ từ tối qua','COMPLETED',306,13,NULL),(150796,'Đơn thuốc hạ sốt','2025-06-13','Mong được phát thuốc Paracetamol','PROCESSING',307,14,NULL),(150797,'Yêu cầu kiểm tra tiêu hóa','2025-06-14','Đau bụng sáng nay','SUBMITTED',308,15,NULL),(150798,'Phát thuốc ho','2025-06-15','Ho khan kéo dài','PROCESSING',309,16,NULL),(150799,'Yêu cầu thăm khám','2025-06-15','Bé kêu chóng mặt','COMPLETED',310,17,NULL),(150800,'Khám tổng quát','2025-06-16','Kiểm tra sức khỏe định kỳ','SUBMITTED',311,18,NULL),(150801,'Cấp thuốc dị ứng','2025-06-16','Dị ứng nhẹ sau bữa ăn','PROCESSING',312,19,NULL),(150802,'Yêu cầu kiểm tra mắt','2025-06-17','Nhìn mờ trên lớp','SUBMITTED',313,20,NULL),(150803,'Khám hô hấp','2025-06-18','Thở khò khè nhẹ','PROCESSING',314,21,NULL),(150804,'Kiểm tra tai','2025-06-18','Đau tai trái','COMPLETED',315,22,NULL),(150805,'Yêu cầu thuốc tiêu hóa','2025-06-19','Khó tiêu sau ăn','SUBMITTED',316,23,NULL),(150806,'Cấp thuốc dị ứng','2025-06-19','Nổi mẩn ở tay','PROCESSING',317,24,NULL),(150807,'Khám da liễu','2025-06-20','Mụn đỏ vùng mặt','COMPLETED',318,25,NULL),(150808,'22222','2025-07-16','444','PROCESSING',209,10,NULL);
/*!40000 ALTER TABLE `medical_request` ENABLE KEYS */;
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
