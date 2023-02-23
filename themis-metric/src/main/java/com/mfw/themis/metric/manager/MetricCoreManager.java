package com.mfw.themis.metric.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mfw.themis.common.constant.MetricParamConstant;
import com.mfw.themis.common.constant.enums.CompareTypeEnum;
import com.mfw.themis.common.constant.enums.EnableEnum;
import com.mfw.themis.common.constant.enums.RuleStatusEnum;
import com.mfw.themis.common.convert.AlarmRuleConvert;
import com.mfw.themis.common.model.dto.AlarmRuleDTO;
import com.mfw.themis.common.model.dto.AppMetricDTO;
import com.mfw.themis.common.model.message.AlertMessage;
import com.mfw.themis.common.util.DebugHelper;
import com.mfw.themis.common.util.PlaceHolderUtils;
import com.mfw.themis.common.util.RuleCompareUtils;
import com.mfw.themis.dao.mapper.AlarmRuleDao;
import com.mfw.themis.dao.mapper.AppDao;
import com.mfw.themis.dao.po.AlarmRulePO;
import com.mfw.themis.dao.po.AppPO;
import com.mfw.themis.metric.event.MetricExecuteEvent;
import com.mfw.themis.metric.message.AlertMessageProducer;
import com.mfw.themis.metric.model.MetricCompareResult;
import com.mfw.themis.metric.model.MetricExecuteResult;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * 指标核心服务 负责指标采集后的计算与存储
 *
 * @author liuqi
 */
@Component
@Slf4j
public class MetricCoreManager {

    @Autowired
    private AlarmRuleDao alarmRuleDao;
    @Autowired
    private AlertMessageProducer producer;
    @Autowired
    private MetricRuleCacheManager metricRuleCacheManager;
    @Autowired
    private MetricCompareManager metricCompareManager;
    @Autowired
    private AppDao appDao;
    @Autowired
    private DebugHelper debugHelper;

    @Async
    @EventListener(value = MetricExecuteEvent.class)
    public void consumerMetricEvent(MetricExecuteEvent metricExecuteEvent) {
        AppMetricDTO appMetric = (AppMetricDTO) metricExecuteEvent.getSource();
        if (appMetric == null) {
            return;
        }
        if (metricExecuteEvent.getAlarmRule() != null) {
            compareResultSetWithRule(metricExecuteEvent);
        } else {
            List<AlarmRuleDTO> alarmRuleList = getSingleHitAlarmRuleListByAppMetricId(appMetric.getId());
            for (AlarmRuleDTO alarmRule : alarmRuleList) {
                metricExecuteEvent.setAlarmRule(alarmRule);
                compareResultSetWithRule(metricExecuteEvent);
            }
        }
    }

    /**
     * 比较结果集是否满足规则
     *
     * @param metricExecuteEvent 指标事件
     * @return 没有执行比较策略返回false
     */
    public boolean compareResultSetWithRule(MetricExecuteEvent metricExecuteEvent) {
        AlarmRuleDTO alarmRule = metricExecuteEvent.getAlarmRule();
        if (!RuleCompareUtils.compareRuleEffective(alarmRule, metricExecuteEvent.getExecuteTime())) {
            log.info("rule id:{} 不在报警时间范围内，execute time:{}", alarmRule.getId(),
                    DateFormatUtils.format(metricExecuteEvent.getExecuteTime(), "yyyy-MM-dd HH:mm:ss"));
            return false;
        }

        MetricExecuteEvent preEvent = metricRuleCacheManager.getPreEventFromCache(alarmRule.getId());
        // 比较指标和规则
        if (metricExecuteEvent.getResult() != null) {
            // 单一命中场景
            List<MetricCompareResult> results = compareSingleMetricValueWithRule(metricExecuteEvent.getResult(),
                    preEvent == null ? null : preEvent.getResult(), alarmRule,
                    preEvent == null ? null : preEvent.getAlarmRule());
            results.stream().filter(result -> RuleCompareUtils
                    .canAlertMessage(result.getCurrentStatus(), result.getOldStatus()))
                    .forEach(result -> sendAlertMessage(alarmRule, result, metricExecuteEvent.getExecuteTime()));
        } else if (metricExecuteEvent.getResultList() != null) {
            // 连续命中场景
            int continuousHitTimes = alarmRule.getContinuousHitTimes();
            if (debugHelper.isDebugMetricId(alarmRule.getAppMetricId())) {
                log.info("compare continuous hit times. rule id:{} ,time:{}", alarmRule.getId(), continuousHitTimes);
            }
            List<MetricCompareResult> totalResults = Lists.newArrayList();
            for (int i = 0; i < metricExecuteEvent.getResultList().size(); i++) {
                MetricExecuteResult preResult = null;
                if (preEvent != null && preEvent.getResultList() != null && preEvent.getResultList().size() > i) {
                    preResult = preEvent.getResultList().get(i);
                }
                List<MetricCompareResult> results = compareSingleMetricValueWithRule(
                        metricExecuteEvent.getResultList().get(i), preResult, alarmRule,
                        preEvent == null ? null : preEvent.getAlarmRule());
                totalResults.addAll(results);
            }
            // 转化为key:节点  value:报警结果list
            Map<String, List<MetricCompareResult>> compareRuleResultMap = totalResults.stream()
                    .collect(Collectors.groupingBy(MetricCompareResult::getEndPoint, Collectors.toList()));

            for (Entry<String, List<MetricCompareResult>> entry : compareRuleResultMap.entrySet()) {
                long errorCount = entry.getValue().stream()
                        .filter(result -> result.getCurrentStatus() == RuleStatusEnum.IN_ERROR).count();

                boolean oldFlag = entry.getValue().stream()
                        .anyMatch(result -> result.getOldStatus() == RuleStatusEnum.IN_ERROR);
                if (errorCount >= continuousHitTimes || oldFlag) {
                    sendAlertMessage(alarmRule, entry.getValue(), metricExecuteEvent.getExecuteTime());
                }
            }
        }
        // 缓存上次结果
        metricRuleCacheManager.cachePreEvent(metricExecuteEvent, alarmRule.getId());
        return true;
    }

    /**
     * 比较单一结果是否满足规则
     *
     * @param results   结果集
     * @param alarmRule 规则
     * @return
     */
    private List<MetricCompareResult> compareSingleMetricValueWithRule(
            MetricExecuteResult results, MetricExecuteResult preResults,
            AlarmRuleDTO alarmRule, AlarmRuleDTO preAlarmRule) {
        if (alarmRule.getCompare() == CompareTypeEnum.EXIST || alarmRule.getCompare() == CompareTypeEnum.NOT_EXIST) {
            return metricCompareManager.processExistTypeRules(results, alarmRule, preResults);
        } else {
            return metricCompareManager.processCompareTypeRules(results, alarmRule, preResults, preAlarmRule);
        }
    }

    /**
     * 发送报警
     *
     * @param alarmRule           报警规则
     * @param metricCompareResult 指标值
     * @param metricExecuteTime   执行时间
     */
    private void sendAlertMessage(AlarmRuleDTO alarmRule, MetricCompareResult metricCompareResult,
            Date metricExecuteTime) {
        Map<String, String> alertParam = buildAlertParam(alarmRule, metricCompareResult);

        String alertContent = null;
        if (StringUtils.isNotBlank(alarmRule.getAlarmContent())) {
            alertContent = PlaceHolderUtils.replace(alarmRule.getAlarmContent(), alertParam);
        }
        AlertMessage alertMessage = AlertMessage.builder()
                .alertTitle(alertParam.get(MetricParamConstant.METRIC_TITLE))
                .alertContent(alertContent)
                .alertTime(metricExecuteTime)
                .appMetricId(alarmRule.getAppMetricId())
                .currentMetricValue(metricCompareResult.getMetricValue().getValue())
                .moreUrl(StringUtils.EMPTY)
                .endPoint(metricCompareResult.getEndPoint())
                .ruleId(alarmRule.getId()).build();
        producer.sendAlertMessage(alertMessage);
    }

    /**
     * 发送报警
     *
     * @param alarmRule           报警规则
     * @param metricCompareResults 指标值
     * @param metricExecuteTime   执行时间
     */
    private void sendAlertMessage(AlarmRuleDTO alarmRule, List<MetricCompareResult> metricCompareResults,
            Date metricExecuteTime) {
        if (CollectionUtils.isEmpty(metricCompareResults)) {
            return;
        }
        MetricCompareResult metricCompareResult = metricCompareResults.get(0);
        Map<String, String> alertParam = buildAlertParam(alarmRule, metricCompareResult);

        String alertContent = null;
        if (StringUtils.isNotBlank(alarmRule.getAlarmContent())) {
            alertContent = PlaceHolderUtils.replace(alarmRule.getAlarmContent(), alertParam);
        }
        AlertMessage alertMessage = AlertMessage.builder()
                .alertTitle(alertParam.get(MetricParamConstant.METRIC_TITLE))
                .alertContent(alertContent)
                .alertTime(metricExecuteTime)
                .appMetricId(alarmRule.getAppMetricId())
                .currentMetricValues(metricCompareResults.stream().map(e -> e.getMetricValue().getValue())
                        .collect(Collectors.toList()))
                .moreUrl(StringUtils.EMPTY)
                .endPoint(metricCompareResult.getEndPoint())
                .ruleId(alarmRule.getId()).build();
        producer.sendAlertMessage(alertMessage);
    }

    private Map<String, String> buildAlertParam(AlarmRuleDTO alarmRule, MetricCompareResult metricCompareResult) {

        Map<String, String> alertParam = metricCompareResult.getMetricValue().getMetricExtInfo();
        if (alertParam == null) {
            alertParam = Maps.newHashMap();
        }
        AppPO appPO = appDao.selectById(alarmRule.getAppId());

        alertParam.put(MetricParamConstant.METRIC_ENDPOINT, metricCompareResult.getEndPoint());
        alertParam.put(MetricParamConstant.METRIC_THRESHOLD, alarmRule.getThreshold());
        alertParam.put(MetricParamConstant.METRIC_VALUE, metricCompareResult.getMetricValue().getValue());
        alertParam.put(MetricParamConstant.METRIC_APP_CODE, appPO.getAppCode());
        alertParam.put(MetricParamConstant.METRIC_TITLE, "[" + appPO.getAppName() + "]" + alarmRule.getRuleName());
        return alertParam;
    }

    /**
     * 获取绑定该metric对应的单一命中规则
     *
     * @param appMetricId
     * @return
     */
    private List<AlarmRuleDTO> getSingleHitAlarmRuleListByAppMetricId(Long appMetricId) {
        LambdaQueryWrapper<AlarmRulePO> wrapper = new QueryWrapper<AlarmRulePO>()
                .lambda().eq(AlarmRulePO::getAppMetricId, appMetricId)
                .eq(AlarmRulePO::getContinuousHitTimes, 1)
                .eq(AlarmRulePO::getStatus, EnableEnum.ENABLE.getCode())
                .eq(AlarmRulePO::getIsDelete, 0);
        return alarmRuleDao.selectList(wrapper).stream().map(AlarmRuleConvert::toDTO).collect(Collectors.toList());
    }


}
