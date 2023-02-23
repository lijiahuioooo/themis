package com.mfw.themis.common.model.dto;

import com.mfw.themis.common.constant.enums.AlarmMetricUnitEnum;
import com.mfw.themis.common.constant.enums.CollectTypeEnum;
import com.mfw.themis.common.constant.enums.DataSourceTypeEnum;
import com.mfw.themis.common.constant.enums.GroupTypeEnum;
import com.mfw.themis.common.constant.enums.MetricTypeEnum;
import com.mfw.themis.common.constant.enums.TimeWindowEnum;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author guosp
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveAlarmMetricDTO {

    private Long id;

    @NotNull(message = "指标名称不能为空")
    private String name;

    @NotNull(message = "指标表达式不能为空")
    private String expression;

    /**
     * 描述
     */
    private String description;

    /**
     * 时间窗口
     */
    @NotNull(message = "时间窗口参数错误")
    private TimeWindowEnum timeWindow;

    /**
     * 指标采集的数据源类型
     */
    @NotNull(message = "数据源类型参数错误")
    private DataSourceTypeEnum sourceType;

    /**
     * 指标分类（系统指标、应用指标、业务指标）
     */
    @NotNull(message = "指标分类参数错误")
    private MetricTypeEnum metricType;

    /**
     * 收集类型（单一指标，复合指标）
     */
    @NotNull(message = "收集类型参数错误")
    private CollectTypeEnum collectType;

    /**
     * 指标标签（预留分类）
     */
    private List<String> metricTag;
    /**
     * 聚合方式
     */
    private GroupTypeEnum groupType;

    /**
     * 聚合字段（es）
     */
    private String groupField;

    /**
     * 复合指标表达式
     */
    private String compositeMetricExpression;

    /**
     * 计算公式
     */
    private String formula;

    /**
     * 指标单位
     */
    private AlarmMetricUnitEnum unit;
    /**
     * 扩展标记
     */
    private String extData;
    /**
     * 操作人
     */
    @NotNull(message = "操作人不允许为空")
    private Long operator;

}
