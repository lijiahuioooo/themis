package com.mfw.themis.portal.service;

/**
 * @author guosp
 */
public interface InitRuleService {

    /**
     * 新增应用同步规则
     * @param appId
     * @return
     */
    boolean initAlarmRule(Long appId);


    /**
     * 新增告警规则模版同步到所有应用
     * @param metricId
     * @return
     */
    boolean syncRuleToApp(Long metricId);
}
