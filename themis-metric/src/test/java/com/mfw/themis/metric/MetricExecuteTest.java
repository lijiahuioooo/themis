package com.mfw.themis.metric;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mfw.themis.common.constant.enums.AlarmMetricUnitEnum;
import com.mfw.themis.common.constant.enums.DataSourceTypeEnum;
import com.mfw.themis.common.constant.enums.GroupTypeEnum;
import com.mfw.themis.common.model.dto.AlarmDataSourceDTO;
import com.mfw.themis.dependent.exception.DependentCommunicateException;
import com.mfw.themis.dependent.prometheus.PrometheusClient;
import com.mfw.themis.dependent.prometheus.model.PrometheusResponse.PrometheusResponseData;
import com.mfw.themis.dependent.prometheus.model.PrometheusResponse.PrometheusResponseResult;
import com.mfw.themis.metric.engine.ElasticSearchEngine;
import com.mfw.themis.metric.engine.PrometheusEngine;
import com.mfw.themis.metric.model.Metric;
import com.mfw.themis.metric.model.MetricExecuteResult;

import com.mfw.themis.metric.model.MetricValue;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class MetricExecuteTest {

    @InjectMocks
    private PrometheusEngine prometheusEngine;
    @Mock
    private PrometheusClient prometheusClient;

    @InjectMocks
    private ElasticSearchEngine elasticSearchEngine;

    @Test
    public void testSingleMetricEs() throws DependentCommunicateException {
        // 构造指标
        Metric metric = Metric.builder()
                .unit(AlarmMetricUnitEnum.NONE)
                .expression("[\n"
                        + "    {\n"
                        + "        \"filterMetricOperator\":\"is\",\n"
                        + "        \"metric\":\"app_code\",\n"
                        + "        \"metricValue\":\"fsadmin\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "        \"filterMetricOperator\":\"is\",\n"
                        + "        \"metric\":\"event_code\",\n"
                        + "        \"metricValue\":\"http_provider\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "        \"filterMetricOperator\":\"is\",\n"
                        + "        \"metric\":\"attr.path\",\n"
                        + "        \"metricValue\":\"/deploy/status\"\n"
                        + "    }\n"
                        + "]")
                .executeTime(new Date())
                .metricExtData(null)
                .groupType(GroupTypeEnum.AVG)
                .timeWindow(25)
                .appCode("fsadmin")
                .groupField("attr.response_time")
                .build();
        metric.setGroupField("attr.response_time");


        // 构造数据源
        AlarmDataSourceDTO dataSource = new AlarmDataSourceDTO();
        dataSource.setAddress("https://mes-es-traffic.mfwdev.com");
        dataSource.setType(DataSourceTypeEnum.ELASTIC_SEARCH);
        dataSource.setProperties("{\"es_doc_index\":\"mes_${app_code}_${date_month}*\",\"es_type_name\":\"server_event\",\"date_field\":\"datetime\",\"date_field_type\":\"1\"}");

        MetricExecuteResult metricExecuteResult = elasticSearchEngine.executeSingleMetric(dataSource, metric);
        Map<String, MetricValue> metricValueMap = metricExecuteResult.getMetricValueMap();

        System.out.println("aaa"+ metricExecuteResult.toString());
    }

    @Test
    public void testCompositeMetricEs(){
        // 构造数据源
        AlarmDataSourceDTO dataSource = new AlarmDataSourceDTO();
        dataSource.setAddress("https://mes-es-traffic.mfwdev.com/");
        dataSource.setType(DataSourceTypeEnum.ELASTIC_SEARCH);
        dataSource.setProperties("{\"es_doc_index\":\"mes_${app_code}_${date_month}*\",\"es_type_name\":\"server_event\",\"date_field\":\"datetime\",\"date_field_type\":\"1\"}");

        Metric singleMetricA = Metric.builder()
                .unit(AlarmMetricUnitEnum.NONE)
                .metricName("total")
                .expression("[\n"
                        + "    {\n"
                        + "        \"filterMetricOperator\":\"is\",\n"
                        + "        \"metric\":\"app_code\",\n"
                        + "        \"metricValue\":\"ftwl\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "        \"filterMetricOperator\":\"is\",\n"
                        + "        \"metric\":\"event_code\",\n"
                        + "        \"metricValue\":\"http_provider\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "        \"filterMetricOperator\":\"is_one_of\",\n"
                        + "        \"metric\":\"attr.response_status.keyword\",\n"
                        + "        \"metricValue\":\"true,false\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "        \"filterMetricOperator\":\"is_one_of\",\n"
                        + "        \"metric\":\"attr.path\",\n"
                        + "        \"metricValue\":\"/order/payValidate\"\n"
                        + "    }\n"
                        + "]")
                .executeTime(new Date())
                .metricExtData(null)
                .groupType(GroupTypeEnum.COUNT)
                .timeWindow(60)
                .appCode("ftwl")
                .groupField("total")
                .build();

        Metric singleMetricB = Metric.builder()
                .unit(AlarmMetricUnitEnum.NONE)
                .metricName("success")
                .expression("[\n"
                        + "    {\n"
                        + "        \"filterMetricOperator\":\"is\",\n"
                        + "        \"metric\":\"app_code\",\n"
                        + "        \"metricValue\":\"ftwl\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "        \"filterMetricOperator\":\"is\",\n"
                        + "        \"metric\":\"event_code\",\n"
                        + "        \"metricValue\":\"http_provider\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "        \"filterMetricOperator\":\"is_one_of\",\n"
                        + "        \"metric\":\"attr.response_status.keyword\",\n"
                        + "        \"metricValue\":\"true\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "        \"filterMetricOperator\":\"is_one_of\",\n"
                        + "        \"metric\":\"attr.path\",\n"
                        + "        \"metricValue\":\"/order/payValidate\"\n"
                        + "    }\n"
                        + "]")
                .executeTime(new Date())
                .metricExtData(null)
                .groupType(GroupTypeEnum.COUNT)
                .timeWindow(60)
                .appCode("ftwl")
                .groupField("success")
                .build();

        Metric compositeMetric = Metric.builder()
                .unit(AlarmMetricUnitEnum.NONE)
                .appCode("ftwl")
                .groupField("total")
                .formula("success / total < 0.2 && total > 1")
                .build();

        List<Metric> singleMetricList = Arrays.asList(singleMetricA, singleMetricB);

        MetricExecuteResult metricExecuteResult = elasticSearchEngine.executeCompositeMetric(
                dataSource, singleMetricList, compositeMetric);
        Map<String, MetricValue> metricValueMap = metricExecuteResult.getMetricValueMap();
        System.out.println(metricValueMap);
    }

    @Test
    public void testSingleMetric() throws DependentCommunicateException {
        // 构造指标
        Metric metric = new Metric();
        metric.setTimeWindow(1);
        metric.setExpression("test");
        metric.setGroupType(GroupTypeEnum.NONE);
        metric.setExecuteTime(new Date());
        metric.setMetricName("test");
        metric.setUnit(AlarmMetricUnitEnum.NONE);
        Map<String, String> extData = Maps.newHashMap();
        extData.put("metric_field_key", "instance");
        metric.setMetricExtData(extData);
        // 构造数据源
        AlarmDataSourceDTO dataSource = new AlarmDataSourceDTO();
        dataSource.setAddress("");
        dataSource.setType(DataSourceTypeEnum.PROMETHEUS);
        String instance = "10.10.10.10";
        String value = "10";
        Mockito.when(prometheusClient.query(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(prometheusResponseData(instance, value));

        MetricExecuteResult metricExecuteResult = prometheusEngine.executeSingleMetric(dataSource, metric);
        Map<String, MetricValue> metricValueMap = metricExecuteResult.getMetricValueMap();
        Assert.assertEquals(metricValueMap.size(), 1);
        Assert.assertEquals(metricValueMap.get(instance).getValue(), value);
    }

    @Test
    public void testSinglePercentageMetric() throws DependentCommunicateException {
        // 构造指标
        Metric metric = new Metric();
        metric.setTimeWindow(1);
        metric.setExpression("test");
        metric.setGroupType(GroupTypeEnum.NONE);
        metric.setExecuteTime(new Date());
        metric.setMetricName("test");
        metric.setUnit(AlarmMetricUnitEnum.PERCENTAGE);
        Map<String, String> extData = Maps.newHashMap();
        extData.put("metric_field_key", "instance");
        metric.setMetricExtData(extData);
        // 构造数据源
        AlarmDataSourceDTO dataSource = new AlarmDataSourceDTO();
        dataSource.setAddress("");
        dataSource.setType(DataSourceTypeEnum.PROMETHEUS);
        String instance = "10.10.10.10";
        String value = "0.01";
        Mockito.when(prometheusClient.query(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(prometheusResponseData(instance, value));

        MetricExecuteResult metricExecuteResult = prometheusEngine.executeSingleMetric(dataSource, metric);
        Map<String, MetricValue> metricValueMap = metricExecuteResult.getMetricValueMap();
        Assert.assertEquals(metricValueMap.size(), 1);
        Assert.assertEquals(metricValueMap.get(instance).getValue(), "1.00");
    }

    @Test
    public void testSingleMBMetric() throws DependentCommunicateException {
        // 构造指标
        Metric metric = new Metric();
        metric.setTimeWindow(1);
        metric.setExpression("test");
        metric.setGroupType(GroupTypeEnum.NONE);
        metric.setExecuteTime(new Date());
        metric.setMetricName("test");
        metric.setUnit(AlarmMetricUnitEnum.MB);
        Map<String, String> extData = Maps.newHashMap();
        extData.put("metric_field_key", "instance");
        metric.setMetricExtData(extData);
        // 构造数据源
        AlarmDataSourceDTO dataSource = new AlarmDataSourceDTO();
        dataSource.setAddress("");
        dataSource.setType(DataSourceTypeEnum.PROMETHEUS);
        String instance = "10.10.10.10";
        String value = Long.toString(1024L * 1024L * 1024L);
        Mockito.when(prometheusClient.query(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(prometheusResponseData(instance, value));

        MetricExecuteResult metricExecuteResult = prometheusEngine.executeSingleMetric(dataSource, metric);
        Map<String, MetricValue> metricValueMap = metricExecuteResult.getMetricValueMap();
        Assert.assertEquals(metricValueMap.size(), 1);
        Assert.assertEquals(metricValueMap.get(instance).getValue(), "1024.0");
    }

    @Test
    public void testMultiMetric() throws DependentCommunicateException {
        // 构造指标
        Metric metric = new Metric();
        metric.setTimeWindow(1);
        metric.setExpression("test");
        metric.setGroupType(GroupTypeEnum.NONE);
        metric.setExecuteTime(new Date());
        metric.setMetricName("test");
        metric.setUnit(AlarmMetricUnitEnum.NONE);
        Map<String, String> extData = Maps.newHashMap();
        extData.put("metric_field_key", "instance");
        metric.setMetricExtData(extData);
        // 构造数据源
        AlarmDataSourceDTO dataSource = new AlarmDataSourceDTO();
        dataSource.setAddress("");
        dataSource.setType(DataSourceTypeEnum.PROMETHEUS);
        String instance = "10.10.10.10";
        String value = Long.toString(10);
        Mockito.when(prometheusClient.queryRange(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(prometheusResponseMultiList(instance, value));

        List<MetricExecuteResult> metricExecuteResults = prometheusEngine.executeMultiSingleMetric(dataSource, metric, 1, 3);
        Assert.assertEquals(metricExecuteResults.size(), 3);
        for (MetricExecuteResult metricExecuteResult : metricExecuteResults) {
            Map<String, MetricValue> metricValueMap = metricExecuteResult.getMetricValueMap();
            Assert.assertEquals(metricValueMap.size(), 1);
            Assert.assertEquals(metricValueMap.get(instance).getValue(), "10");
        }
    }

    @Test
    public void testCompositeMetric() throws DependentCommunicateException {
        // 构造指标
        Metric metricA = new Metric();
        metricA.setTimeWindow(1);
        metricA.setExpression("test");
        metricA.setGroupType(GroupTypeEnum.NONE);
        metricA.setExecuteTime(new Date());
        metricA.setMetricName("a");
        metricA.setUnit(AlarmMetricUnitEnum.NONE);
        Map<String, String> extData = Maps.newHashMap();
        extData.put("metric_field_key", "instance");
        metricA.setMetricExtData(extData);

        Metric metricB = new Metric();
        metricB.setTimeWindow(1);
        metricB.setExpression("test");
        metricB.setGroupType(GroupTypeEnum.NONE);
        metricB.setExecuteTime(new Date());
        metricB.setMetricName("b");
        metricB.setUnit(AlarmMetricUnitEnum.NONE);
        metricB.setMetricExtData(extData);
        List<Metric> metrics = Lists.newArrayList(metricA, metricB);
        // 构造数据源
        AlarmDataSourceDTO dataSource = new AlarmDataSourceDTO();
        dataSource.setAddress("");
        dataSource.setType(DataSourceTypeEnum.PROMETHEUS);
        String instance = "10.10.10.10";
        String value = Long.toString(10);
        Mockito.when(prometheusClient.query(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(prometheusResponseData(instance, value));
        String formula = "(a+b)*10";

        Metric metricC = new Metric();
        metricC.setTimeWindow(1);
        metricC.setExpression("test");
        metricC.setGroupType(GroupTypeEnum.NONE);
        metricC.setExecuteTime(new Date());
        metricC.setMetricName("");
        metricC.setUnit(AlarmMetricUnitEnum.NONE);
        metricC.setMetricExtData(extData);
        metricC.setFormula(formula);

        MetricExecuteResult metricExecuteResult = prometheusEngine.executeCompositeMetric(dataSource, metrics, metricC);
        Map<String, MetricValue> metricValueMap = metricExecuteResult.getMetricValueMap();
        Assert.assertEquals(metricValueMap.size(), 1);
        Assert.assertEquals(metricValueMap.get(instance).getValue(), "200");
    }


    private PrometheusResponseData prometheusResponseData(String instance, String value) {
        PrometheusResponseData data = new PrometheusResponseData();
        List<PrometheusResponseResult> list = Lists.newArrayList();
        PrometheusResponseResult responseResult = new PrometheusResponseResult();
        Map<String, String> metric = Maps.newHashMap();
        metric.put("instance", instance);
        responseResult.setMetric(metric);
        responseResult.setValue(Lists.newArrayList(System.currentTimeMillis() / 1000, value));
        list.add(responseResult);
        data.setResult(list);
        return data;
    }

    private PrometheusResponseData prometheusResponseMultiList(String instance, String value) {
        PrometheusResponseData data = new PrometheusResponseData();
        List<PrometheusResponseResult> list = Lists.newArrayList();
        PrometheusResponseResult responseResult = new PrometheusResponseResult();
        Map<String, String> metric = Maps.newHashMap();
        metric.put("instance", instance);
        responseResult.setMetric(metric);
        List<Object> valueList = Lists.newArrayList(System.currentTimeMillis() / 1000, value);
        List<List<Object>> valuesList = Lists.newArrayList(valueList, valueList, valueList);
        responseResult.setValues(valuesList);
        list.add(responseResult);
        data.setResult(list);
        return data;
    }


}
