package com.mfw.themis.metric.engine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.mfw.themis.common.constant.EngineConstant;
import com.mfw.themis.common.constant.enums.AlarmMetricUnitEnum;
import com.mfw.themis.common.constant.enums.TimeWindowTypeEnum;
import com.mfw.themis.common.model.CustomTimeWindow;
import com.mfw.themis.common.model.dto.AlarmDataSourceDTO;
import com.mfw.themis.common.util.DateFormatUtils;
import com.mfw.themis.common.util.DebugHelper;
import com.mfw.themis.common.util.PlaceHolderUtils;
import com.mfw.themis.common.util.TimeWindowUtils;
import com.mfw.themis.dependent.elasticsearch.EsRestClient;
import com.mfw.themis.dependent.elasticsearch.constant.enums.AggTypeEnum;
import com.mfw.themis.dependent.elasticsearch.constant.enums.DateFieldTypeEnum;
import com.mfw.themis.dependent.elasticsearch.model.EsAggRequest;
import com.mfw.themis.dependent.elasticsearch.model.EsAggRequest.EsAggMetric;
import com.mfw.themis.dependent.elasticsearch.model.EsAggResponse;
import com.mfw.themis.metric.exception.DataSourceEngineNotSupportException;
import com.mfw.themis.metric.exception.MetricCollectException;
import com.mfw.themis.metric.exception.TimeWindowException;
import com.mfw.themis.metric.model.Metric;
import com.mfw.themis.metric.model.MetricExecuteResult;
import com.mfw.themis.metric.model.MetricValue;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author guosp
 */
@Component
@Slf4j
public class ElasticSearchEngine extends AbstractExecuteEngine implements ExecuteEngine {

    public static final String ES_DOC_APP_CODE = "app_code";
    public static final String ES_DOC_DATE_MONTH = "date_month";
    @Autowired
    private DebugHelper debugHelper;
    /**
     * 业务自定义上报数据，ES配置
     */
    public static final String ES_COLLECT_DATASOURCE_PROPERTIES =
            "{\"es_doc_index\":\"themis_${app_code}_${date_month}*\"," +
                    "\"es_type_name\":\"themis_event\",\"date_field\":\"timestamp\",\"date_field_type\":\"2\"}";

    @Override
    public MetricExecuteResult executeSingleMetric(AlarmDataSourceDTO dataSource, Metric metric) {

        MetricExecuteResult metricExecuteResult = new MetricExecuteResult();

        EsAggRequest esAggRequest = buildEsAggRequest(dataSource, metric);
        boolean needLog = debugHelper.isDebugMetricId(metric.getAppMetricId());
        if (needLog) {
            log.info("metric:{},es request, {}", metric, esAggRequest.toString());
        }
        EsAggResponse esAggResponse = EsRestClient.aggQuery(dataSource.getAddress(), esAggRequest, needLog);
        if (needLog) {
            log.info("metric:{},es response, {}", metric, esAggResponse.toString());
        }
        if (!esAggResponse.isSuccess()) {
            throw new MetricCollectException(dataSource.getType(), dataSource.getId(), metric.getMetricName(),
                    metric.getAppMetricId());
        }

        Map<String, MetricValue> metricValueGroupMap = Maps.newHashMap();
        MetricValue metricValue = new MetricValue();
        metricValue.setValue(esAggResponse.getData().getValue().toString());
        metricValue.setTimeStamp(metric.getExecuteTime().getTime());
        metricValue.setUnit(metric.getUnit());
        Map<String, String> metricExtInfo = Maps.newHashMap();
        metricExtInfo.put("request",esAggRequest.toString());
        metricExtInfo.put("response",esAggResponse.toString());
        metricValue.setMetricExtInfo(metricExtInfo);
        String endPoint = esAggResponse.getData().getMetric();
        if (endPoint == null) {
            endPoint = StringUtils.EMPTY;
        }
        metricValueGroupMap.put(endPoint, metricValue);

        metricExecuteResult.setMetricValueMap(metricValueGroupMap);

        return metricExecuteResult;
    }

    /**
     * ES请求参数拼装
     *
     * @param dataSource
     * @param metric
     * @return
     */
    private EsAggRequest buildEsAggRequest(AlarmDataSourceDTO dataSource, Metric metric) {
        EsAggRequest esAggRequest = new EsAggRequest();

        Map properties;
        if (metric.getCollectId() > 0) {
            // 业务自定义上报
            properties = (Map) JSON.parse(ES_COLLECT_DATASOURCE_PROPERTIES);
        } else {
            properties = (Map) JSON.parse(dataSource.getProperties());
        }

//        log.info("es properties, {}", properties.toString());

        String docIndex = getDocIndexName(properties, metric);
        esAggRequest.setIndexName(docIndex);

        String typeName = (String) properties.get(EngineConstant.PROPERTIES_ES_TYPE_NAME);
        if (StringUtils.isNotBlank(typeName)) {
            esAggRequest.setTypeName(typeName);
        }

        String dateField = (String) properties.get(EngineConstant.PROPERTIES_ES_DATE_FIELD);
        if (StringUtils.isNotBlank(dateField)) {
            esAggRequest.setDateField(dateField);
        }

        String dateFieldType = (String) properties.get(EngineConstant.PROPERTIES_ES_DATE_FIELD_TYPE);
        if (StringUtils.isNotBlank(dateFieldType)) {
            esAggRequest.setDateFieldType(DateFieldTypeEnum.getByCode(Integer.parseInt(dateFieldType)));
        }

        esAggRequest.setAggType(AggTypeEnum.getByCode(metric.getGroupType().getCode()));
        if (StringUtils.isNotBlank(metric.getGroupField())) {
            esAggRequest.setGroupField(metric.getGroupField());
        }

        // 设置查询时间窗口
        setReuestTimeWindow(esAggRequest, metric);

        List<EsAggMetric> esFilterMetricList = JSONObject.parseArray(metric.getExpression(), EsAggMetric.class);

        // 针对Mone迁移的应用，app_code需要使用 mone_app_code
        if (StringUtils.isNotEmpty(metric.getMoneAppCode())) {
            esFilterMetricList.forEach(item -> {
                if (item.getMetric().equals("app_code")) {
                    item.setMetricValue(metric.getMoneAppCode());
                }
            });
        }

        esAggRequest.setFilterMetrics(esFilterMetricList);

        return esAggRequest;
    }

    private void setReuestTimeWindow(EsAggRequest esAggRequest, Metric metric) {
        // 时间窗口偏移
        int beforeMinutes = 0;
        if (null != metric.getTimeWindowOffset()) {
            beforeMinutes = TimeWindowUtils.getBeforeMinutes(metric.getTimeWindowOffset());
        }

        if (TimeWindowTypeEnum.DEFAULT.getCode() == metric.getTimeWindowType().getCode()) {
            esAggRequest.setStartTime(DateUtils.addMinutes(
                    metric.getExecuteTime(), 0 - metric.getTimeWindow() - beforeMinutes));
            esAggRequest.setEndTime(DateUtils.addMinutes(
                    metric.getExecuteTime(), 0 - beforeMinutes));
            return;
        }

        // 自定义时间窗口
        Date startTime = DateUtils.addMinutes(metric.getExecuteTime(), 0 - beforeMinutes);
        CustomTimeWindow customTimeWindow = metric.getCustomTimeWindow();

        final String pattern = "yyyy-MM-dd HH:mm:ss";

        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String startDate = df.format(startTime);

            esAggRequest.setStartTime(DateUtils.parseDate(startDate + " " + customTimeWindow.getFrom(), pattern));
            esAggRequest.setEndTime(DateUtils.parseDate(startDate + " " + customTimeWindow.getTo(), pattern));
        } catch (Exception e) {
            log.error("EsAggRequest 日期时间格式错误", e);
            throw new TimeWindowException(customTimeWindow.getFrom() + "  " + customTimeWindow.getTo());
        }

    }

    private String getDocIndexName(Map properties, Metric metric) {
        //索引名称规则 - mes_${appCode}_${dateMonth}
        String docIndexFormat = String.valueOf(properties.get(EngineConstant.PROPERTIES_ES_DOC_INDEX));

        List<String> placeHolderList = PlaceHolderUtils.getPlaceHolderKeyList(docIndexFormat);
        Map<String, String> data = new HashMap<>(4);
        for (String s : placeHolderList) {
            switch (s) {
                case ES_DOC_APP_CODE:
                    data.put(ES_DOC_APP_CODE,
                            StringUtils.isNotEmpty(metric.getMoneAppCode()) ? metric.getMoneAppCode()
                                    : metric.getAppCode());
                    break;
                case ES_DOC_DATE_MONTH:
                    String dateMonth;
                    // 判断时间是否有偏移量
                    int beforeMinutes = 0;
                    if (null != metric.getTimeWindowOffset()) {
                        beforeMinutes = TimeWindowUtils.getBeforeMinutes(metric.getTimeWindowOffset());
                    }

                    if (beforeMinutes > 0) {
                        Date beforeDate = DateUtils.addMinutes(metric.getExecuteTime(), 0 - beforeMinutes);

                        DateFormat df = new SimpleDateFormat(DateFormatUtils.YYYYMM_PATTERN);
                        dateMonth = df.format(beforeDate);
                    } else {
                        dateMonth = DateFormatUtils.currentDate(DateFormatUtils.YYYYMM_PATTERN);
                    }

                    data.put(ES_DOC_DATE_MONTH, dateMonth);
                    break;
                default:
                    throw new DataSourceEngineNotSupportException(docIndexFormat);
            }
        }

        return PlaceHolderUtils.replace(docIndexFormat, data);
    }


    /**
     * 执行复合指标
     *
     * @param dataSource      数据源
     * @param singleMetrics   单指标
     * @param compositeMetric 复合指标
     * @return
     */
    @Override
    public MetricExecuteResult executeCompositeMetric(AlarmDataSourceDTO dataSource,
            List<Metric> singleMetrics, Metric compositeMetric) {

        if (singleMetrics.size() == 0 || null == compositeMetric) {
            return null;
        }

        // <App_Code, <参数名称，指标值>>
        Map<String, Object> compositeMetricMap = new HashMap<>();
        AlarmMetricUnitEnum unit = singleMetrics.get(0).getUnit();
        for (Metric metric : singleMetrics) {
            MetricExecuteResult metricExecuteResult = executeSingleMetric(dataSource, metric);
            metricExecuteResult.getMetricValueMap().forEach((key, val) -> {

                BigDecimal value = new BigDecimal(val.getValue());
                compositeMetricMap.put(metric.getMetricName(), value);
            });
        }

        Expression expression = AviatorEvaluator.compile(compositeMetric.getFormula());

        MetricExecuteResult result = new MetricExecuteResult();
        Map<String, MetricValue> metricMap = new HashMap<>();
        result.setMetricValueMap(metricMap);

        Object objResult = expression.execute(compositeMetricMap);
        log.info("复合指标结算结果 compositeMetric: {},singleMetrics:{}, compositeMetricMap: {}, formula: {},result:{}",
                compositeMetric, singleMetrics,
                compositeMetricMap, compositeMetric.getFormula(), objResult);
        MetricValue metricValue = new MetricValue();
        metricValue.setTimeStamp(System.currentTimeMillis());

        if (objResult instanceof Boolean) {
            Boolean calResult = (Boolean) objResult;
            metricValue.setValue(calResult ? "1" : "0");
        } else if (objResult instanceof Double) {
            Double calResult = (Double) objResult;
            BigDecimal calResultVal = convertWithUnit(new BigDecimal(calResult), unit);
            metricValue.setValue(calResultVal.toString());
        } else {
            BigDecimal calResult = (BigDecimal) objResult;
            calResult = convertWithUnit(calResult, unit);
            metricValue.setValue(calResult.toString());
        }

        metricMap.put(singleMetrics.get(0).getAppCode(), metricValue);

        return result;
    }
}
