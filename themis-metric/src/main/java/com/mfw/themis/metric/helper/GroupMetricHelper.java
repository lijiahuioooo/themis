package com.mfw.themis.metric.helper;

import com.mfw.themis.common.constant.enums.GroupTypeEnum;
import com.mfw.themis.metric.model.MetricValue;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.CollectionUtils;

/**
 * 指标结果聚合计算
 *
 * @author liuqi
 */
public class GroupMetricHelper {

    public static MetricValue groupMetricByGroupType(List<MetricValue> metricValues, GroupTypeEnum groupType) {
        if (CollectionUtils.isEmpty(metricValues)) {
            return null;
        }
        MetricValue metricValue = metricValues.get(0);
        List<BigDecimal> valueList = metricValues.stream().map(m -> new BigDecimal(m.getValue()))
                .collect(Collectors.toList());
        switch (groupType) {
            case COUNT:
                metricValue.setValue(Integer.toString(valueList.size()));
                break;
            case SUM:
                metricValue.setValue(
                        Long.toString(valueList.stream().mapToLong(BigDecimal::longValue).sum()));
                break;
            case MIN:
                metricValue.setValue(
                        valueList.stream().min(BigDecimal::compareTo).orElse(new BigDecimal("0")).toString());
                break;
            case AVG:
                Double avg = valueList.stream().collect(Collectors.averagingLong(BigDecimal::longValue));
                metricValue.setValue(Double.toString(avg));
                break;
            case MAX:
                metricValue.setValue(
                        valueList.stream().max(BigDecimal::compareTo).orElse(new BigDecimal("0")).toString());
                break;
            case NONE:
            default:
        }
        return metricValue;
    }
}
