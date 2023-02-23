package com.mfw.themis.metric.controller;

import com.alibaba.fastjson.JSON;
import com.mfw.themis.common.model.ResponseResult;
import com.mfw.themis.dao.mapper.AppMetricDao;
import com.mfw.themis.metric.service.MetricExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/metric/metricExecute")
@Slf4j
public class MetricExecuteController {

    @Autowired
    private MetricExecuteService metricExecuteService;
    @Autowired
    private AppMetricDao appMetricDao;

    /**
     * 指标计算 返回执行时间,30s超时异常
     * @param appMetricId
     * @return
     */
    @GetMapping("/execute_by_appmetricid")
    public ResponseResult<Long> executeByAppIdMetricId(Long appMetricId) {
        log.info("Enter method executeByAppMetricId. appMetricId:{}", appMetricId);
        ResponseResult<Long> result = ResponseResult.OK(metricExecuteService.metricExecute(appMetricId));
        log.info("End method executeByAppIdMetricId. res:{}", JSON.toJSONString(result));
        return result;
    }

    @GetMapping("/updateDatasource")
    public ResponseResult<Boolean> executeByAppIdMetricId(Long oldDataSource, Long newDatasource) {
        appMetricDao.updateDataSource(oldDataSource, newDatasource);
        return ResponseResult.OK(true);
    }

}
