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
  `vaccine_name` varchar(100) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `record_id` int DEFAULT NULL,
  `vaccine_id` int DEFAULT NULL,
  PRIMARY KEY (`vaccine_history_id`),
  KEY `fk_vaccine_history_vaccine_program` (`vaccine_id`),
  KEY `fk_vaccine_history_record` (`record_id`),
  CONSTRAINT `fk_vaccine_history_vaccine_program` FOREIGN KEY (`vaccine_id`) REFERENCES `vaccine_program` (`vaccine_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `vaccine_history_ibfk_1` FOREIGN KEY (`record_id`) REFERENCES `medical_record` (`record_id`) ON DELETE CASCADE,
  CONSTRAINT `chk_vaccine_name` CHECK (((`vaccine_name` is null) or (`vaccine_name` in (_utf8mb4'HEPATITIS_A',_utf8mb4'HEPATITIS_B',_utf8mb4'INFLUENZA',_utf8mb4'CHICKENPOX',_utf8mb4'JAPANESE_ENCEPHALITIS',_utf8mb4'POLIO',_utf8mb4'DIPHTHERIA',_utf8mb4'PERTUSSIS',_utf8mb4'TETANUS_HEPATITIS_B',_utf8mb4'HIB',_utf8mb4'HPV'))))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vaccine_history`
--

LOCK TABLES `vaccine_history` WRITE;
/*!40000 ALTER TABLE `vaccine_history` DISABLE KEYS */;
INSERT INTO `vaccine_history` VALUES (1,'HEPATITIS_A','fdsfds',53,NULL),(2,'HEPATITIS_A','2',54,NULL);
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

-- Dump completed on 2025-07-03  1:13:55
