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
-- Table structure for table `health_check_result`
--

DROP TABLE IF EXISTS `health_check_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `health_check_result` (
  `health_result_id` int NOT NULL AUTO_INCREMENT,
  `diagnosis` varchar(255) DEFAULT NULL,
  `level` varchar(50) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `vision` varchar(50) DEFAULT NULL,
  `hearing` varchar(50) DEFAULT NULL,
  `weight` varchar(50) DEFAULT NULL,
  `height` varchar(50) DEFAULT NULL,
  `health_check_form_id` int DEFAULT NULL,
  PRIMARY KEY (`health_result_id`),
  KEY `health_check_form_id` (`health_check_form_id`),
  CONSTRAINT `health_check_result_ibfk_1` FOREIGN KEY (`health_check_form_id`) REFERENCES `health_check_form` (`health_check_form_id`) ON DELETE CASCADE,
  CONSTRAINT `health_check_result_chk_1` CHECK ((`level` in (_utf8mb4'GOOD',_utf8mb4'FAIR',_utf8mb4'AVERAGE',_utf8mb4'POOR')))
) ENGINE=InnoDB AUTO_INCREMENT=243 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `health_check_result`
--

LOCK TABLES `health_check_result` WRITE;
/*!40000 ALTER TABLE `health_check_result` DISABLE KEYS */;
INSERT INTO `health_check_result` VALUES (201,'Khỏe mạnh','GOOD','Không vấn đề gì','8/10','Normal','65','170',1001),(202,'Mệt nhẹ','FAIR','Nên theo dõi thêm','8/10','Normal','65','170',1002),(203,'Cần nghỉ ngơi','POOR','Chóng mặt','8/10','Normal','65','170',1003),(204,'Khỏe mạnh','GOOD','Không có dấu hiệu bất thường','8/10','Normal','65','170',1004),(205,'Dị ứng nhẹ','FAIR','Cần tránh tiếp xúc với phấn hoa','8/10','Normal','65','170',1005),(206,'Cân nặng thấp','AVERAGE','Cần cải thiện chế độ ăn','8/10','Normal','65','170',1006),(207,'Sâu răng','FAIR','Nên đi khám nha khoa','8/10','Normal','65','170',1007),(208,'Bình thường','GOOD','Phát triển tốt','8/10','Normal','65','170',1008),(209,'Cận thị nhẹ','AVERAGE','Đề xuất đo lại thị lực định kỳ','8/10','Normal','65','170',1009),(210,'Da mẩn đỏ','FAIR','Khuyến cáo sử dụng kem dị ứng','8/10','Normal','65','170',1010),(211,'Khỏe mạnh','GOOD','Không có triệu chứng bất thường','8/10','Normal','65','170',1011),(212,'Viêm họng nhẹ','FAIR','Cần giữ ấm cổ họng','8/10','Normal','65','170',1012),(213,'Chậm tăng chiều cao','AVERAGE','Nên bổ sung canxi','8/10','Normal','65','170',1013),(214,'Sức khỏe tốt','GOOD','Phát triển bình thường','8/10','Normal','65','170',1014),(215,'Viêm da tiếp xúc','FAIR','Tránh dùng xà phòng mạnh','8/10','Normal','65','170',1015),(216,'Béo phì mức 1','AVERAGE','Cần tập luyện nhiều hơn','8/10','Normal','65','170',1016),(217,'Thị lực tốt','GOOD','Không cần can thiệp','8/10','Normal','65','170',1017),(218,'Hô hấp ổn định','GOOD','Không có dấu hiệu khò khè','8/10','Normal','65','170',1018),(219,'Viêm mũi dị ứng','FAIR','Cần tránh bụi và lông động vật','8/10','Normal','65','170',1019),(220,'Sâu răng nhiều','POOR','Cần điều trị nha khoa sớm','8/10','Normal','65','170',1020),(221,'Suy dinh dưỡng nhẹ','AVERAGE','Cần bổ sung năng lượng','8/10','Normal','65','170',1021),(222,'Không có bệnh','GOOD','Bé rất khỏe mạnh','8/10','Normal','65','170',1022),(223,'Viêm kết mạc','FAIR','Cần tra thuốc đúng lịch','8/10','Normal','65','170',1023),(224,'Thừa cân nhẹ','AVERAGE','Khuyên giảm ăn ngọt','8/10','Normal','65','170',1024),(225,'Hô hấp tốt','GOOD','Không có bất thường','8/10','Normal','65','170',1025),(226,'Viêm da cơ địa','FAIR','Nên theo dõi thêm','8/10','Normal','65','170',1026),(227,'Hệ tiêu hóa ổn','GOOD','Không có vấn đề tiêu hóa','8/10','Normal','65','170',1027),(228,'Thiếu máu nhẹ','AVERAGE','Cần bổ sung sắt','8/10','Normal','65','170',1028),(229,'Khỏe mạnh','GOOD','Không ghi nhận bất thường','8/10','Normal','65','170',1029),(230,'Đau mắt đỏ','FAIR','Tránh tiếp xúc gần với bạn bè','8/10','Normal','65','170',1030),(231,'Đau đầu nhẹ','AVERAGE','Theo dõi thêm nếu tái phát','8/10','Normal','65','170',1031),(232,'Không bệnh lý','GOOD','Bình thường theo độ tuổi','8/10','Normal','65','170',1032),(233,'Viêm phế quản nhẹ','FAIR','Nghỉ ngơi và uống nước ấm','8/10','Normal','65','170',1033),(234,'Tăng huyết áp nhẹ','AVERAGE','Nên theo dõi thêm','8/10','Normal','65','170',1034),(235,'Cơ xương phát triển tốt','GOOD','Không lệch vẹo cột sống','8/10','Normal','65','170',1035),(236,'Thiếu vitamin D','AVERAGE','Cần tiếp xúc ánh nắng nhiều hơn','8/10','Normal','65','170',1036),(237,'Không bất thường','GOOD','Hoàn toàn khỏe mạnh','8/10','Normal','65','170',1037),(238,'Cận thị trung bình','FAIR','Cần đeo kính khi học','8/10','Normal','65','170',1038),(239,'Chán ăn','AVERAGE','Khuyến khích vận động nhẹ','8/10','Normal','65','170',1039),(240,'Khỏe mạnh','GOOD','Không vấn đề gì','8/10','Normal','65','170',1040),(241,'Đầy bụng khó tiêu','FAIR','Nên ăn uống điều độ','8/10','Normal','65','170',1041),(242,'Viêm lợi','AVERAGE','Cần khám nha sĩ','8/10','Normal','65','170',1042);
/*!40000 ALTER TABLE `health_check_result` ENABLE KEYS */;
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
