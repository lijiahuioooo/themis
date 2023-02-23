package com.mfw.themis.metric;

import com.google.common.collect.Lists;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 存在/不存在的比较方式单测
 */
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class MetricExistTest {

    private String endPoint1 = "10.10.10.10";
    private String endPoint2 = "10.10.10.11";
    private String endPoint3 = "10.10.10.12";
    private String endPoint4 = "10.10.10.13";
    private String endPoint5 = "10.10.10.14";
    private String endPoint6 = "10.10.10.15";
    @InjectMocks
    private MetricCompareManager metricCompareManager;

    @Test
    public void testExist() {

        MetricExecuteResult result = buildMetricResult(endPoint1, "25");
        AlarmRuleDTO rule = buildMetricRule(CompareTypeEnum.EXIST, "");

        List<MetricCompareResult> compareRuleResults = metricCompareManager
                .processExistTypeRules(result, rule, result);
        MetricCompareResult compareRuleResult = compareRuleResults.stream()
                .filter(r -> StringUtils.equals(r.getEndPoint(), endPoint1)).findAny().get();
        Assert.assertEquals(compareRuleResult.getCurrentStatus(), RuleStatusEnum.IN_ERROR);
        Assert.assertEquals(compareRuleResult.getOldStatus(), RuleStatusEnum.IN_ERROR);

        result= buildEmptyMetricResult();
        MetricExecuteResult preResult = buildMetricResult(endPoint2, "25");
        compareRuleResults = metricCompareManager.processExistTypeRules(result, rule, preResult);
        compareRuleResult = compareRuleResults.stream()
                .filter(r -> StringUtils.equals(r.getEndPoint(), endPoint2)).findAny().get();
        Assert.assertEquals(compareRuleResult.getCurrentStatus(), RuleStatusEnum.SOLVE);
        Assert.assertEquals(compareRuleResult.getOldStatus(), RuleStatusEnum.IN_ERROR);

    }

    /**
     * 两次指标
     * 当前是endPoint1
     * 上一次是endPoint2
     */
    @Test
    public void testExist1() {
        MetricExecuteResult result = buildMetricResult(endPoint1, "25");
        MetricExecuteResult preResult = buildMetricResult(endPoint2, "25");
        AlarmRuleDTO rule = buildMetricRule(CompareTypeEnum.EXIST, "");

        List<MetricCompareResult> compareRuleResults = metricCompareManager
                .processExistTypeRules(result, rule, preResult);

        MetricCompareResult compareRuleResult = compareRuleResults.stream()
                .filter(r -> StringUtils.equals(r.getEndPoint(), endPoint1)).findAny().get();
        Assert.assertEquals(compareRuleResult.getCurrentStatus(), RuleStatusEnum.IN_ERROR);
        Assert.assertEquals(compareRuleResult.getOldStatus(), RuleStatusEnum.SOLVE);

        compareRuleResult = compareRuleResults.stream()
                .filter(r -> StringUtils.equals(r.getEndPoint(), endPoint2)).findAny().get();
        Assert.assertEquals(compareRuleResult.getCurrentStatus(), RuleStatusEnum.SOLVE);
        Assert.assertEquals(compareRuleResult.getOldStatus(), RuleStatusEnum.IN_ERROR);
    }

    /**
     * 两次指标
     * 当前是endPoint1，endPoint2，endPoint3，endPoint5
     * 上一次是endPoint1，endPoint2，endPoint6
     */
    @Test
    public void testExist2() {
        MetricExecuteResult result = buildMetricResult(Lists.newArrayList(endPoint1, endPoint2, endPoint3, endPoint5), "10");
        MetricExecuteResult preResult = buildMetricResult(Lists.newArrayList(endPoint1, endPoint2, endPoint6), "10");
        AlarmRuleDTO rule = buildMetricRule(CompareTypeEnum.EXIST, "");

        List<MetricCompareResult> compareRuleResults = metricCompareManager
                .processExistTypeRules(result, rule, preResult);
        Map<String, MetricCompareResult> resultMap = compareRuleResults.stream()
                .collect(Collectors.toMap(MetricCompareResult::getEndPoint, e -> e));
        Assert.assertEquals(resultMap.get(endPoint1).getCurrentStatus(), RuleStatusEnum.IN_ERROR);
        Assert.assertEquals(resultMap.get(endPoint1).getOldStatus(), RuleStatusEnum.IN_ERROR);
        Assert.assertEquals(resultMap.get(endPoint2).getCurrentStatus(), RuleStatusEnum.IN_ERROR);
        Assert.assertEquals(resultMap.get(endPoint2).getOldStatus(), RuleStatusEnum.IN_ERROR);
        Assert.assertEquals(resultMap.get(endPoint3).getCurrentStatus(), RuleStatusEnum.IN_ERROR);
        Assert.assertEquals(resultMap.get(endPoint3).getOldStatus(), RuleStatusEnum.SOLVE);
        Assert.assertNull(resultMap.get(endPoint4));
        Assert.assertEquals(resultMap.get(endPoint5).getCurrentStatus(), RuleStatusEnum.IN_ERROR);
        Assert.assertEquals(resultMap.get(endPoint5).getOldStatus(), RuleStatusEnum.SOLVE);
        Assert.assertEquals(resultMap.get(endPoint6).getCurrentStatus(), RuleStatusEnum.SOLVE);
        Assert.assertEquals(resultMap.get(endPoint6).getOldStatus(), RuleStatusEnum.IN_ERROR);
    }

    @Test
    public void testExist3() {
        MetricExecuteResult result = buildEmptyMetricResult();
        MetricExecuteResult preResult = buildMetricResult(Lists.newArrayList(endPoint2), "10");
        AlarmRuleDTO rule = buildMetricRule(CompareTypeEnum.EXIST, "");

        List<MetricCompareResult> compareRuleResults = metricCompareManager
                .processExistTypeRules(result, rule, preResult);
        MetricCompareResult compareRuleResult = compareRuleResults.stream()
                .filter(r -> StringUtils.equals(r.getEndPoint(), endPoint2)).findAny().get();
        Assert.assertEquals(compareRuleResult.getCurrentStatus(), RuleStatusEnum.SOLVE);
        Assert.assertEquals(compareRuleResult.getOldStatus(), RuleStatusEnum.IN_ERROR);
    }

    @Test
    public void testExist4() {
        MetricExecuteResult result = buildMetricResult(endPoint1, "25");
        AlarmRuleDTO rule = buildMetricRule(CompareTypeEnum.EXIST, "");

        List<MetricCompareResult> compareRuleResults = metricCompareManager.processExistTypeRules(result, rule, null);
        MetricCompareResult compareRuleResult = compareRuleResults.stream()
                .filter(r -> StringUtils.equals(r.getEndPoint(), endPoint1))
                .findAny().get();
        Assert.assertEquals(compareRuleResult.getCurrentStatus(), RuleStatusEnum.IN_ERROR);
        Assert.assertEquals(compareRuleResult.getOldStatus(), RuleStatusEnum.SOLVE);
    }

    @Test
    public void testNotExist() {
        MetricExecuteResult result = buildMetricResult(endPoint1, "25");
        MetricExecuteResult preResult = buildMetricResult(endPoint2, "25");
        AlarmRuleDTO rule = buildMetricRule(CompareTypeEnum.NOT_EXIST, "");

        List<MetricCompareResult> compareRuleResults = metricCompareManager
                .processExistTypeRules(result, rule, result);
        MetricCompareResult compareRuleResult = compareRuleResults.stream()
                .filter(r -> StringUtils.equals(r.getEndPoint(), endPoint1)).findAny().get();
        Assert.assertEquals(compareRuleResult.getCurrentStatus(), RuleStatusEnum.SOLVE);
        Assert.assertEquals(compareRuleResult.getOldStatus(), RuleStatusEnum.SOLVE);

        compareRuleResults = metricCompareManager
                .processExistTypeRules(result, rule, preResult);
        compareRuleResult = compareRuleResults.stream()
                .filter(r -> StringUtils.equals(r.getEndPoint(), endPoint1)).findAny().get();
        Assert.assertEquals(compareRuleResult.getCurrentStatus(), RuleStatusEnum.SOLVE);
        Assert.assertEquals(compareRuleResult.getOldStatus(), RuleStatusEnum.IN_ERROR);


    }

    @Test
    public void testNotExist1() {
        MetricExecuteResult result = buildMetricResult(Lists.newArrayList(endPoint1, endPoint2, endPoint3, endPoint5), "10");
        MetricExecuteResult preResult = buildMetricResult(Lists.newArrayList(endPoint1, endPoint2, endPoint6), "10");
        AlarmRuleDTO rule = buildMetricRule(CompareTypeEnum.NOT_EXIST, "");

        List<MetricCompareResult> compareRuleResults = metricCompareManager
                .processExistTypeRules(result, rule, preResult);
        Map<String, MetricCompareResult> resultMap = compareRuleResults.stream()
                .collect(Collectors.toMap(MetricCompareResult::getEndPoint, e -> e));
        Assert.assertEquals(resultMap.get(endPoint1).getCurrentStatus(), RuleStatusEnum.SOLVE);
        Assert.assertEquals(resultMap.get(endPoint1).getOldStatus(), RuleStatusEnum.SOLVE);
        Assert.assertEquals(resultMap.get(endPoint2).getCurrentStatus(), RuleStatusEnum.SOLVE);
        Assert.assertEquals(resultMap.get(endPoint2).getOldStatus(), RuleStatusEnum.SOLVE);
        Assert.assertEquals(resultMap.get(endPoint3).getCurrentStatus(), RuleStatusEnum.SOLVE);
        Assert.assertEquals(resultMap.get(endPoint3).getOldStatus(), RuleStatusEnum.IN_ERROR);
        Assert.assertNull(resultMap.get(endPoint4));
        Assert.assertEquals(resultMap.get(endPoint5).getCurrentStatus(), RuleStatusEnum.SOLVE);
        Assert.assertEquals(resultMap.get(endPoint5).getOldStatus(), RuleStatusEnum.IN_ERROR);
        Assert.assertEquals(resultMap.get(endPoint6).getCurrentStatus(), RuleStatusEnum.IN_ERROR);
        Assert.assertEquals(resultMap.get(endPoint6).getOldStatus(), RuleStatusEnum.SOLVE);
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

    private MetricExecuteResult buildMetricResult(List<String> keys, String value) {
        MetricExecuteResult result = new MetricExecuteResult();
        Map<String, MetricValue> map = Maps.newHashMap();
        MetricValue metricValue = new MetricValue();
        metricValue.setTimeStamp(System.currentTimeMillis());
        metricValue.setUnit(AlarmMetricUnitEnum.NONE);
        metricValue.setValue(value);
        for (String key : keys) {
            map.put(key, metricValue);
        }
        result.setMetricValueMap(map);
        return result;
    }

    private MetricExecuteResult buildEmptyMetricResult() {
        MetricExecuteResult result = new MetricExecuteResult();
        Map<String, MetricValue> map = Maps.newHashMap();
        result.setMetricValueMap(map);
        return result;
    }
}
