package com.mfw.themis.metric.model;

import com.mfw.themis.common.constant.enums.AlarmMetricUnitEnum;
import java.util.Map;
import lombok.Data;

/**
 * 指标数据
 *
 * @author liuqi
 */
@Data
public class MetricValue {

    /**
     * 时间戳
     */
    private Long timeStamp;
    /**
     * 指标值
     */
    private String value;
    /**
     * 指标单位
     */
    private AlarmMetricUnitEnum unit;
    /**
     * 指标额外
     */
    private Map<String, String> metricExtInfo;

}
