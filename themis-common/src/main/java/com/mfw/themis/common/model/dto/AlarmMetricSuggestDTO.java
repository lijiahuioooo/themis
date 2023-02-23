package com.mfw.themis.common.model.dto;

import com.mfw.themis.common.constant.enums.CollectTypeEnum;
import com.mfw.themis.common.constant.enums.DataSourceTypeEnum;
import com.mfw.themis.common.constant.enums.MetricTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author wenhong
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmMetricSuggestDTO {

    private Long id;

    private String name;

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
}
