package com.mfw.themis.metric.model;

import com.mfw.themis.common.constant.enums.AlarmMetricUnitEnum;
import com.mfw.themis.common.constant.enums.GroupTypeEnum;
import com.mfw.themis.common.constant.enums.TimeWindowTypeEnum;
import com.mfw.themis.common.model.CustomTimeWindow;
import com.mfw.themis.common.model.TimeWindowOffset;
import java.util.Date;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liuqi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Metric {

    /**
     * appId
     */
    private Long appId;

    private Long appMetricId;

    /**
     * 数据上报ID
     */
    private Integer collectId;
    /**
     * appCode
     */
    private String appCode;

    /**
     * moneAppCode
     */
    private String moneAppCode;

    /**
     * 指标名称 单一指标可以为空
     */
    private String metricName;
    /**
     * 聚合字段
     */
    private String groupField;
    /**
     * 聚合方式
     */
    private GroupTypeEnum groupType;
    /**
     * 聚合计算公式
     */
    private String formula;

    /**
     * 计量单位
     */
    private AlarmMetricUnitEnum unit;
    /**
     * 补充数据
     */
    private Map<String, String> metricExtData;
    /**
     * 指标表达式
     */
    private String expression;
    /**
     * 指标执行时间
     */
    private Date executeTime;
    /**
     * 时间窗口
     */
    private Integer timeWindow;
    /**
     * 时间窗口类型
     */
    private TimeWindowTypeEnum timeWindowType;
    /**
     * 自定义时间窗口
     */
    private CustomTimeWindow customTimeWindow;
    /**
     * 时间窗口偏移
     */
    private TimeWindowOffset timeWindowOffset;
}