package com.mfw.themis.metric.handler;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mfw.themis.common.constant.enums.EnableEnum;
import com.mfw.themis.common.convert.AlarmRuleConvert;
import com.mfw.themis.common.model.message.AlertMessage;
import com.mfw.themis.common.util.RuleCompareUtils;
import com.mfw.themis.dao.mapper.AlarmRuleDao;
import com.mfw.themis.dao.po.AlarmRulePO;
import com.mfw.themis.metric.message.AlertMessageProducer;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 增加线程运行异常报警
 *
 * @author liuqi
 */
@Component
@Slf4j
public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${themis.metric.rule_id}")
    private Long ruleId;

    @Autowired
    private AlarmRuleDao alarmRuleDao;

    @Autowired
    private AlertMessageProducer producer;
    // 缓存规则
    private LoadingCache<Long, AlarmRulePO> cache = CacheBuilder.newBuilder()
            .initialCapacity(1)
            .maximumSize(1)
            .expireAfterWrite(4, TimeUnit.HOURS)
            .build(new CacheLoader<Long, AlarmRulePO>() {
                @Override
                public AlarmRulePO load(Long aLong) throws Exception {
                    return new AlarmRulePO();
                }
            });

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("themis-metric指标计算异常", e);

        try {
            AlarmRulePO alarmRule = cache.get(ruleId);
            if (alarmRule.getId() == null) {
                alarmRule = alarmRuleDao.selectById(ruleId);
                cache.put(ruleId, alarmRule);
            }

            // 禁用不发告警
            if(alarmRule.getStatus().equals(EnableEnum.DISABLE.getCode())){
                return;
            }

            Date now = new Date();
            if (!RuleCompareUtils.compareRuleEffective(AlarmRuleConvert.toDTO(alarmRule), now)) {
                log.info("rule id:{} 不在报警时间范围内，execute time:{}", ruleId,
                        DateFormatUtils.format(now, "yyyy-MM-dd HH:mm:ss"));
                return;
            }
        } catch (Exception ex) {
            log.warn(e.getMessage(), e);
        }
        AlertMessage alertMessage = AlertMessage.builder()
                .alertTitle("themis-metric 指标计算异常")
                .alertContent(e.getMessage() == null ? e.toString() : e.getMessage())
                .alertTime(new Date())
                .currentMetricValue("1")
                .moreUrl(StringUtils.EMPTY)
                .ruleId(ruleId).build();

        producer.sendAlertMessage(alertMessage);
    }
}
