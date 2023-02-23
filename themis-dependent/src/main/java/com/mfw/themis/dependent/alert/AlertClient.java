package com.mfw.themis.dependent.alert;

import com.mfw.themis.dependent.alert.model.AlertRequest;
import com.mfw.themis.dependent.alert.model.AlertResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 系统部企业微信报警，接口文档 https://yapi.mfwdev.com/project/1599/interface/api/22777
 *
 * @author liuqi
 */
@FeignClient(name = "alert", url = "${themis.alarm.alertSender.host:}")
public interface AlertClient {

    @PostMapping("/api/v1/alertsender/send")
    AlertResponse sendAlertMessage(@RequestBody AlertRequest req);
}
