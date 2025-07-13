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
-- Table structure for table `vaccine_history`
--

DROP TABLE IF EXISTS `vaccine_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vaccine_history` (
  `vaccine_history_id` int NOT NULL AUTO_INCREMENT,
  `note` varchar(255) DEFAULT NULL,
  `create_by` tinyint DEFAULT NULL,
  `record_id` int DEFAULT NULL,
  `vaccine_name_id` int DEFAULT NULL,
  PRIMARY KEY (`vaccine_history_id`),
  KEY `record_id` (`record_id`),
  KEY `fk_vaccine_history_vaccine_name` (`vaccine_name_id`),
  CONSTRAINT `fk_vaccine_history_vaccine_name` FOREIGN KEY (`vaccine_name_id`) REFERENCES `vaccine_name` (`vaccine_name_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `vaccine_history_ibfk_1` FOREIGN KEY (`record_id`) REFERENCES `medical_record` (`record_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=261 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vaccine_history`
--

LOCK TABLES `vaccine_history` WRITE;
/*!40000 ALTER TABLE `vaccine_history` DISABLE KEYS */;
INSERT INTO `vaccine_history` VALUES (44,'Tiêm mũi 1',NULL,10,NULL),(45,'Mũi 2',NULL,11,NULL),(46,'Đã tiêm đủ',NULL,12,NULL),(199,'Tiêm lúc 2 tuổi',NULL,13,3),(200,'Đã tiêm đủ 3 mũi',NULL,14,6),(201,'Uống vắc xin lần 1',NULL,15,9),(202,'Tiêm nhắc lại năm 2022',NULL,16,7),(203,'Tiêm mũi đầu tiên',NULL,17,6),(204,'Tiêm sau chấn thương',NULL,18,10),(205,'Tiêm phòng bệnh Hib',NULL,19,9),(206,'Tiêm mũi 3',NULL,20,2),(207,'Tiêm trước nhập học',NULL,21,4),(208,'Mũi đầu tiên',NULL,22,4),(209,'Tiêm mỗi năm',NULL,23,8),(210,'Nhắc lại sau 1 năm',NULL,24,5),(211,'Tiêm nhắc lúc 6 tuổi',NULL,25,1),(212,'Tiêm đủ 2 mũi',NULL,26,11),(213,'Tiêm định kỳ',NULL,27,9),(214,'Tiêm mũi 2',NULL,28,1),(215,'Mũi cuối',NULL,29,11),(216,'Mũi đầu lúc 2 tháng',NULL,30,7),(217,'Tiêm mũi 2',NULL,31,3),(218,'Mũi thứ 2',NULL,32,2),(219,'Tiêm định kỳ mỗi đông',NULL,33,2),(220,'Đủ 2 mũi',NULL,34,2),(221,'Mũi 1',NULL,35,6),(222,'Tiêm nhỏ miệng',NULL,36,10),(223,'Đủ liều cơ bản',NULL,37,10),(224,'Đã tiêm mũi 3',NULL,38,8),(225,'Tiêm khi 4 tháng tuổi',NULL,39,8),(226,'Tiêm định kỳ',NULL,40,7),(227,'Hoàn thành lộ trình',NULL,41,10),(228,'Mũi tiêm lúc sinh',NULL,42,6),(229,'Tiêm đủ 2 mũi',NULL,43,1),(230,'Mũi định kỳ 2025',NULL,44,6),(231,'Đã tiêm năm ngoái',NULL,45,7),(232,'Đã từng mắc, không cần tiêm',NULL,46,6),(233,'Tiêm khi 6 tháng tuổi',NULL,47,6),(234,'Mũi tiêm ở phường',NULL,48,11),(235,'Tiêm đủ mũi',NULL,49,4),(236,NULL,NULL,54,1),(237,'1',NULL,55,1),(238,'1',NULL,56,1),(239,'2',NULL,56,2),(240,'aaaaaa',NULL,57,1),(241,'11',NULL,58,1),(242,'1',NULL,59,1),(243,'2',NULL,59,8),(244,'3',NULL,59,7),(245,'2222',NULL,60,7),(246,'1',NULL,61,6),(247,'2',NULL,61,6),(248,'1',NULL,62,8),(249,'2',NULL,62,4),(250,'5151515',NULL,63,2),(251,'5151515',NULL,64,2),(252,'2222',NULL,64,8),(257,'2222',0,65,4),(258,'2222111111111111111111111111111111111111111111111111444444',0,65,3),(259,'8888',1,65,2),(260,'9999',1,65,7);
/*!40000 ALTER TABLE `vaccine_history` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-07-14  2:27:03
