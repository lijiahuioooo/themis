package com.mfw.themis.alarm.manager;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class PrometheusMonitorTest {

    @Test
    public void countTest(){
        Map<String, Object>  tags = new HashMap<>();
        tags.put("appCode", "themis-alarm");
        tags.put("appName", "服务质量平台");
        tags.put("ruleId", 100);
        tags.put("ruleName", "HTTP请求时间过长");
        tags.put("appMetricId", 6);
        PrometheusMonitor.counter("alarm_test", tags);
    }
}
