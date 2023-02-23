package com.mfw.themis.metric.engine;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mfw.themis.common.constant.EngineConstant;
import com.mfw.themis.common.model.dto.AlarmDataSourceDTO;
import com.mfw.themis.common.util.DateFormatUtils;
import com.mfw.themis.common.util.DebugHelper;
import com.mfw.themis.dependent.exception.DependentCommunicateException;
import com.mfw.themis.dependent.prometheus.PrometheusClient;
import com.mfw.themis.dependent.prometheus.model.PrometheusRequest;
import com.mfw.themis.dependent.prometheus.model.PrometheusResponse.PrometheusResponseData;
import com.mfw.themis.dependent.prometheus.model.PrometheusResponse.PrometheusResponseResult;
import com.mfw.themis.metric.exception.MetricCollectException;
import com.mfw.themis.metric.helper.GroupMetricHelper;
import com.mfw.themis.metric.model.Metric;
import com.mfw.themis.metric.model.MetricExecuteResult;
import com.mfw.themis.metric.model.MetricValue;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author liuqi
 */
@Component
@Slf4j
public class PrometheusEngine extends AbstractExecuteEngine implements ExecuteEngine {

    @Autowired
    private PrometheusClient prometheusClient;
    @Autowired
    DebugHelper debugHelper;

    @Override
    public MetricExecuteResult executeSingleMetric(AlarmDataSourceDTO dataSource, Metric metric) {
        PrometheusRequest request = PrometheusRequest.builder()
                .query(metric.getExpression())
                .end(DateFormatUtils.formatToRfc3339(metric.getExecuteTime()))
                .start(DateFormatUtils
                        .formatToRfc3339(DateUtils.addMinutes(metric.getExecuteTime(), 0 - metric.getTimeWindow())))
                .build();
        MetricExecuteResult metricExecuteResult = new MetricExecuteResult();
        try {
            PrometheusResponseData responseData = prometheusClient.query(dataSource.getAddress(), request);
            if (debugHelper.isDebugMetricId(metric.getAppMetricId())) {
                log.info("debug prometheus. metric id:{},request:{},response:{}", metric, JSON.toJSON(request),
                        JSON.toJSONString(responseData));
            }
            String metricFieldKeyStr = MapUtils
                    .getString(metric.getMetricExtData(), EngineConstant.METRIC_FIELD_KEY);
            List<String> metricFieldKeys = null;
            if (StringUtils.isNotBlank(metricFieldKeyStr)) {
                metricFieldKeys = Arrays.stream(StringUtils.split(metricFieldKeyStr, ";"))
                        .collect(Collectors.toList());
            }
            // 返回数据
            Map<String, List<MetricValue>> metricValueMap = Maps.newHashMap();
            // 聚合数据
            Map<String, MetricValue> metricValueGroupMap = Maps.newHashMap();

            for (PrometheusResponseResult responseResult : responseData.getResult()) {
                String key = "";
                if (CollectionUtils.isNotEmpty(metricFieldKeys)) {
                    key = metricFieldKeys.stream().map(k -> MapUtils.getString(responseResult.getMetric(), k)).collect(
                            Collectors.joining(";"));
                } else if (StringUtils.isNotBlank(metric.getMetricName())) {
                    key = metric.getMetricName();
                }
                if (CollectionUtils.isNotEmpty(responseResult.getValue())) {
                    MetricValue metricValue = new MetricValue();
                    metricValue.setUnit(metric.getUnit());
                    Object timeString = responseResult.getValue().get(0);
                    BigDecimal timeStamp = null;
                    if (timeString instanceof Double) {
                        timeStamp = new BigDecimal((Double) timeString);
                    } else if (timeString instanceof Long) {
                        timeStamp = new BigDecimal((Long) timeString);
                    } else if (timeString instanceof String) {
                        timeStamp = new BigDecimal((String) timeString);
                    } else if (timeString instanceof Integer) {
                        timeStamp = new BigDecimal((Integer) timeString);
                    } else {
                        log.warn("timestamp can not identify. timeStamp:{}, object:{}", timeString,
                                timeString.getClass().getName());
                    }
                    try {
                        metricValue.setTimeStamp(timeStamp == null ? 0L : timeStamp.longValue());
                        BigDecimal value = convertWithUnit(new BigDecimal((String) responseResult.getValue().get(1)),
                                metric.getUnit());
                        metricValue.setValue(value.toString());
                        metricValue.setMetricExtInfo(responseResult.getMetric());
                        if (!metricValueMap.containsKey(key)) {
                            metricValueMap.put(key, Lists.newArrayList(metricValue));
                        } else {
                            metricValueMap.get(key).add(metricValue);
                        }
                    } catch (Exception e) {
                        log.error("execute error.metric" + JSON.toJSONString(metric) + "request:" + JSON.toJSON(request)
                                + " response:" + JSON.toJSONString(responseData), e);
                    }
                }
            }
            for (Entry<String, List<MetricValue>> entry : metricValueMap.entrySet()) {
                MetricValue metricValue = GroupMetricHelper
                        .groupMetricByGroupType(entry.getValue(), metric.getGroupType());
                metricValueGroupMap.put(entry.getKey(), metricValue);
            }
            metricExecuteResult.setMetricValueMap(metricValueGroupMap);
        } catch (DependentCommunicateException e) {
            throw new MetricCollectException(dataSource.getType(), dataSource.getId(), metric.getMetricName(),
                    metric.getAppMetricId());
        }
        return metricExecuteResult;
    }

    @Override
    public List<MetricExecuteResult> executeMultiSingleMetric(AlarmDataSourceDTO dataSource, Metric metric,
            Integer timeWindow, int times) {
        PrometheusRequest request = PrometheusRequest.builder()
                .query(metric.getExpression())
                .end(DateFormatUtils.formatToRfc3339(metric.getExecuteTime()))
                .start(DateFormatUtils.formatToRfc3339(
                        DateUtils.addMinutes(metric.getExecuteTime(), 0 - (metric.getTimeWindow() * (times - 1)))))
                .step(timeWindow + "m")
                .build();

        List<Map<String, List<MetricValue>>> metricValueMapList = Lists.newArrayList();
        List<MetricExecuteResult> metricExecuteResults = Lists.newArrayList();
        try {
            PrometheusResponseData responseData = prometheusClient.queryRange(dataSource.getAddress(), request);

            String metricFieldKey = MapUtils.getString(metric.getMetricExtData(), EngineConstant.METRIC_FIELD_KEY);
            // 构造数据结构
            for (int i = 0; i < times; i++) {
                Map<String, List<MetricValue>> metricValuesMap = Maps.newHashMap();
                Map<String, MetricValue> metricValueMap = Maps.newHashMap();
                MetricExecuteResult result = new MetricExecuteResult();
                result.setMetricValueMap(metricValueMap);
                metricValueMapList.add(metricValuesMap);
                metricExecuteResults.add(result);
            }
            for (PrometheusResponseResult responseResult : responseData.getResult()) {
                String key = "";
                if (metricFieldKey != null) {
                    key = MapUtils.getString(responseResult.getMetric(), metricFieldKey);
                } else if (StringUtils.isNotBlank(metric.getMetricName())) {
                    key = metric.getMetricName();
                }

                if (CollectionUtils.isNotEmpty(responseResult.getValues())) {
                    // 多节点
                    List<List<Object>> responseValues = responseResult.getValues();
                    for (int i = 0; i < responseValues.size(); i++) {
                        MetricValue metricValue = new MetricValue();
                        List<Object> responseResultValue = responseValues.get(i);
                        BigDecimal time = null;
                        if (responseResultValue.get(0) instanceof Double) {
                            time = new BigDecimal((Double) responseResultValue.get(0));
                        } else if (responseResultValue.get(0) instanceof Long) {
                            time = new BigDecimal((Long) responseResultValue.get(0));
                        } else {
                            time = new BigDecimal((String) responseResultValue.get(0));
                        }

                        metricValue.setTimeStamp(time.longValue());
                        BigDecimal value = convertWithUnit(new BigDecimal((String) responseResultValue.get(1)),
                                metric.getUnit());
                        metricValue.setValue(value.toString());
                        metricValue.setUnit(metric.getUnit());
                        metricValue.setMetricExtInfo(responseResult.getMetric());
                        Map<String, List<MetricValue>> metricValueMap = metricValueMapList.get(i);
                        if (metricValueMap.containsKey(key)) {
                            metricValueMap.get(key).add(metricValue);
                        } else {
                            metricValueMap.put(key, Lists.newArrayList(metricValue));
                        }
                    }
                }
            }
            // 进行聚合计算
            for (int i = 0; i < metricValueMapList.size(); i++) {
                Map<String, List<MetricValue>> metricValueMap = metricValueMapList.get(i);
                for (Entry<String, List<MetricValue>> entry : metricValueMap.entrySet()) {
                    MetricValue metricValue = GroupMetricHelper
                            .groupMetricByGroupType(entry.getValue(), metric.getGroupType());
                    metricExecuteResults.get(i).getMetricValueMap().put(entry.getKey(), metricValue);
                }
            }
        } catch (DependentCommunicateException e) {
            throw new MetricCollectException(dataSource.getType(), dataSource.getId(), metric.getMetricName(),
                    metric.getAppMetricId());
        }
        return metricExecuteResults;
    }

}
