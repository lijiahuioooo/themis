CREATE TABLE `t_app` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `app_code` varchar(50) NOT NULL COMMENT '应用编码',
  `app_name` varchar(50) NOT NULL COMMENT '应用名称',
  `source` int(11) NOT NULL DEFAULT '0' COMMENT '应用来源',
  `project_type` int(11) NOT NULL DEFAULT '0' COMMENT '项目类型',
  `department` varchar(50) NOT NULL COMMENT '部门',
  `description` varchar(256) NOT NULL DEFAULT '' COMMENT '描述',
  `contacts` varchar(100) NOT NULL DEFAULT '' COMMENT '联系人，项目负责人',
  `creater` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',
  `operator` varchar(50) NOT NULL DEFAULT '' COMMENT '最近操作人',
  `is_delete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `ctime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `mtime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `ix_app_code` (`app_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用';

CREATE TABLE `t_app_metric` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `app_id` bigint(20) NOT NULL COMMENT '应用id',
  `metric_id` bigint(20) NOT NULL COMMENT '指标id',
  `datasource_id` bigint(20) NOT NULL COMMENT '数据源id',
  `attr_value` varchar(1000) NOT NULL DEFAULT '' COMMENT '指标占位符',
  `status` int(11) NOT NULL COMMENT '启用禁用状态',
  `is_delete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `ctime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `mtime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`id`),
  KEY `uni_app_mertric_data` (`app_id`,`metric_id`,`datasource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用指标关系';

CREATE TABLE `t_alarm_rule` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `app_id` bigint(20) NOT NULL COMMENT '应用id',
  `app_metric_id` bigint(20) NOT NULL COMMENT '应用指标关系表关联id',
  `rule_name` varchar(50) NOT NULL COMMENT '规则名称',
  `compare` int(11) NOT NULL DEFAULT '0' COMMENT '比较方式 （大于、小于等）',
  `threshold` varchar(20) NOT NULL DEFAULT '0' COMMENT '阈值',
  `start_effective_time` varchar(50) NOT NULL DEFAULT '' COMMENT '生效起始时间',
  `end_effective_time` varchar(50) NOT NULL DEFAULT '' COMMENT '生效结束时间',
  `always_effective` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否一直生效',
  `continuous_hit_times` int(11) NOT NULL DEFAULT '0' COMMENT '连续命中次数',
  `alarm_level_id` int(11) NOT NULL DEFAULT '0' COMMENT '报警等级',
  `alarm_content` varchar(500) NOT NULL DEFAULT '' COMMENT '报警文案',
  `contacts` varchar(200) NOT NULL DEFAULT '' COMMENT '规则报警联系人',
  `status` tinyint(4) NOT NULL DEFAULT '2' COMMENT '启用禁用状态',
  `creater` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建人',
  `operator` bigint(20) NOT NULL DEFAULT '0' COMMENT '操作人',
  `is_delete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除',
  `ctime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `mtime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`id`),
  KEY `uni_app_mertric_id` (`app_metric_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=' 报警规则';

CREATE TABLE `t_alarm_metric` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(45) NOT NULL COMMENT '指标名称',
  `expression` varchar(200) NOT NULL DEFAULT '' COMMENT '指标表达式',
  `time_window` int(11) NOT NULL DEFAULT '0' COMMENT '时间窗口',
  `description` varchar(100) NOT NULL DEFAULT '' COMMENT '描述',
  `source_type` int(11) NOT NULL DEFAULT '0' COMMENT '指标采集的数据源类型',
  `metric_tag` varchar(45) NOT NULL DEFAULT '' COMMENT '指标标签',
  `metric_type` int(11) NOT NULL COMMENT '指标类型',
  `group_type` int(11) NOT NULL DEFAULT '0' COMMENT '聚合方式',
  `collect_type` int(11) NOT NULL COMMENT '收集类型',
  `metric_unit` int(11) NOT NULL DEFAULT '0' COMMENT '指标单位',
  `composite_metric_expression` varchar(500) NOT NULL DEFAULT '' COMMENT '负责指标计算',
  `formula` varchar(45) NOT NULL DEFAULT '' COMMENT '计算公式',
  `ext_data` varchar(1000) NOT NULL DEFAULT '' COMMENT '扩展标记',
  `is_delete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `ctime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `mtime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指标';

CREATE TABLE `t_alarm_level` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `level` varchar(20) NOT NULL DEFAULT '' COMMENT '报警等级',
  `alarm_channels` varchar(50) NOT NULL DEFAULT '' COMMENT '报警渠道',
  `alarm_scope` varchar(50) NOT NULL DEFAULT '' COMMENT '报警范围',
  `alarm_rate` varchar(256) DEFAULT '' COMMENT '报警速率',
  `is_delete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `ctime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `mtime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=' 报警等级';

CREATE TABLE `t_alarm_record` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `app_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '应用id',
  `rule_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '规则id',
  `end_point` varchar(255) NOT NULL DEFAULT '' COMMENT '节点',
  `start_time` datetime NOT NULL COMMENT '报警起始时间',
  `end_time` datetime DEFAULT NULL COMMENT '报警结束时间',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '报警状态',
  `level` varchar(20) NOT NULL DEFAULT '' COMMENT '报警等级',
  `alert_times` int(11) NOT NULL DEFAULT '0' COMMENT '报警次数',
  `last_alert_time` datetime DEFAULT NULL COMMENT '上次报警时间',
  `receivers` varchar(512) NOT NULL DEFAULT '' COMMENT '报警接收人',
  `ctime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `mtime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_app_rule` (`app_id`,`rule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=' 报警等级';

CREATE TABLE `t_datasource` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) NOT NULL COMMENT '数据源名称',
  `description` varchar(100) NOT NULL DEFAULT '' COMMENT '描述',
  `type` int(11) NOT NULL DEFAULT '0' COMMENT '数据源类型',
  `address` varchar(200) NOT NULL COMMENT '数据源地址',
  `username` varchar(50) NOT NULL DEFAULT '' COMMENT '数据源用户名',
  `password` varchar(50) NOT NULL DEFAULT '' COMMENT '密码',
  `contacts` varchar(50) NOT NULL DEFAULT '' COMMENT '负责人',
  `properties` varchar(200) NOT NULL DEFAULT '' COMMENT '扩展信息',
  `is_delete` tinyint(4) NOT NULL COMMENT '是否删除',
  `ctime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `mtime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='挂载数据源';