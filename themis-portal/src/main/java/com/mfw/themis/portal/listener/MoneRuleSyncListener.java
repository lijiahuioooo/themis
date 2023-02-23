package com.mfw.themis.portal.listener;

import com.mfw.themis.common.constant.enums.AppSourceEnum;
import com.mfw.themis.dao.mapper.AppDao;
import com.mfw.themis.dao.po.AppPO;
import com.mfw.themis.portal.event.AppCreateEvent;
import com.mfw.themis.portal.event.MoneRuleSyncEvent;
import com.mfw.themis.portal.service.InitMetricService;
import com.mfw.themis.portal.service.InitRuleService;
import com.mfw.themis.portal.service.MoneRuleSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author guosp
 */

@Component
public class MoneRuleSyncListener {

    @Autowired
    private MoneRuleSyncService moneRuleSyncService;

    @EventListener(value = MoneRuleSyncEvent.class)
    public void consumerMetricEvent(MoneRuleSyncEvent moneRuleSyncEvent) {
        Long appId = (Long) moneRuleSyncEvent.getSource();
        moneRuleSyncService.consumeSyncAppRule(appId);
    }
}
