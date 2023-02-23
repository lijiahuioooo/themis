package com.mfw.themis.common.model.dto;

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
public class EsAggMetricDTO {
    private String metric;

    private String filterMetricOperator;

    private String metricValue;
}
