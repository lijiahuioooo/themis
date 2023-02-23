package com.mfw.themis.common.model.bo;

import com.mfw.themis.common.constant.enums.TimeWindowTypeEnum;
import com.mfw.themis.common.model.CustomTimeWindow;
import com.mfw.themis.common.model.TimeWindowOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wenhong
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppMetricBO {

    private Long id;

    private Long appId;

    private Long metricId;

    private Long datasourceId;

    private Integer collectId;

    private Integer status;

    private Boolean isDelete;

    private String appCode;

    private String appName;

    private Long creater;

    /**
     * 指标名称
     */
    private String name;

    /**
     * 指标编码
     */
    private String metricName;

    /**
     * 指标收集规则
     */
    private String expression;

    /**
     * 指标变量列表
     */
    private List<Map<String, Object>> expressionList;

    /**
     * 指标变量值
     */
    private String attrValue;

    /**
     * 指标描述
     */
    private String description;

    /**
     * 指标时间窗口
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
     * 时间窗口偏移量
     */
    private TimeWindowOffset timeWindowOffset;

    /**
     * 指标采集的数据源类型
     */
    private Integer sourceType;

    /**
     * 指标分类（系统指标、应用指标、业务指标）
     */
    private Integer metricType;

    /**
     * 收集类型（单一指标，复合指标）
     */
    private Integer collectType;

    /**
     * 指标标签（预留分类）
     */
    private String metricTag;

    /**
     * 指标单位
     */
    private Integer metricUnit;

    /**
     * 复合指标表达式
     */
    private String compositeMetricExpression;

    /**
     * 聚合方式
     */
    private Integer groupType;

    /**
     * 聚合字段（es）
     */
    private String groupField;

    /**
     * 扩展标记
     */
    private String extData;


    /**
     * 计算公式
     */
    private String formula;

    /**
     * 构成复合指标的单一指标列表
     */
    List<AppMetricBO> metricList;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date ctime;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date mtime;
}
