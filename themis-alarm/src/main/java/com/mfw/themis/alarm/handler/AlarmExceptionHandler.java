package com.mfw.themis.alarm.handler;

import com.mfw.themis.common.util.DateFormatUtils;
import com.mfw.themis.dependent.alert.AlertClient;
import com.mfw.themis.dependent.alert.model.AlertRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 异常发送报警消息
 */
@Component
@Slf4j
public class AlarmExceptionHandler {

    @Value("${app.env}")
    private String env;

    @Autowired
    private AlertClient alertClient;

    public void uncaughtException(String message) {

        List<String> channel = Arrays.asList("wechat_work");
        List<String> employees = Arrays.asList("56379418", "76252806");

        String alertTimeStr = DateFormatUtils.formatToRfc3339China(new Date());
        AlertRequest request = AlertRequest.builder().title(env.equals("prod") ? "" : "[dev]" + "[themis-alarm]下发报警失败")
                .alertTime(alertTimeStr)
                .channels(channel)
                .level("urgency")
                .content(message)
                .receivers(employees)
                .source("themis-alarm")
                .state("in")
                .build();

        try {
            alertClient.sendAlertMessage(request);
        } catch (Exception e) {
            log.error("AlarmExceptionHandler sendAlertMessage, request: {}, error : {}", request.toString(), e);
        }
    }
}