/*
 Navicat Premium Data Transfer

 Source Server         : root_123456
 Source Server Type    : MySQL
 Source Server Version : 50733
 Source Host           : localhost:3306
 Source Schema         : material_sys

 Target Server Type    : MySQL
 Target Server Version : 50733
 File Encoding         : 65001

 Date: 27/05/2022 01:48:44
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for role_account
-- ----------------------------
DROP TABLE IF EXISTS `role_account`;
CREATE TABLE `role_account`  (
  `roleId` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色ID',
  `accountId` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户ID',
  `status` int(4) NULL DEFAULT 0 COMMENT '状态',
  `delFlag` int(4) NULL DEFAULT 0 COMMENT '是否删除',
  `createBy` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `createDate` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `updateBy` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `updateDate` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`roleId`, `accountId`) USING BTREE,
  INDEX `accountId`(`accountId`) USING BTREE,
  CONSTRAINT `per_role_account_account` FOREIGN KEY (`accountId`) REFERENCES `sys_account` (`accountId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `per_role_account_role` FOREIGN KEY (`roleId`) REFERENCES `sys_role` (`roleId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_account
-- ----------------------------
INSERT INTO `role_account` VALUES ('1', '1', 0, 0, 'system', '2022-01-10 19:18:53', 'system', '2022-01-10 19:18:56');
INSERT INTO `role_account` VALUES ('1483054942884200450', '1483055132546433025', 0, 0, 'root', '2022-04-21 19:59:33', 'root', '2022-04-21 19:59:33');
INSERT INTO `role_account` VALUES ('1483054942884200450', '1505393687117451266', 0, 0, 'root', '2022-04-21 20:32:10', 'root', '2022-04-21 20:32:10');
INSERT INTO `role_account` VALUES ('1483054942884200450', '1521421169599709185', 0, 0, 'root', '2022-05-03 17:28:05', 'root', '2022-05-03 17:28:05');
INSERT INTO `role_account` VALUES ('2', '1', 0, 0, 'root', '2022-04-19 22:47:32', 'root', '2022-04-19 22:47:32');
INSERT INTO `role_account` VALUES ('2', '1482744556838453250', 0, 1, 'system', '2022-04-19 15:31:01', 'system', '2022-04-19 15:32:40');
INSERT INTO `role_account` VALUES ('2', '1483055132546433025', 0, 0, 'root', '2022-04-21 20:32:00', 'root', '2022-04-21 20:32:00');
INSERT INTO `role_account` VALUES ('3', '1', 0, 0, 'root', '2022-04-19 20:17:33', 'root', '2022-04-19 20:17:33');
INSERT INTO `role_account` VALUES ('3', '1482744556838453250', 0, 1, 'system', '2022-04-19 15:31:01', 'system', '2022-04-19 15:32:40');
INSERT INTO `role_account` VALUES ('3', '1505393687117451266', 0, 0, 'root', '2022-04-21 20:32:10', 'root', '2022-04-21 20:32:10');
INSERT INTO `role_account` VALUES ('4', '1', 0, 0, 'root', '2022-04-19 22:47:32', 'root', '2022-04-19 22:47:32');
INSERT INTO `role_account` VALUES ('4', '1482744556838453250', 0, 0, 'root', '2022-04-21 19:58:51', 'root', '2022-04-21 19:58:51');
INSERT INTO `role_account` VALUES ('4', '1511955793581703169', 0, 0, 'root', '2022-04-19 20:18:40', 'root', '2022-04-19 20:18:40');

-- ----------------------------
-- Table structure for role_menu
-- ----------------------------
DROP TABLE IF EXISTS `role_menu`;
CREATE TABLE `role_menu`  (
  `roleId` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色ID',
  `menuId` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '权限ID',
  `status` int(4) NULL DEFAULT 0 COMMENT '状态',
  `delFlag` int(4) NULL DEFAULT 0 COMMENT '是否删除',
  `createBy` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `createDate` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `updateBy` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `updateDate` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`roleId`, `menuId`) USING BTREE,
  INDEX `per_role_menu_menu`(`menuId`) USING BTREE,
  CONSTRAINT `per_role_menu_menu` FOREIGN KEY (`menuId`) REFERENCES `sys_menu` (`menuId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `per_role_menu_role` FOREIGN KEY (`roleId`) REFERENCES `sys_role` (`roleId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_menu
-- ----------------------------
INSERT INTO `role_menu` VALUES ('1', '1', 0, 0, 'system', '2022-01-14 17:45:47', 'system', '2022-01-14 17:45:50');
INSERT INTO `role_menu` VALUES ('1', '1483054389278015490', 0, 0, 'root', '2022-04-19 17:21:40', 'root', '2022-04-19 17:21:40');
INSERT INTO `role_menu` VALUES ('1', '1513939953699610626', 0, 0, 'root', '2022-04-19 17:21:40', 'root', '2022-04-19 17:21:40');
INSERT INTO `role_menu` VALUES ('1', '2', 0, 0, 'root', '2022-04-19 17:48:29', 'root', '2022-04-19 17:48:29');
INSERT INTO `role_menu` VALUES ('1', '3', 0, 0, 'root', '2022-04-19 17:38:31', 'root', '2022-04-19 17:38:31');
INSERT INTO `role_menu` VALUES ('1', '4', 0, 0, 'root', '2022-04-19 17:36:52', 'root', '2022-04-19 17:36:52');
INSERT INTO `role_menu` VALUES ('1483054942884200450', '1516400846282162177', 0, 0, 'root', '2022-04-21 20:29:34', 'root', '2022-04-21 20:29:34');
INSERT INTO `role_menu` VALUES ('1483054942884200450', '1517114891389927426', 0, 0, 'root', '2022-04-26 01:22:08', 'root', '2022-04-26 01:22:08');
INSERT INTO `role_menu` VALUES ('1483054942884200450', '1518641188847038466', 0, 0, 'root', '2022-04-26 01:22:08', 'root', '2022-04-26 01:22:08');
INSERT INTO `role_menu` VALUES ('1483054942884200450', '2', 0, 0, 'root', '2022-04-21 20:29:34', 'root', '2022-04-21 20:29:34');
INSERT INTO `role_menu` VALUES ('1483054942884200450', '3', 0, 0, 'root', '2022-04-21 20:29:34', 'root', '2022-04-21 20:29:34');
INSERT INTO `role_menu` VALUES ('2', '1517111093158268929', 0, 0, 'root', '2022-04-21 20:33:22', 'root', '2022-04-21 20:33:22');
INSERT INTO `role_menu` VALUES ('2', '1517114891389927426', 0, 0, 'root', '2022-04-21 20:16:18', 'root', '2022-04-21 20:16:18');
INSERT INTO `role_menu` VALUES ('2', '1518641188847038466', 0, 0, 'root', '2022-04-26 01:21:40', 'root', '2022-04-26 01:21:40');
INSERT INTO `role_menu` VALUES ('2', '2', 0, 0, 'system', '2022-04-19 15:26:34', 'system', '2022-04-19 15:27:46');
INSERT INTO `role_menu` VALUES ('2', '3', 0, 0, 'system', '2022-04-19 15:26:34', 'system', '2022-04-19 15:27:46');
INSERT INTO `role_menu` VALUES ('2', '4', 0, 0, 'root', '2022-05-03 17:28:25', 'root', '2022-05-03 17:28:25');
INSERT INTO `role_menu` VALUES ('3', '1517111093158268929', 0, 0, 'root', '2022-04-21 20:02:33', 'root', '2022-04-21 20:02:33');
INSERT INTO `role_menu` VALUES ('3', '1517114891389927426', 0, 0, 'root', '2022-04-26 01:22:21', 'root', '2022-04-26 01:22:21');
INSERT INTO `role_menu` VALUES ('3', '1518641188847038466', 0, 0, 'root', '2022-04-26 01:21:30', 'root', '2022-04-26 01:21:30');
INSERT INTO `role_menu` VALUES ('3', '2', 0, 0, 'root', '2022-04-21 19:59:57', 'root', '2022-04-21 19:59:57');
INSERT INTO `role_menu` VALUES ('3', '3', 0, 0, 'root', '2022-04-21 19:59:57', 'root', '2022-04-21 19:59:57');
INSERT INTO `role_menu` VALUES ('4', '1513939953699610626', 0, 0, 'root', '2022-04-19 22:46:45', 'root', '2022-04-19 22:46:45');
INSERT INTO `role_menu` VALUES ('4', '1517111093158268929', 0, 0, 'root', '2022-04-26 01:24:16', 'root', '2022-04-26 01:24:16');
INSERT INTO `role_menu` VALUES ('4', '1517114891389927426', 0, 0, 'root', '2022-04-21 20:22:58', 'root', '2022-04-21 20:22:58');
INSERT INTO `role_menu` VALUES ('4', '1518641188847038466', 0, 0, 'root', '2022-04-26 01:24:16', 'root', '2022-04-26 01:24:16');
INSERT INTO `role_menu` VALUES ('4', '3', 0, 0, 'root', '2022-04-21 19:59:12', 'root', '2022-04-21 19:59:12');
INSERT INTO `role_menu` VALUES ('4', '4', 0, 0, 'root', '2022-04-21 19:59:12', 'root', '2022-04-21 19:59:12');

-- ----------------------------
-- Table structure for sys_account
-- ----------------------------
DROP TABLE IF EXISTS `sys_account`;
CREATE TABLE `sys_account`  (
  `accountId` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户ID',
  `username` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户密码',
  `status` int(4) NULL DEFAULT 0 COMMENT '账号状态',
  `delFlag` int(4) NULL DEFAULT 0 COMMENT '删除位',
  `createBy` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `createDate` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `updateBy` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `updateDate` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`accountId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_account
-- ----------------------------
INSERT INTO `sys_account` VALUES ('1', 'root', 'kLAKEPUUhpZhGNGjCbsybQ==', 0, 0, 'system', '2022-01-10 19:17:59', 'system', '2022-05-01 12:14:35', '系统默认账号');
INSERT INTO `sys_account` VALUES ('1482744556838453250', 'test', 'kLAKEPUUhpZhGNGjCbsybQ==', 0, 0, 'system', '2022-01-17 14:50:51', 'system', '2022-01-17 10:31:50', '业务员');
INSERT INTO `sys_account` VALUES ('1483055132546433025', 'test2', 'kLAKEPUUhpZhGNGjCbsybQ==', 0, 0, 'system', '2022-01-17 12:34:32', 'system', '2022-01-17 21:27:49', '财务主管');
INSERT INTO `sys_account` VALUES ('1505393687117451266', 'test3', 'kLAKEPUUhpZhGNGjCbsybQ==', 0, 0, 'system', '2022-03-20 11:59:59', 'system', '2022-04-12 00:41:18', '仓库管理员');
INSERT INTO `sys_account` VALUES ('1511955793581703169', 'tt', 'm5vHu9Vq4Z6jdghqAq1vcQ==', 0, 0, 'system', '2022-04-07 14:35:27', 'root', '2022-04-18 22:16:57', '测试');
INSERT INTO `sys_account` VALUES ('1513471797403058177', 'cs', 'kLAKEPUUhpZhGNGjCbsybQ==', 0, 0, 'system', '2022-04-11 18:59:30', 'system', '2022-04-11 18:59:30', NULL);
INSERT INTO `sys_account` VALUES ('1513570041156141057', 'aab', 'kLAKEPUUhpZhGNGjCbsybQ==', 1, 1, 'system', '2022-04-12 01:29:53', 'system', '2022-04-12 01:29:54', NULL);
INSERT INTO `sys_account` VALUES ('1519939467924828162', 'xuqiang', 'm5vHu9Vq4Z6jdghqAq1vcQ==', 0, 0, 'root', '2022-04-29 15:19:43', 'system', '2022-04-29 15:19:43', NULL);
INSERT INTO `sys_account` VALUES ('1519939610619244546', 'yujing', 'm5vHu9Vq4Z6jdghqAq1vcQ==', 0, 0, 'root', '2022-04-29 15:20:17', 'system', '2022-04-29 15:20:17', NULL);
INSERT INTO `sys_account` VALUES ('1519939731973042178', 'dongxiulan', 'm5vHu9Vq4Z6jdghqAq1vcQ==', 0, 0, 'root', '2022-04-29 15:20:46', 'system', '2022-04-29 15:20:46', NULL);
INSERT INTO `sys_account` VALUES ('1519939816114974722', 'guchao', 'm5vHu9Vq4Z6jdghqAq1vcQ==', 0, 0, 'root', '2022-04-29 15:21:06', 'system', '2022-04-29 15:21:06', NULL);
INSERT INTO `sys_account` VALUES ('1519939883483885570', 'tangli', 'm5vHu9Vq4Z6jdghqAq1vcQ==', 0, 0, 'root', '2022-04-29 15:21:22', 'system', '2022-04-29 15:21:22', NULL);
INSERT INTO `sys_account` VALUES ('1519939974584168449', 'chengyang', 'm5vHu9Vq4Z6jdghqAq1vcQ==', 0, 0, 'root', '2022-04-29 15:21:44', 'system', '2022-04-29 15:21:58', NULL);
INSERT INTO `sys_account` VALUES ('1519940132440993793', 'fangjuan', 'm5vHu9Vq4Z6jdghqAq1vcQ==', 0, 0, 'root', '2022-04-29 15:22:21', 'system', '2022-04-29 15:22:21', NULL);
INSERT INTO `sys_account` VALUES ('1519940209884622849', 'mengwei', 'm5vHu9Vq4Z6jdghqAq1vcQ==', 0, 0, 'root', '2022-04-29 15:22:40', 'system', '2022-04-29 15:22:40', NULL);
INSERT INTO `sys_account` VALUES ('1519940314561867778', 'hemin', 'm5vHu9Vq4Z6jdghqAq1vcQ==', 0, 0, 'root', '2022-04-29 15:23:05', 'system', '2022-04-29 15:23:05', NULL);
INSERT INTO `sys_account` VALUES ('1519940429599043585', 'sunguiyin', 'm5vHu9Vq4Z6jdghqAq1vcQ==', 0, 0, 'root', '2022-04-29 15:23:32', 'system', '2022-04-29 15:23:32', NULL);
INSERT INTO `sys_account` VALUES ('1519940545567354881', 'a1008', 'm5vHu9Vq4Z6jdghqAq1vcQ==', 0, 0, 'root', '2022-04-29 15:24:00', 'system', '2022-04-29 15:25:38', '仓库设备号');
INSERT INTO `sys_account` VALUES ('1519940607617888258', 'a1009', 'm5vHu9Vq4Z6jdghqAq1vcQ==', 0, 0, 'root', '2022-04-29 15:24:15', 'system', '2022-04-29 15:25:41', '仓库设备号');
INSERT INTO `sys_account` VALUES ('1519940677352386562', 'a1010', 'm5vHu9Vq4Z6jdghqAq1vcQ==', 0, 0, 'root', '2022-04-29 15:24:31', 'system', '2022-04-29 15:25:44', '仓库设备号');
INSERT INTO `sys_account` VALUES ('1519940807073820673', 'gaoyong', 'm5vHu9Vq4Z6jdghqAq1vcQ==', 0, 0, 'root', '2022-04-29 15:25:02', 'system', '2022-04-29 15:25:02', NULL);
INSERT INTO `sys_account` VALUES ('1519940868558123010', 'jiajie', 'm5vHu9Vq4Z6jdghqAq1vcQ==', 0, 0, 'root', '2022-04-29 15:25:17', 'system', '2022-04-29 15:25:17', NULL);
INSERT INTO `sys_account` VALUES ('1520616691892072450', 'cca', 'm5vHu9Vq4Z6jdghqAq1vcQ==', 0, 0, 'system', '2022-05-01 12:10:46', NULL, '2022-05-01 12:10:46', NULL);
INSERT INTO `sys_account` VALUES ('1521421169599709185', 'wanggang', 'kLAKEPUUhpZhGNGjCbsybQ==', 0, 0, 'root', '2022-05-03 17:27:28', 'system', '2022-05-03 17:27:28', NULL);

-- ----------------------------
-- Table structure for sys_account_info
-- ----------------------------
DROP TABLE IF EXISTS `sys_account_info`;
CREATE TABLE `sys_account_info`  (
  `accountId` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `sex` int(2) NULL DEFAULT 0,
  `age` int(4) NULL DEFAULT 0,
  `phone` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `email` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `sign` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `createBy` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `createDate` datetime(0) NULL DEFAULT NULL,
  `updateBy` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `updateDate` datetime(0) NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`accountId`) USING BTREE,
  CONSTRAINT `per_sys_account_id` FOREIGN KEY (`accountId`) REFERENCES `sys_account` (`accountId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_account_info
-- ----------------------------
INSERT INTO `sys_account_info` VALUES ('1', 0, 16, '18591816580', 'p.bqrj@xtsufmrwem.mil', 'https://resources.hidewnd.cn/avatar/2022-05-04/img4.jpg', 'aaa', 'system', '2022-04-11 18:59:30', 'system', '2022-05-04 20:51:55', NULL);
INSERT INTO `sys_account_info` VALUES ('1482744556838453250', 1, 0, '14922447719', 'p.dmud@mxcmmlbc.cn', NULL, NULL, 'system', '2022-04-11 20:56:55', 'system', '2022-04-11 20:57:02', NULL);
INSERT INTO `sys_account_info` VALUES ('1483055132546433025', 0, 23, '19944733187', NULL, NULL, NULL, 'system', '2022-01-17 12:34:32', 'system', '2022-01-17 12:41:45', NULL);
INSERT INTO `sys_account_info` VALUES ('1505393687117451266', 0, 18, NULL, NULL, 'https://resources.hidewnd.cn/avatar/2022-04-21/img5.png', 'ssg', 'system', '2022-03-20 11:59:59', 'system', '2022-04-21 20:42:51', NULL);
INSERT INTO `sys_account_info` VALUES ('1511955793581703169', 0, 18, '180', 'aa@qq.com', NULL, 'aaassas从', 'system', '2022-04-11 18:59:30', 'system', '2022-04-15 11:06:42', NULL);
INSERT INTO `sys_account_info` VALUES ('1513471797403058177', 0, 0, NULL, NULL, NULL, NULL, 'system', '2022-04-11 18:59:30', 'system', '2022-04-11 18:59:30', NULL);
INSERT INTO `sys_account_info` VALUES ('1513570041156141057', 1, 0, NULL, NULL, NULL, NULL, 'system', '2022-04-12 01:29:53', 'system', '2022-04-12 01:29:53', NULL);
INSERT INTO `sys_account_info` VALUES ('1519939467924828162', 1, 0, NULL, NULL, NULL, NULL, 'system', '2022-04-29 15:19:43', 'system', '2022-04-29 15:19:43', NULL);
INSERT INTO `sys_account_info` VALUES ('1519939610619244546', 1, 0, NULL, NULL, NULL, NULL, 'system', '2022-04-29 15:20:17', 'system', '2022-04-29 15:20:17', NULL);
INSERT INTO `sys_account_info` VALUES ('1519939731973042178', 1, 0, NULL, NULL, NULL, NULL, 'system', '2022-04-29 15:20:46', 'system', '2022-04-29 15:20:46', NULL);
INSERT INTO `sys_account_info` VALUES ('1519939816114974722', 1, 0, NULL, NULL, NULL, NULL, 'system', '2022-04-29 15:21:06', 'system', '2022-04-29 15:21:06', NULL);
INSERT INTO `sys_account_info` VALUES ('1519939883483885570', 1, 0, NULL, NULL, NULL, NULL, 'system', '2022-04-29 15:21:22', 'system', '2022-04-29 15:21:22', NULL);
INSERT INTO `sys_account_info` VALUES ('1519939974584168449', 1, 0, NULL, NULL, NULL, NULL, 'system', '2022-04-29 15:21:44', 'system', '2022-04-29 15:21:58', NULL);
INSERT INTO `sys_account_info` VALUES ('1519940132440993793', 1, 0, NULL, NULL, NULL, NULL, 'system', '2022-04-29 15:22:21', 'system', '2022-04-29 15:22:21', NULL);
INSERT INTO `sys_account_info` VALUES ('1519940209884622849', 1, 0, NULL, NULL, NULL, NULL, 'system', '2022-04-29 15:22:40', 'system', '2022-04-29 15:22:40', NULL);
INSERT INTO `sys_account_info` VALUES ('1519940314561867778', 1, 0, NULL, NULL, NULL, NULL, 'system', '2022-04-29 15:23:05', 'system', '2022-04-29 15:23:05', NULL);
INSERT INTO `sys_account_info` VALUES ('1519940429599043585', 1, 0, NULL, NULL, NULL, NULL, 'system', '2022-04-29 15:23:32', 'system', '2022-04-29 15:23:32', NULL);
INSERT INTO `sys_account_info` VALUES ('1519940545567354881', 1, 0, NULL, NULL, NULL, NULL, 'system', '2022-04-29 15:24:00', 'system', '2022-04-29 15:25:38', NULL);
INSERT INTO `sys_account_info` VALUES ('1519940607617888258', 1, 0, NULL, NULL, NULL, NULL, 'system', '2022-04-29 15:24:15', 'system', '2022-04-29 15:25:41', NULL);
INSERT INTO `sys_account_info` VALUES ('1519940677352386562', 1, 0, NULL, NULL, NULL, NULL, 'system', '2022-04-29 15:24:31', 'system', '2022-04-29 15:25:44', NULL);
INSERT INTO `sys_account_info` VALUES ('1519940807073820673', 1, 0, NULL, NULL, NULL, NULL, 'system', '2022-04-29 15:25:02', 'system', '2022-04-29 15:25:02', NULL);
INSERT INTO `sys_account_info` VALUES ('1519940868558123010', 1, 0, NULL, NULL, NULL, NULL, 'system', '2022-04-29 15:25:17', 'system', '2022-04-29 15:25:17', NULL);
INSERT INTO `sys_account_info` VALUES ('1521421169599709185', 1, 0, NULL, NULL, NULL, NULL, 'system', '2022-05-03 17:27:28', 'system', '2022-05-03 17:27:28', NULL);

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menuId` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '权限ID',
  `parentId` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '父级ID',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '权限名',
  `url` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '请求路径',
  `perms` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '权限符',
  `status` int(4) NULL DEFAULT 0 COMMENT '权限状态',
  `delFlag` int(4) NULL DEFAULT 0,
  `createBy` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `createDate` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `updateBy` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `updateDate` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`menuId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('1', '0', 'default', '/**', '*:*:*', 0, 0, 'system', '2022-01-14 17:42:37', 'system', '2022-01-14 17:42:40', NULL);
INSERT INTO `sys_menu` VALUES ('1483054389278015490', '2', 'sysUpdate', '/sys/update/**', 'sys:update:*', 0, 0, 'system', '2022-01-17 12:31:35', 'system', '2022-04-29 15:01:55', NULL);
INSERT INTO `sys_menu` VALUES ('1513939953699610626', '1', 'test', '/aa', 'aa:*:*', 0, 0, 'system', '2022-04-13 01:59:47', 'system', '2022-04-13 15:28:02', 'ss');
INSERT INTO `sys_menu` VALUES ('1516400846282162177', '3', 'authUpload', '/auth/upload/**', 'auth:upload:*', 0, 0, 'root', '2022-04-19 20:58:30', 'system', '2022-04-29 15:02:21', NULL);
INSERT INTO `sys_menu` VALUES ('1517111093158268929', '1', 'materialManager', '/manager/mat/**', 'manager:mat:*', 0, 0, 'root', '2022-04-21 20:00:46', 'system', '2022-04-21 20:41:53', NULL);
INSERT INTO `sys_menu` VALUES ('1517114891389927426', '1517111093158268929', 'materialManagerMaterial', '/manager/mat/material', 'manager:mat:material', 0, 0, 'root', '2022-04-21 20:15:51', 'root', '2022-04-21 20:15:51', NULL);
INSERT INTO `sys_menu` VALUES ('1518641188847038466', '1517114891389927426', 'materialStatistics', '/manager/mat/statistics', 'manager:mat:statistics', 0, 0, 'root', '2022-04-26 01:20:49', 'root', '2022-04-26 01:20:49', NULL);
INSERT INTO `sys_menu` VALUES ('1519934136305938433', '1517111093158268929', 'managerDepot', '/manager/mat/depot', 'manager:mat:depot', 0, 0, 'root', '2022-04-29 14:58:32', 'root', '2022-04-29 14:58:32', NULL);
INSERT INTO `sys_menu` VALUES ('1519934238630178818', '1517111093158268929', 'managerRack', '/manager/mat/rack', 'manager:mat:rack', 0, 0, 'root', '2022-04-29 14:58:56', 'root', '2022-04-29 14:58:56', NULL);
INSERT INTO `sys_menu` VALUES ('1519934392322060289', '1517111093158268929', 'managerInfo', '/manager/mat/materialinfo', 'manager:mat:materialinfo', 0, 0, 'root', '2022-04-29 14:59:33', 'root', '2022-04-29 14:59:33', NULL);
INSERT INTO `sys_menu` VALUES ('1519934451918925826', '1517111093158268929', 'managerRecord', '/manager/mat/record', 'manager:mat:record', 0, 0, 'root', '2022-04-29 14:59:47', 'root', '2022-04-29 14:59:47', NULL);
INSERT INTO `sys_menu` VALUES ('1519934672388321281', '3', 'authToken', '/auth/token/**', 'auth:token:*', 0, 0, 'root', '2022-04-29 15:00:40', 'root', '2022-04-29 15:00:40', NULL);
INSERT INTO `sys_menu` VALUES ('1519934810200567809', '1519934672388321281', 'authLogin', '/auth/token/login', 'auth:token:login', 0, 0, 'root', '2022-04-29 15:01:12', 'root', '2022-04-29 15:01:12', NULL);
INSERT INTO `sys_menu` VALUES ('1519934929528516610', '1519934672388321281', 'authLogout', '/auth/token/logout', 'auth:token:logout', 0, 0, 'root', '2022-04-29 15:01:41', 'root', '2022-04-29 15:01:41', NULL);
INSERT INTO `sys_menu` VALUES ('1519935472565055489', '1', 'material', '/mat/**', 'mat:*:*', 0, 0, 'root', '2022-04-29 15:03:50', 'root', '2022-04-29 15:03:50', NULL);
INSERT INTO `sys_menu` VALUES ('1519935598570336257', '1519935472565055489', 'materialQuery', '/mat/query/**', 'mat:query:*', 0, 0, 'root', '2022-04-29 15:04:20', 'root', '2022-04-29 15:04:20', NULL);
INSERT INTO `sys_menu` VALUES ('1519935672910180354', '1519935472565055489', 'materialUpdate', '/mat/update/**', 'mat:update:*', 0, 0, 'root', '2022-04-29 15:04:38', 'root', '2022-04-29 15:04:38', NULL);
INSERT INTO `sys_menu` VALUES ('2', '1', 'sys', '/sys/**', 'sys:*:*', 0, 0, 'system', '2022-01-14 17:43:39', 'system', '2022-01-14 17:43:37', NULL);
INSERT INTO `sys_menu` VALUES ('3', '1', 'auth', '/auth/**', 'auth:*:*', 0, 0, 'system', '2022-01-14 17:44:13', 'system', '2022-01-14 17:44:15', NULL);
INSERT INTO `sys_menu` VALUES ('4', '3', 'sysQuery', '/sys/query/**', 'sys:query:*', 0, 0, 'system', '2022-01-14 17:45:15', 'system', '2022-04-29 15:02:59', '332');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `roleId` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色ID',
  `roleName` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色名',
  `roleNameZh` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '中文角色名',
  `roleSort` int(4) NOT NULL DEFAULT 0 COMMENT '角色排序',
  `stated` int(4) NULL DEFAULT 0,
  `delFlag` int(4) NULL DEFAULT 0 COMMENT '是否删除',
  `createBy` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `createDate` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `updateBy` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `updateDate` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`roleId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('1', 'root', '超级管理员', 1, 0, 0, 'system', '2022-01-10 17:09:20', 'system', '2022-01-10 17:09:24', NULL);
INSERT INTO `sys_role` VALUES ('1483054942884200450', 'common', '普通用户', 12, 0, 0, 'system', '2022-03-30 22:31:20', 'system', '2022-04-21 20:27:13', NULL);
INSERT INTO `sys_role` VALUES ('1513886037146415106', 'aa', 's达到', 15, 0, 1, 'root', '2022-04-12 22:25:33', 'system', '2022-04-12 22:44:45', NULL);
INSERT INTO `sys_role` VALUES ('2', 'financial', '财务主管', 2, 0, 0, 'system', '2022-01-10 19:15:15', 'system', '2022-03-30 19:15:17', NULL);
INSERT INTO `sys_role` VALUES ('3', 'warehouse', '仓库管理员', 3, 0, 0, 'system', '2022-01-14 17:46:50', 'system', '2022-03-30 17:46:52', NULL);
INSERT INTO `sys_role` VALUES ('4', 'salesman', '业务员', 4, 0, 0, 'system', '2022-01-17 12:33:47', 'system', '2022-03-30 12:44:41', NULL);

SET FOREIGN_KEY_CHECKS = 1;
