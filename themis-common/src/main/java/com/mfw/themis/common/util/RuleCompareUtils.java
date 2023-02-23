package com.mfw.themis.common.util;

import com.mfw.themis.common.constant.enums.CompareTypeEnum;
import com.mfw.themis.common.constant.enums.RuleStatusEnum;
import com.mfw.themis.common.model.dto.AlarmRuleDTO;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalTime;

/**
 * 规则比较工具类
 *
 * @author liuqi
 */
public class RuleCompareUtils {

    public static boolean compareRule(List<String> metricValues, AlarmRuleDTO alarmLevelDTO) {
        boolean result = true;
        for (String metricValue : metricValues) {
            result = result && compareRule(metricValue, alarmLevelDTO);
        }
        return result;
    }

    /**
     * 比较执行器
     *
     * @param metricValue
     * @return true: inError  false: solve
     */
    public static boolean compareRule(String metricValue, AlarmRuleDTO alarmLevelDTO) {
        if (alarmLevelDTO == null) {
            throw new IllegalArgumentException();
        }
        if (alarmLevelDTO.getCompare() == CompareTypeEnum.EXIST) {
            return metricValue != null;
        }
        if (alarmLevelDTO.getCompare() == CompareTypeEnum.NOT_EXIST) {
            return metricValue == null;
        }
        if (metricValue == null) {
            throw new IllegalArgumentException();
        }
        BigDecimal metricDecimal = new BigDecimal(metricValue);
        BigDecimal thresholdDecimal = new BigDecimal(alarmLevelDTO.getThreshold());
        switch (alarmLevelDTO.getCompare()) {
            case EQ:
                return StringUtils.equals(metricValue, alarmLevelDTO.getThreshold());
            case GT:
                return metricDecimal.compareTo(thresholdDecimal) > 0;
            case LT:
                return metricDecimal.compareTo(thresholdDecimal) < 0;
            case GT_EQ:
                return metricDecimal.compareTo(thresholdDecimal) >= 0;
            case LT_EQ:
                return metricDecimal.compareTo(thresholdDecimal) <= 0;
            case NON_EQ:
                return !StringUtils.equals(metricValue, alarmLevelDTO.getThreshold());
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * 判断规则是否在有效期内
     *
     * @param alarmRule
     * @param executeTime
     * @return
     */
    public static boolean compareRuleEffective(AlarmRuleDTO alarmRule, Date executeTime) {
        if (alarmRule.getAlwaysEffective()) {
            return true;
        }
        LocalTime startTime = LocalTime.parse(alarmRule.getStartEffectiveTime());
        LocalTime endTime = LocalTime.parse(alarmRule.getEndEffectiveTime());
        LocalTime currentTime = LocalTime.fromDateFields(executeTime);
        return currentTime.compareTo(startTime) >= 0 && currentTime.compareTo(endTime) <= 0;
    }

    /**
     * 是否发送报警消息，发送报警消息需要两种条件：
     * 1.当前处于故障中，发送故障报警
     * 2.当前处于正常状态，上一次状态为故障中，发送恢复报警
     *
     * @return
     */
    public static boolean canAlertMessage(RuleStatusEnum currentStatus, RuleStatusEnum preStatus) {
        return currentStatus == RuleStatusEnum.IN_ERROR || preStatus == RuleStatusEnum.IN_ERROR;
    }
}
