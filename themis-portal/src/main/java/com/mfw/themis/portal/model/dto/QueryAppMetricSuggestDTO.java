package com.mfw.themis.portal.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author wenhong
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryAppMetricSuggestDTO {

    /**
     * name
     */
    private String keyword;

    /**
     * 指标类型
     */
    private Integer metricType;

    /**
     * 收集类型
     */
    private Integer collectType;

    private Long appId;

    private String appCode;

    private String name;

    private Integer sourceType;
}
