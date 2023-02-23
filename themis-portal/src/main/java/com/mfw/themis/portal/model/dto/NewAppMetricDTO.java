package com.mfw.themis.portal.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mfw.themis.common.constant.enums.AlarmMetricUnitEnum;
import com.mfw.themis.common.constant.enums.GroupTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author wenhong
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewAppMetricDTO {

    /**
     * appMetricId
     */
    @JsonProperty("id")
    private Long appMetricId;

    /**
     * 数据源类型
     */
    @NotNull( message = "数据源类型不能为空")
    private Integer sourceType;

    /**
     * 数据源id
     */
    private Long datasourceId;

    /**
     * 数据上报ID
     */
    private Integer collectId;

    /**
     * 指标类型
     */
    @NotNull( message = "指标类型不能为空")
    private Integer metricType;

    /**
     * 收集类型
     */
    @NotNull( message = "收集类型不能为空")
    private Integer collectType;

    /**
     * 指标名称
     */
    private String name;
    /**
     * 指标描述
     */
    private String description;
    /**
     * 指标id
     */
    private Long metricId;

    /**
     * 聚合方式
     */
    private Integer groupType;

    /**
     * 聚合字段（es）
     */
    private String groupField;

    /**
     * 指标单位
     */
    private Integer metricUnit;

    /**
     * 时间窗口
     */
    private Integer timeWindow;

    /**
     * 时间窗口类型
     */
    private Integer timeWindowType;

    /**
     * 时间窗口偏移量
     */
    private Map<String, Object> timeWindowOffset;

    /**
     * 自定义时间窗口
     */
    private Map<String, Object> customTimeWindow;

    /**
     * 扩展标记
     */
    private String extData;


    /**
     * 计算公式
     */
    private String formula;

    /**
     * 表达式列表
     */
    private List<Map<String, Object>> expressionList;

    /**
     * 复合指标的单指标列表
     */
    private List<Map<String, Object>> metricList;

    /**
     * appId
     */
    @NotNull( message = "appId不能为空")
    private Long appId;

    /**
     * 操作人uid
     */
    @JsonProperty("uid")
    private Long creater;
}
