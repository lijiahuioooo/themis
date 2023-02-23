package com.mfw.themis.alarm.controller;

import com.mfw.themis.alarm.manager.AlarmCoreManager;
import com.mfw.themis.common.model.message.AlertMessage;
import com.mfw.themis.common.model.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuqi
 */
@RestController
@RequestMapping
public class AlertController {

    @Autowired
    private AlarmCoreManager alarmCoreManager;

    @PostMapping("/sendAlert")
    public ResponseResult<Boolean> sendAlert(@RequestBody  AlertMessage alertMessage) {
        alarmCoreManager.sendAlertMessage(alertMessage);
        return ResponseResult.OK(true);
    }
}
