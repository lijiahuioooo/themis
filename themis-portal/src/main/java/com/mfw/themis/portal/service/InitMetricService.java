package com.mfw.themis.portal.service;

/**
 * @author guosp
 */
public interface InitMetricService {

    /**
     * 初始化应用指标(系统指标、运维指标、应用指标)
     * @param appId
     * @return
     */
    boolean initAllAppMetric(Long appId);

    /**
     * 新增指标同步到所有应用
     * @param metricId
     * @return
     */
    boolean syncMetricToApp(Long metricId);

}
