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
-- Table structure for table `medical_event`
--

DROP TABLE IF EXISTS `medical_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medical_event` (
  `event_id` int NOT NULL AUTO_INCREMENT,
  `type_event` varchar(100) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `student_id` int DEFAULT NULL,
  `nurse_id` int DEFAULT NULL,
  PRIMARY KEY (`event_id`),
  KEY `student_id` (`student_id`),
  KEY `nurse_id` (`nurse_id`),
  CONSTRAINT `medical_event_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`) ON DELETE CASCADE,
  CONSTRAINT `medical_event_ibfk_2` FOREIGN KEY (`nurse_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=80 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medical_event`
--

LOCK TABLES `medical_event` WRITE;
/*!40000 ALTER TABLE `medical_event` DISABLE KEYS */;
INSERT INTO `medical_event` VALUES (6,'Ngất xỉu trong lớp','2025-06-17 00:00:00','Học sinh ngất xỉu khi đang học môn Thể dục, đã được sơ cứu và chuyển đến phòng y tế.',209,9),(7,'Ngất xỉu trong lớp','2025-06-17 00:00:00','Học sinh ngất xỉu khi đang học môn Thể dục, đã được sơ cứu và chuyển đến phòng y tế.',216,9),(8,'Ngất xỉu trong lớp','2025-06-17 00:00:00','Học sinh ngất xỉu khi đang học môn Thể dục, đã được sơ cứu và chuyển đến phòng y tế.',226,9),(52,'Sốt','2025-06-01 00:00:00','Sốt cao trên 39 độ',205,9),(53,'Ngất xỉu trong lớp','2025-06-01 00:00:00','Mất ý thức tạm thời',206,2),(54,'Té ngã','2025-06-02 00:00:00','Ngã trong sân trường',207,9),(56,'Sốt','2025-06-02 00:00:00','Sốt do thời tiết',209,1),(57,'Té ngã','2025-06-03 00:00:00','Vấp ngã khi chơi thể thao',300,9),(58,'Ngất xỉu trong lớp','2025-06-03 00:00:00','Chóng mặt rồi ngất',299,9),(59,'Đau bụng','2025-06-03 00:00:00','Đau bụng do ăn uống',250,9),(60,'Té ngã','2025-06-04 00:00:00','Ngã xe đạp',270,9),(61,'Đau bụng','2025-06-04 00:00:00','Đau bụng sáng sớm',278,9),(62,'Sốt','2025-06-05 00:00:00','Sốt kèm mệt mỏi',303,9),(63,'Ngất xỉu trong lớp','2025-06-05 00:00:00','Ngất trong giờ học',301,9),(64,'Đau bụng','2025-06-05 00:00:00','Đau bụng nhẹ',302,9),(65,'Khám sức khỏe định kỳ','2025-06-01 00:00:00','Khám tổng quát học kỳ II',304,9),(66,'Sơ cứu','2025-06-02 00:00:00','Bé bị té trầy nhẹ đầu gối',305,9),(67,'Phát hiện bệnh','2025-06-03 00:00:00','Có dấu hiệu viêm họng',306,9),(68,'Khám sức khỏe','2025-06-04 00:00:00','Đo chiều cao, cân nặng định kỳ',307,9),(69,'Sơ cứu','2025-06-05 00:00:00','Bé bị chảy máu mũi nhẹ',308,9),(70,'Tư vấn y tế','2025-06-06 00:00:00','Tư vấn vệ sinh cá nhân',309,9),(71,'Phát hiện dị ứng','2025-06-07 00:00:00','Phát ban sau khi ăn hải sản',310,9),(72,'Khám thị lực','2025-06-08 00:00:00','Kiểm tra mắt khi bé nhìn không rõ',311,9),(73,'Khám tai mũi họng','2025-06-09 00:00:00','Bé ho và có đờm',312,9),(74,'Sơ cứu','2025-06-10 00:00:00','Trầy xước tay do chơi cầu trượt',313,9),(75,'Phát hiện sâu răng','2025-06-11 00:00:00','Đau răng khi ăn uống',314,9),(76,'Khám da liễu','2025-06-12 00:00:00','Phát hiện nổi mẩn ở tay',315,9),(77,'Tư vấn sức khỏe','2025-06-13 00:00:00','Tư vấn dinh dưỡng',316,9),(78,'Khám tổng quát','2025-06-14 00:00:00','Kiểm tra chỉ số BMI',317,9),(79,'Khám tai','2025-06-15 00:00:00','Bé nghe không rõ',318,9);
/*!40000 ALTER TABLE `medical_event` ENABLE KEYS */;
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
