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
-- Table structure for table `feedback`
--

DROP TABLE IF EXISTS `feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedback` (
  `feedback_id` int NOT NULL AUTO_INCREMENT,
  `satisfaction` varchar(20) DEFAULT NULL,
  `comment` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `parent_id` int DEFAULT NULL,
  `nurse_id` int DEFAULT NULL,
  `vaccine_result_id` int DEFAULT NULL,
  `health_result_id` int DEFAULT NULL,
  `response_nurse` text,
  `status` varchar(20) DEFAULT 'NOT_REPLIED',
  PRIMARY KEY (`feedback_id`),
  KEY `parent_id` (`parent_id`),
  KEY `nurse_id` (`nurse_id`),
  KEY `vaccine_result_id` (`vaccine_result_id`),
  KEY `health_result_id` (`health_result_id`),
  CONSTRAINT `feedback_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `feedback_ibfk_2` FOREIGN KEY (`nurse_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `feedback_ibfk_3` FOREIGN KEY (`vaccine_result_id`) REFERENCES `vaccine_result` (`vaccine_result_id`) ON DELETE CASCADE,
  CONSTRAINT `feedback_ibfk_4` FOREIGN KEY (`health_result_id`) REFERENCES `health_check_result` (`health_result_id`) ON DELETE CASCADE,
  CONSTRAINT `feedback_chk_1` CHECK ((`satisfaction` in (_utf8mb4'SATISFIED',_utf8mb4'UNSATISFIED'))),
  CONSTRAINT `feedback_chk_2` CHECK ((`status` in (_utf8mb4'NOT_REPLIED',_utf8mb4'REPLIED')))
) ENGINE=InnoDB AUTO_INCREMENT=80 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedback`
--

LOCK TABLES `feedback` WRITE;
/*!40000 ALTER TABLE `feedback` DISABLE KEYS */;
INSERT INTO `feedback` VALUES (1,'SATISFIED','Cảm ơn cô y tá đã hỗ trợ nhiệt tình.','2025-06-01 08:00:00',11,9,NULL,204,NULL,'NOT_REPLIED'),(2,'UNSATISFIED','Phản hồi chậm, mong cải thiện.','2025-06-01 08:10:00',12,9,NULL,205,'Chúng tôi xin lỗi và sẽ cải thiện.','REPLIED'),(3,'SATISFIED','Bé được khám kỹ lưỡng, tôi yên tâm.','2025-06-01 08:20:00',13,9,NULL,206,NULL,'NOT_REPLIED'),(4,'UNSATISFIED','Chưa được giải thích rõ ràng.','2025-06-01 08:30:00',14,9,NULL,207,'Chúng tôi sẽ giải thích lại chi tiết hơn.','REPLIED'),(5,'SATISFIED','Dịch vụ tốt, cảm ơn nhà trường.','2025-06-01 08:40:00',15,9,NULL,208,NULL,'NOT_REPLIED'),(6,'UNSATISFIED','Kết quả chưa rõ ràng.','2025-06-01 08:50:00',16,9,NULL,209,'Đã gửi lại kết quả chi tiết cho phụ huynh.','REPLIED'),(7,'SATISFIED','Tôi rất hài lòng với chương trình.','2025-06-01 09:00:00',17,9,NULL,210,NULL,'NOT_REPLIED'),(8,'UNSATISFIED','Không nhận được thông báo trước.','2025-06-01 09:10:00',18,9,NULL,211,'Chúng tôi sẽ cập nhật thông tin tốt hơn.','REPLIED'),(9,'SATISFIED','Cảm ơn cô y tá đã tư vấn tận tình.','2025-06-01 09:20:00',19,9,NULL,212,NULL,'NOT_REPLIED'),(10,'UNSATISFIED','Quy trình chậm trễ.','2025-06-01 09:30:00',20,9,NULL,213,'Sẽ cải thiện quy trình xử lý.','REPLIED'),(11,'SATISFIED','Bé rất vui sau buổi khám.','2025-06-01 09:40:00',21,9,NULL,214,NULL,'NOT_REPLIED'),(12,'UNSATISFIED','Thiếu thông tin về bệnh lý.','2025-06-01 09:50:00',22,9,NULL,215,'Chúng tôi sẽ bổ sung chi tiết.','REPLIED'),(13,'SATISFIED','Hài lòng với kết quả khám.','2025-06-01 10:00:00',23,9,NULL,216,NULL,'NOT_REPLIED'),(14,'UNSATISFIED','Không có ai hỗ trợ khi hỏi.','2025-06-01 10:10:00',24,9,NULL,217,'Chúng tôi xin lỗi và sẽ khắc phục.','REPLIED'),(15,'SATISFIED','Rất chuyên nghiệp.','2025-06-01 10:20:00',25,9,NULL,218,NULL,'NOT_REPLIED'),(16,'UNSATISFIED','Không nhận được phiếu kết quả.','2025-06-01 10:30:00',26,9,NULL,219,'Chúng tôi sẽ gửi lại ngay.','REPLIED'),(17,'SATISFIED','Cảm ơn sự hỗ trợ tận tình.','2025-06-01 10:40:00',27,9,NULL,220,NULL,'NOT_REPLIED'),(18,'UNSATISFIED','Không rõ ràng về quy trình.','2025-06-01 10:50:00',28,9,NULL,221,'Sẽ gửi tài liệu hướng dẫn cụ thể.','REPLIED'),(19,'SATISFIED','Tôi đánh giá cao đội ngũ y tế.','2025-06-01 11:00:00',29,9,NULL,222,NULL,'NOT_REPLIED'),(20,'UNSATISFIED','Khó liên hệ phản hồi.','2025-06-01 11:10:00',30,9,NULL,223,'Sẽ bổ sung thêm kênh liên hệ.','REPLIED'),(21,'SATISFIED','Không có vấn đề gì.','2025-06-01 11:20:00',31,9,NULL,224,NULL,'NOT_REPLIED'),(22,'UNSATISFIED','Thiếu người hướng dẫn tại chỗ.','2025-06-01 11:30:00',32,9,NULL,225,'Đã bổ sung người hỗ trợ trực.','REPLIED'),(23,'SATISFIED','Rất nhanh chóng và hiệu quả.','2025-06-01 11:40:00',33,9,NULL,226,NULL,'NOT_REPLIED'),(24,'UNSATISFIED','Không biết phải làm gì tiếp theo.','2025-06-01 11:50:00',34,9,NULL,227,'Sẽ gửi hướng dẫn chi tiết cho PH.','REPLIED'),(25,'SATISFIED','Thái độ phục vụ tốt.','2025-06-01 12:00:00',35,9,NULL,228,NULL,'NOT_REPLIED'),(26,'UNSATISFIED','Không rõ kết luận khám.','2025-06-01 12:10:00',36,9,NULL,229,'Đã gửi lại thông tin chi tiết.','REPLIED'),(27,'SATISFIED','Không có gì để phàn nàn.','2025-06-01 12:20:00',37,9,NULL,230,NULL,'NOT_REPLIED'),(28,'UNSATISFIED','Gửi nhầm kết quả học sinh khác.','2025-06-01 12:30:00',38,9,NULL,231,'Đã đính chính lại và xin lỗi.','REPLIED'),(29,'SATISFIED','Bé được quan tâm tận tình.','2025-06-01 12:40:00',39,9,NULL,232,NULL,'NOT_REPLIED'),(30,'UNSATISFIED','Thiếu hướng dẫn rõ ràng.','2025-06-01 12:50:00',40,9,NULL,233,'Đã bổ sung tài liệu hướng dẫn.','REPLIED'),(31,'SATISFIED','Hài lòng về thông tin cung cấp.','2025-06-01 13:00:00',41,9,NULL,234,NULL,'NOT_REPLIED'),(32,'UNSATISFIED','Thông tin thiếu chính xác.','2025-06-01 13:10:00',42,9,NULL,235,'Đã điều chỉnh lại thông tin.','REPLIED'),(33,'SATISFIED','Rất đáng tin cậy.','2025-06-01 13:20:00',43,9,NULL,236,NULL,'NOT_REPLIED'),(34,'UNSATISFIED','Không hiểu rõ kết quả.','2025-06-01 13:30:00',44,9,NULL,237,'Đã giải thích lại qua email.','REPLIED'),(35,'SATISFIED','Bé hợp tác tốt trong khám.','2025-06-01 13:40:00',45,9,NULL,238,NULL,'NOT_REPLIED'),(36,'UNSATISFIED','Không hài lòng với phản hồi.','2025-06-01 13:50:00',46,9,NULL,239,'Xin lỗi, sẽ tiếp thu ý kiến.','REPLIED'),(37,'SATISFIED','Mọi thứ diễn ra ổn định.','2025-06-01 14:00:00',47,9,NULL,240,NULL,'NOT_REPLIED'),(38,'UNSATISFIED','Chậm xử lý thắc mắc.','2025-06-01 14:10:00',48,9,NULL,241,'Chúng tôi sẽ cải thiện tốc độ.','REPLIED'),(39,'SATISFIED','Bé được chăm sóc tốt.','2025-06-01 14:20:00',49,9,NULL,242,NULL,'NOT_REPLIED'),(41,'SATISFIED','Cảm ơn nhà trường đã tiêm đúng lịch.','2025-06-01 08:00:00',11,9,3,NULL,NULL,'REPLIED'),(42,'UNSATISFIED','Sau tiêm bé hơi sốt, cần tư vấn thêm.','2025-06-02 08:00:00',12,9,4,NULL,NULL,'NOT_REPLIED'),(43,'SATISFIED','Không có phản ứng, rất tốt.','2025-06-03 08:00:00',13,9,5,NULL,NULL,'REPLIED'),(44,'UNSATISFIED','Bé mệt nhẹ sau tiêm.','2025-06-04 08:00:00',14,9,6,NULL,NULL,'NOT_REPLIED'),(45,'SATISFIED','Rất an tâm với quy trình.','2025-06-05 08:00:00',15,9,7,NULL,NULL,'REPLIED'),(46,'UNSATISFIED','Mong y tá kiểm tra kỹ hơn lần sau.','2025-06-06 08:00:00',16,9,8,NULL,NULL,'NOT_REPLIED'),(47,'SATISFIED','Phản hồi nhanh, hỗ trợ tốt.','2025-06-07 08:00:00',17,9,9,NULL,NULL,'REPLIED'),(48,'UNSATISFIED','Bé có biểu hiện buồn ngủ nhiều.','2025-06-08 08:00:00',18,9,10,NULL,NULL,'NOT_REPLIED'),(49,'SATISFIED','Đội ngũ y tế làm việc chuyên nghiệp.','2025-06-09 08:00:00',19,9,11,NULL,NULL,'REPLIED'),(50,'UNSATISFIED','Chưa rõ thông tin vaccine.','2025-06-10 08:00:00',20,9,12,NULL,NULL,'NOT_REPLIED'),(51,'SATISFIED','Phụ huynh rất yên tâm.','2025-06-11 08:00:00',21,9,13,NULL,NULL,'REPLIED'),(52,'UNSATISFIED','Bé kêu đau tay sau tiêm.','2025-06-12 08:00:00',22,9,14,NULL,NULL,'NOT_REPLIED'),(53,'SATISFIED','Không có phản ứng nào sau tiêm.','2025-06-13 08:00:00',23,9,15,NULL,NULL,'REPLIED'),(54,'UNSATISFIED','Mong kiểm tra lại nhiệt độ.','2025-06-14 08:00:00',24,9,16,NULL,NULL,'NOT_REPLIED'),(55,'SATISFIED','Cảm ơn nhà trường đã hỗ trợ.','2025-06-15 08:00:00',25,9,17,NULL,NULL,'REPLIED'),(56,'UNSATISFIED','Bé hơi quấy khóc.','2025-06-16 08:00:00',26,9,18,NULL,NULL,'NOT_REPLIED'),(57,'SATISFIED','Thông tin rõ ràng, minh bạch.','2025-06-17 08:00:00',27,9,19,NULL,NULL,'REPLIED'),(58,'UNSATISFIED','Chưa thấy báo cáo chi tiết.','2025-06-18 08:00:00',28,9,20,NULL,NULL,'NOT_REPLIED'),(59,'SATISFIED','Rất đáng khen đội y tế.','2025-06-19 08:00:00',29,9,21,NULL,NULL,'REPLIED'),(60,'UNSATISFIED','Đề nghị gọi điện xác minh thêm.','2025-06-20 08:00:00',30,9,22,NULL,NULL,'NOT_REPLIED'),(61,'SATISFIED','Dịch vụ tiêm chủng tốt.','2025-06-21 08:00:00',31,9,23,NULL,NULL,'REPLIED'),(62,'UNSATISFIED','Phản ứng hơi mạnh.','2025-06-22 08:00:00',32,9,24,NULL,NULL,'NOT_REPLIED'),(63,'SATISFIED','Bé vẫn ăn uống bình thường.','2025-06-23 08:00:00',33,9,25,NULL,NULL,'REPLIED'),(64,'UNSATISFIED','Không rõ bé tiêm loại gì.','2025-06-24 08:00:00',34,9,26,NULL,NULL,'NOT_REPLIED'),(65,'SATISFIED','Mọi thứ đều ổn.','2025-06-25 08:00:00',35,9,27,NULL,NULL,'REPLIED'),(66,'UNSATISFIED','Có dấu hiệu đỏ vùng tiêm.','2025-06-26 08:00:00',36,9,28,NULL,NULL,'NOT_REPLIED'),(67,'SATISFIED','Rất tốt, không vấn đề gì.','2025-06-27 08:00:00',37,9,29,NULL,NULL,'REPLIED'),(68,'UNSATISFIED','Tiêm xong bé khó ngủ.','2025-06-28 08:00:00',38,9,30,NULL,NULL,'NOT_REPLIED'),(69,'SATISFIED','Phụ huynh hài lòng.','2025-06-29 08:00:00',39,9,31,NULL,NULL,'REPLIED'),(70,'UNSATISFIED','Chưa rõ thông tin hậu kiểm.','2025-06-30 08:00:00',40,9,32,NULL,NULL,'NOT_REPLIED'),(71,'SATISFIED','Đội ngũ y tế hỗ trợ tốt.','2025-07-01 08:00:00',41,9,33,NULL,NULL,'REPLIED'),(72,'UNSATISFIED','Bé có phản ứng chậm.','2025-07-02 08:00:00',42,9,34,NULL,NULL,'NOT_REPLIED'),(73,'SATISFIED','Thời gian tiêm hợp lý.','2025-07-03 08:00:00',43,9,35,NULL,NULL,'REPLIED'),(74,'UNSATISFIED','Chưa được tư vấn rõ ràng.','2025-07-04 08:00:00',44,9,36,NULL,NULL,'NOT_REPLIED'),(75,'SATISFIED','Bé khỏe mạnh bình thường.','2025-07-05 08:00:00',45,9,37,NULL,NULL,'REPLIED'),(76,'UNSATISFIED','Mong nhận báo cáo tiêm.','2025-07-06 08:00:00',46,9,38,NULL,NULL,'NOT_REPLIED'),(77,'SATISFIED','Không có phản ứng nào đáng lo.','2025-07-07 08:00:00',47,9,39,NULL,NULL,'REPLIED'),(78,'UNSATISFIED','Phụ huynh cần gọi lại xác nhận.','2025-07-08 08:00:00',48,9,40,NULL,NULL,'NOT_REPLIED'),(79,'SATISFIED','Rất an tâm.','2025-07-09 08:00:00',49,9,41,NULL,NULL,'REPLIED');
/*!40000 ALTER TABLE `feedback` ENABLE KEYS */;
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
