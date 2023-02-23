package com.mfw.themis.metric;

import com.mfw.themis.common.constant.enums.AlarmMetricUnitEnum;
import com.mfw.themis.common.constant.enums.CompareTypeEnum;
import com.mfw.themis.common.constant.enums.EnableEnum;
import com.mfw.themis.common.convert.AlarmRuleConvert;
import com.mfw.themis.common.model.dto.AlarmRuleDTO;
import com.mfw.themis.dao.mapper.AlarmRuleDao;
import com.mfw.themis.dao.po.AlarmRulePO;
import com.mfw.themis.metric.event.MetricExecuteEvent;
import com.mfw.themis.metric.manager.MetricCompareManager;
import com.mfw.themis.metric.manager.MetricCoreManager;
import com.mfw.themis.metric.manager.MetricRuleCacheManager;
import com.mfw.themis.metric.model.MetricExecuteResult;
import com.mfw.themis.metric.model.MetricValue;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class MetricCoreManagerTest {

    @InjectMocks
    private MetricCoreManager metricCoreManager;
    @Mock
    private MetricCompareManager metricCompareManager;
    @Mock
    private MetricRuleCacheManager metricRuleCacheManager;
    @Mock
    private AlarmRuleDao alarmRuleDao;

    @Before
    public void prepareTestData() {
        List<AlarmRulePO> alarmRulePOList = new ArrayList<>();
        alarmRulePOList.add(AlarmRuleConvert.toPO(buildRule()));
        Mockito.when(alarmRuleDao.selectList(Mockito.any())).thenReturn(alarmRulePOList);
    }

    @Test
    public void testProcessRulesEffectiveTime() {
        MetricExecuteEvent metricExecuteEvent = buildExecuteEvent();
        boolean result = metricCoreManager.compareResultSetWithRule(metricExecuteEvent);
        Assert.assertTrue(result);

        AlarmRuleDTO alarmRule = buildRule("10:00:00", "12:00:00");
        alarmRule.setAlwaysEffective(false);
        metricExecuteEvent.setAlarmRule(alarmRule);

        Date date = DateTime.parse("2020-10-10T09:59:59").toDate();
        metricExecuteEvent.setExecuteTime(date);
        result = metricCoreManager.compareResultSetWithRule(metricExecuteEvent);
        Assert.assertFalse(result);

        date = DateTime.parse("2020-10-10T12:00:01").toDate();
        metricExecuteEvent.setExecuteTime(date);
        result = metricCoreManager.compareResultSetWithRule(metricExecuteEvent);
        Assert.assertFalse(result);

        date = DateTime.parse("2020-10-10T13:00:01").toDate();
        metricExecuteEvent.setExecuteTime(date);
        result = metricCoreManager.compareResultSetWithRule(metricExecuteEvent);
        Assert.assertFalse(result);

        date = DateTime.parse("2020-10-10T12:00:00").toDate();
        metricExecuteEvent.setExecuteTime(date);
        result = metricCoreManager.compareResultSetWithRule(metricExecuteEvent);
        Assert.assertTrue(result);

        date = DateTime.parse("2020-10-10T11:00:00").toDate();
        metricExecuteEvent.setExecuteTime(date);
        result = metricCoreManager.compareResultSetWithRule(metricExecuteEvent);
        Assert.assertTrue(result);

        date = DateTime.parse("2020-10-10T10:00:00").toDate();
        metricExecuteEvent.setExecuteTime(date);
        result = metricCoreManager.compareResultSetWithRule(metricExecuteEvent);
        Assert.assertTrue(result);

    }

    private MetricExecuteResult buildMetricResult() {
        MetricExecuteResult result = new MetricExecuteResult();
        MetricValue metricValue = new MetricValue();
        metricValue.setValue("12");
        metricValue.setUnit(AlarmMetricUnitEnum.NONE);
        metricValue.setTimeStamp(System.currentTimeMillis());
        Map<String, String> metrics = new HashMap<>();
        metrics.put("metric", "testName");
        metricValue.setMetricExtInfo(metrics);
        Map<String, MetricValue> metricValueMap = new HashMap<>();

        metricValueMap.put("10.10.10.10", metricValue);
        result.setMetricValueMap(metricValueMap);
        return result;
    }

    private MetricExecuteEvent buildExecuteEvent() {
        MetricExecuteEvent event = new MetricExecuteEvent();
        event.setExecuteTime(new Date());
        event.setResult(buildMetricResult());
        event.setAlarmRule(buildRule());
        return event;
    }

    private AlarmRuleDTO buildRule() {
        return buildRule("00:00:00", "23:59:59");
    }

    private AlarmRuleDTO buildRule(String startTime, String endTime) {
        return AlarmRuleDTO.builder()
                .id(1L)
                .alarmLevelId(1L)
                .alarmContent("")
                .alwaysEffective(true)
                .appId(1L)
                .appMetricId(1L)
                .compare(CompareTypeEnum.GT)
                .contacts("11")
                .continuousHitTimes(1)
                .startEffectiveTime(startTime)
                .endEffectiveTime(endTime)
                .metricName("test")
                .ruleName("test")
                .status(EnableEnum.ENABLE)
                .threshold("10")
                .creater(1L)
                .operator(1L)
                .ctime(new Date())
                .mtime(new Date())
                .isDelete(false)
                .build();
    }
}
