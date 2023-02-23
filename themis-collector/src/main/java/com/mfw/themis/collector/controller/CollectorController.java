package com.mfw.themis.collector.controller;

import com.alibaba.fastjson.JSON;
import com.mfw.themis.collector.manager.CollectorMetricManager;
import com.mfw.themis.collector.server.CollectorBatchService;
import com.mfw.themis.common.model.ResponseResult;
import com.mfw.themis.common.model.message.CollectorMessage;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author liuqi
 */
@RestController
@RequestMapping("/collector/")
@Slf4j
public class CollectorController {

    @Autowired
    private CollectorMetricManager collectorMetricManager;

    @Autowired
    private CollectorBatchService collectorBatchService;

    @GetMapping("metricList")
    public String collectMetricList() {
        return JSON.toJSONString(collectorMetricManager.getCurrentConfig());
    }

    @PostMapping("batch_report")
    public ResponseResult<Boolean> batchReport(@RequestBody List<CollectorMessage> collectorMessageList) {
        if (collectorMessageList.size() > 0) {
            CollectorMessage collectorMessage = collectorMessageList.get(0);
            log.info("Enter batchReport. appCode:{},metric:{}", collectorMessage.getAppCode(),
                    collectorMessage.getMetric());
        }
        return ResponseResult.OK(collectorBatchService.batchReport(collectorMessageList));
    }
}
