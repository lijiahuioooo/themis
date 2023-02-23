package com.mfw.themis.portal.controller;

import com.mfw.themis.common.model.ResponseResult;
import com.mfw.themis.portal.service.InitMetricService;
import com.mfw.themis.portal.service.InitRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author guosp
 */
@RestController
@RequestMapping(value = "/portal/init")
@Slf4j
public class InitRuleController {

    @Autowired
    private InitRuleService initRuleService;


    @GetMapping(value = "/initAlarmRule")
    public ResponseResult<String> initAlarmRule(Long appId) {

        initRuleService.initAlarmRule(appId);

        return ResponseResult.OK("OK");
    }

    @GetMapping(value = "/syncRuleToApp")
    public ResponseResult<String> syncMetricToApp(Long metricId) {

        initRuleService.syncRuleToApp(metricId);
        return ResponseResult.OK("OK");
    }

}