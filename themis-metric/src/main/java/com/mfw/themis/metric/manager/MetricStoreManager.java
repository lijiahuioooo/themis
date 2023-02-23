//package com.mfw.themis.metric.manager;
//
//import com.google.common.collect.Maps;
//import com.mfw.themis.common.model.dto.AppMetricDTO;
//import com.mfw.themis.dao.mapper.AlarmDataSourceDao;
//import com.mfw.themis.metric.event.MetricExecuteEvent;
//import com.mfw.themis.metric.model.MetricExecuteResult;
//import com.mfw.themis.metric.model.MetricValue;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//import lombok.extern.slf4j.Slf4j;
//import org.influxdb.InfluxDB;
//import org.influxdb.dto.BatchPoints;
//import org.influxdb.dto.Point;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//
///**
// * @author liuqi
// */
//@Component
//@Slf4j
//public class MetricStoreManager {
//
//    private static final String METRIC_MEASUREMENT_PREFIX = "measure_";
//    @Autowired
//    private InfluxDB influxDB;
//    @Autowired
//    private AlarmDataSourceDao alarmDataSourceDao;
//
//    @Async
////    @EventListener(value = MetricExecuteEvent.class)
//    public void storeInfluxDbMetric(MetricExecuteEvent metricExecuteEvent) {
//        if (metricExecuteEvent.getAlarmRule() != null || metricExecuteEvent.getResultList() != null) {
//            return;
//        }
//        AppMetricDTO appMetricDTO = (AppMetricDTO) metricExecuteEvent.getSource();
////        AlarmDataSourcePO alarmDataSource = alarmDataSourceDao.selectById(appMetricDTO.getDatasourceId());
////        if (alarmDataSource.getType() != DataSourceTypeEnum.ELASTIC_SEARCH.getCode()) {
////            return;
////        }
//        log.info("store influxdb data:{}", metricExecuteEvent);
//        Long appId = appMetricDTO.getAppId();
//        Long alarmId = appMetricDTO.getMetricId();
//        MetricExecuteResult metricExecuteResult = metricExecuteEvent.getResult();
//        Map<String, MetricValue> metricValueMap = metricExecuteResult.getMetricValueMap();
//        List<Point> pointList = metricValueMap.values().stream().map(metricValue -> {
//            Map<String, String> metricExtInfo = metricValue.getMetricExtInfo();
//            if (metricExtInfo == null) {
//                metricExtInfo = Maps.newHashMap();
//            }
//            metricExtInfo.put("appId", Long.toString(appId));
//            Double metricVal = null;
//            try {
//                metricVal = Double.valueOf(metricValue.getValue());
//            } catch (Exception e) {
//
//            }
//            return Point.measurement(METRIC_MEASUREMENT_PREFIX + alarmId).tag(metricExtInfo)
//                    .addField("appMetricId", appMetricDTO.getId())
//                    .addField("metric", metricVal)
//                    .build();
//        }).collect(Collectors.toList());
//        BatchPoints points = BatchPoints.builder().points(pointList).build();
//        influxDB.write(points);
//    }
//
//}
