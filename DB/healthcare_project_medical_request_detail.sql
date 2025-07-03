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
-- Table structure for table `medical_request_detail`
--

DROP TABLE IF EXISTS `medical_request_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medical_request_detail` (
  `detail_id` int NOT NULL AUTO_INCREMENT,
  `medicine_name` varchar(100) DEFAULT NULL,
  `dosage` varchar(100) DEFAULT NULL,
  `time` varchar(200) DEFAULT NULL,
  `request_id` int DEFAULT NULL,
  PRIMARY KEY (`detail_id`),
  KEY `request_id` (`request_id`),
  CONSTRAINT `medical_request_detail_ibfk_1` FOREIGN KEY (`request_id`) REFERENCES `medical_request` (`request_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=150815 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medical_request_detail`
--

LOCK TABLES `medical_request_detail` WRITE;
/*!40000 ALTER TABLE `medical_request_detail` DISABLE KEYS */;
INSERT INTO `medical_request_detail` VALUES (150794,'Paracetamol 500mg','1 viên','Sáng sau ăn',150792),(150795,'Vitamin C','1 viên','Chiều sau ăn',150792),(150796,'Paracetamol 250mg','1 viên','Sáng, chiều',150793),(150797,'Xịt mũi Natri Clorid 0.9%','2 lần/ngày','Sáng và tối',150794),(150798,'Thuốc hạ sốt Hapacol','1 viên 250mg','Khi sốt trên 38.5°C',150795),(150799,'Paracetamol gói bột','1 gói','Sau ăn sáng và tối',150796),(150800,'Men tiêu hóa Lactomin','1 gói','Trước bữa ăn',150797),(150801,'Siro ho Prospan','5ml','3 lần/ngày sau ăn',150798),(150802,'Oresol','1 gói pha nước','Uống từng ngụm nhỏ, 3 lần/ngày',150799),(150803,'Vitamin C 100mg','1 viên','Sáng sau ăn',150800),(150804,'Thuốc dị ứng Clorpheniramin','1 viên','Tối trước khi ngủ',150801),(150805,'Thuốc nhỏ mắt V-Rohto','2 giọt','Sáng và tối',150802),(150806,'Dung dịch súc miệng Betadine','10ml','Sau bữa ăn trưa và tối',150803),(150807,'Thuốc nhỏ tai Otipax','2 giọt','3 lần/ngày',150804),(150808,'Thuốc tiêu hóa Enterogermina','1 ống','Sáng sau ăn',150805),(150809,'Viên dị ứng Telfast','1 viên','Tối sau ăn',150806),(150810,'Kem bôi trị viêm da Fucicort','Bôi mỏng','2 lần/ngày',150807),(150811,'rr3','r3r3r','Sau ăn sáng từ 9h-9h30',150808),(150812,'Parracetamon','Uống 1 viên','Trước ăn trưa: 10h30-11h',150809),(150813,'Parracetamon222','Uống 1 muỗng','Sau ăn trưa: từ 11h30-12h',150809),(150814,'sdf','dfsd','Sau ăn trưa: từ 11h30-12h',150810);
/*!40000 ALTER TABLE `medical_request_detail` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-07-03  1:13:57
