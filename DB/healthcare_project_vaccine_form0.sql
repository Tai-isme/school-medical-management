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
-- Table structure for table `vaccine_form`
--

DROP TABLE IF EXISTS `vaccine_form`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vaccine_form` (
  `vaccine_form_id` int NOT NULL AUTO_INCREMENT,
  `vaccine_id` int DEFAULT NULL,
  `student_id` int DEFAULT NULL,
  `parent_id` int DEFAULT NULL,
  `form_date` date DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `commit` tinyint(1) DEFAULT NULL,
  `status` varchar(10) DEFAULT 'draft',
  PRIMARY KEY (`vaccine_form_id`),
  KEY `vaccine_id` (`vaccine_id`),
  KEY `student_id` (`student_id`),
  KEY `parent_id` (`parent_id`),
  CONSTRAINT `vaccine_form_ibfk_1` FOREIGN KEY (`vaccine_id`) REFERENCES `vaccine_program` (`vaccine_id`) ON DELETE CASCADE,
  CONSTRAINT `vaccine_form_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`) ON DELETE CASCADE,
  CONSTRAINT `vaccine_form_ibfk_3` FOREIGN KEY (`parent_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `chk_vaccine_form_status` CHECK ((`status` in (_utf8mb4'DRAFT',_utf8mb4'SENT')))
) ENGINE=InnoDB AUTO_INCREMENT=342 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vaccine_form`
--

LOCK TABLES `vaccine_form` WRITE;
/*!40000 ALTER TABLE `vaccine_form` DISABLE KEYS */;
INSERT INTO `vaccine_form` VALUES (301,1,209,10,'2025-11-11',NULL,0,'SENT'),(302,1,205,1,'2025-06-19','xxx',0,'SENT'),(303,2,304,11,'2025-06-01','Đã điền đầy đủ thông tin',1,'SENT'),(304,2,305,12,'2025-06-02','Chưa xác nhận',0,'DRAFT'),(305,2,306,13,'2025-06-03','Sẽ gửi lại sau',0,'DRAFT'),(306,2,307,14,'2025-06-04','Đã xác nhận tham gia',1,'SENT'),(307,2,308,15,'2025-06-05','Mẹ đã ký tên',1,'SENT'),(308,2,309,16,'2025-06-06','Đợi y tá kiểm tra lại',0,'DRAFT'),(309,2,310,17,'2025-06-07','Yêu cầu thêm thông tin',0,'DRAFT'),(310,2,311,18,'2025-06-08','Đã gửi bản cứng',1,'SENT'),(311,2,312,19,'2025-06-09','Đã ký tên và xác nhận',1,'SENT'),(312,2,313,20,'2025-06-10','Chờ phản hồi từ trường',0,'DRAFT'),(313,2,314,21,'2025-06-11','Hỏi lại phụ huynh',0,'DRAFT'),(314,2,315,22,'2025-06-12','Đã đồng ý tham gia',1,'SENT'),(315,2,316,23,'2025-06-13','Chưa gửi bản gốc',0,'DRAFT'),(316,2,317,24,'2025-06-14','Phụ huynh yêu cầu kiểm tra thêm',0,'DRAFT'),(317,2,318,25,'2025-06-15','Sẽ xác nhận trước hạn',1,'SENT'),(318,2,319,26,'2025-06-16','Không tham gia đợt này',0,'DRAFT'),(319,2,320,27,'2025-06-17','Đã đăng ký online',1,'SENT'),(320,2,321,28,'2025-06-18','Đang xem xét',0,'DRAFT'),(321,2,322,29,'2025-06-19','Chờ ngày tiêm',1,'SENT'),(322,2,323,30,'2025-06-20','Bé có lịch đi xa',0,'DRAFT'),(323,2,324,31,'2025-06-21','Tham gia đầy đủ',1,'SENT'),(324,2,325,32,'2025-06-22','Không rõ loại vaccine',0,'DRAFT'),(325,2,326,33,'2025-06-23','Cần giải thích thêm',0,'DRAFT'),(326,2,327,34,'2025-06-24','Xác nhận qua Zalo',1,'SENT'),(327,2,328,35,'2025-06-25','Đồng ý tham gia',1,'SENT'),(328,2,329,36,'2025-06-26','Chưa rõ thời gian tiêm',0,'DRAFT'),(329,2,330,37,'2025-06-27','Có tiền sử dị ứng',0,'DRAFT'),(330,2,331,38,'2025-06-28','Đã ký giấy xác nhận',1,'SENT'),(331,2,332,39,'2025-06-29','Đang xử lý',0,'DRAFT'),(332,2,333,40,'2025-06-30','Đã gửi lại đơn chính thức',1,'SENT'),(333,2,334,41,'2025-07-01','Đã xác nhận',1,'SENT'),(334,2,335,42,'2025-07-02','Chưa nhận được giấy mời',0,'DRAFT'),(335,2,336,43,'2025-07-03','Tham gia qua đăng ký trực tuyến',1,'SENT'),(336,2,337,44,'2025-07-04','Cần hỏi lại bác sĩ gia đình',0,'DRAFT'),(337,2,338,45,'2025-07-05','Đã gửi mẫu xác nhận',1,'SENT'),(338,2,339,46,'2025-07-06','Không đồng ý tiêm',0,'DRAFT'),(339,2,340,47,'2025-07-07','Mong sắp xếp lịch phù hợp',0,'DRAFT'),(340,2,341,48,'2025-07-08','Đã liên hệ y tế',1,'SENT'),(341,2,342,49,'2025-07-09','Gửi nhầm mã số học sinh',0,'DRAFT');
/*!40000 ALTER TABLE `vaccine_form` ENABLE KEYS */;
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
