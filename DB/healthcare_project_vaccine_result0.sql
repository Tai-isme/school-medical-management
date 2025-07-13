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
-- Table structure for table `vaccine_result`
--

DROP TABLE IF EXISTS `vaccine_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vaccine_result`
--

LOCK TABLES `vaccine_result` WRITE;
/*!40000 ALTER TABLE `vaccine_result` DISABLE KEYS */;
INSERT INTO `vaccine_result` VALUES (2,'Tốt','Cần theo dõi thêm','Không có biểu hiện xấu','2025-06-21 03:39:07',301),(3,'Tốt','Không có biểu hiện bất thường','Không phản ứng','2025-06-23 14:51:39',303),(4,'Bình thường','Theo dõi thêm 24h','Sốt nhẹ','2025-06-23 14:51:39',304),(5,'Tốt','Phản ứng nhẹ, không đáng lo','Đỏ tại chỗ tiêm','2025-06-23 14:51:39',305),(6,'Tốt','Không có phản ứng phụ','Không phản ứng','2025-06-23 14:51:39',306),(7,'Khá','Theo dõi thêm','Sưng nhẹ','2025-06-23 14:51:39',307),(8,'Bình thường','Phản ứng nhẹ sau 2h','Ngủ nhiều hơn','2025-06-23 14:51:39',308),(9,'Tốt','Không có biểu hiện gì','Không phản ứng','2025-06-23 14:51:39',309),(10,'Bình thường','Mệt mỏi trong ngày đầu','Sốt nhẹ','2025-06-23 14:51:39',310),(11,'Tốt','Phản ứng nhẹ và đã hết','Đỏ nhẹ','2025-06-23 14:51:39',311),(12,'Tốt','Trạng thái tốt sau tiêm','Không phản ứng','2025-06-23 14:51:39',312),(13,'Khá','Theo dõi thêm','Chán ăn nhẹ','2025-06-23 14:51:39',313),(14,'Bình thường','Không sốt nhưng hơi mệt','Buồn ngủ','2025-06-23 14:51:39',314),(15,'Tốt','Không có biểu hiện gì','Không phản ứng','2025-06-23 14:51:39',315),(16,'Tốt','Bé vui chơi bình thường','Không phản ứng','2025-06-23 14:51:39',316),(17,'Bình thường','Hơi sốt trong buổi chiều','Sốt nhẹ','2025-06-23 14:51:39',317),(18,'Tốt','Tiêm xong không có phản ứng','Không phản ứng','2025-06-23 14:51:39',318),(19,'Tốt','Sức khỏe ổn định sau tiêm','Không phản ứng','2025-06-23 14:51:39',319),(20,'Bình thường','Theo dõi tại nhà 1 ngày','Sưng nhẹ','2025-06-23 14:51:39',320),(21,'Tốt','Không có biểu hiện bất thường','Không phản ứng','2025-06-23 14:51:39',321),(22,'Khá','Khóc nhiều sau tiêm','Khó chịu nhẹ','2025-06-23 14:51:39',322),(23,'Tốt','Trạng thái tốt','Không phản ứng','2025-06-23 14:51:39',323),(24,'Tốt','Không phản ứng gì cả','Không phản ứng','2025-06-23 14:51:39',324),(25,'Bình thường','Mệt nhẹ vào buổi tối','Mệt mỏi','2025-06-23 14:51:39',325),(26,'Tốt','Không có dấu hiệu sốt','Không phản ứng','2025-06-23 14:51:39',326),(27,'Tốt','Bé ăn uống bình thường','Không phản ứng','2025-06-23 14:51:39',327),(28,'Bình thường','Sốt nhẹ, đã ổn','Sốt nhẹ','2025-06-23 14:51:39',328),(29,'Tốt','Không bị sưng đau','Không phản ứng','2025-06-23 14:51:39',329),(30,'Tốt','Không phát hiện phản ứng phụ','Không phản ứng','2025-06-23 14:51:39',330),(31,'Bình thường','Cần theo dõi thêm','Đau nhẹ','2025-06-23 14:51:39',331),(32,'Tốt','Không phản ứng','Không phản ứng','2025-06-23 14:51:39',332),(33,'Tốt','Bé khỏe mạnh sau tiêm','Không phản ứng','2025-06-23 14:51:39',333),(34,'Tốt','Không có phản ứng phụ','Không phản ứng','2025-06-23 14:51:39',334),(35,'Khá','Đỏ tại vị trí tiêm','Đỏ nhẹ','2025-06-23 14:51:39',335),(36,'Tốt','Ổn định','Không phản ứng','2025-06-23 14:51:39',336),(37,'Tốt','Không có biểu hiện gì bất thường','Không phản ứng','2025-06-23 14:51:39',337),(38,'Bình thường','Có phản ứng nhẹ','Sưng nhẹ','2025-06-23 14:51:39',338),(39,'Tốt','Không có dấu hiệu gì','Không phản ứng','2025-06-23 14:51:39',339),(40,'Tốt','Không sốt, chơi bình thường','Không phản ứng','2025-06-23 14:51:39',340),(41,'Bình thường','Theo dõi tại nhà','Sốt nhẹ','2025-06-23 14:51:39',341);
/*!40000 ALTER TABLE `vaccine_result` ENABLE KEYS */;
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
