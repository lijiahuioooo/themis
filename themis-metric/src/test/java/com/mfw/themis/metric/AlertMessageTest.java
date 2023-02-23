package com.mfw.themis.metric;

import com.mfw.themis.common.model.message.AlertMessage;
import com.mfw.themis.metric.message.AlertMessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class AlertMessageTest {

    @Autowired
    private AlertMessageProducer producer;

    @Test
    public void alertTest(){

        AlertMessage alertMessage = AlertMessage.builder()
                .alertTitle("themis-metric 指标计算异常")
                .alertContent("报警测试")
                .alertTime(new Date())
                .currentMetricValue("1")
                .moreUrl(StringUtils.EMPTY)
                .ruleId(1364L).build();


        log.info("alertMessage:{}", alertMessage.toString());
        producer.sendAlertMessage(alertMessage);
    }

}
