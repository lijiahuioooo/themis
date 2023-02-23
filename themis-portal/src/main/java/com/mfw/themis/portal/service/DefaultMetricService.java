package com.mfw.themis.portal.service;

import com.mfw.themis.common.constant.enums.DefaultMetricEnum;

public interface DefaultMetricService {

    /**
     * 服务首次开通创建默认指标
     * @param 
     * @return
     */
    int creatDefaultMetric(String appCode, DefaultMetricEnum defaultMetricEnum);

    /**
     * 刷新已开通的服务的默认指标
     * @param
     * @return
     */
    void refreshDefaultMetric();

}
