package com.mfw.themis.portal.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEvent;

/**
 * 应用创建事件
 *
 * @author guosp
 */
@Data
public class AppCreateEvent extends ApplicationEvent {

    public AppCreateEvent(Long appId) {
        super(appId);
    }
}
