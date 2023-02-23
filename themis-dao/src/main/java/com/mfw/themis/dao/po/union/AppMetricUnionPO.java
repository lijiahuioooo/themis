package com.mfw.themis.dao.po.union;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author guosp
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppMetricUnionPO {

    private Long id;

    private Long appId;

    private Long metricId;

    /**
     * 指标表达式变量值
     */
    private String attrValue;

    private Long datasourceId;

    private Integer collectId;

    private Integer status;

    private Boolean isDelete;

    /***  app ***/

    private String appCode;

    private String appName;

    /**
     * metric name
     */
    private String name;

    /**
     * 指标收集规则
     */
    private String expression;

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
    private Integer timeWindowType;
    /**
     * 时间窗口偏移
     */
    private String timeWindowOffset;
    /**
     * 自定义时间窗口
     */
    private String customTimeWindow;

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

    private Integer metricUnit;

    private Integer groupType;
    private String groupField;

    /**
     * 指标标签（预留分类）
     */
    private String metricTag;

    /**
     * 复合指标表达式
     */
    private String compositeMetricExpression;

    /**
     * 计算公式
     */
    private String formula;

    /**
     * 扩展标记
     */
    private String extData;

    /**
     * 数据源名称
     */
    private String datasourceName;

    /**
     * 数据源类型
     */
    private String datasourceType;

    /**
     * 创建者uid
     */
    private Long creater;

    /**
     * 创建时间
     */
    private Date ctime;
    /**
     * 更新时间
     */
    private Date mtime;
}
