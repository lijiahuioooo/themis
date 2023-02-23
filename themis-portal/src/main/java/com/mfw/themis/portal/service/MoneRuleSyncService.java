package com.mfw.themis.portal.service;

/**
 * @author guosp
 */
public interface MoneRuleSyncService {

    /**
     * 同步mone告警规则
     * @param appId
     * @return
     */
    boolean consumeSyncAppRule(Long appId);


    /**
     * sync
     * @param appId
     * @return
     */
    boolean syncAppRule(Long appId);

    /**
     * 设置alarmMetric数据
     * @param appId
     * @return
     */
    boolean refreshAlarmMetricItem(Long appId);

    /**
     * 设置alarm metric name & alarm rule name
     * @param appId
     * @return
     */
    boolean refreshMetricName(Long appId);


    /**
     * 清除alarmMetric数据
     * @param appId
     * @return
     */
    boolean cleanAlarmMetricItem(Long appId);

}
