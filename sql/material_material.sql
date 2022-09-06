/*
 Navicat Premium Data Transfer

 Source Server         : root_123456
 Source Server Type    : MySQL
 Source Server Version : 50733
 Source Host           : localhost:3306
 Source Schema         : material_material

 Target Server Type    : MySQL
 Target Server Version : 50733
 File Encoding         : 65001

 Date: 27/05/2022 01:48:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for mr_depot
-- ----------------------------
DROP TABLE IF EXISTS `mr_depot`;
CREATE TABLE `mr_depot`  (
  `depot_id` bigint(32) NOT NULL AUTO_INCREMENT COMMENT '仓库ID',
  `depot_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '仓库名',
  `depot_name_zh` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '仓库中文名',
  `place` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '仓库地址',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '仓库描述',
  `max_capacity` bigint(32) NULL DEFAULT 0 COMMENT '最大容量',
  `threshold` bigint(32) NULL DEFAULT 0 COMMENT '容量阈值',
  `capacity` bigint(32) NULL DEFAULT 0 COMMENT '当前容量',
  `status` int(4) NULL DEFAULT 0 COMMENT '启用状态',
  `del_flag` int(4) NULL DEFAULT 0 COMMENT '逻辑删除符',
  `createBy` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `createDate` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `updateBy` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `updateDate` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`depot_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1515337733160128515 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mr_depot
-- ----------------------------

-- ----------------------------
-- Table structure for mr_material
-- ----------------------------
DROP TABLE IF EXISTS `mr_material`;
CREATE TABLE `mr_material`  (
  `material_id` bigint(32) NOT NULL AUTO_INCREMENT COMMENT '物料ID',
  `material_type_id` bigint(32) NULL DEFAULT 1 COMMENT '物料类型ID',
  `material_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '物料系统名',
  `material_name_zh` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物料中文名',
  `value` double(32, 2) NULL DEFAULT 0.00 COMMENT '物料价值',
  `capacity_ratio` int(32) NULL DEFAULT 1 COMMENT '容量比值',
  `message` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物料信息',
  `status` int(4) NULL DEFAULT 0 COMMENT '启用状态',
  `del_flag` int(4) NULL DEFAULT 0 COMMENT '逻辑删除符',
  `createBy` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `createDate` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `updateBy` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `updateDate` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`material_id`) USING BTREE,
  INDEX `material_type_foreign`(`material_type_id`) USING BTREE,
  CONSTRAINT `material_type_foreign` FOREIGN KEY (`material_type_id`) REFERENCES `mr_type` (`type_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1521421804260864002 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mr_material
-- ----------------------------

-- ----------------------------
-- Table structure for mr_material_reg
-- ----------------------------
DROP TABLE IF EXISTS `mr_material_reg`;
CREATE TABLE `mr_material_reg`  (
  `registration_id` bigint(32) NOT NULL COMMENT '记录ID',
  `material_id` bigint(32) NOT NULL COMMENT '物料ID',
  `number` bigint(32) NULL DEFAULT 0 COMMENT '记录数量',
  PRIMARY KEY (`registration_id`, `material_id`) USING BTREE,
  INDEX `map_mat_id`(`material_id`) USING BTREE,
  CONSTRAINT `map_mat_id` FOREIGN KEY (`material_id`) REFERENCES `mr_material` (`material_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `map_reg_id` FOREIGN KEY (`registration_id`) REFERENCES `mr_registration` (`reg_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mr_material_reg
-- ----------------------------
INSERT INTO `mr_material_reg` VALUES (1509810576832856066, 1484804092155990020, 1);
INSERT INTO `mr_material_reg` VALUES (1509810576832856066, 1498308224414179329, 5);
INSERT INTO `mr_material_reg` VALUES (1511251739536879617, 1484804092155990019, 14);
INSERT INTO `mr_material_reg` VALUES (1511286574506573825, 1484804092155990019, 3);
INSERT INTO `mr_material_reg` VALUES (1511286574506573825, 1484804092155990021, 1);
INSERT INTO `mr_material_reg` VALUES (1512029582772412418, 1484804092155990019, 12);
INSERT INTO `mr_material_reg` VALUES (1512029582772412418, 1484804092155990021, 5);
INSERT INTO `mr_material_reg` VALUES (1514995032757661697, 1484804092155990018, 1);
INSERT INTO `mr_material_reg` VALUES (1515288004967559170, 1484804092155990018, 8);
INSERT INTO `mr_material_reg` VALUES (1515288004967559170, 1484804092155990019, 2);
INSERT INTO `mr_material_reg` VALUES (1515288004967559170, 1484804092155990020, 5);
INSERT INTO `mr_material_reg` VALUES (1515541357224116226, 1484804092155990020, 2);
INSERT INTO `mr_material_reg` VALUES (1515592637661429761, 1484804092155990019, 5);
INSERT INTO `mr_material_reg` VALUES (1515620614726074369, 1484804092155990019, 2);
INSERT INTO `mr_material_reg` VALUES (1517530599015383042, 1484804092155990018, 29);
INSERT INTO `mr_material_reg` VALUES (1517788554443472898, 1484804092155990018, 5);
INSERT INTO `mr_material_reg` VALUES (1517788554443472898, 1484804092155990019, 5);
INSERT INTO `mr_material_reg` VALUES (1517788554443472898, 1484804092155990020, 5);
INSERT INTO `mr_material_reg` VALUES (1517788554443472898, 1484804092155990021, 5);
INSERT INTO `mr_material_reg` VALUES (1517788554443472898, 1498308224414179329, 5);
INSERT INTO `mr_material_reg` VALUES (1517788554443472898, 1514513194528907265, 5);
INSERT INTO `mr_material_reg` VALUES (1517792251705671681, 1484804092155990018, 5);
INSERT INTO `mr_material_reg` VALUES (1517792251705671681, 1484804092155990019, 5);
INSERT INTO `mr_material_reg` VALUES (1517792251705671681, 1484804092155990020, 5);
INSERT INTO `mr_material_reg` VALUES (1517792251705671681, 1484804092155990021, 5);
INSERT INTO `mr_material_reg` VALUES (1517797560532713473, 1515364450914357250, 2);
INSERT INTO `mr_material_reg` VALUES (1517845119091458049, 1484804092155990018, 3);
INSERT INTO `mr_material_reg` VALUES (1517845119091458049, 1484804092155990019, 5);
INSERT INTO `mr_material_reg` VALUES (1517845119091458049, 1484804092155990021, 6);
INSERT INTO `mr_material_reg` VALUES (1517845119091458049, 1498308224414179329, 1);
INSERT INTO `mr_material_reg` VALUES (1517845119091458049, 1514513194528907265, 4);
INSERT INTO `mr_material_reg` VALUES (1517868625405526018, 1484804092155990019, 2);
INSERT INTO `mr_material_reg` VALUES (1517868625405526018, 1484804092155990021, 1);
INSERT INTO `mr_material_reg` VALUES (1517868625405526018, 1514513194528907265, 10);
INSERT INTO `mr_material_reg` VALUES (1517868724756004865, 1484804092155990019, 2);
INSERT INTO `mr_material_reg` VALUES (1517868724756004865, 1484804092155990020, 1);
INSERT INTO `mr_material_reg` VALUES (1517868724756004865, 1484804092155990021, 1);
INSERT INTO `mr_material_reg` VALUES (1517868724756004865, 1514513194528907265, 10);
INSERT INTO `mr_material_reg` VALUES (1517881173982953473, 1484804092155990021, 1);
INSERT INTO `mr_material_reg` VALUES (1518085879745249281, 1484804092155990018, 2);
INSERT INTO `mr_material_reg` VALUES (1518085879745249281, 1484804092155990019, 3);
INSERT INTO `mr_material_reg` VALUES (1518085879745249281, 1498308224414179329, 12);
INSERT INTO `mr_material_reg` VALUES (1518085879745249281, 1514513194528907265, 5);
INSERT INTO `mr_material_reg` VALUES (1518085912796364802, 1484804092155990018, 2);
INSERT INTO `mr_material_reg` VALUES (1518085912796364802, 1484804092155990019, 3);
INSERT INTO `mr_material_reg` VALUES (1518085912796364802, 1514513194528907265, 30);
INSERT INTO `mr_material_reg` VALUES (1518847621089775617, 1517529175325999105, 1);
INSERT INTO `mr_material_reg` VALUES (1518856308630016002, 1484804092155990019, 2);
INSERT INTO `mr_material_reg` VALUES (1518856308630016002, 1484804092155990021, 1);
INSERT INTO `mr_material_reg` VALUES (1518856947397349377, 1484804092155990019, 1);
INSERT INTO `mr_material_reg` VALUES (1518856961012060162, 1484804092155990019, 1);
INSERT INTO `mr_material_reg` VALUES (1518856971900473345, 1484804092155990019, 1);
INSERT INTO `mr_material_reg` VALUES (1518857006797082626, 1484804092155990020, 1);
INSERT INTO `mr_material_reg` VALUES (1521368953035710466, 1518852120558751745, 100);
INSERT INTO `mr_material_reg` VALUES (1521392410108649474, 1484804092155990019, 2);
INSERT INTO `mr_material_reg` VALUES (1521392410108649474, 1498308224414179329, 5);
INSERT INTO `mr_material_reg` VALUES (1521422637174140930, 1521421804260864001, 1);
INSERT INTO `mr_material_reg` VALUES (1521423601947947010, 1521421804260864001, 1);
INSERT INTO `mr_material_reg` VALUES (1521450319182061570, 1521421804260864001, 2);
INSERT INTO `mr_material_reg` VALUES (1521454830831296513, 1521421804260864001, 2);
INSERT INTO `mr_material_reg` VALUES (1521457355991048194, 1521421804260864001, 2);
INSERT INTO `mr_material_reg` VALUES (1521457809370144770, 1518852120558751745, 2);
INSERT INTO `mr_material_reg` VALUES (1521835638708563969, 1484804092155990019, 1);
INSERT INTO `mr_material_reg` VALUES (1521836247704727554, 1484804092155990019, 1);
INSERT INTO `mr_material_reg` VALUES (1523966848601288706, 1484804092155990018, 1);
INSERT INTO `mr_material_reg` VALUES (1523969309961482242, 1484804092155990018, 1);
INSERT INTO `mr_material_reg` VALUES (1528624737520050177, 1484804092155990019, 1);
INSERT INTO `mr_material_reg` VALUES (1528625444486766593, 1484804092155990019, 1);
INSERT INTO `mr_material_reg` VALUES (1529064057947062274, 1484804092155990019, 1);
INSERT INTO `mr_material_reg` VALUES (1529064671766675458, 1484804092155990019, 1);

-- ----------------------------
-- Table structure for mr_rack
-- ----------------------------
DROP TABLE IF EXISTS `mr_rack`;
CREATE TABLE `mr_rack`  (
  `rack_id` bigint(32) NOT NULL AUTO_INCREMENT COMMENT '货架ID',
  `depot_id` bigint(32) NULL DEFAULT NULL,
  `max_capacity` bigint(64) NOT NULL DEFAULT 0 COMMENT '最大容量',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `status` int(4) NULL DEFAULT 0 COMMENT '启用状态',
  `del_flag` int(4) NULL DEFAULT 0 COMMENT '逻辑删除符',
  `createBy` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `createDate` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `updateBy` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `updateDate` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`rack_id`) USING BTREE,
  INDEX `for_rack_depot_id`(`depot_id`) USING BTREE,
  CONSTRAINT `for_rack_depot_id` FOREIGN KEY (`depot_id`) REFERENCES `mr_depot` (`depot_id`) ON DELETE SET NULL ON UPDATE SET NULL
) ENGINE = InnoDB AUTO_INCREMENT = 1519932855722000386 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mr_rack
-- ----------------------------


-- ----------------------------
-- Table structure for mr_rack_material
-- ----------------------------
DROP TABLE IF EXISTS `mr_rack_material`;
CREATE TABLE `mr_rack_material`  (
  `rack_id` bigint(32) NOT NULL COMMENT '货架编号',
  `record_id` bigint(32) NOT NULL DEFAULT 1 COMMENT '入库编号',
  `material_id` bigint(32) NOT NULL COMMENT '物料编号',
  `stock` bigint(32) NULL DEFAULT 0 COMMENT '物料存量',
  PRIMARY KEY (`rack_id`, `material_id`, `record_id`) USING BTREE,
  INDEX `rack_material_materialId`(`material_id`) USING BTREE,
  INDEX `rack_material_recordId`(`record_id`) USING BTREE,
  CONSTRAINT `rack_material_materialId` FOREIGN KEY (`material_id`) REFERENCES `mr_material` (`material_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `rack_material_rackId` FOREIGN KEY (`rack_id`) REFERENCES `mr_rack` (`rack_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `rack_material_recordId` FOREIGN KEY (`record_id`) REFERENCES `mr_registration` (`reg_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mr_rack_material
-- ----------------------------

-- ----------------------------
-- Table structure for mr_registration
-- ----------------------------
DROP TABLE IF EXISTS `mr_registration`;
CREATE TABLE `mr_registration`  (
  `reg_id` bigint(32) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `reg_sign` int(2) NOT NULL DEFAULT 0 COMMENT '进出库标识',
  `number` bigint(32) NULL DEFAULT 0 COMMENT '记录中物料总数量',
  `value` double(32, 2) NULL DEFAULT 0.00 COMMENT '记录中物料总价值',
  `status` int(4) NULL DEFAULT 0 COMMENT '记录状态',
  `del_flag` int(4) NULL DEFAULT 0 COMMENT '逻辑删除符',
  `createBy` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `createDate` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `updateBy` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `updateDate` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`reg_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1529064671766675459 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mr_registration
-- ----------------------------

-- ----------------------------
-- Table structure for mr_type
-- ----------------------------
DROP TABLE IF EXISTS `mr_type`;
CREATE TABLE `mr_type`  (
  `type_id` bigint(32) NOT NULL AUTO_INCREMENT COMMENT '物料类型ID',
  `parent_id` bigint(32) NULL DEFAULT NULL,
  `type_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '物料类型名',
  `type_name_zh` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物料类型中文',
  `status` int(4) NULL DEFAULT 0 COMMENT '启用状态',
  `del_flag` int(4) NULL DEFAULT 0 COMMENT '逻辑删除符',
  `createBy` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `createDate` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `updateBy` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `updateDate` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`type_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1519932434995560450 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mr_type
-- ----------------------------


-- ----------------------------
-- View structure for view_rack_material
-- ----------------------------
DROP VIEW IF EXISTS `view_rack_material`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `view_rack_material` AS select `rack`.`rack_id` AS `rack.id`,`rack`.`max_capacity` AS `rack.max_capacity`,`mr_rack_material`.`record_id` AS `record_id`,`material`.`material_id` AS `mat.id`,`mr_rack_material`.`stock` AS `mat.stock`,`material`.`material_type_id` AS `mat.type_id`,`material`.`material_name` AS `mat.name`,`material`.`material_name_zh` AS `mat.name_zh`,`material`.`value` AS `mat.value`,`material`.`capacity_ratio` AS `mat.ratio`,`material`.`message` AS `mat.message`,`material`.`status` AS `mat.status`,`material`.`del_flag` AS `mat.del_flag`,`material`.`createBy` AS `mat.createBy`,`material`.`createDate` AS `mat.createDate`,`material`.`updateBy` AS `mat.updateBy`,`material`.`updateDate` AS `mat.updateDate`,`material`.`remark` AS `mat.remark` from ((`mr_rack` `rack` join `mr_rack_material` on((`rack`.`rack_id` = `mr_rack_material`.`rack_id`))) join `mr_material` `material` on((`mr_rack_material`.`material_id` = `material`.`material_id`))) where ((`rack`.`status` = 0) and (`rack`.`del_flag` = 0) and (`material`.`status` = 0) and (`material`.`del_flag` = 0)) group by `rack`.`rack_id`,`material`.`material_id`,`mr_rack_material`.`record_id`;

-- ----------------------------
-- View structure for view_registration_material
-- ----------------------------
DROP VIEW IF EXISTS `view_registration_material`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `view_registration_material` AS select `mr_registration`.`reg_id` AS `reg.id`,`mr_registration`.`reg_sign` AS `reg.sign`,`mr_registration`.`value` AS `reg.value`,`mr_material_reg`.`number` AS `reg.number`,`mr_material`.`material_id` AS `mat.id`,`mr_material`.`material_type_id` AS `mat.type_id`,`mr_material`.`material_name` AS `mat.name`,`mr_material`.`material_name_zh` AS `mat.name_zh`,`mr_material`.`value` AS `mat.value`,`mr_material`.`message` AS `mat.message`,`mr_material`.`remark` AS `mat.remark`,`mr_material`.`status` AS `mat.status`,`mr_material`.`del_flag` AS `mat.del_flag`,`mr_material`.`createBy` AS `mat.createBy`,`mr_material`.`createDate` AS `mat.createDate`,`mr_material`.`updateBy` AS `mat.updateBy`,`mr_material`.`updateDate` AS `mat.updateDate` from ((`mr_registration` join `mr_material_reg` on((`mr_material_reg`.`registration_id` = `mr_registration`.`reg_id`))) join `mr_material` on((`mr_material_reg`.`material_id` = `mr_material`.`material_id`))) group by `mr_registration`.`reg_id`,`mr_material`.`material_id`;

SET FOREIGN_KEY_CHECKS = 1;
