package com.mfw.themis.metric.engine;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.mfw.themis.common.constant.enums.AlarmMetricUnitEnum;
import com.mfw.themis.common.model.dto.AlarmDataSourceDTO;
import com.mfw.themis.metric.model.Metric;
import com.mfw.themis.metric.model.MetricExecuteResult;
import com.mfw.themis.metric.model.MetricValue;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.CollectionUtils;

/**
 * @author liuqi
 */
public abstract class AbstractExecuteEngine implements ExecuteEngine {

    /**
     * 执行复合指标
     * @param dataSource        数据源
     * @param singleMetrics     单指标
     * @param compositeMetric   复合指标
     * @return
     */
    @Override
    public MetricExecuteResult executeCompositeMetric(AlarmDataSourceDTO dataSource,
            List<Metric> singleMetrics, Metric compositeMetric) {

        if (CollectionUtils.isEmpty(singleMetrics) || null == compositeMetric) {
            return null;
        }

        // <节点名称, <参数名称，指标值>>
        Map<String, Map<String, Object>> compositeMetricMap = Maps.newHashMap();
        Map<String,List<MetricValue>> metricExtInfoMap = Maps.newHashMap();
        AlarmMetricUnitEnum unit = singleMetrics.get(0).getUnit();
        for (Metric metric : singleMetrics) {
            MetricExecuteResult metricExecuteResult = executeSingleMetric(dataSource, metric);
            metricExecuteResult.getMetricValueMap().forEach((key, val) -> {
                if (compositeMetricMap.containsKey(key)) {
                    BigDecimal value = new BigDecimal(val.getValue());
                    compositeMetricMap.get(key).put(metric.getMetricName(), value);
                    metricExtInfoMap.get(key).add(val);
                } else {
                    Map<String, Object> map = Maps.newHashMap();
                    BigDecimal value = new BigDecimal(val.getValue());
                    map.put(metric.getMetricName(), value);
                    compositeMetricMap.put(key, map);
                    List<MetricValue> list = Lists.newArrayList();
                    list.add(val);
                    metricExtInfoMap.put(key, list);
                }
            });
        }

        Expression expression = AviatorEvaluator.compile(compositeMetric.getFormula());
        MetricExecuteResult result = new MetricExecuteResult();
        Map<String, MetricValue> metricMap = Maps.newHashMap();
        result.setMetricValueMap(metricMap);

        for (Entry<String, Map<String, Object>> entry : compositeMetricMap.entrySet()) {
            MetricValue metricValue = new MetricValue();
            BigDecimal calResult = (BigDecimal) expression.execute(entry.getValue());
            calResult = convertWithUnit(calResult, unit);
            metricValue.setValue(calResult.toString());
            metricValue.setTimeStamp(System.currentTimeMillis());
            Map<String, String> extMap = Maps.newHashMap();
            extMap.put(entry.getKey(),JSON.toJSONString(metricExtInfoMap.get(entry.getKey())));
            metricValue.setMetricExtInfo(extMap);
            metricMap.put(entry.getKey(), metricValue);
        }
        return result;
    }

    /**
     * 默认连续窗口实现方式
     *
     * @param metric     指标
     * @param timeWindow 时间窗口
     * @param times      执行次数
     * @return
     */
    @Override
    public List<MetricExecuteResult> executeMultiSingleMetric(AlarmDataSourceDTO dataSource, Metric metric,
            Integer timeWindow,
            int times) {
        List<MetricExecuteResult> resultList = Lists.newArrayList();
        for (int i = 0; i < times; i++) {
            metric.setExecuteTime(DateUtils.addMinutes(metric.getExecuteTime(), i * (0 - timeWindow)));
            resultList.add(executeSingleMetric(dataSource, metric));
        }
        return resultList;
    }

    /**
     * 复合指标连续窗口实现方式
     *
     * @param dataSource        数据源
     * @param singleMetrics     单指标
     * @param compositeMetric   复合指标
     * @param times             执行次数
     * @return
     */
    @Override
    public List<MetricExecuteResult> executeMultiCompositeMetric(AlarmDataSourceDTO dataSource,
            List<Metric> singleMetrics, Metric compositeMetric, int times) {
        List<MetricExecuteResult> resultList = Lists.newArrayList();
        for (int i = 0; i < times; i++) {
            for (Metric metric : singleMetrics) {
                metric.setExecuteTime(DateUtils.addMinutes(metric.getExecuteTime(), i * (0 - metric.getTimeWindow())));
            }
            resultList.add(executeCompositeMetric(dataSource, singleMetrics, compositeMetric));
        }
        return resultList;
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

}
