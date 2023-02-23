package com.mfw.themis.collector.message.producer;

import com.alibaba.fastjson.JSON;
import com.mfw.coeus.producer.sdk.MfwMessageRequest;
import com.mfw.coeus.producer.sdk.MfwMessageResponse;
import com.mfw.coeus.producer.sdk.MfwProducer;
import com.mfw.coeus.producer.sdk.exception.MfwMQException;
import com.mfw.coeus.producer.sdk.exception.RequestInvalidException;
import com.mfw.coeus.producer.sdk.exception.RequestLimitException;
import com.mfw.themis.collector.manager.PrometheusMonitor;
import com.mfw.themis.common.model.message.CollectorMessage;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author wenhong
 */
@Component
@Slf4j
public class CollectorMessageProducer {

    @Value("${spring.application.name}")
    private String applicationName;
    @Value("${collectorMessage.rocketServer}")
    private String rocketServer;
    @Value("${collectorMessage.producerGroup}")
    private String producerGroup;
    @Value("${collectorMessage.tags}")
    private String tags;
    @Value("${collectorMessage.topic}")
    private String topic;
    @Value("${monitor.metric:}")
    private String monitorMetric;
    @Autowired
    private MfwProducer mfwProducer;

    /**
     * 发送报警触发消息
     *
     * @param collectorMessage
     */
    public MfwMessageResponse sendCollectorMessage(CollectorMessage collectorMessage) {
        MfwMessageRequest request = new MfwMessageRequest();
        request.setTopic(topic);
        request.setTags(tags);
        request.setProducerGroup(producerGroup);
        request.setKey(Long.toString(System.currentTimeMillis()));
        request.setAppCode(applicationName);
        request.setContent(JSON.toJSONString(collectorMessage));
        try {

            // 数据上报
            Map<String, Object> tags = new HashMap<>();
            tags.put("topic", topic);
            tags.put("appCode", collectorMessage.getAppCode());
            PrometheusMonitor.counter("collector_statistic", tags);

            MfwMessageResponse response = mfwProducer.send(rocketServer, request);
            if (StringUtils.equals(monitorMetric, collectorMessage.getMetric())) {
                log.info("mfw collector send message success. msgId:{},request:{}", response.getMsgId(),
                        JSON.toJSONString(request));
            }
            return response;
        } catch (MfwMQException | RequestLimitException | RequestInvalidException e) {
            log.error("mfw collector send message to MQ error.", e);
        }

        return null;
    }
}
