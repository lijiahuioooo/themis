package com.mfw.themis.portal.listener;

import com.mfw.themis.common.constant.enums.AppSourceEnum;
import com.mfw.themis.common.constant.enums.DefaultMetricEnum;
import com.mfw.themis.dao.mapper.AppDao;
import com.mfw.themis.dao.po.AppPO;
import com.mfw.themis.portal.event.AppCreateEvent;
import com.mfw.themis.portal.service.DefaultMetricService;
import com.mfw.themis.portal.service.InitMetricService;
import com.mfw.themis.portal.service.InitRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author guosp
 */

@Component
public class AppCreateListener {

    @Autowired
    private AppDao appDao;

    @Autowired
    private InitRuleService initRuleService;

    @Autowired
    private InitMetricService initMetricService;

    @Autowired
    private DefaultMetricService defaultMetricService;

    @Async
    @EventListener(value = AppCreateEvent.class)
    public void consumerMetricEvent(AppCreateEvent appCreateEvent) {
        Long appId = (Long) appCreateEvent.getSource();
        AppPO appPO = appDao.selectById(appId);
        if (appPO.getSource() == AppSourceEnum.AOS.getCode()) {
            initMetricService.initAllAppMetric(appId);
            initRuleService.initAlarmRule(appId);
        }
        defaultMetricService.creatDefaultMetric(appPO.getAppCode(), DefaultMetricEnum.DEFAULT_HTTP_METRIC);
        defaultMetricService.creatDefaultMetric(appPO.getAppCode(), DefaultMetricEnum.DEFAULT_DUBBO_METRIC);
    }
}
