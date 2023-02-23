package com.mfw.themis.metric;

import com.google.common.collect.Maps;
import com.mfw.themis.common.constant.enums.AlarmMetricUnitEnum;
import com.mfw.themis.common.constant.enums.CompareTypeEnum;
import com.mfw.themis.common.constant.enums.EnableEnum;
import com.mfw.themis.common.constant.enums.RuleStatusEnum;
import com.mfw.themis.common.model.dto.AlarmRuleDTO;
import com.mfw.themis.metric.manager.MetricCompareManager;
import com.mfw.themis.metric.model.MetricCompareResult;
import com.mfw.themis.metric.model.MetricExecuteResult;
import com.mfw.themis.metric.model.MetricValue;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 比较方式单测
 */
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class MetricCompareTest {

    @InjectMocks
    private MetricCompareManager metricCompareManager;

    @Test
    public void testGT() {
        // 判断25>10  5>10
        String endPoint = "10.10.10.10";
        AlarmRuleDTO rule = buildMetricRule(CompareTypeEnum.GT, "10");
        MetricExecuteResult result = buildMetricResult(endPoint, "25");
        MetricExecuteResult preResult = buildMetricResult(endPoint, "5");

        List<MetricCompareResult> compareRuleResults = metricCompareManager
                .processCompareTypeRules(result, rule, preResult, rule);
        MetricCompareResult compareRuleResult = compareRuleResults.stream()
                .filter(r -> StringUtils.equals(r.getEndPoint(), endPoint)).findAny().get();
        Assert.assertNotNull(compareRuleResult);
        Assert.assertEquals(compareRuleResult.getCurrentStatus(), RuleStatusEnum.IN_ERROR);
        Assert.assertEquals(compareRuleResult.getOldStatus(), RuleStatusEnum.SOLVE);

        //10 >10
        result = buildMetricResult(endPoint, "10");
        preResult = buildMetricResult(endPoint, "10");

        compareRuleResults = metricCompareManager
                .processCompareTypeRules(result, rule, preResult, rule);
        compareRuleResult = compareRuleResults.stream()
                .filter(r -> StringUtils.equals(r.getEndPoint(), endPoint)).findAny().get();
        Assert.assertNotNull(compareRuleResult);
        Assert.assertEquals(compareRuleResult.getCurrentStatus(), RuleStatusEnum.SOLVE);
        Assert.assertEquals(compareRuleResult.getOldStatus(), RuleStatusEnum.SOLVE);

        //10>=10
        rule.setCompare(CompareTypeEnum.GT_EQ);
        compareRuleResults = metricCompareManager.processCompareTypeRules(result, rule, preResult, rule);
        compareRuleResult = compareRuleResults.stream()
                .filter(r -> StringUtils.equals(r.getEndPoint(), endPoint)).findAny().get();
        Assert.assertNotNull(compareRuleResult);
        Assert.assertEquals(compareRuleResult.getCurrentStatus(), RuleStatusEnum.IN_ERROR);
        Assert.assertEquals(compareRuleResult.getOldStatus(), RuleStatusEnum.IN_ERROR);

        //25 >=10 5>=10
        result = buildMetricResult(endPoint, "25");
        preResult = buildMetricResult(endPoint, "5");
        compareRuleResults = metricCompareManager.processCompareTypeRules(result, rule, preResult, rule);
        compareRuleResult = compareRuleResults.stream()
                .filter(r -> StringUtils.equals(r.getEndPoint(), endPoint)).findAny().get();
        Assert.assertNotNull(compareRuleResult);
        Assert.assertEquals(compareRuleResult.getCurrentStatus(), RuleStatusEnum.IN_ERROR);
        Assert.assertEquals(compareRuleResult.getOldStatus(), RuleStatusEnum.SOLVE);
    }

    @Test
    public void testLT() {
        String endPoint = "10.10.10.10";
        // 100<10 25<10
        AlarmRuleDTO rule = buildMetricRule(CompareTypeEnum.LT, "10");
        MetricExecuteResult result = buildMetricResult(endPoint, "100");
        MetricExecuteResult preResult = buildMetricResult(endPoint, "25");
        List<MetricCompareResult> compareRuleResults = metricCompareManager
                .processCompareTypeRules(result, rule, preResult, rule);
        MetricCompareResult compareRuleResult = compareRuleResults.stream()
                .filter(r -> StringUtils.equals(r.getEndPoint(), endPoint)).findAny().get();
        Assert.assertNotNull(compareRuleResult);
        Assert.assertEquals(compareRuleResult.getCurrentStatus(), RuleStatusEnum.SOLVE);
        Assert.assertEquals(compareRuleResult.getOldStatus(), RuleStatusEnum.SOLVE);

        // 1<10  10<10
        result = buildMetricResult(endPoint, "1");
        preResult = buildMetricResult(endPoint, "10");
        compareRuleResults = metricCompareManager
                .processCompareTypeRules(result, rule, preResult, rule);
        compareRuleResult = compareRuleResults.stream()
                .filter(r -> StringUtils.equals(r.getEndPoint(), endPoint)).findAny().get();
        Assert.assertNotNull(compareRuleResult);
        Assert.assertEquals(compareRuleResult.getCurrentStatus(), RuleStatusEnum.IN_ERROR);
        Assert.assertEquals(compareRuleResult.getOldStatus(), RuleStatusEnum.SOLVE);

        // 1<=10  10<=10
        rule.setCompare(CompareTypeEnum.LT_EQ);
        compareRuleResults = metricCompareManager
                .processCompareTypeRules(result, rule, preResult, rule);
        compareRuleResult = compareRuleResults.stream()
                .filter(r -> StringUtils.equals(r.getEndPoint(), endPoint)).findAny().get();
        Assert.assertNotNull(compareRuleResult);
        Assert.assertEquals(compareRuleResult.getCurrentStatus(), RuleStatusEnum.IN_ERROR);
        Assert.assertEquals(compareRuleResult.getOldStatus(), RuleStatusEnum.IN_ERROR);

        // 100<=10 25<=10
        result = buildMetricResult(endPoint, "100");
        preResult = buildMetricResult(endPoint, "25");
        compareRuleResults = metricCompareManager
                .processCompareTypeRules(result, rule, preResult, rule);
        compareRuleResult = compareRuleResults.stream()
                .filter(r -> StringUtils.equals(r.getEndPoint(), endPoint)).findAny().get();
        Assert.assertNotNull(compareRuleResult);
        Assert.assertEquals(compareRuleResult.getCurrentStatus(), RuleStatusEnum.SOLVE);
        Assert.assertEquals(compareRuleResult.getOldStatus(), RuleStatusEnum.SOLVE);

    }

    @Test
    public void testEQAndNEQ() {
        String endPoint = "10.10.10.10";
        AlarmRuleDTO rule = buildMetricRule(CompareTypeEnum.EQ, "10");
        MetricExecuteResult result = buildMetricResult(endPoint, "10");
        MetricExecuteResult preResult = buildMetricResult(endPoint, "25");
        List<MetricCompareResult> compareRuleResults = metricCompareManager
                .processCompareTypeRules(result, rule, preResult, rule);
        MetricCompareResult compareRuleResult = compareRuleResults.stream()
                .filter(r -> StringUtils.equals(r.getEndPoint(), endPoint)).findAny().get();
        Assert.assertNotNull(compareRuleResult);
        Assert.assertEquals(compareRuleResult.getCurrentStatus(), RuleStatusEnum.IN_ERROR);
        Assert.assertEquals(compareRuleResult.getOldStatus(), RuleStatusEnum.SOLVE);

        rule.setCompare(CompareTypeEnum.NON_EQ);
        compareRuleResults = metricCompareManager
                .processCompareTypeRules(result, rule, preResult, rule);
        compareRuleResult = compareRuleResults.stream()
                .filter(r -> StringUtils.equals(r.getEndPoint(), endPoint)).findAny().get();
        Assert.assertNotNull(compareRuleResult);
        Assert.assertEquals(compareRuleResult.getCurrentStatus(), RuleStatusEnum.SOLVE);
        Assert.assertEquals(compareRuleResult.getOldStatus(), RuleStatusEnum.IN_ERROR);
    }

    private AlarmRuleDTO buildMetricRule(CompareTypeEnum compare, String threshold) {
        return AlarmRuleDTO.builder()
                .id(1L)
                .appId(1L)
                .appMetricId(1L)
                .status(EnableEnum.ENABLE)
                .threshold(threshold)
                .startEffectiveTime("")
                .endEffectiveTime("")
                .alwaysEffective(true)
                .ruleName("")
                .metricName("")
                .continuousHitTimes(1)
                .contacts("2610")
                .compare(compare)
                .alarmContent("")
                .alarmLevelId(1L)
                .isDelete(false)
                .build();
    }

    private MetricExecuteResult buildMetricResult(String key, String value) {
        MetricExecuteResult result = new MetricExecuteResult();
        Map<String, MetricValue> map = Maps.newHashMap();
        MetricValue metricValue = new MetricValue();
        metricValue.setTimeStamp(System.currentTimeMillis());
        metricValue.setUnit(AlarmMetricUnitEnum.NONE);
        metricValue.setValue(value);
        map.put(key, metricValue);
        result.setMetricValueMap(map);
        return result;
    }

    /**
     * 指标单位转换
     *
     * @param bigDecimal 指标值
     * @param unit       指标单位
     * @return
     */
    protected BigDecimal convertWithUnit(BigDecimal bigDecimal, AlarmMetricUnitEnum unit) {
        if (bigDecimal == null) {
            return null;
        }
        switch (unit) {
            case GB:
                bigDecimal = bigDecimal.divide(new BigDecimal(1024 * 1024 * 1024), 1, BigDecimal.ROUND_HALF_UP);
                break;
            case MB:
                bigDecimal = bigDecimal.divide(new BigDecimal(1024 * 1024), 1, BigDecimal.ROUND_HALF_UP);
                break;
            case PERCENTAGE:
                bigDecimal = bigDecimal.multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                break;
            case MILL_SECOND:
                bigDecimal = bigDecimal.multiply(new BigDecimal(1000));
            case STRING:
            case NONE:
            default:
                bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return bigDecimal;
    }

    @Test
    public void decimalScaleTest(){
        BigDecimal big = new BigDecimal(2.3333);
        Assert.assertEquals(convertWithUnit(big, AlarmMetricUnitEnum.NONE).toString(), "2.33");

        big = new BigDecimal(5.35554);
        Assert.assertEquals(convertWithUnit(big, AlarmMetricUnitEnum.NONE).toString(), "5.36");

        big = new BigDecimal(0.22222);
        Assert.assertEquals(convertWithUnit(big, AlarmMetricUnitEnum.NONE).toString(), "0.22");
    }

}
