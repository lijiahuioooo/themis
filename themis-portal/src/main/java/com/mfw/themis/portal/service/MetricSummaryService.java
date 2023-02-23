package com.mfw.themis.portal.service;

import com.mfw.themis.common.model.dto.MetricSummaryDTO;
import com.mfw.themis.common.model.dto.SaveAlarmMetricDTO;


/**
 * @author guosp
 */
public interface MetricSummaryService {
    /**
     * 创建指标
     *
     * @param summaryDTO
     * @return
     */
    MetricSummaryDTO create(MetricSummaryDTO summaryDTO);
}
