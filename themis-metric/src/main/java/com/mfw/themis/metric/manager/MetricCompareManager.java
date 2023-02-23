package com.mfw.themis.metric.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mfw.themis.common.constant.enums.CompareTypeEnum;
import com.mfw.themis.common.constant.enums.RuleStatusEnum;
import com.mfw.themis.common.model.dto.AlarmRuleDTO;
import com.mfw.themis.common.util.DebugHelper;
import com.mfw.themis.common.util.RuleCompareUtils;
import com.mfw.themis.metric.model.MetricCompareResult;
import com.mfw.themis.metric.model.MetricExecuteResult;
import com.mfw.themis.metric.model.MetricValue;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 处理指标和规则的比较逻辑
 *
 * @author liuqi
 */
@Component
@Slf4j
public class MetricCompareManager {

    @Autowired
    private DebugHelper debugHelper;

    /**
     * 处理 存在/不存在 类型规则
     *
     * @param results    当前结果集
     * @param alarmRule  比较规则
     * @param preResults 上一次结果集
     * @return
     */
    public List<MetricCompareResult> processExistTypeRules(MetricExecuteResult results, AlarmRuleDTO alarmRule,
            MetricExecuteResult preResults) {
        Set<String> endPointSet = Sets.newHashSet();
        List<MetricCompareResult> list = Lists.newArrayList();
        boolean existCompare = alarmRule.getCompare() == CompareTypeEnum.EXIST;
        for (Entry<String, MetricValue> entry : results.getMetricValueMap().entrySet()) {
            boolean oldStatusExist =
                    preResults != null && preResults.getMetricValueMap().containsKey(entry.getKey());
            list.add(MetricCompareResult.builder()
                    .endPoint(entry.getKey())
                    .metricValue(entry.getValue())
                    .currentStatus(existCompare ? RuleStatusEnum.IN_ERROR : RuleStatusEnum.SOLVE)
                    .oldStatus((oldStatusExist == existCompare) ? RuleStatusEnum.IN_ERROR : RuleStatusEnum.SOLVE)
                    .build());
            endPointSet.add(entry.getKey());
        }
        if (preResults != null) {
            for (Entry<String, MetricValue> entry : preResults.getMetricValueMap().entrySet()) {
                if (endPointSet.contains(entry.getKey())) {
                    continue;
                }
                boolean curStatusExist = results.getMetricValueMap().containsKey(entry.getKey());
                entry.getValue().setValue(
                        curStatusExist ? results.getMetricValueMap().get(entry.getKey()).getValue() : null);
                list.add(MetricCompareResult.builder()
                        .endPoint(entry.getKey())
                        .metricValue(entry.getValue())
                        .currentStatus(curStatusExist == existCompare ? RuleStatusEnum.IN_ERROR : RuleStatusEnum.SOLVE)
                        .oldStatus(existCompare ? RuleStatusEnum.IN_ERROR : RuleStatusEnum.SOLVE)
                        .build());
            }
        }
        return list;
    }

    /**
     * 处理比较类型规则
     *
     * @param results      结果集
     * @param alarmRule    规则
     * @param preResults   上次结果集
     * @param preAlarmRule 上次规则
     * @return
     */
    public List<MetricCompareResult> processCompareTypeRules(MetricExecuteResult results, AlarmRuleDTO alarmRule,
            MetricExecuteResult preResults, AlarmRuleDTO preAlarmRule) {
        List<MetricCompareResult> list = Lists.newArrayList();
        for (Entry<String, MetricValue> entry : results.getMetricValueMap().entrySet()) {
            RuleStatusEnum oldStatus = Optional.ofNullable(preResults).map(MetricExecuteResult::getMetricValueMap)
                    .map(e -> e.get(entry.getKey())).map(MetricValue::getValue)
                    .map(e -> hitRuleToStatus(RuleCompareUtils.compareRule(e, preAlarmRule)))
                    .orElse(null);
            if (debugHelper.isDebugMetricId(alarmRule.getAppMetricId())) {
                log.info("metric results match alarmRuleDTO id:{}. endPoint:{}, currentValue:{}, compare:{}, threshold:{}",
                        alarmRule.getId(), entry.getKey(), entry.getValue().getValue(),alarmRule.getCompare().getDescription(), alarmRule.getThreshold());
            }
            RuleStatusEnum curStatus = hitRuleToStatus(
                    RuleCompareUtils.compareRule(entry.getValue().getValue(), alarmRule));

            list.add(MetricCompareResult.builder()
                    .endPoint(entry.getKey())
                    .metricValue(entry.getValue())
                    .currentStatus(curStatus)
                    .oldStatus(oldStatus)
                    .build());
        }
        return list;
    }

    /**
     * 命中规则转换为规则状态
     *
     * @param result 命中规则=true
     * @return
     */
    private RuleStatusEnum hitRuleToStatus(boolean result) {
        return result ? RuleStatusEnum.IN_ERROR : RuleStatusEnum.SOLVE;
    }
}
