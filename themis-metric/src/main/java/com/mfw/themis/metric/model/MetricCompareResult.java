package com.mfw.themis.metric.model;

import com.mfw.themis.common.constant.enums.RuleStatusEnum;
import lombok.Builder;
import lombok.Data;

/**
 * 指标结果与规则比较之后的结果
 *
 * @author liuqi
 */
@Data
@Builder
public class MetricCompareResult {

    /**
     * 上一次比较状态
     */
    private RuleStatusEnum oldStatus;
    /**
     * 当前比较状态
     */
    private RuleStatusEnum currentStatus;
    /**
     * 指标计算结果
     */
    private MetricValue metricValue;
    /**
     * 指标断点
     */
    private String endPoint;
}