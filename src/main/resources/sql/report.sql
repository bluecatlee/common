CREATE TABLE `report_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `report_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT '' COMMENT '报表名称',
  `report_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT '' COMMENT '报表类别',
  `proc_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT '' COMMENT '存储过程名称',
  `sort` int(10) DEFAULT NULL COMMENT '排序',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '备注',
  `del_flag` tinyint(2) NOT NULL DEFAULT 2 COMMENT '是否删除（1.删除  2.未删除）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='报表配置表';