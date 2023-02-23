package com.mfw.themis.metric.exception;

import com.mfw.themis.common.constant.enums.DataSourceTypeEnum;
import com.mfw.themis.common.exception.ServiceException;

/**
 * 指标采集异常
 *
 * @author liuqi
 */
public class MetricCollectException extends ServiceException {

    private static final String ERROR_MSG = "指标采集异常，数据源类型:%s,数据源id:%d, 指标名称:%s,appMetric id:%d";

    public MetricCollectException(DataSourceTypeEnum dataSourceType, Long dataSourceId, String metricName,
            Long appMetricId) {
        super(String.format(ERROR_MSG, dataSourceType.getDesc(), dataSourceId, metricName, appMetricId));
    }
}
