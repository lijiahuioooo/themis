package com.mfw.themis.metric.model;

import java.util.Map;
import lombok.Data;

/**
 * 指标计算结果
 *
 * @author liuqi
 */
@Data
public class MetricExecuteResult {

    /**
     * 多节点指标值 key:节点命名 value:指标数据
     */
    private Map<String, MetricValue> metricValueMap;

}
