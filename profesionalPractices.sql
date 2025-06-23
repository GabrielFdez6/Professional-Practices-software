-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: professionalpractices
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `academic`
--

DROP TABLE IF EXISTS `academic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `academic` (
  `idAcademic` int NOT NULL AUTO_INCREMENT,
  `idSubjectGroup` int DEFAULT NULL,
  `firstName` varchar(50) NOT NULL,
  `lastNameFather` varchar(50) NOT NULL,
  `lastNameMother` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `status` tinyint(1) DEFAULT NULL,
  `idUser` int NOT NULL,
  PRIMARY KEY (`idAcademic`),
  UNIQUE KEY `idUser` (`idUser`),
  KEY `idSubjectGroup` (`idSubjectGroup`),
  CONSTRAINT `academic_ibfk_1` FOREIGN KEY (`idSubjectGroup`) REFERENCES `subjectgroup` (`idSubjectGroup`),
  CONSTRAINT `academic_ibfk_2` FOREIGN KEY (`idUser`) REFERENCES `useraccount` (`idUser`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `academic`
--

LOCK TABLES `academic` WRITE;
/*!40000 ALTER TABLE `academic` DISABLE KEYS */;
INSERT INTO `academic` VALUES (1,1,'Roberto','Méndez','Vázquez','roberto.mendez@uv.mx',1,2),(2,2,'Alejandro','Torres','Jiménez','alejandro.torres@uv.mx',1,10),(3,3,'Patricia','Morales','Ruiz','patricia.morales@uv.mx',1,11);
/*!40000 ALTER TABLE `academic` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coordinator`
--

DROP TABLE IF EXISTS `coordinator`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `coordinator` (
  `idCoordinator` int NOT NULL AUTO_INCREMENT,
  `firstName` varchar(45) NOT NULL,
  `lastNameFather` varchar(45) NOT NULL,
  `lastNameMother` varchar(45) DEFAULT NULL,
  `email` varchar(60) DEFAULT NULL,
  `idUser` int NOT NULL,
  PRIMARY KEY (`idCoordinator`),
  UNIQUE KEY `idUser` (`idUser`),
  CONSTRAINT `coordinator_ibfk_1` FOREIGN KEY (`idUser`) REFERENCES `useraccount` (`idUser`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `coordinator`
--

LOCK TABLES `coordinator` WRITE;
/*!40000 ALTER TABLE `coordinator` DISABLE KEYS */;
INSERT INTO `coordinator` VALUES (1,'Laura','Castro','Mendoza','laura.castro@uv.mx',3);
/*!40000 ALTER TABLE `coordinator` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `delivery`
--

DROP TABLE IF EXISTS `delivery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `delivery` (
  `idDelivery` int NOT NULL AUTO_INCREMENT,
  `idRecord` int DEFAULT NULL,
  `idDeliveryDefinition` int DEFAULT NULL,
  `dateDelivered` datetime DEFAULT NULL,
  `delivered` tinyint(1) NOT NULL DEFAULT '0',
  `status` enum('PENDIENTE','ENTREGADO','EN_REVISION','APROBADO','RECHAZADO') NOT NULL DEFAULT 'PENDIENTE',
  `filePath` varchar(255) DEFAULT NULL,
  `observations` varchar(1000) DEFAULT NULL,
  `grade` decimal(4,2) DEFAULT NULL,
  `reportedHours` int DEFAULT NULL,
  PRIMARY KEY (`idDelivery`),
  KEY `idRecord` (`idRecord`),
  KEY `fk_delivery_definition` (`idDeliveryDefinition`),
  CONSTRAINT `delivery_ibfk_1` FOREIGN KEY (`idRecord`) REFERENCES `record` (`idRecord`),
  CONSTRAINT `fk_delivery_definition` FOREIGN KEY (`idDeliveryDefinition`) REFERENCES `deliverydefinition` (`idDeliveryDefinition`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `delivery`
--

LOCK TABLES `delivery` WRITE;
/*!40000 ALTER TABLE `delivery` DISABLE KEYS */;
INSERT INTO `delivery` VALUES (1,NULL,1,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(2,NULL,1,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(3,1,1,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(4,1,1,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(5,1,1,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(6,2,1,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(7,2,1,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(8,3,1,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(9,3,1,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(22,1,1,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(23,2,1,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(24,3,1,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(25,4,1,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(26,5,1,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(27,6,1,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(28,12,1,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(29,1,1,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(30,2,1,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(31,3,1,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(32,4,1,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(33,5,1,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(34,6,1,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(35,12,1,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(36,15,1,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(37,16,1,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(38,17,1,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(39,5,6,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(40,6,6,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(41,3,6,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(42,4,6,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(43,18,6,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(44,1,6,'2025-06-22 20:51:54',1,'ENTREGADO','deliveries\\REPORT\\1750625514481_SEW13_CastilloCarlos.pdf','pruebaReporte',7.60,10),(45,2,6,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(46,12,6,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(47,15,6,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(48,16,6,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL),(49,17,6,NULL,0,'PENDIENTE',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `delivery` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `deliverydefinition`
--

DROP TABLE IF EXISTS `deliverydefinition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `deliverydefinition` (
  `idDeliveryDefinition` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `startDate` datetime NOT NULL,
  `endDate` datetime NOT NULL,
  `deliveryType` enum('INITIAL DOCUMENT','FINAL DOCUMENT','REPORT') NOT NULL,
  `idInitialDocumentTemplate` int DEFAULT NULL,
  `idFinalDocumentTemplate` int DEFAULT NULL,
  `idReportDocumentTemplate` int DEFAULT NULL,
  `idTerm` int DEFAULT NULL,
  PRIMARY KEY (`idDeliveryDefinition`),
  KEY `fk_initial_doc_template` (`idInitialDocumentTemplate`),
  KEY `fk_final_doc_template` (`idFinalDocumentTemplate`),
  KEY `fk_report_doc_template` (`idReportDocumentTemplate`),
  KEY `fk_delivery_def_term` (`idTerm`),
  CONSTRAINT `fk_delivery_def_term` FOREIGN KEY (`idTerm`) REFERENCES `term` (`idTerm`),
  CONSTRAINT `fk_final_doc_template` FOREIGN KEY (`idFinalDocumentTemplate`) REFERENCES `finaldocument` (`idFinalDocument`),
  CONSTRAINT `fk_initial_doc_template` FOREIGN KEY (`idInitialDocumentTemplate`) REFERENCES `initialdocument` (`idInitialDocument`),
  CONSTRAINT `fk_report_doc_template` FOREIGN KEY (`idReportDocumentTemplate`) REFERENCES `reportdocument` (`idReportDocument`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `deliverydefinition`
--

LOCK TABLES `deliverydefinition` WRITE;
/*!40000 ALTER TABLE `deliverydefinition` DISABLE KEYS */;
INSERT INTO `deliverydefinition` VALUES (1,'Entrega General de Prácticas','Definición de entrega genérica para prácticas profesionales.','2025-01-01 00:00:00','2025-12-31 23:59:59','INITIAL DOCUMENT',NULL,NULL,NULL,1),(6,'Reporte de Implementación de Módulos.pdf','pruebaReporte','2025-06-22 06:00:00','2025-06-25 06:00:00','REPORT',NULL,NULL,12,1);
/*!40000 ALTER TABLE `deliverydefinition` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evaluationcriteria`
--

DROP TABLE IF EXISTS `evaluationcriteria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluationcriteria` (
  `idCriteria` int NOT NULL AUTO_INCREMENT,
  `criteriaName` varchar(255) NOT NULL,
  `competent` text,
  `independent` text,
  `advancedBasic` text,
  `thresholdBasic` text,
  `notCompetent` text,
  PRIMARY KEY (`idCriteria`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluationcriteria`
--

LOCK TABLES `evaluationcriteria` WRITE;
/*!40000 ALTER TABLE `evaluationcriteria` DISABLE KEYS */;
INSERT INTO `evaluationcriteria` VALUES (1,'USO DE MÉTODOS Y TÉCNICAS DE LA IS','Los métodos y técnicas de la IS optimizan el aseguramiento de calidad y se han aplicado de manera correcta.','Los métodos y técnicas de la IS, son adecuados y se han aplicado de manera correcta.','Los métodos y técnicas de la IS, son adecuados, aunque se presentan algunas deficiencias en su aplicación.','Los métodos y técnicas de la IS, no son adecuados, pero se han aplicado de manera correcta.','No se han aplicado métodos y técnicas de la IS.'),(2,'REQUISITOS','Cumplió con todos los requisitos. Excedió las expectativas.','Todos los requisitos fueron cumplidos.','No cumple satisfactoriamente con un requisito.','Más de un requisito no fue cumplido satisfactoriamente.','Más de dos requisitos no fueron cumplidos satisfactoriamente.'),(3,'SEGURIDAD Y DOMINIO','El dominio del tema es excelente, la exposición es dada con seguridad.','Se posee un dominio adecuado y la exposición fue fluida.','Aunque con algunos fallos en el dominio, la exposición fue fluida.','Se demuestra falta de dominio y una exposición deficiente.','No existe dominio sobre el tema y la exposición es deficiente.'),(4,'CONTENIDO','Cubre los temas a profundidad con detalles y ejemplos. El conocimiento del tema es excelente.','Incluye conocimiento básico sobre el tema. El contenido parece ser bueno.','Incluye información esencial sobre el tema, pero tiene 1-2 errores en los hechos.','El contenido es mínimo y tiene tres errores en los hechos.','El contenido es mínimo y tiene varios errores en los hechos.'),(5,'ORTOGRAFÍA Y REDACCIÓN','No hay errores de gramática, ortografía o puntuación.','Casi no hay errores de gramática, ortografía o puntuación.','Algunos errores de gramática, ortografía o puntuación.','Varios errores de gramática, ortografía o puntuación.','Demasiados errores de gramática, ortografía o puntuación.');
/*!40000 ALTER TABLE `evaluationcriteria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evaluationdetail`
--

DROP TABLE IF EXISTS `evaluationdetail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluationdetail` (
  `idEvaluationDetail` int NOT NULL AUTO_INCREMENT,
  `idEvaluation` int NOT NULL,
  `idCriteria` int NOT NULL,
  `grade` float NOT NULL,
  PRIMARY KEY (`idEvaluationDetail`),
  KEY `fk_evaluation_detail_evaluation_idx` (`idEvaluation`),
  KEY `fk_evaluation_detail_criteria_idx` (`idCriteria`),
  CONSTRAINT `fk_evaluation_detail_to_criteria` FOREIGN KEY (`idCriteria`) REFERENCES `evaluationcriteria` (`idCriteria`),
  CONSTRAINT `fk_evaluation_detail_to_presentationevaluation` FOREIGN KEY (`idEvaluation`) REFERENCES `presentationevaluation` (`idEvaluation`)
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluationdetail`
--

LOCK TABLES `evaluationdetail` WRITE;
/*!40000 ALTER TABLE `evaluationdetail` DISABLE KEYS */;
/*!40000 ALTER TABLE `evaluationdetail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `finaldocument`
--

DROP TABLE IF EXISTS `finaldocument`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `finaldocument` (
  `idFinalDocument` int NOT NULL AUTO_INCREMENT,
  `name` varchar(65) NOT NULL,
  `date` date DEFAULT NULL,
  `delivered` tinyint(1) NOT NULL DEFAULT '0',
  `status` enum('ENTREGADO','NO_ENTREGADO','EN_REVISION') NOT NULL,
  `filePath` varchar(255) DEFAULT NULL,
  `observations` varchar(200) DEFAULT NULL,
  `grade` decimal(4,2) DEFAULT NULL,
  PRIMARY KEY (`idFinalDocument`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `finaldocument`
--

LOCK TABLES `finaldocument` WRITE;
/*!40000 ALTER TABLE `finaldocument` DISABLE KEYS */;
INSERT INTO `finaldocument` VALUES (1,'Presentación Ejecutiva del Proyecto.pptx','2025-06-17',1,'ENTREGADO','docsfinal1_Presentacion_Ejecutiva.pptx','Excelente presentación, muy completa',9.50),(2,'Sistema de Gestión Completo.zip','2025-06-18',1,'EN_REVISION','docsfinal2_Sistema_Gestion.zip','Pendiente revisión de funcionalidades',NULL),(3,'Manual de Usuario Final.pdf','2025-06-19',0,'NO_ENTREGADO',NULL,'Pendiente de entrega',NULL),(4,'Presentación final del proyecto','2025-06-16',1,'EN_REVISION','deliveries\\FINAL_DOCUMENT\\1750073345677_Propuesta de proyecto-19-39.pdf',NULL,NULL),(5,'Presentación final del proyecto','2025-06-16',1,'EN_REVISION','deliveries\\FINAL_DOCUMENT\\1750073948629_Propuesta de proyecto-19-39.pdf','',NULL);
/*!40000 ALTER TABLE `finaldocument` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `initialdocument`
--

DROP TABLE IF EXISTS `initialdocument`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `initialdocument` (
  `idInitialDocument` int NOT NULL AUTO_INCREMENT,
  `name` varchar(65) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `delivered` tinyint(1) NOT NULL DEFAULT '0',
  `status` enum('ENTREGADO','NO_ENTREGADO','EN_REVISION') NOT NULL,
  `filePath` varchar(255) DEFAULT NULL,
  `observations` varchar(200) DEFAULT NULL,
  `grade` decimal(4,2) DEFAULT NULL,
  PRIMARY KEY (`idInitialDocument`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `initialdocument`
--

LOCK TABLES `initialdocument` WRITE;
/*!40000 ALTER TABLE `initialdocument` DISABLE KEYS */;
INSERT INTO `initialdocument` VALUES (1,'Propuesta Inicial de Proyecto.docx','2025-06-16',0,'ENTREGADO','docsinitial1_Propuesta_Inicial.docx','Bien estructurada, objetivos claros',8.75),(2,'Especificaciones Técnicas.pdf','2025-06-17',0,'EN_REVISION','docsinitial2_Especificaciones_Tecnicas.pdf','Revisar sección de arquitectura',NULL),(3,'Análisis de Requerimientos.docx','2025-06-18',0,'ENTREGADO','docsinitial3_Analisis_Requerimientos.docx','Muy detallado y completo',9.25),(4,'Carta de Presentación.pdf','2025-06-15',0,'NO_ENTREGADO',NULL,'Falta entregar documento',NULL),(5,'Propuesta inicial del proyecto','2025-06-16',1,'EN_REVISION','deliveries\\INITIAL_DOCUMENT\\1750073932170_Copia de Propuesta de proyecto.pdf','',NULL),(6,'Propuesta inicial del proyecto','2025-06-16',1,'EN_REVISION','deliveries\\INITIAL_DOCUMENT\\1750116532448_clases2.pdf','',NULL),(7,'asdfasdf','2025-06-16',1,'EN_REVISION','deliveries\\INITIAL_DOCUMENT\\1750121124650_ordiDiseno.pdf','',NULL),(8,'asdfasdf','2025-06-16',1,'EN_REVISION','deliveries\\INITIAL_DOCUMENT\\1750126001610_clases.pdf','',NULL),(9,'Entrega 1','2025-06-17',1,'EN_REVISION','deliveries\\INITIAL_DOCUMENT\\1750181336911_clases.pdf',NULL,NULL),(10,'Entrega 1','2025-06-17',1,'ENTREGADO','deliveries\\INITIAL_DOCUMENT\\1750181404531_robustez.pdf',NULL,NULL),(11,'Entrega 1','2025-06-17',1,'EN_REVISION','deliveries\\INITIAL_DOCUMENT\\1750181493043_clases2.pdf',NULL,NULL),(12,'Propuesta Inicial de Proyecto.docx','2025-06-22',0,'EN_REVISION',NULL,NULL,NULL);
/*!40000 ALTER TABLE `initialdocument` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `linkedorganization`
--

DROP TABLE IF EXISTS `linkedorganization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `linkedorganization` (
  `idLinkedOrganization` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `address` varchar(70) DEFAULT NULL,
  `phone` varchar(10) DEFAULT NULL,
  `isActive` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`idLinkedOrganization`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `linkedorganization`
--

LOCK TABLES `linkedorganization` WRITE;
/*!40000 ALTER TABLE `linkedorganization` DISABLE KEYS */;
INSERT INTO `linkedorganization` VALUES (1,'Desarrollo Web Profesional SA','Av. Universidad 456, Xalapa, Veracruz','2288123456',1),(2,'Sistemas Integrales del Golfo','Calle Revolución 789, Veracruz, Ver.','2299876543',1),(3,'Innovación Tecnológica Veracruzana','Blvd. Adolfo Ruiz Cortines 321, Boca del Río','2291234567',1),(4,'Consultoría Digital del Sureste','Av. Lázaro Cárdenas 654, Coatzacoalcos, Ver.','9211234567',1),(5,'TechSolutions Veracruz','Calle Enríquez 123, Centro, Xalapa','2287654321',1),(6,'Grupo Empresarial del Puerto','Av. Independencia 987, Puerto de Veracruz','2292345678',1),(7,'Desarrollo de Software Jarocho','Blvd. Cristóbal Colón 555, Xalapa, Ver.','2283456789',1);
/*!40000 ALTER TABLE `linkedorganization` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `presentationevaluation`
--

DROP TABLE IF EXISTS `presentationevaluation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `presentationevaluation` (
  `idEvaluation` int NOT NULL AUTO_INCREMENT,
  `date` date DEFAULT NULL,
  `grade` decimal(4,2) DEFAULT NULL,
  `observations` text,
  `idRecord` int DEFAULT NULL,
  PRIMARY KEY (`idEvaluation`),
  KEY `idRecord` (`idRecord`),
  CONSTRAINT `presentationevaluation_ibfk_1` FOREIGN KEY (`idRecord`) REFERENCES `record` (`idRecord`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `presentationevaluation`
--

LOCK TABLES `presentationevaluation` WRITE;
/*!40000 ALTER TABLE `presentationevaluation` DISABLE KEYS */;
INSERT INTO `presentationevaluation` VALUES (1,'2025-06-18',9.20,'Excelente dominio del tema, presentación clara y bien estructurada. Demostró conocimiento técnico sólido.',1),(2,'2025-06-19',8.75,'Buena explicación de la metodología utilizada. Podría mejorar en la gestión del tiempo de presentación.',2),(3,'2025-06-20',9.50,'Presentación excepcional, respondió todas las preguntas con seguridad y demostró dominio completo del proyecto.',3),(4,'2025-06-21',8.90,'Muy buena presentación, resultados claros y bien documentados. Excelente uso de recursos visuales.',4),(5,'2025-06-16',6.00,'asdfasdf',12),(6,'2025-06-16',6.00,'asdfasdf		',12),(7,'2025-06-16',6.00,'asdfasdfsad	',12),(8,'2025-06-16',6.00,'asdfasdfasdf',6),(15,'2025-06-17',7.00,'pruebaComentarios Y observaciones sdsadasdsad',17);
/*!40000 ALTER TABLE `presentationevaluation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `project` (
  `idProject` int NOT NULL AUTO_INCREMENT,
  `idProjectManager` int DEFAULT NULL,
  `idLinkedOrganization` int DEFAULT NULL,
  `idCoordinator` int DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  `department` varchar(30) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL,
  `methodology` varchar(45) DEFAULT NULL,
  `availability` int DEFAULT NULL,
  PRIMARY KEY (`idProject`),
  KEY `idProjectManager` (`idProjectManager`),
  KEY `idLinkedOrganization` (`idLinkedOrganization`),
  KEY `idCoordinator` (`idCoordinator`),
  CONSTRAINT `project_ibfk_2` FOREIGN KEY (`idProjectManager`) REFERENCES `projectmanager` (`idProjectManager`),
  CONSTRAINT `project_ibfk_3` FOREIGN KEY (`idLinkedOrganization`) REFERENCES `linkedorganization` (`idLinkedOrganization`),
  CONSTRAINT `project_ibfk_4` FOREIGN KEY (`idCoordinator`) REFERENCES `coordinator` (`idCoordinator`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project`
--

LOCK TABLES `project` WRITE;
/*!40000 ALTER TABLE `project` DISABLE KEYS */;
INSERT INTO `project` VALUES (1,1,1,1,'Sistema de Gestión Empresarial','Desarrollo Frontend','Desarrollo de una plataforma web para gestión integral de procesos empresariales con interfaz moderna.','Scrum',1),(2,2,2,1,'Automatización de Procesos','Backend Development','Implementación de sistema automatizado para optimización de flujos de trabajo empresariales.','Kanban',1),(3,3,3,1,'Plataforma E-commerce','Full Stack','Desarrollo completo de tienda en línea con sistema de pagos y gestión de inventarios.','Agile',4),(4,4,4,1,'Sistema de Monitoreo IoT','Desarrollo de Software','Creación de dashboard para monitoreo en tiempo real de dispositivos IoT industriales.','DevOps',1),(5,5,5,1,'App Móvil Corporativa','Mobile Development','Aplicación móvil para gestión de recursos humanos y comunicación interna empresarial.','Lean',2),(6,6,6,1,'Portal de Servicios Digitales','Web Development','Portal web para digitalización de trámites y servicios gubernamentales locales.','Waterfall',1),(12,1,1,1,'Analisis para creacion de paginas web','PruebaDepartamento','Se debe realizar un trabajo de investigacion','SCRUM',2),(14,1,1,1,'PruebaDeProyecto','PruebaDepartamento2','PruebaDescripcion','MetodologiaPrueba',3);
/*!40000 ALTER TABLE `project` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `projectassignment`
--

DROP TABLE IF EXISTS `projectassignment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `projectassignment` (
  `idProject` int NOT NULL,
  `idRecord` int NOT NULL,
  PRIMARY KEY (`idProject`,`idRecord`),
  KEY `fk_assignment_record_idx` (`idRecord`),
  CONSTRAINT `fk_assignment_project` FOREIGN KEY (`idProject`) REFERENCES `project` (`idProject`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_assignment_record` FOREIGN KEY (`idRecord`) REFERENCES `record` (`idRecord`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `projectassignment`
--

LOCK TABLES `projectassignment` WRITE;
/*!40000 ALTER TABLE `projectassignment` DISABLE KEYS */;
INSERT INTO `projectassignment` VALUES (1,1),(2,2),(3,3),(4,4),(5,5),(6,6),(12,12);
/*!40000 ALTER TABLE `projectassignment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `projectmanager`
--

DROP TABLE IF EXISTS `projectmanager`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `projectmanager` (
  `idProjectManager` int NOT NULL AUTO_INCREMENT,
  `idLinkedOrganization` int DEFAULT NULL,
  `firstName` varchar(45) NOT NULL,
  `lastNameFather` varchar(45) NOT NULL,
  `lastNameMother` varchar(45) DEFAULT NULL,
  `position` varchar(50) DEFAULT NULL,
  `email` varchar(60) DEFAULT NULL,
  `phone` varchar(10) NOT NULL,
  PRIMARY KEY (`idProjectManager`),
  KEY `idLinkedOrganization` (`idLinkedOrganization`),
  CONSTRAINT `projectmanager_ibfk_1` FOREIGN KEY (`idLinkedOrganization`) REFERENCES `linkedorganization` (`idLinkedOrganization`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `projectmanager`
--

LOCK TABLES `projectmanager` WRITE;
/*!40000 ALTER TABLE `projectmanager` DISABLE KEYS */;
INSERT INTO `projectmanager` VALUES (1,1,'María','Fernández','López','Directora de Proyectos Tecnológicos','maria.fernandez@desarrolloweb.mx','2288123456'),(2,2,'José','Hernández','García','Gerente de Desarrollo de Software','jose.hernandez@sistemasgolfo.mx','2299876543'),(3,3,'Carmen','Rodríguez','Martínez','Coordinadora de Innovación Digital','carmen.rodriguez@innovacionver.mx','2291234567'),(4,4,'Fernando','González','Pérez','Jefe de Consultoría Tecnológica','fernando.gonzalez@consultoriadigital.mx','9211234567'),(5,5,'Adriana','Jiménez','Morales','Líder de Proyectos de Software','adriana.jimenez@techsolutions.mx','2287654321'),(6,6,'Ricardo','Vázquez','Ruiz','Director de Desarrollo Empresarial','ricardo.vazquez@grupopuerto.mx','2292345678'),(14,2,'juan','perez','sanchez','encargado','admin@gmail.com','1234567894');
/*!40000 ALTER TABLE `projectmanager` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `record`
--

DROP TABLE IF EXISTS `record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `record` (
  `idRecord` int NOT NULL AUTO_INCREMENT,
  `idStudent` int NOT NULL,
  `idSubjectGroup` int NOT NULL,
  `hoursCount` int DEFAULT NULL,
  `reportPath` varchar(100) DEFAULT NULL,
  `presentationPath` varchar(100) DEFAULT NULL,
  `idTerm` int DEFAULT NULL,
  PRIMARY KEY (`idRecord`),
  KEY `idStudent` (`idStudent`),
  KEY `idSubjectGroup` (`idSubjectGroup`),
  KEY `idTerm` (`idTerm`),
  CONSTRAINT `record_ibfk_1` FOREIGN KEY (`idStudent`) REFERENCES `student` (`idStudent`),
  CONSTRAINT `record_ibfk_2` FOREIGN KEY (`idSubjectGroup`) REFERENCES `subjectgroup` (`idSubjectGroup`),
  CONSTRAINT `record_ibfk_3` FOREIGN KEY (`idTerm`) REFERENCES `term` (`idTerm`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `record`
--

LOCK TABLES `record` WRITE;
/*!40000 ALTER TABLE `record` DISABLE KEYS */;
INSERT INTO `record` VALUES (1,1,1,120,'reportselena_silva_report.pdf','presentationselena_silva_presentation.pptx',1),(2,2,1,95,'reportscarlos_mendoza_report.pdf','presentationscarlos_mendoza_presentation.pptx',1),(3,3,2,140,'reportsana_torres_report.pdf','presentationsana_torres_presentation.pptx',1),(4,4,2,110,'reportsmiguel_vargas_report.pdf','presentationsmiguel_vargas_presentation.pptx',1),(5,5,3,85,'reportssofia_herrera_report.pdf',NULL,1),(6,6,3,75,NULL,NULL,1),(12,7,1,0,'reportaldo_antonio_campos.pdf','reportaldo_antonio_campos.pptx',1),(15,8,1,0,'reportomar_espinosa_fernandez.pdf','reportomar_espinosa_fernandez.pptx',1),(16,9,1,0,'reportcarlos_castillo_barradas.pdf','reportcarlos_castillo_barradas.pptx',1),(17,10,1,0,'reportbrayan_fernandez_tlapa.pdf','reportbrayan_fernandez_tlapa.pptx',1),(18,11,2,200,'reportgab_fernandez_tlapa.pdf','reportgab_fernandez_tlapa.pptx',1);
/*!40000 ALTER TABLE `record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reportdocument`
--

DROP TABLE IF EXISTS `reportdocument`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reportdocument` (
  `idReportDocument` int NOT NULL AUTO_INCREMENT,
  `reportedHours` int NOT NULL,
  `date` date NOT NULL,
  `grade` decimal(4,2) NOT NULL,
  `name` varchar(65) DEFAULT NULL,
  `delivered` tinyint(1) NOT NULL DEFAULT '0',
  `status` enum('ENTREGADO','NO_ENTREGADO','EN_REVISION') NOT NULL,
  `filePath` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`idReportDocument`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reportdocument`
--

LOCK TABLES `reportdocument` WRITE;
/*!40000 ALTER TABLE `reportdocument` DISABLE KEYS */;
INSERT INTO `reportdocument` VALUES (1,40,'2025-06-16',8.50,'Reporte Mensual de Actividades - Junio.pdf',1,'ENTREGADO','docs\reports1_Reporte_Mensual_Junio.pdf'),(2,35,'2025-06-17',9.00,'Informe Semanal de Progreso.pdf',1,'ENTREGADO','docs\reports2_Informe_Semanal.pdf'),(3,45,'2025-06-18',8.75,'Reporte de Implementación de Módulos.pdf',1,'EN_REVISION','docs\reports3_Reporte_Implementacion.pdf'),(4,30,'2025-06-19',9.25,'Documento de Pruebas del Sistema.pdf',1,'ENTREGADO','docs\reports4_Pruebas_Sistema.pdf'),(5,25,'2025-06-20',0.00,'Reporte de Avances Semanales.pdf',0,'NO_ENTREGADO',NULL),(6,50,'2025-06-16',8.50,'Reporte mensual de avances',1,'EN_REVISION','deliveries\\REPORT\\1750073844896_Propuesta de proyecto-19-39.pdf'),(7,10,'2025-06-16',8.50,'Reporte mensual de avances',1,'EN_REVISION','deliveries\\REPORT\\1750126080838_ordiDiseno.pdf'),(12,0,'2025-06-22',0.00,'Reporte de Implementación de Módulos.pdf',0,'EN_REVISION',NULL);
/*!40000 ALTER TABLE `reportdocument` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student` (
  `idStudent` int NOT NULL AUTO_INCREMENT,
  `firstName` varchar(45) NOT NULL,
  `lastNameFather` varchar(45) NOT NULL,
  `lastNameMother` varchar(45) DEFAULT NULL,
  `enrollment` varchar(45) NOT NULL,
  `email` varchar(45) DEFAULT NULL,
  `phone` varchar(10) DEFAULT NULL,
  `credits` int DEFAULT NULL,
  `semester` varchar(45) DEFAULT NULL,
  `isAssignedToProject` tinyint(1) DEFAULT NULL,
  `projectSelection` varchar(100) DEFAULT NULL,
  `grade` decimal(3,2) DEFAULT NULL,
  `idUser` int NOT NULL,
  PRIMARY KEY (`idStudent`),
  UNIQUE KEY `idUser` (`idUser`),
  CONSTRAINT `student_ibfk_1` FOREIGN KEY (`idUser`) REFERENCES `useraccount` (`idUser`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
INSERT INTO `student` VALUES (1,'Elena','Silva','Morales','S210001','zS210001@estudiantes.uv.mx','2288765432',335,'9',1,'Sistema de Gestión Empresarial',8.85,1),(2,'Carlos','Mendoza','Rivera','S200502','zS200502@estudiantes.uv.mx','2287654321',342,'10',1,'Automatización de Procesos',9.10,5),(3,'Ana','Torres','Jiménez','S190807','zS190807@estudiantes.uv.mx','2291234567',358,'11',1,'Plataforma E-commerce',9.45,6),(4,'Miguel','Vargas','Castillo','S200156','zS200156@estudiantes.uv.mx','2299876543',348,'10',1,'Sistema de Monitoreo IoT',8.75,7),(5,'Sofía','Herrera','Delgado','S210234','zS210234@estudiantes.uv.mx','2283456789',325,'9',1,'App Móvil Corporativa',8.60,8),(6,'Diego','Ramírez','Flores','S200789','zS200789@estudiantes.uv.mx','2292345678',340,'10',1,'Portal de Servicios Digitales',9.20,9),(7,'Aldo','Antonio','Campos','s230145','zs230145@estudiantes.uv.mx','2283421934',0,'9',1,'Analisis para creacion de paginas web',NULL,12),(8,'Omar','Espinosa','Fernandez','s230146','zs230146@estudiantes.uv.mx','2292345678',0,'9',0,NULL,0.00,13),(9,'Carlos','Castillo','Barradas','s230147','zs230147@estudiantes.uv.mx','2292345678',0,'9',0,NULL,0.00,14),(10,'Brayan','Fernandez','Tlapa','s230148','zs230148@estudiantes.uv.mx','2292345678',0,'9',0,'',0.00,15),(11,'Gabriel','Tlapa','Fernández','s237648','s2379648@estudiantes.uv.mx','2297654678',0,'9',0,'',0.00,16);
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `studentingroup`
--

DROP TABLE IF EXISTS `studentingroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `studentingroup` (
  `idStudent` int NOT NULL,
  `idSubjectGroup` int NOT NULL,
  PRIMARY KEY (`idStudent`,`idSubjectGroup`),
  KEY `idSubjectGroup` (`idSubjectGroup`),
  CONSTRAINT `studentingroup_ibfk_1` FOREIGN KEY (`idStudent`) REFERENCES `student` (`idStudent`),
  CONSTRAINT `studentingroup_ibfk_2` FOREIGN KEY (`idSubjectGroup`) REFERENCES `subjectgroup` (`idSubjectGroup`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `studentingroup`
--

LOCK TABLES `studentingroup` WRITE;
/*!40000 ALTER TABLE `studentingroup` DISABLE KEYS */;
INSERT INTO `studentingroup` VALUES (1,1),(2,1),(3,2),(4,2),(5,3),(6,3);
/*!40000 ALTER TABLE `studentingroup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subject`
--

DROP TABLE IF EXISTS `subject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subject` (
  `idSubject` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `credits` int NOT NULL,
  PRIMARY KEY (`idSubject`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subject`
--

LOCK TABLES `subject` WRITE;
/*!40000 ALTER TABLE `subject` DISABLE KEYS */;
INSERT INTO `subject` VALUES (1,'Prácticas Profesionales',14),(2,'Servicio Social',12);
/*!40000 ALTER TABLE `subject` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subjectgroup`
--

DROP TABLE IF EXISTS `subjectgroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subjectgroup` (
  `idSubjectGroup` int NOT NULL AUTO_INCREMENT,
  `idTerm` int DEFAULT NULL,
  `idSubject` int DEFAULT NULL,
  `schedule` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`idSubjectGroup`),
  KEY `idTerm` (`idTerm`),
  KEY `idSubject` (`idSubject`),
  CONSTRAINT `subjectgroup_ibfk_1` FOREIGN KEY (`idTerm`) REFERENCES `term` (`idTerm`),
  CONSTRAINT `subjectgroup_ibfk_2` FOREIGN KEY (`idSubject`) REFERENCES `subject` (`idSubject`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subjectgroup`
--

LOCK TABLES `subjectgroup` WRITE;
/*!40000 ALTER TABLE `subjectgroup` DISABLE KEYS */;
INSERT INTO `subjectgroup` VALUES (1,1,1,'Lunes y Miércoles 08:00 - 10:00'),(2,1,1,'Martes y Jueves 15:00 - 17:00'),(3,1,1,'Viernes 09:00 - 13:00');
/*!40000 ALTER TABLE `subjectgroup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teachingassignment`
--

DROP TABLE IF EXISTS `teachingassignment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teachingassignment` (
  `idSubjectGroup` int NOT NULL,
  `idAcademic` int NOT NULL,
  PRIMARY KEY (`idSubjectGroup`,`idAcademic`),
  KEY `idAcademic` (`idAcademic`),
  CONSTRAINT `teachingassignment_ibfk_1` FOREIGN KEY (`idSubjectGroup`) REFERENCES `subjectgroup` (`idSubjectGroup`),
  CONSTRAINT `teachingassignment_ibfk_2` FOREIGN KEY (`idAcademic`) REFERENCES `academic` (`idAcademic`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teachingassignment`
--

LOCK TABLES `teachingassignment` WRITE;
/*!40000 ALTER TABLE `teachingassignment` DISABLE KEYS */;
INSERT INTO `teachingassignment` VALUES (1,1),(2,2),(3,3);
/*!40000 ALTER TABLE `teachingassignment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `term`
--

DROP TABLE IF EXISTS `term`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `term` (
  `idTerm` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `startDate` date DEFAULT NULL,
  `endDate` date DEFAULT NULL,
  PRIMARY KEY (`idTerm`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `term`
--

LOCK TABLES `term` WRITE;
/*!40000 ALTER TABLE `term` DISABLE KEYS */;
INSERT INTO `term` VALUES (1,'Febrero 2025 - Julio 2025','2025-02-03','2025-07-25'),(2,'Agosto 2025 - Enero 2026','2025-08-04','2026-01-30'),(3,'Febrero 2026 - Julio 2026','2026-02-02','2026-07-24');
/*!40000 ALTER TABLE `term` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `useraccount`
--

DROP TABLE IF EXISTS `useraccount`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `useraccount` (
  `idUser` int NOT NULL AUTO_INCREMENT,
  `username` varchar(30) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('STUDENT','TEACHER','COORDINATOR','EVALUATOR') NOT NULL,
  PRIMARY KEY (`idUser`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `useraccount`
--

LOCK TABLES `useraccount` WRITE;
/*!40000 ALTER TABLE `useraccount` DISABLE KEYS */;
INSERT INTO `useraccount` VALUES (1,'elena.silva','$2a$12$T9jlF9NsVTrsjK/oN7L5F.xcObhzTmzx7YhjCaTwHoNXdZcgdxap2','STUDENT'),(2,'roberto.mendez','$2a$12$B3OdA1NG8.9ZPiLi2R//j.JTGwPHZ834WnlAtmJoO19OofZc.4y6i','TEACHER'),(3,'laura.castro','$2a$12$FiMo4aqBSUndJdAwMtZBgudpCfl2owi48rQ0jC4FfCdM8lA0SyyCK','COORDINATOR'),(4,'daniel.reyes','$2a$12$/e5JBFiJ6wrXDmdS5CT6Y.2wXG5HijSbW9qX5saWQRmxuxOszeQgm','EVALUATOR'),(5,'carlos.mendoza','$2a$10$Hj8vX2nL9Qp4Ks6Rt3Ym8eUzVwBcDfGhIjKlMnOpQrStUvWxYzAb2','STUDENT'),(6,'ana.torres','$2a$10$Mn7pQ4rS8tU9vW2xY5zA6bCdEfGhIjKlMnOpQrStUvWxYzAbCdEf','STUDENT'),(7,'miguel.vargas','$2a$10$Pq9sT6uV0wX3yZ8aB7cD4eGhIjKlMnOpQrStUvWxYzAbCdEfGhIj','STUDENT'),(8,'sofia.herrera','$2a$10$Rs1tU8vW2xY5zA6bCdEfGhIjKlMnOpQrStUvWxYzAbCdEfGhIjKl','STUDENT'),(9,'diego.ramirez','$2a$10$Tu3vW6yZ9aB7cD4eGhIjKlMnOpQrStUvWxYzAbCdEfGhIjKlMnOp','STUDENT'),(10,'alejandro.torres','$2a$10$Vw5xY8zA6bCdEfGhIjKlMnOpQrStUvWxYzAbCdEfGhIjKlMnOpQr','TEACHER'),(11,'patricia.morales','$2a$10$Xy7zA9bCdEfGhIjKlMnOpQrStUvWxYzAbCdEfGhIjKlMnOpQrStU','TEACHER'),(12,'aldo','$2a$12$T9jlF9NsVTrsjK/oN7L5F.xcObhzTmzx7YhjCaTwHoNXdZcgdxap2','STUDENT'),(13,'omar','$2a$12$T9jlF9NsVTrsjK/oN7L5F.xcObhzTmzx7YhjCaTwHoNXdZcgdxap2','STUDENT'),(14,'carlos','$2a$12$T9jlF9NsVTrsjK/oN7L5F.xcObhzTmzx7YhjCaTwHoNXdZcgdxap2','STUDENT'),(15,'brayan','$2a$12$T9jlF9NsVTrsjK/oN7L5F.xcObhzTmzx7YhjCaTwHoNXdZcgdxap2','STUDENT'),(16,'gabriel','$2a$12$T9jlF9NsVTrsjK/oN7L5F.xcObhzTmzx7YhjCaTwHoNXdZcgdxap2','STUDENT');
/*!40000 ALTER TABLE `useraccount` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-22 18:59:57
