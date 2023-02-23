package com.mfw.themis.portal.event;

import lombok.Builder;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

/**
 * 应用创建事件
 *
 * @author guosp
 */
@Data
public class MoneRuleSyncEvent extends ApplicationEvent {

    public MoneRuleSyncEvent(Long appId) {
        super(appId);
    }
}
