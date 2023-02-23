package com.mfw.themis.dependent.alert.model;

import java.util.List;
import lombok.Data;

/**
 * @author liuqi
 */
@Data
public class AlertResponse {

    private List<WechatAlertFailure> failures;

    @Data
    public static class WechatAlertFailure {

        private String receiver;
        private String channel;
        private String info;
    }

}
