package com.mfw.themis.metric.service;

public interface MetricExecuteService {

    /**
     * 指标计算
     * @param appMetricId
     * @return
     */
    Long metricExecute(Long appMetricId);
}
