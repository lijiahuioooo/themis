package com.mfw.themis.common.model.dto;

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
public class CompositeAppMetricDTO {
    private Long id;
    /**
     * 指标名
     */
    private String metricName;
    /**
     * 复合指标appMetricId
     */
    private Long compositeAppMetricId;
    /**
     * 单指标appMetricId
     */
    private Long singleAppMetricId;
}
