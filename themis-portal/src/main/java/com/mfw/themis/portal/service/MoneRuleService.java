package com.mfw.themis.portal.service;

public interface MoneRuleService {

    /**
     * 禁用报警规则
     * @param appCode
     * @return
     */
    Integer disableRuleByAppCode(String appCode);

    /**
     * 启用报警规则
     * @param appCode
     * @return
     */
    Integer enableRuleByAppCode(String appCode);
}
