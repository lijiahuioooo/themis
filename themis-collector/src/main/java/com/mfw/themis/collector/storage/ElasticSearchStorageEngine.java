package com.mfw.themis.collector.storage;

import com.google.common.collect.Maps;
import com.mfw.themis.collector.exception.MetricValidateException;
import com.mfw.themis.collector.manager.CollectorMetricManager;
import com.mfw.themis.collector.manager.EsBulkManager;
import com.mfw.themis.collector.manager.IndexManager;
import com.mfw.themis.collector.model.EsBulkRequest;
import com.mfw.themis.common.constant.enums.CollectFieldTypeEnum;
import com.mfw.themis.common.model.dto.CollectMetricFieldDTO;
import com.mfw.themis.common.model.dto.CollectMetricFieldDTO.Field;
import com.mfw.themis.common.model.message.CollectorMessage;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ES 存储引擎
 *
 * @author wenhong
 */
@Component
@Slf4j
public class ElasticSearchStorageEngine implements StorageEngine {

    /**
     * es indexType
     */
    public static final String INDEX_TYPE = "themis_event";

    @Autowired
    private EsBulkManager esBulkManager;

    @Autowired
    IndexManager indexManager;

    @Autowired
    private CollectorMetricManager collectorMetricManager;

    /**
     * 数据校验
     *
     * @param message
     */
    @Override
    public void validate(CollectorMessage message) {
        if (null == message) {
            throw new MetricValidateException("校验数据为空");
        }

        if (null == message.getAppCode() || StringUtils.isBlank(message.getAppCode())) {
            throw new MetricValidateException("app_code为空, message: " + message.toString());
        }

        if (null == message.getMetric() || StringUtils.isBlank(message.getMetric())) {
            throw new MetricValidateException("metric为空, message: " + message.toString());
        }

        if (null == message.getTimestamp()) {
            throw new MetricValidateException("timestamp为空, message: " + message.toString());
        }

        CollectMetricFieldDTO field = collectorMetricManager.getItem(
                message.getAppCode(), message.getMetric());

        if (null == field) {
            throw new MetricValidateException("未匹配到对应上报事件定义, message: " + message.toString());
        }
        indexManager.checkIndex(getIndexName(message.getAppCode(), message.getTimestamp()));
    }

    /**
     * 获取索引名称
     *
     * @param appCode
     * @param timestamp
     * @return
     */
    public String getIndexName(String appCode, Long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateYmd = sdf.format(new Date(timestamp));

        return "themis_" + appCode + "_" + dateYmd;
    }

    /**
     * 拼装ES数据
     *
     * @param message
     * @return
     */
    public Map<String, Object> buildEsRequestData(CollectorMessage message) {
        String metric = message.getMetric();
        String appCode = message.getAppCode();
        CollectMetricFieldDTO field = collectorMetricManager.getItem(appCode, metric);

        Map<String, Object> tagsMap = field.getTags().stream()
                .collect(Collectors.toMap(Field::getMetric, Field::getType));
        Map<String, Object> fieldsMap = Optional.ofNullable(field.getFields()).orElseGet(ArrayList::new).stream()
                .collect(Collectors.toMap(Field::getMetric, Field::getType));

        Map<String, Object> newMap = Maps.newHashMap();
        newMap.put("appCode", appCode);
        newMap.put("themis_appCode", appCode);

        message.getData().forEach((k, v) -> {
            if (v != null) {
                if (tagsMap.containsKey(k)) {
                    newMap.put(k, convertType(appCode, metric, k, v, tagsMap.get(k).toString()));
                }

                if (fieldsMap.containsKey(k)) {
                    newMap.put(k, convertType(appCode, metric, k, v, fieldsMap.get(k).toString()));
                }
            }
        });
        if (newMap.size() <= 0) {
            throw new MetricValidateException("上报数据自定义字段缺失 " + message.toString());
        }

        /**
         * 用来区分不同的上报事件
         */
        newMap.put("_metric", metric);
        newMap.put("themis_metric", metric);

        ZoneId zone = ZoneId.of("Asia/Shanghai");
        newMap.put("ctime", OffsetDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), zone));
        newMap.put("timestamp", message.getTimestamp());
        newMap.put("datetime", OffsetDateTime.ofInstant(Instant.ofEpochMilli(message.getTimestamp()), zone));

        return newMap;
    }

    private Object convertType(String appCode, String metric, String k, Object v, String type) {
        try {

            if (type.equals(CollectFieldTypeEnum.LONG.getName())) {
                return Long.valueOf(v.toString());
            }

            if (type.equals(CollectFieldTypeEnum.DOUBLE.getName())) {
                return Double.valueOf(v.toString());
            }
        } catch (Exception e) {
            log.error("appCode:{},metric:{}.{} can not convert to {}, object class is {}", appCode, metric, k, type,
                    v.getClass());
            return null;
        }

        return v.toString();
    }

    /**
     * 加工处理，数据入库
     *
     * @param message
     */
    @Override
    public void process(CollectorMessage message) {

        EsBulkRequest esBulkRequest = new EsBulkRequest();
        esBulkRequest.setIndex(getIndexName(message.getAppCode(), message.getTimestamp()));
        esBulkRequest.setType(INDEX_TYPE);
        esBulkRequest.setData(buildEsRequestData(message));
        esBulkRequest.setUniqId(message.get_collectorId());

        esBulkManager.bulkInsert(esBulkRequest);
    }
}

