package com.mfw.themis.portal.controller;

import com.mfw.themis.common.model.ResponseResult;
import com.mfw.themis.portal.service.InitMetricService;
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
public class InitMetricController {

    @Autowired
    private InitMetricService initService;


    @GetMapping(value = "/initAppMetric")
    public ResponseResult<String> initAppMetric(Long appId) {

        initService.initAllAppMetric(appId);
        return ResponseResult.OK("OK");
    }

    @GetMapping(value = "/syncMetricToApp")
    public ResponseResult<String> syncMetricToApp(Long metricId) {

        initService.syncMetricToApp(metricId);
        return ResponseResult.OK("OK");
    }
}