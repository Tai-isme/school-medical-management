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
-- Table structure for table `vaccine_name`
--

DROP TABLE IF EXISTS `vaccine_name`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vaccine_name` (
  `vaccine_name_id` int NOT NULL AUTO_INCREMENT,
  `vaccine_name` varchar(100) NOT NULL,
  `manufacture` varchar(100) DEFAULT NULL,
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`vaccine_name_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vaccine_name`
--

LOCK TABLES `vaccine_name` WRITE;
/*!40000 ALTER TABLE `vaccine_name` DISABLE KEYS */;
INSERT INTO `vaccine_name` VALUES (1,'Viêm gan A','Công ty Dược A',NULL,'Ngừa viêm gan do virus A'),(2,'Viêm gan B','Công ty Dược B',NULL,'Tiêm chủng sơ sinh và trẻ nhỏ'),(3,'Cúm mùa','Viện Pasteur',NULL,'Ngừa cúm theo mùa, tiêm nhắc hàng năm'),(4,'Thủy đậu','Công ty Sinh học X',NULL,'Ngừa bệnh thủy đậu ở trẻ em'),(5,'Viêm não Nhật Bản','Viện Vắc xin Nha Trang',NULL,'Tiêm từ 12 tháng tuổi trở lên'),(6,'Bại liệt','WHO phân phối',NULL,'Phòng bệnh bại liệt ở trẻ dưới 5 tuổi'),(7,'Bạch hầu','VNVC',NULL,'Thường tiêm trong gói 5 trong 1'),(8,'Ho gà','GSK',NULL,'Kết hợp trong các vắc xin tổng hợp'),(9,'Uốn ván, viêm gan B','Sanofi',NULL,'Tiêm cho trẻ sơ sinh hoặc thai phụ'),(10,'Hib','BioPharma',NULL,'Ngừa viêm phổi, viêm màng não do Hib'),(11,'HPV','MSD',NULL,'Ngừa ung thư cổ tử cung, tiêm cho nữ 9–26 tuổi'),(12,'Covid-19','USA',NULL,'Mới Nhập');
/*!40000 ALTER TABLE `vaccine_name` ENABLE KEYS */;
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
