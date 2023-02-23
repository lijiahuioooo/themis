package com.mfw.themis.common.model.bo;

import com.mfw.themis.common.constant.enums.AlarmMetricUnitEnum;
import com.mfw.themis.common.constant.enums.CollectTypeEnum;
import com.mfw.themis.common.constant.enums.DataSourceTypeEnum;
import com.mfw.themis.common.constant.enums.GroupTypeEnum;
import com.mfw.themis.common.constant.enums.MetricTypeEnum;
import com.mfw.themis.common.constant.enums.TimeWindowEnum;
import com.mfw.themis.common.model.dto.CompositeAppMetricDTO;
import java.util.List;

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
public class AlarmMetricBO {

    private Long id;

    private String name;

    private String expression;

    /**
     * 描述
     */
    private String description;

    /**
     * 时间窗口
     */
    private TimeWindowEnum timeWindow;

    /**
     * 指标采集的数据源类型
     */
    private DataSourceTypeEnum sourceType;

    /**
     * 指标分类（系统指标、应用指标、业务指标）
     */
    private MetricTypeEnum metricType;

    /**
     * 收集类型（单一指标，复合指标）
     */
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

}
