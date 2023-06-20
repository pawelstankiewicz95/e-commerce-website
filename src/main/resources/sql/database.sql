-- MySQL dump 10.13  Distrib 8.0.31, for Win64 (x86_64)
--
-- Host: localhost    Database: full-stack-ecommerce
-- ------------------------------------------------------
-- Server version	8.0.31

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
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_product`
--

DROP TABLE IF EXISTS `cart_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_product` (
  `surrogate_id` bigint NOT NULL AUTO_INCREMENT,
  `cart_product_id` bigint DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `unit_price` bigint DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `cart_id` bigint DEFAULT NULL,
  PRIMARY KEY (`surrogate_id`),
  KEY `cart_id` (`cart_id`),
  CONSTRAINT `cart_product_ibfk_1` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_product`
--

LOCK TABLES `cart_product` WRITE;
/*!40000 ALTER TABLE `cart_product` DISABLE KEYS */;
/*!40000 ALTER TABLE `cart_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `phone_number` int DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `quantity` int DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(600) DEFAULT NULL,
  `unit_price` int DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `order_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  CONSTRAINT `order_item_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `summary_id` bigint DEFAULT NULL,
  `customer_id` bigint DEFAULT NULL,
  `shipping_address_id` bigint DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `date_created` datetime DEFAULT NULL,
  `last_updated` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `customer_id` (`customer_id`),
  KEY `shipping_address_id` (`shipping_address_id`),
  KEY `summary_id` (`summary_id`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`shipping_address_id`) REFERENCES `shipping_address` (`id`),
  CONSTRAINT `orders_ibfk_3` FOREIGN KEY (`summary_id`) REFERENCES `summary` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sku` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `unit_price` decimal(13,2) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `active` bit(1) DEFAULT b'1',
  `units_in_stock` int DEFAULT NULL,
  `date_created` datetime(6) DEFAULT NULL,
  `last_updated` datetime(6) DEFAULT NULL,
  `category_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_category` (`category_id`),
  CONSTRAINT `fk_category` FOREIGN KEY (`category_id`) REFERENCES `product_category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (17,'SKU001','The Art of Fiction','A captivating book about the art of storytelling',29.99,'assets/img/products/placeholder.png',_binary '',50,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',1),(18,'SKU002','The Secret Garden','A classic children\'s novel about the power of nature',19.99,'assets/img/products/placeholder.png',_binary '',20,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',1),(19,'SKU003','The Chronicles of Narnia','A magical fantasy series by C.S. Lewis',39.99,'assets/img/products/placeholder.png',_binary '',10,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',1),(20,'SKU004','Harry Potter and the Philosopher\'s Stone','The first book in the Harry Potter series',24.99,'assets/img/products/placeholder.png',_binary '',30,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',1),(21,'SKU005','To Kill a Mockingbird','A powerful novel addressing racial injustice',22.99,'assets/img/products/placeholder.png',_binary '',15,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',1),(22,'SKU006','The Great Gatsby','A classic tale of the American Dream',19.99,'assets/img/products/placeholder.png',_binary '',25,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',1),(23,'SKU007','Pride and Prejudice','Jane Austen\'s timeless romantic novel',29.99,'assets/img/products/placeholder.png',_binary '',10,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',1),(24,'SKU008','1984','A dystopian novel by George Orwell',24.99,'assets/img/products/placeholder.png',_binary '',20,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',1),(25,'SKU009','Brave New World','A thought-provoking science fiction novel',27.99,'assets/img/products/placeholder.png',_binary '',15,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',1),(26,'SKU010','The Catcher in the Rye','A coming-of-age story by J.D. Salinger',21.99,'assets/img/products/placeholder.png',_binary '',30,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',1),(27,'SKU011','Warcraft III: Reign of Chaos','A popular real-time strategy game',49.99,'assets/img/products/placeholder.png',_binary '',50,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',2),(28,'SKU012','The Elder Scrolls V: Skyrim','An open-world role-playing game',59.99,'assets/img/products/placeholder.png',_binary '',20,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',2),(29,'SKU013','Minecraft','A sandbox game that allows players to build and explore',29.99,'assets/img/products/placeholder.png',_binary '',10,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',2),(30,'SKU014','Grand Theft Auto V','An action-adventure game set in an open-world environment',39.99,'assets/img/products/placeholder.png',_binary '',30,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',2),(31,'SKU015','The Witcher 3: Wild Hunt','A critically acclaimed role-playing game',49.99,'assets/img/products/placeholder.png',_binary '',15,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',2),(32,'SKU016','Overwatch','A team-based multiplayer first-person shooter',29.99,'assets/img/products/placeholder.png',_binary '',25,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',2),(33,'SKU017','Fallout 4','A post-apocalyptic action role-playing game',39.99,'assets/img/products/placeholder.png',_binary '',10,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',2),(34,'SKU018','FIFA 22','A popular soccer simulation game',59.99,'assets/img/products/placeholder.png',_binary '',20,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',2),(35,'SKU019','Counter-Strike: Global Offensive','A multiplayer first-person shooter',19.99,'assets/img/products/placeholder.png',_binary '',15,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',2),(36,'SKU020','The Sims 4','A life simulation game',39.99,'assets/img/products/placeholder.png',_binary '',30,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',2),(37,'SKU021','The Shawshank Redemption','A gripping drama about a prison inmate',14.99,'assets/img/products/placeholder.png',_binary '',50,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',3),(38,'SKU022','Pulp Fiction','A cult classic crime film by Quentin Tarantino',12.99,'assets/img/products/placeholder.png',_binary '',20,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',3),(39,'SKU023','The Godfather','An iconic mafia film directed by Francis Ford Coppola',16.99,'assets/img/products/placeholder.png',_binary '',10,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',3),(40,'SKU024','Inception','A mind-bending science fiction thriller',12.99,'assets/img/products/placeholder.png',_binary '',30,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',3),(41,'SKU025','The Matrix','A groundbreaking action film set in a dystopian future',14.99,'assets/img/products/placeholder.png',_binary '',15,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',3),(42,'SKU026','Forrest Gump','A heartwarming story about a simple-minded man',9.99,'assets/img/products/placeholder.png',_binary '',25,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',3),(43,'SKU027','Fight Club','A psychological drama about an underground fight club',12.99,'assets/img/products/placeholder.png',_binary '',10,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',3),(44,'SKU028','The Dark Knight','A superhero film featuring Batman',14.99,'assets/img/products/placeholder.png',_binary '',20,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',3),(45,'SKU029','The Avengers','A superhero ensemble film from the Marvel Cinematic Universe',16.99,'assets/img/products/placeholder.png',_binary '',15,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',3),(46,'SKU030','Titanic','A romantic disaster film set on the ill-fated ship',9.99,'assets/img/products/placeholder.png',_binary '',30,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',3),(47,'SKU031','iPhone 12','A powerful and sleek smartphone by Apple',999.99,'assets/img/products/placeholder.png',_binary '',50,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',4),(48,'SKU032','Samsung Galaxy S21','A feature-rich Android smartphone',899.99,'assets/img/products/placeholder.png',_binary '',20,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',4),(49,'SKU033','Google Pixel 6','An innovative smartphone with exceptional camera capabilities',799.99,'assets/img/products/placeholder.png',_binary '',10,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',4),(50,'SKU034','OnePlus 9 Pro','A flagship-grade smartphone with top-tier performance',899.99,'assets/img/products/placeholder.png',_binary '',30,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',4),(51,'SKU035','Xiaomi Mi 11','A value-for-money smartphone with cutting-edge features',699.99,'assets/img/products/placeholder.png',_binary '',15,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',4),(52,'SKU036','Sony Xperia 1 III','A high-end smartphone with a stunning display',1099.99,'assets/img/products/placeholder.png',_binary '',25,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',4),(53,'SKU037','Huawei P40 Pro','A flagship smartphone with exceptional photography capabilities',899.99,'assets/img/products/placeholder.png',_binary '',10,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',4),(54,'SKU038','LG Velvet','A stylish smartphone with a unique design',699.99,'assets/img/products/placeholder.png',_binary '',20,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',4),(55,'SKU039','Motorola Edge+','A feature-packed smartphone with 5G connectivity',799.99,'assets/img/products/placeholder.png',_binary '',15,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',4),(56,'SKU040','Nokia 8.3','A reliable smartphone with a durable build',499.99,'assets/img/products/placeholder.png',_binary '',30,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',4),(57,'SKU041','Apple Watch Series 6','A feature-packed smartwatch by Apple',399.99,'assets/img/products/placeholder.png',_binary '',50,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',5),(58,'SKU042','Samsung Galaxy Watch 4','A versatile smartwatch with a vibrant display',349.99,'assets/img/products/placeholder.png',_binary '',20,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',5),(59,'SKU043','Fitbit Versa 3','A fitness-focused smartwatch with advanced health tracking',199.99,'assets/img/products/placeholder.png',_binary '',10,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',5),(60,'SKU044','Garmin Fenix 6','A rugged smartwatch built for outdoor activities',499.99,'assets/img/products/placeholder.png',_binary '',30,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',5),(61,'SKU045','Huawei Watch GT 2','A stylish smartwatch with long battery life',249.99,'assets/img/products/placeholder.png',_binary '',15,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',5),(62,'SKU046','Fossil Gen 5','A fashionable smartwatch with Wear OS',299.99,'assets/img/products/placeholder.png',_binary '',25,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',5),(63,'SKU047','TicWatch Pro 3','A powerful smartwatch with dual-screen technology',349.99,'assets/img/products/placeholder.png',_binary '',10,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',5),(64,'SKU048','Amazfit GTS 2','A budget-friendly smartwatch with essential features',149.99,'assets/img/products/placeholder.png',_binary '',20,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',5),(65,'SKU049','Garmin Venu 2','A smartwatch with advanced health and fitness tracking',399.99,'assets/img/products/placeholder.png',_binary '',15,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',5),(66,'SKU050','Samsung Galaxy Watch Active 2','A sleek and lightweight smartwatch',299.99,'assets/img/products/placeholder.png',_binary '',30,'2023-06-20 18:52:35.000000','2023-06-20 18:52:35.000000',5);
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_category`
--

DROP TABLE IF EXISTS `product_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_category`
--

LOCK TABLES `product_category` WRITE;
/*!40000 ALTER TABLE `product_category` DISABLE KEYS */;
INSERT INTO `product_category` VALUES (1,'Books'),(2,'PC Games'),(3,'Movies'),(4,'Smartphones'),(5,'Smartwatches');
/*!40000 ALTER TABLE `product_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipping_address`
--

DROP TABLE IF EXISTS `shipping_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipping_address` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `country` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `street_address` varchar(255) DEFAULT NULL,
  `zip_code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipping_address`
--

LOCK TABLES `shipping_address` WRITE;
/*!40000 ALTER TABLE `shipping_address` DISABLE KEYS */;
/*!40000 ALTER TABLE `shipping_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `summary`
--

DROP TABLE IF EXISTS `summary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `summary` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `total_price` int DEFAULT NULL,
  `total_quantity` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `summary`
--

LOCK TABLES `summary` WRITE;
/*!40000 ALTER TABLE `summary` DISABLE KEYS */;
/*!40000 ALTER TABLE `summary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-06-20 21:39:20
