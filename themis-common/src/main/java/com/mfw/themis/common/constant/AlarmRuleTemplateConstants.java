package com.mfw.themis.common.constant;

/**
 * @author guosp
 */
public class AlarmRuleTemplateConstants {

    public static String AUTO_INIT_TEMPLATE_LIST  = "["
//            + "{\"alarmContent\":\"您的应用${appCode}-${endPoint}  load 1m指标超过阈值。阈值：${threshold}，当前值：${metricValue}\",\"alarmLevelId\":3,\"alwaysEffective\":true,\"compare\":1,\"continuousHitTimes\":1,\"metricId\":7,\"ruleName\":\"近一分钟负载超过20\",\"threshold\":\"20\"},"
//            + "{\"alarmContent\":\"您的应用${appCode}-${endPoint}  CPU 使用率指标超过阈值。阈值：${threshold}，当前值：${metricValue}\",\"alarmLevelId\":3,\"alwaysEffective\":true,\"compare\":1,\"continuousHitTimes\":1,\"metricId\":8,\"ruleName\":\"CPU使用率过高\",\"threshold\":\"40\"},"
            + "{\"alarmContent\":\"您的应用${appCode} HTTP 500错误指标超过阈值。阈值：${threshold}，当前值：${metricValue}\",\"alarmLevelId\":3,\"alwaysEffective\":true,\"compare\":1,\"continuousHitTimes\":5,\"metricId\":17,\"ruleName\":\"HTTP 500错误\",\"threshold\":\"1\"},"
            + "{\"alarmContent\":\"您的应用${appCode}-${endPoint}  JVM堆内存使用占比指标超过阈值。阈值：${threshold}，当前值：${metricValue}\",\"alarmLevelId\":3,\"alwaysEffective\":true,\"compare\":1,\"continuousHitTimes\":1,\"metricId\":31,\"ruleName\":\"JVM堆内存使用占比\",\"threshold\":\"90\"},"
            + "{\"alarmContent\":\"应用实例发生重启：`${biz_application}/${pod}/${container}` 在近3m内发生重启，次数为：${metricValue}\\n  摘要: 应用实例发生重启\",\"alarmLevelId\":3,\"alwaysEffective\":true,\"compare\":1,\"continuousHitTimes\":1,\"metricId\":34,\"ruleName\":\"实例发生重启\",\"threshold\":\"0\"}"
            + "{\"alarmContent\":\"应用实例 `${biz_application}/${container_label_io_kubernetes_pod_name}` 在1m内, 磁盘 Write IOPS过高，值为：${metricValue}\",\"alarmLevelId\":3,\"alwaysEffective\":true,\"compare\":1,\"continuousHitTimes\":1,\"metricId\":35,\"ruleName\":\"容器实例近1m内磁盘写IOPS 过高\",\"threshold\":\"0\"}"
            + "{\"alarmContent\":\"应用实例 `${biz_application}/${pod}` 为非 Running 状态\",\"alarmLevelId\":3,\"alwaysEffective\":true,\"compare\":1,\"continuousHitTimes\":1,\"metricId\":36,\"ruleName\":\"容器实例 未运行\",\"threshold\":\"0\"}"
            + "{\"alarmContent\":\"应用实例 `${biz_application}/${pod}/${container}` 在近1分钟内发生OOM，次数为：${metricValue}\",\"alarmLevelId\":3,\"alwaysEffective\":true,\"compare\":1,\"continuousHitTimes\":1,\"metricId\":37,\"ruleName\":\"容器实例 OOM\",\"threshold\":\"0\"}"
            + "{\"alarmContent\":\"应用实例 `${biz_application}/${container_label_io_kubernetes_pod_name}` 在1m内，流量异常，流入速度：${metricValue}Mbps\",\"alarmLevelId\":3,\"alwaysEffective\":true,\"compare\":1,\"continuousHitTimes\":1,\"metricId\":38,\"ruleName\":\"容器实例 接收流量过高\",\"threshold\":\"0\"}"
            + "{\"alarmContent\":\"应用实例 `${biz_application}/${container_label_io_kubernetes_pod_name}` 在1m内，流量异常，流出速度：${metricValue}Mbps\",\"alarmLevelId\":3,\"alwaysEffective\":true,\"compare\":1,\"continuousHitTimes\":1,\"metricId\":39,\"ruleName\":\"容器实例 发送流量过高\",\"threshold\":\"0\"}"
            + "{\"alarmContent\":\"应用实例 `${biz_application}/${container_label_io_kubernetes_pod_name}` 在1m内, 磁盘 Read IOPS过高，值为：${metricValue}\",\"alarmLevelId\":3,\"alwaysEffective\":true,\"compare\":1,\"continuousHitTimes\":1,\"metricId\":47,\"ruleName\":\"容器实例 近 1m 内磁盘读 IOPS 过高\",\"threshold\":\"0\"}"
            + "{\"alarmContent\":\"应用实例 `${biz_application}/${container_label_io_kubernetes_pod_name}/${container_label_io_kubernetes_container_name}` 近1m内平均负载为: ${metricValue}\",\"alarmLevelId\":3,\"alwaysEffective\":true,\"compare\":1,\"continuousHitTimes\":1,\"metricId\":61,\"ruleName\":\"容器实例 load\",\"threshold\":\"20\"}"
            + "{\"alarmContent\":\"应用实例 `${biz_application}/${container_label_io_kubernetes_pod_name}/${container_label_io_kubernetes_container_name}` 磁盘使用过高, 已使用 ${metricValue}GB\",\"alarmLevelId\":3,\"alwaysEffective\":true,\"compare\":1,\"continuousHitTimes\":1,\"metricId\":68,\"ruleName\":\"容器磁盘使用过高\",\"threshold\":\"0\"}"
            + "]";
    
}
