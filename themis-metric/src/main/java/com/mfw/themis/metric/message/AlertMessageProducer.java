package com.mfw.themis.metric.message;

import com.alibaba.fastjson.JSON;
import com.mfw.coeus.producer.sdk.MfwMessageRequest;
import com.mfw.coeus.producer.sdk.MfwMessageResponse;
import com.mfw.coeus.producer.sdk.MfwProducer;
import com.mfw.coeus.producer.sdk.exception.MfwMQException;
import com.mfw.coeus.producer.sdk.exception.RequestInvalidException;
import com.mfw.coeus.producer.sdk.exception.RequestLimitException;
import com.mfw.themis.common.model.message.AlertMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author liuqi
 */
@Component
@Slf4j
public class AlertMessageProducer {

    @Value("${spring.application.name}")
    private String applicationName;
    @Value("${alertMessage.rocketServer}")
    private String rocketServer;
    @Value("${alertMessage.producerGroup}")
    private String producerGroup;
    @Value("${alertMessage.tags}")
    private String tags;
    @Value("${alertMessage.topic}")
    private String topic;
    @Autowired
    private MfwProducer mfwProducer;

    /**
     * 发送报警触发消息
     *
     * @param alertMessage
     */
    public void sendAlertMessage(AlertMessage alertMessage) {
        MfwMessageRequest request = new MfwMessageRequest();
        request.setTopic(topic);
        request.setTags(tags);
        request.setProducerGroup(producerGroup);
        request.setKey(Long.toString(System.currentTimeMillis()));
        request.setAppCode(applicationName);
        request.setContent(JSON.toJSONString(alertMessage));
        try {
            MfwMessageResponse response = mfwProducer.send(rocketServer, request);
            log.info("send message success. msgId:{},request:{}", response.getMsgId(), JSON.toJSONString(request));
        } catch (MfwMQException | RequestLimitException | RequestInvalidException e) {
            log.error("send message to MQ error.", e);
        }
    }
}
