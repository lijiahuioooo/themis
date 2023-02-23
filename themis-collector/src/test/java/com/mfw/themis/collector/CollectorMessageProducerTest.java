package com.mfw.themis.collector;

import com.mfw.themis.collector.message.producer.CollectorMessageProducer;
import com.mfw.themis.common.model.message.CollectorMessage;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
@Ignore
public class CollectorMessageProducerTest {

    @Autowired
    private CollectorMessageProducer collectorMessageProducer;

    @Test
    public void generateMessage(){

        Random random = new Random(999);

        for(int i=0; i < 160; i++){

            CollectorMessage collectorMessage = new CollectorMessage();
            collectorMessage.setAppCode("honey-account");
            collectorMessage.setMetric("http_response_time");
            collectorMessage.setTimestamp(System.currentTimeMillis());
            collectorMessage.set_collectorId(UUID.randomUUID().toString());

            Map<String, Object> insertItem = new HashMap<>();
            insertItem.put("api_name", "getInfo");
            insertItem.put("http_status", 200);
            insertItem.put("sort", i);
            insertItem.put("response_time", Double.valueOf(String.valueOf(random.nextInt(1000))));

            collectorMessage.setData(insertItem);

            collectorMessageProducer.sendCollectorMessage(collectorMessage);
        }

        // 单测同时启动了消息总线的consumer，等待30s再退出，否则有消息丢失。
        try{
            Thread.sleep(30000);
        }catch (Exception e){}
    }

}
