package com.mfw.themis.alarm.consumer;

import com.alibaba.fastjson.JSON;
import com.mfw.coeus.consumer.sdk.annotation.CoeusRocketMQMessageListener;
import com.mfw.coeus.consumer.sdk.core.CoeusRocketMQListener;
import com.mfw.themis.alarm.manager.AlarmCoreManager;
import com.mfw.themis.common.model.message.AlertMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author liuqi
 */
@Component
@Slf4j
@CoeusRocketMQMessageListener(appCode = "${spring.application.name}", topic = "${alertMessage.topic}",cluster = "coeus-c-common")
public class AlertMessageConsumer implements CoeusRocketMQListener<String> {

    @Autowired
    private AlarmCoreManager alarmCoreManager;

    @Override
    public void onMessage(String message) {
        log.info("consumer alert message. message:{}", message);
        alarmCoreManager.sendAlertMessage(JSON.parseObject(message, AlertMessage.class));
    }
}
