package com.mfw.themis.portal.controller;

import com.mfw.themis.common.model.ResponseResult;
import com.mfw.themis.portal.service.InitRuleService;
import com.mfw.themis.portal.service.MoneRuleService;
import com.mfw.themis.portal.service.MoneRuleSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author guosp
 */
@RestController
@RequestMapping(value = "/portal/mone_sync")
@Slf4j
public class MoneRuleSyncController {

    @Autowired
    private MoneRuleSyncService moneRuleSyncService;

    @Autowired
    private MoneRuleService moneRuleService;

    @GetMapping(value = "/sync")
    public ResponseResult<String> syncAppRule(Long appId) {


        moneRuleSyncService.syncAppRule(appId);

        return ResponseResult.OK("OK");
    }

    /**
     * 将alarmMetric中涉及Es单一指标的expression是变量的替换为有值表达式
     * @param appId
     * @return
     */
    @GetMapping(value = "/refresh_alarm_item")
    public ResponseResult<String> refreshAlarmItem(Long appId) {

        moneRuleSyncService.refreshAlarmMetricItem(appId);

        return ResponseResult.OK("OK");
    }

    @GetMapping(value = "/clean")
    public ResponseResult<String> cleanAppRule(Long appId) {

        moneRuleSyncService.cleanAlarmMetricItem(appId);

        return ResponseResult.OK("OK");
    }

    /**
     * 将alarmMetric中的name重刷
     * @param appId
     * @return
     */
    @GetMapping(value = "/refresh_metric_name")
    public ResponseResult<String> refreshMetricName(Long appId) {

        moneRuleSyncService.refreshMetricName(appId);

        return ResponseResult.OK("OK");
    }

    /**
     * 根据appCode启用规则
     * @param appCode
     * @return
     */
    @GetMapping(value = "/enable_rule")
    public ResponseResult<Integer> enableRule(String appCode){
        return ResponseResult.OK(moneRuleService.enableRuleByAppCode(appCode));
    }

    /**
     * 根据appCode禁用规则
     * @param appCode
     * @return
     */
    @GetMapping(value = "/disable_rule")
    public ResponseResult<Integer> disableRule(String appCode){
        return ResponseResult.OK(moneRuleService.disableRuleByAppCode(appCode));
    }
}