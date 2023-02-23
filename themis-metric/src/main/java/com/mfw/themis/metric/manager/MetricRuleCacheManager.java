package com.mfw.themis.metric.manager;

import com.alibaba.fastjson.JSON;
import com.mfw.themis.metric.event.MetricExecuteEvent;
import java.time.Duration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 缓存规则的报警状态
 *
 * @author liuqi
 */
@Component
public class MetricRuleCacheManager {

    private static final String METRIC_STATUS_PREFIX = "metric-status:";
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 缓存事件作为下次报警的对照
     *
     * @param metricExecuteEvent 指标事件
     * @param ruleId             规则id
     */
    public void cachePreEvent(MetricExecuteEvent metricExecuteEvent, Long ruleId) {
        redisTemplate.opsForValue().set(getMetricStatusKey(ruleId), JSON.toJSONString(metricExecuteEvent),
                Duration.ofMinutes(10));
    }

    /**
     * 获取缓存事件
     *
     * @param ruleId 规则id
     * @return
     */
    public MetricExecuteEvent getPreEventFromCache(Long ruleId) {
        String str = redisTemplate.opsForValue().get(getMetricStatusKey(ruleId));
        if (StringUtils.isNotBlank(str)) {
            return JSON.parseObject(str, MetricExecuteEvent.class);
        }
        return null;
    }

    private String getMetricStatusKey(Long ruleId) {
        return METRIC_STATUS_PREFIX + ruleId;
    }
}
