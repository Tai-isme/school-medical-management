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
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `full_name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `address` varchar(200) DEFAULT NULL,
  `role` varchar(50) DEFAULT 'PARENT',
  `active` bit(1) NOT NULL,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `users_chk_1` CHECK ((`role` in (_utf8mb4'PARENT',_utf8mb4'NURSE',_utf8mb4'ADMIN')))
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Parent 1','','','0707333797','HCM','PARENT',_binary '\0'),(2,'Parent 2','','','0797687276','HCM','PARENT',_binary '\0'),(3,'Parent 3','','','0389617970','HCM','PARENT',_binary ''),(4,'Parent 4','','','0767988879','HCM','PARENT',_binary ''),(5,'Parent 5','','','0938824674','HCM','PARENT',_binary ''),(6,'Parent 6','','','0913538249','HCM','PARENT',_binary ''),(7,'Parent 7','','','0898158174','HCM','PARENT',_binary ''),(8,'Parent 8','nguyenhonghieutai7a9@gmail.com','','','HCM','ADMIN',_binary ''),(9,'Parent 9','thienltnse182117@fpt.edu.vn','','','HCM','ADMIN',_binary ''),(10,'Parent 10','thienisme2004@gmail.com','','','HCM','PARENT',_binary ''),(11,'Nguyễn Văn A1','parent1@example.com',NULL,'0900000001','Hà Nội','PARENT',_binary '\0'),(12,'Nguyễn Văn A2','parent2@example.com',NULL,'0900000002','Hà Nội','PARENT',_binary '\0'),(13,'Nguyễn Văn A3','parent3@example.com',NULL,'0900000003','Hà Nội','PARENT',_binary '\0'),(14,'Nguyễn Văn A4','parent4@example.com',NULL,'0900000004','Hà Nội','PARENT',_binary '\0'),(15,'Nguyễn Văn A5','parent5@example.com',NULL,'0900000005','Hà Nội','PARENT',_binary '\0'),(16,'Nguyễn Văn A6','parent6@example.com',NULL,'0900000006','Hà Nội','PARENT',_binary '\0'),(17,'Nguyễn Văn A7','parent7@example.com',NULL,'0900000007','Hà Nội','PARENT',_binary '\0'),(18,'Nguyễn Văn A8','parent8@example.com',NULL,'0900000008','Hà Nội','PARENT',_binary '\0'),(19,'Nguyễn Văn A9','parent9@example.com',NULL,'0900000009','Hà Nội','PARENT',_binary '\0'),(20,'Nguyễn Văn A10','parent10@example.com',NULL,'0900000010','Hà Nội','PARENT',_binary '\0'),(21,'Nguyễn Văn A11','parent11@example.com',NULL,'0900000011','Hà Nội','PARENT',_binary '\0'),(22,'Nguyễn Văn A12','parent12@example.com',NULL,'0900000012','Hà Nội','PARENT',_binary '\0'),(23,'Nguyễn Văn A13','parent13@example.com',NULL,'0900000013','Hà Nội','PARENT',_binary '\0'),(24,'Nguyễn Văn A14','parent14@example.com',NULL,'0900000014','Hà Nội','PARENT',_binary '\0'),(25,'Nguyễn Văn A15','parent15@example.com',NULL,'0900000015','Hà Nội','PARENT',_binary '\0'),(26,'Nguyễn Văn A16','parent16@example.com',NULL,'0900000016','Hà Nội','PARENT',_binary '\0'),(27,'Nguyễn Văn A17','parent17@example.com',NULL,'0900000017','Hà Nội','PARENT',_binary '\0'),(28,'Nguyễn Văn A18','parent18@example.com',NULL,'0900000018','Hà Nội','PARENT',_binary '\0'),(29,'Nguyễn Văn A19','parent19@example.com',NULL,'0900000019','Hà Nội','PARENT',_binary '\0'),(30,'Nguyễn Văn A20','parent20@example.com',NULL,'0900000020','Hà Nội','PARENT',_binary '\0'),(31,'Nguyễn Văn A21','parent21@example.com',NULL,'0900000021','Hà Nội','PARENT',_binary '\0'),(32,'Nguyễn Văn A22','parent22@example.com',NULL,'0900000022','Hà Nội','PARENT',_binary '\0'),(33,'Nguyễn Văn A23','parent23@example.com',NULL,'0900000023','Hà Nội','PARENT',_binary '\0'),(34,'Nguyễn Văn A24','parent24@example.com',NULL,'0900000024','Hà Nội','PARENT',_binary '\0'),(35,'Nguyễn Văn A25','parent25@example.com',NULL,'0900000025','Hà Nội','PARENT',_binary '\0'),(36,'Nguyễn Văn A26','parent26@example.com',NULL,'0900000026','Hà Nội','PARENT',_binary '\0'),(37,'Nguyễn Văn A27','parent27@example.com',NULL,'0900000027','Hà Nội','PARENT',_binary '\0'),(38,'Nguyễn Văn A28','parent28@example.com',NULL,'0900000028','Hà Nội','PARENT',_binary '\0'),(39,'Nguyễn Văn A29','parent29@example.com',NULL,'0900000029','Hà Nội','PARENT',_binary '\0'),(40,'Nguyễn Văn A30','parent30@example.com',NULL,'0900000030','Hà Nội','PARENT',_binary '\0'),(41,'Nguyễn Văn A31','parent31@example.com',NULL,'0900000031','Hà Nội','PARENT',_binary '\0'),(42,'Nguyễn Văn A32','parent32@example.com',NULL,'0900000032','Hà Nội','PARENT',_binary '\0'),(43,'Nguyễn Văn A33','parent33@example.com',NULL,'0900000033','Hà Nội','PARENT',_binary '\0'),(44,'Nguyễn Văn A34','parent34@example.com',NULL,'0900000034','Hà Nội','PARENT',_binary '\0'),(45,'Nguyễn Văn A35','parent35@example.com',NULL,'0900000035','Hà Nội','PARENT',_binary '\0'),(46,'Nguyễn Văn A36','parent36@example.com',NULL,'0900000036','Hà Nội','PARENT',_binary '\0'),(47,'Nguyễn Văn A37','parent37@example.com',NULL,'0900000037','Hà Nội','PARENT',_binary '\0'),(48,'Nguyễn Văn A38','parent38@example.com',NULL,'0900000038','Hà Nội','PARENT',_binary '\0'),(49,'Nguyễn Văn A39','parent39@example.com',NULL,'0900000039','Hà Nội','PARENT',_binary '\0'),(50,'Nguyễn Văn A40','parent40@example.com',NULL,'0900000040','Hà Nội','PARENT',_binary '\0'),(54,'123','se182101trieuphuthinh@gmail.com',NULL,NULL,NULL,'PARENT',_binary ''),(55,'Thinh Trieu','thinhbo17032004@gmail.com',NULL,'9396661123','297 Chu Van An','PARENT',_binary '');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
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
