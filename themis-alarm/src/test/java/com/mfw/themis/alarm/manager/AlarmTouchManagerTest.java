package com.mfw.themis.alarm.manager;

import com.mfw.themis.common.constant.enums.AlertChannelEnum;
import com.mfw.themis.common.model.message.AlertMessage;
import com.mfw.themis.dao.po.AlarmLevelPO;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AlarmTouchManagerTest {

    @Autowired
    private AlarmCoreManager alarmCoreManager;

    @Value("${themis.metric.rule_id}")
    private Long ruleId;

    @Test
    public void parseChannelsTest(){
        AlarmLevelPO alarmLevelPO = new AlarmLevelPO();
        alarmLevelPO.setAlarmChannels("1,3");

        AlarmTouchManager alarmTouchManager = new AlarmTouchManager();

        List<AlertChannelEnum> channels = alarmTouchManager.parseChannels(alarmLevelPO);
        System.out.println(channels);
    }

    @Test
    public void sendMessageFailureTest(){

        AlertMessage alertMessage = AlertMessage.builder()
                .alertTitle("themis-metric 指标计算异常")
                .alertContent("报警测试")
                .alertTime(new Date())
                .currentMetricValue("1")
                .moreUrl(StringUtils.EMPTY)
                .ruleId(ruleId).build();

        alarmCoreManager.sendAlertMessage(alertMessage);
    }
}
