package com.mfw.themis.collector.message.consumer;

import com.alibaba.fastjson.JSON;
import com.mfw.coeus.consumer.sdk.annotation.CoeusRocketMQMessageListener;
import com.mfw.coeus.consumer.sdk.core.CoeusRocketMQListener;
import com.mfw.themis.collector.exception.MetricValidateException;
import com.mfw.themis.collector.factory.StorageEngineFactory;
import com.mfw.themis.collector.storage.StorageEngine;
import com.mfw.themis.common.constant.enums.DataSourceTypeEnum;
import com.mfw.themis.common.model.message.CollectorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * rocket 数据上报处理
 * @author wenhong
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "themis.collector.consumer.enable", havingValue = "true")
@CoeusRocketMQMessageListener(appCode = "${spring.application.name}", topic = "${collectorMessage.topic}",cluster = "coeus-c-common")
public class CollectorMessageConsumer implements CoeusRocketMQListener<String> {

    @Autowired
    private StorageEngineFactory storageEngineFactory;

    @Override
    public void onMessage(String message) {
//        log.info("consumer message: {}", message);

        try {
            StorageEngine storageEngine = storageEngineFactory.getStorageEngine(DataSourceTypeEnum.ELASTIC_SEARCH);
            CollectorMessage collectorMessage = JSON.parseObject(message, CollectorMessage.class);

            storageEngine.validate(collectorMessage);
            storageEngine.process(collectorMessage);
        } catch (MetricValidateException e) {
            log.warn("collector validate failed, message : " + message);
        }
    }

}
