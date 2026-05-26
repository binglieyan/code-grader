/*
 Navicat Premium Dump SQL

 Source Server         : prod
 Source Server Type    : MySQL
 Source Server Version : 90700 (9.7.0)
 Source Host           : binglieyan.icu:3306
 Source Schema         : code_grader

 Target Server Type    : MySQL
 Target Server Version : 90700 (9.7.0)
 File Encoding         : 65001

 Date: 26/05/2026 11:43:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for assignments
-- ----------------------------
DROP TABLE IF EXISTS `assignments`;
CREATE TABLE `assignments`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '作业ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '作业标题',
  `total_score` decimal(5, 2) NOT NULL DEFAULT 100.00 COMMENT '作业总分',
  `class_id` bigint NOT NULL COMMENT '所属班级ID',
  `start_time` datetime(3) NOT NULL COMMENT '开始时间',
  `deadline` datetime(3) NOT NULL COMMENT '截止时间',
  `assignment_status_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL DEFAULT 'DRAFT' COMMENT '作业状态编码',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_assignments_class_id`(`class_id` ASC) USING BTREE,
  INDEX `fk_assignments_status_code`(`assignment_status_code` ASC) USING BTREE,
  CONSTRAINT `fk_assignments_class_id` FOREIGN KEY (`class_id`) REFERENCES `classes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_assignments_status_code` FOREIGN KEY (`assignment_status_code`) REFERENCES `dict_data` (`data_code`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '作业表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of assignments
-- ----------------------------
INSERT INTO `assignments` VALUES (1, '数组练习', 100.00, 1, '2026-03-13 08:55:58.000', '2026-12-08 04:36:56.000', 'PUBLISHED', '2026-03-13 20:41:03.101', '2026-05-17 21:11:17.989');
INSERT INTO `assignments` VALUES (2, '编程题-鸡兔同笼', 100.00, 2, '2026-04-20 00:00:00.000', '2026-04-21 00:00:00.000', 'DRAFT', '2026-04-20 21:08:15.328', '2026-04-20 21:08:15.328');

-- ----------------------------
-- Table structure for classes
-- ----------------------------
DROP TABLE IF EXISTS `classes`;
CREATE TABLE `classes`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '班级ID',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '班级名称',
  `class_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '班级代码',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL COMMENT '班级描述',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_classes_class_code`(`class_code` ASC) USING BTREE,
  UNIQUE INDEX `uk_classes_class_name`(`class_name` ASC) USING BTREE,
  INDEX `fk_classes_teacher_id`(`teacher_id` ASC) USING BTREE,
  CONSTRAINT `fk_classes_teacher_id` FOREIGN KEY (`teacher_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '教学班表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of classes
-- ----------------------------
INSERT INTO `classes` VALUES (1, 'Java程序设计A班', 'C01', '针对22级学生。', 2, '2026-03-13 19:53:07.337', '2026-04-05 21:17:11.418');
INSERT INTO `classes` VALUES (2, '56A', '12025056a', NULL, 2, '2026-04-20 21:06:58.842', '2026-04-20 21:06:58.842');

-- ----------------------------
-- Table structure for department
-- ----------------------------
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '院系ID',
  `department_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '院系代码',
  `department_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '院系名称',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_department_code`(`department_code` ASC) USING BTREE,
  UNIQUE INDEX `uk_department_name`(`department_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '院系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of department
-- ----------------------------
INSERT INTO `department` VALUES (1, 'D01', '计算机科学与工程学院', '2026-03-13 19:09:46.130', '2026-03-13 19:09:46.130');

-- ----------------------------
-- Table structure for dict_data
-- ----------------------------
DROP TABLE IF EXISTS `dict_data`;
CREATE TABLE `dict_data`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典数据ID',
  `type_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '字典类型编码',
  `data_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '字典数据编码',
  `data_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '字典数据值',
  `sort_order` int NOT NULL DEFAULT 1 COMMENT '排序顺序',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '字典数据描述',
  `is_active` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_dict_data_data_code`(`data_code` ASC) USING BTREE,
  INDEX `fk_dict_data_type_code`(`type_code` ASC) USING BTREE,
  CONSTRAINT `fk_dict_data_type_code` FOREIGN KEY (`type_code`) REFERENCES `dict_type` (`type_code`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '字典数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of dict_data
-- ----------------------------
INSERT INTO `dict_data` VALUES (1, 'ROLE_CODE', 'STUDENT', '学生', 1, '学生用户', 1, '2026-02-01 12:08:23.000', '2026-02-01 12:08:23.000');
INSERT INTO `dict_data` VALUES (2, 'ROLE_CODE', 'TEACHER', '教师', 2, '教师用户', 1, '2026-02-01 12:08:23.000', '2026-02-01 12:08:23.000');
INSERT INTO `dict_data` VALUES (3, 'ROLE_CODE', 'ADMIN', '管理员', 3, '系统管理员', 1, '2026-02-01 12:08:23.000', '2026-02-01 12:08:23.000');
INSERT INTO `dict_data` VALUES (4, 'ASSIGNMENT_STATUS_CODE', 'DRAFT', '草稿', 1, '作业草稿状态', 1, '2026-02-01 12:08:23.000', '2026-02-01 12:08:23.000');
INSERT INTO `dict_data` VALUES (5, 'ASSIGNMENT_STATUS_CODE', 'PUBLISHED', '已发布', 2, '作业已发布', 1, '2026-02-01 12:08:23.000', '2026-02-01 12:08:23.000');
INSERT INTO `dict_data` VALUES (6, 'ASSIGNMENT_STATUS_CODE', 'CLOSED', '已关闭', 3, '作业已关闭', 1, '2026-02-01 12:08:23.000', '2026-02-01 12:08:23.000');
INSERT INTO `dict_data` VALUES (7, 'SUBMISSION_STATUS_CODE', 'SUBMITTED', '已提交', 1, '作业已提交，待批改', 1, '2026-02-01 12:08:23.000', '2026-02-01 12:08:23.000');
INSERT INTO `dict_data` VALUES (8, 'SUBMISSION_STATUS_CODE', 'GRADING', '批改中', 2, '正在批改', 1, '2026-02-01 12:08:23.000', '2026-02-01 12:08:23.000');
INSERT INTO `dict_data` VALUES (9, 'SUBMISSION_STATUS_CODE', 'GRADED', '已批改', 3, '批改完成', 1, '2026-02-01 12:08:23.000', '2026-02-01 12:08:23.000');
INSERT INTO `dict_data` VALUES (10, 'SUBMISSION_STATUS_CODE', 'AUTO_JUDGE_FAILED', '自动判题失败', 4, '批改系统异常', 1, '2026-02-01 12:08:23.000', '2026-04-03 14:08:30.658');
INSERT INTO `dict_data` VALUES (11, 'PLAGIARISM_STATUS_CODE', 'PENDING', '待处理', 1, '查重任务等待处理', 1, '2026-02-01 12:08:23.000', '2026-03-31 20:19:47.016');
INSERT INTO `dict_data` VALUES (12, 'PLAGIARISM_STATUS_CODE', 'PROCESSING', '处理中', 2, '查重任务正在处理', 1, '2026-02-01 12:08:23.000', '2026-03-31 20:19:48.861');
INSERT INTO `dict_data` VALUES (13, 'PLAGIARISM_STATUS_CODE', 'COMPLETED', '已完成', 3, '查重任务完成', 1, '2026-02-01 12:08:23.000', '2026-03-31 20:19:51.932');
INSERT INTO `dict_data` VALUES (14, 'PLAGIARISM_STATUS_CODE', 'FAILED', '失败', 4, '查重任务失败', 1, '2026-02-01 12:08:23.000', '2026-03-31 20:19:56.028');

-- ----------------------------
-- Table structure for dict_type
-- ----------------------------
DROP TABLE IF EXISTS `dict_type`;
CREATE TABLE `dict_type`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典类型ID',
  `type_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '字典类型编码',
  `type_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '字典类型名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '字典类型描述',
  `is_system` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否为系统字典(系统字典不可删除)',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_dict_type_type_code`(`type_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '字典类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of dict_type
-- ----------------------------
INSERT INTO `dict_type` VALUES (1, 'ROLE_CODE', '用户角色', '系统用户角色', 1, '2026-02-01 12:08:23.000', '2026-02-01 12:08:23.000');
INSERT INTO `dict_type` VALUES (2, 'ASSIGNMENT_STATUS_CODE', '作业状态', '作业状态', 1, '2026-02-01 12:08:23.000', '2026-02-01 12:08:23.000');
INSERT INTO `dict_type` VALUES (3, 'SUBMISSION_STATUS_CODE', '提交状态', '作业提交状态', 1, '2026-02-01 12:08:23.000', '2026-02-01 12:08:23.000');
INSERT INTO `dict_type` VALUES (4, 'PLAGIARISM_STATUS_CODE', '查重状态', '代码查重任务状态', 1, '2026-02-01 12:08:23.000', '2026-03-31 20:19:30.837');

-- ----------------------------
-- Table structure for major
-- ----------------------------
DROP TABLE IF EXISTS `major`;
CREATE TABLE `major`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '专业ID',
  `major_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '专业代码',
  `major_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '专业名称',
  `department_id` bigint NOT NULL COMMENT '所属系ID',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_major_code`(`major_code` ASC) USING BTREE,
  UNIQUE INDEX `uk_major_name`(`major_name` ASC) USING BTREE,
  INDEX `fk_major_department_id`(`department_id` ASC) USING BTREE,
  CONSTRAINT `fk_major_department_id` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '专业信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of major
-- ----------------------------
INSERT INTO `major` VALUES (1, 'M01', '计算机科学与技术', 1, '2026-03-13 19:22:37.959', '2026-03-13 19:22:37.959');

-- ----------------------------
-- Table structure for plagiarism_checks
-- ----------------------------
DROP TABLE IF EXISTS `plagiarism_checks`;
CREATE TABLE `plagiarism_checks`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '查重任务ID',
  `assignment_id` bigint NOT NULL COMMENT '作业ID',
  `initiated_by_id` bigint NOT NULL COMMENT '发起查重的教师ID',
  `total_comparisons` int NULL DEFAULT NULL COMMENT '总比较数',
  `execution_time` int NULL DEFAULT NULL COMMENT '执行时间',
  `report_path` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL COMMENT '报告文件路径',
  `status_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL DEFAULT 'PENDING' COMMENT '查重状态编码',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL COMMENT '错误信息',
  `start_time` datetime(3) NULL DEFAULT NULL COMMENT '开始时间',
  `completed_at` datetime(3) NULL DEFAULT NULL COMMENT '完成时间',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_plagiarism_checks_assignment_id`(`assignment_id` ASC) USING BTREE,
  INDEX `fk_plagiarism_checks_teacher_id`(`initiated_by_id` ASC) USING BTREE,
  INDEX `fk_plagiarism_checks_status_code`(`status_code` ASC) USING BTREE,
  CONSTRAINT `fk_plagiarism_checks_assignment_id` FOREIGN KEY (`assignment_id`) REFERENCES `assignments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_plagiarism_checks_status_code` FOREIGN KEY (`status_code`) REFERENCES `dict_data` (`data_code`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_plagiarism_checks_teacher_id` FOREIGN KEY (`initiated_by_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '查重任务表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of plagiarism_checks
-- ----------------------------

-- ----------------------------
-- Table structure for plagiarism_comparisons
-- ----------------------------
DROP TABLE IF EXISTS `plagiarism_comparisons`;
CREATE TABLE `plagiarism_comparisons`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '比较ID',
  `plagiarism_check_id` bigint NOT NULL COMMENT '查重任务ID',
  `first_submission_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '第一个提交名称',
  `second_submission_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '第二个提交名称',
  `avg_similarity` decimal(5, 2) NOT NULL COMMENT '平均相似度',
  `max_similarity` decimal(5, 2) NOT NULL COMMENT '最大相似度',
  `maximum_length` decimal(10, 2) NULL DEFAULT NULL COMMENT '最大长度',
  `longest_match` decimal(10, 2) NULL DEFAULT NULL COMMENT '最长匹配',
  `match_details_path` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL COMMENT '详细匹配文件路径',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_plagiarism_comparisons_check_id`(`plagiarism_check_id` ASC) USING BTREE,
  CONSTRAINT `fk_plagiarism_comparisons_check_id` FOREIGN KEY (`plagiarism_check_id`) REFERENCES `plagiarism_checks` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '相似性比较表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of plagiarism_comparisons
-- ----------------------------

-- ----------------------------
-- Table structure for question_submissions
-- ----------------------------
DROP TABLE IF EXISTS `question_submissions`;
CREATE TABLE `question_submissions`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '题目提交详情ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `student_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL COMMENT '学生答案',
  `score` decimal(5, 2) NULL DEFAULT NULL COMMENT '得分',
  `graded_by_id` bigint NULL DEFAULT NULL COMMENT '批改教师ID',
  `grading_completed_at` datetime(3) NULL DEFAULT NULL COMMENT '批改完成时间',
  `teacher_feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL COMMENT '教师反馈',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_question_submissions_question_id`(`question_id` ASC) USING BTREE,
  INDEX `fk_question_submissions_student_id`(`student_id` ASC) USING BTREE,
  INDEX `fk_question_submissions_graded_by_id`(`graded_by_id` ASC) USING BTREE,
  CONSTRAINT `fk_question_submissions_graded_by_id` FOREIGN KEY (`graded_by_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_question_submissions_question_id` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_question_submissions_student_id` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '题目提交详情表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of question_submissions
-- ----------------------------

-- ----------------------------
-- Table structure for questions
-- ----------------------------
DROP TABLE IF EXISTS `questions`;
CREATE TABLE `questions`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '题目ID',
  `assignment_id` bigint NOT NULL COMMENT '所属作业ID',
  `question_order` int NOT NULL COMMENT '题目顺序',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '题目标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '题目内容',
  `initial_code` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '初始代码',
  `max_score` decimal(5, 2) NOT NULL COMMENT '题目最高分',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_questions_assignment_id`(`assignment_id` ASC) USING BTREE,
  CONSTRAINT `fk_questions_assignment_id` FOREIGN KEY (`assignment_id`) REFERENCES `assignments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '题目表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of questions
-- ----------------------------
INSERT INTO `questions` VALUES (1, 1, 1, 'int类型数组练习', '给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出 和为目标值 target  的那 两个 整数，并返回它们的数组下标。你可以假设每种输入只会对应一个答案，并且你不能使用两次相同的元素。', 'class Solution {\\r\\n    public int[] twoSum(int[] nums, int target) {\\r\\n        \\r\\n    }\\r\\n}', 50.00, '2026-03-13 20:50:50.327', '2026-03-14 11:34:55.917');
INSERT INTO `questions` VALUES (2, 1, 2, 'String类型数组练习', '给你一个字符串数组，请你将 字母异位词 组合在一起。可以按任意顺序返回结果列表。', 'class Solution {\\r\\n    public List<List<String>> groupAnagrams(String[] strs) {\\r\\n        \\r\\n    }\\r\\n}', 50.00, '2026-03-13 20:53:36.729', '2026-03-14 11:35:30.734');

-- ----------------------------
-- Table structure for submissions
-- ----------------------------
DROP TABLE IF EXISTS `submissions`;
CREATE TABLE `submissions`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '提交记录ID',
  `assignment_id` bigint NOT NULL COMMENT '作业ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `submitted_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '提交时间',
  `ip_address` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '提交IP地址',
  `user_agent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL COMMENT '提交用户代理(浏览器信息)',
  `total_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '总分',
  `submission_status_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL DEFAULT 'SUBMITTED' COMMENT '提交状态编码',
  `grading_completed_at` datetime(3) NULL DEFAULT NULL COMMENT '批改完成时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_submissions_status_code`(`submission_status_code` ASC) USING BTREE,
  INDEX `fk_submissions_assignment_id`(`assignment_id` ASC) USING BTREE,
  INDEX `fk_submissions_student_id`(`student_id` ASC) USING BTREE,
  CONSTRAINT `fk_submissions_assignment_id` FOREIGN KEY (`assignment_id`) REFERENCES `assignments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_submissions_status_code` FOREIGN KEY (`submission_status_code`) REFERENCES `dict_data` (`data_code`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_submissions_student_id` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '作业提交表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of submissions
-- ----------------------------

-- ----------------------------
-- Table structure for test_case_results
-- ----------------------------
DROP TABLE IF EXISTS `test_case_results`;
CREATE TABLE `test_case_results`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '测试结果ID',
  `question_submission_id` bigint NOT NULL COMMENT '题目提交详情ID',
  `test_case_id` bigint NOT NULL COMMENT '测试用例ID',
  `submission_id` bigint NOT NULL COMMENT '作业提交记录ID',
  `actual_output` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL COMMENT '实际输出',
  `is_passed` tinyint(1) NOT NULL COMMENT '是否通过',
  `execution_time` int NULL DEFAULT NULL COMMENT '执行时间',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL COMMENT '错误信息',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_test_case_results_submission_detail_id`(`question_submission_id` ASC) USING BTREE,
  INDEX `fk_test_case_results_test_case_id`(`test_case_id` ASC) USING BTREE,
  INDEX `fk_test_case_results_submission_id`(`submission_id` ASC) USING BTREE,
  CONSTRAINT `fk_test_case_results_submission_detail_id` FOREIGN KEY (`question_submission_id`) REFERENCES `question_submissions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_test_case_results_submission_id` FOREIGN KEY (`submission_id`) REFERENCES `submissions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_test_case_results_test_case_id` FOREIGN KEY (`test_case_id`) REFERENCES `test_cases` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '编程题测试结果表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of test_case_results
-- ----------------------------

-- ----------------------------
-- Table structure for test_cases
-- ----------------------------
DROP TABLE IF EXISTS `test_cases`;
CREATE TABLE `test_cases`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '测试用例ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `case_order` int NOT NULL COMMENT '测试用例顺序',
  `input_data` json NOT NULL COMMENT '输入数据',
  `expected_output` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '期望输出',
  `is_hidden` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否为隐藏测试用例',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_test_cases_question_id`(`question_id` ASC) USING BTREE,
  CONSTRAINT `fk_test_cases_question_id` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '编程题测试用例表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of test_cases
-- ----------------------------
INSERT INTO `test_cases` VALUES (1, 1, 1, '[\"int[] nums = {2, 7, 11, 15}\", \"int target = 9\"]', '[0, 1]', 0, '2026-03-14 00:18:16.735', '2026-04-02 11:49:59.149');
INSERT INTO `test_cases` VALUES (2, 1, 2, '[\"int[] nums = {3, 2, 4}\", \"int target = 6\"]', '[1, 2]', 1, '2026-03-14 11:51:13.918', '2026-03-14 18:45:57.700');
INSERT INTO `test_cases` VALUES (3, 1, 3, '[\"int[] nums = {3, 3}\", \"int target = 6\"]', '[0, 1]', 1, '2026-03-14 11:52:06.026', '2026-03-14 18:45:55.549');
INSERT INTO `test_cases` VALUES (4, 2, 1, '[\"String[] strs = {\\\"eat\\\", \\\"tea\\\", \\\"tan\\\", \\\"ate\\\", \\\"nat\\\", \\\"bat\\\"}\"]', '[[eat, tea, ate], [bat], [tan, nat]]', 0, '2026-03-14 12:01:48.129', '2026-04-02 21:47:40.768');
INSERT INTO `test_cases` VALUES (5, 2, 2, '[\"String[] strs = {\\\"\\\"}\"]', '[[]]', 1, '2026-03-14 12:05:27.074', '2026-03-14 12:05:27.074');
INSERT INTO `test_cases` VALUES (6, 2, 3, '[\"String[] strs = {\\\"a\\\"}\"]', '[[a]]', 0, '2026-03-14 12:08:06.917', '2026-04-02 11:50:38.267');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '用户名',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '邮箱',
  `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '密码(加密存储)',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '角色编码',
  `real_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '真实姓名',
  `user_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '用户编号(学号/工号)',
  `major_id` bigint NULL DEFAULT NULL COMMENT '专业ID',
  `class_id` bigint NULL DEFAULT NULL COMMENT '班级ID',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_users_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_users_email`(`email` ASC) USING BTREE,
  UNIQUE INDEX `uk_users_user_number`(`user_number` ASC) USING BTREE,
  INDEX `fk_users_role_code`(`role_code` ASC) USING BTREE,
  INDEX `fk_users_major_id`(`major_id` ASC) USING BTREE,
  INDEX `fk_users_class_id`(`class_id` ASC) USING BTREE,
  CONSTRAINT `fk_users_class_id` FOREIGN KEY (`class_id`) REFERENCES `classes` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_users_major_id` FOREIGN KEY (`major_id`) REFERENCES `major` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_users_role_code` FOREIGN KEY (`role_code`) REFERENCES `dict_data` (`data_code`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', 'admin@qq.com', '$2a$10$fGdE20OZwpHjAOu/bkhLKucwIg2eYSrBtVFf5uPWpk5wBsfQduY66', 'ADMIN', 'gyc', 'A01', NULL, NULL, '2026-02-01 12:08:23.000', '2026-03-13 19:26:50.382');
INSERT INTO `users` VALUES (2, '张老师', 'zhang@qq.com', '$2a$10$sQ6rtyrE2KRM6hbqrPHT6ergPMgwASygKsnMBC9y5Rm2a7H/FA8QK', 'TEACHER', '张', 'T01', 1, NULL, '2026-03-13 19:26:03.687', '2026-05-26 11:43:08.655');
INSERT INTO `users` VALUES (3, '顾', 'g@qq.com', '$2a$10$w58klUsb0wGkFo9mRsJjH.Zd09rWSYL8bW7.WsD4H/A7h3NJYvUCS', 'STUDENT', '顾', 'S01', NULL, 1, '2026-03-13 19:29:56.955', '2026-05-26 11:39:15.286');
INSERT INTO `users` VALUES (4, '乔', 'l@qq.com', '$2a$10$as.4Kg0PZldFi64XzXnJZuIXPu44vfjIbXSGZmLp/Jx4YmXHibr2u', 'STUDENT', '李', 'S02', NULL, 1, '2026-03-13 19:30:40.880', '2026-05-26 11:39:16.924');
INSERT INTO `users` VALUES (5, 'q', 'h@qq.com', '$2a$10$KQhWvKwM1Sm.Pw3EnIEb2ufWX6lgLjmV/soJFiv18peiQGfPxsRJC', 'STUDENT', '黄', 'S03', NULL, 1, '2026-03-13 19:31:13.452', '2026-05-26 11:39:18.358');
INSERT INTO `users` VALUES (6, '梅', 'zj@qq.com', '$2a$10$fK4cT6UE5YfGn5VJXIsNvud6fy3qIMidesgbhKvQTI.2pM/OGPkwG', 'STUDENT', '周', 'S04', NULL, 1, '2026-03-13 19:31:59.831', '2026-05-26 11:39:19.484');
INSERT INTO `users` VALUES (7, '雨', 'zw@qq.com', '$2a$10$Nk4q/Bf6DDZ.WFfKYoFt3.kPuWmIgqBj7q0NV5A4tLWAMUDkWu/eq', 'STUDENT', '赵', 'S05', NULL, 1, '2026-03-13 19:32:31.778', '2026-05-26 11:39:20.918');

SET FOREIGN_KEY_CHECKS = 1;
