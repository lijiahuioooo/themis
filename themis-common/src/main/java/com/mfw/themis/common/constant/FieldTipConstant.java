package com.mfw.themis.common.constant;

/**
 * 表单字段Tips
 * @author wenhong
 */
public class FieldTipConstant {

    public final static String TIP_SOURCE_TYPE = "请根据应用的数据上报数据源设置";

    public final static String TIP_METRIC_TYPE = "业务指标指业务逻辑相关的指标，比如订单量等；\n" +
            "应用指标指应用程序相关的指标，比如http 500、JVM指标等；\n" +
            "系统指标指运行环境相关需要开发人员关注的指标，比如内存、io、cpu等；\n" +
            "运维指标指运行环境相关需要运维人员关注的指标，比如磁盘、流量等；";
    public final static String TIP_METRIC_TAG = "INIT 开通监控告警会默认给应用初始化该指标";
    public final static String TIP_COLLECT_TYPE = "单一指标作为独立的指标规则进行指标数据分析；\n" +
            "复合指标会联合多个单一指标，分别对单一指标进行数据分析，并通过计算公式得出复合指标的分析结果；";
    public final static String TIP_COLLECT_ID = "自定义业务数据上报需要关联上报事件";
    public final static String TIP_ALARM_LEVEL = "P0等级为严重，发送邮件、短信、飞书消息给应用的服务树成员和报警联系人，固定间隔 1min;\n" +
            "P1等级为严重，发送邮件、飞书消息给应用的服务树成员和报警联系人，固定间隔 3min;\n" +
            "P2等级为紧急，发送邮件、飞书消息给应用的服务树成员和报警联系人，固定间隔 3min;\n" +
            "P3等级为警告，发送飞书消息给应用的报警联系人，递增间隔 5min 10min 15min 30min;\n" +
            "P4等级为常规，发送飞书消息给应用的报警联系人，固定次数 1次;";
    public final static String TIP_COLLECT_METRIC = "用来区分不同的上报事件，确保AppCode与事件编码唯一";
    public final static String TIP_COLLECT_TAG = "用来作为查询条件，搜索引擎会自动创建索引，便于更快的查询";
    public final static String TIP_COLLECT_FIELD = "不会作为查询条件，搜索引擎不会创建索引，节省存储空间";
}
