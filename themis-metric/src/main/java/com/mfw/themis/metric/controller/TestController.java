package com.mfw.themis.metric.controller;

import com.mfw.themis.common.convert.AppMetricConvert;
import com.mfw.themis.common.model.ResponseResult;
import com.mfw.themis.dao.mapper.AppMetricDao;
import com.mfw.themis.dao.po.AppMetricPO;
import com.mfw.themis.metric.manager.MetricExecuteManager;
import java.util.Date;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuqi
 */
@RestController
public class TestController {

    @Autowired
    private MetricExecuteManager executeManager;

    @Autowired
    private AppMetricDao appMetricDao;
    @GetMapping(value = "/test")
    public ResponseResult test(Long appMetricId) {

        AppMetricPO appMetric = appMetricDao.selectById(appMetricId);

        executeManager.executeMetricEngine(AppMetricConvert.toDTO(appMetric), null, new Date());

        return ResponseResult.OK();
    }

}
