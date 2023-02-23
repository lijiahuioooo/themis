package com.mfw.themis.portal.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mfw.themis.common.model.ResponseResult;
import com.mfw.themis.common.model.dto.AlarmMetricDTO;
import com.mfw.themis.common.model.dto.AppMetricDTO;
import com.mfw.themis.common.model.dto.MetricSummaryDTO;
import com.mfw.themis.common.model.dto.SaveAlarmMetricDTO;
import com.mfw.themis.portal.model.dto.QueryAlarmMetricDTO;
import com.mfw.themis.portal.service.AlarmMetricService;
import com.mfw.themis.portal.service.MetricSummaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author guosp
 */
@RestController
@RequestMapping(value = "/portal/metricSummary")
@Slf4j
public class MetricSummaryController {

    @Autowired
    private MetricSummaryService metricSummaryService;


    @PostMapping("/create")
    public ResponseResult<MetricSummaryDTO> create(@RequestBody @Validated MetricSummaryDTO req) {
        log.info("Enter method create. req:{}", JSON.toJSONString(req));
        ResponseResult<MetricSummaryDTO> result = ResponseResult.OK(metricSummaryService.create(req));
        log.info("End method create. res:{}", JSON.toJSONString(result));
        return result;
    }


}
