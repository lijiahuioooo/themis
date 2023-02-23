package com.mfw.themis.metric.exception;

import com.mfw.themis.common.exception.ServiceException;

/**
 * @author liuqi
 */
public class MetricCalculateTimeOutOfThresholdException extends ServiceException {

    private static final String ERROR_MSG = "指标执行时间超过阈值,请优化指标表达式. app metric id:%d";

    public MetricCalculateTimeOutOfThresholdException(Long appMetricId) {
        super(String.format(ERROR_MSG, appMetricId));
    }
}
