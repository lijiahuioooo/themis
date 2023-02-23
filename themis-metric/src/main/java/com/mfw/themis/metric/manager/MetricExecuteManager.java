package com.mfw.themis.metric.manager;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.mfw.themis.common.constant.enums.CollectTypeEnum;
import com.mfw.themis.common.constant.enums.ResourceEnum;
import com.mfw.themis.common.constant.enums.TimeWindowEnum;
import com.mfw.themis.common.constant.enums.TimeWindowTypeEnum;
import com.mfw.themis.common.exception.ResourceNotFoundException;
import com.mfw.themis.common.model.dto.AlarmDataSourceDTO;
import com.mfw.themis.common.model.dto.AlarmMetricDTO;
import com.mfw.themis.common.model.dto.AlarmRuleDTO;
import com.mfw.themis.common.model.dto.AppDTO;
import com.mfw.themis.common.model.dto.AppMetricDTO;
import com.mfw.themis.common.util.DebugHelper;
import com.mfw.themis.common.util.PlaceHolderUtils;
import com.mfw.themis.dao.mapper.CompositeAppMetricDao;
import com.mfw.themis.dao.po.CompositeAppMetricPO;
import com.mfw.themis.metric.engine.ExecuteEngine;
import com.mfw.themis.metric.event.MetricExecuteEvent;
import com.mfw.themis.metric.exception.CompositeFormularException;
import com.mfw.themis.metric.exception.MetricCalculateTimeOutOfThresholdException;
import com.mfw.themis.metric.factory.ExecuteEngineFactory;
import com.mfw.themis.metric.model.Metric;
import com.mfw.themis.metric.model.MetricExecuteResult;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * 指标执行
 *
 * @author liuqi
 */
@Component
@Slf4j
public class MetricExecuteManager implements ApplicationContextAware {

    private static final String METRIC_EXECUTE_KEY = "metric_execute:id:%d";
    private final static Long METRIC_CAL_TIME_THRESHOLD = 60 * 1000L;
    private ApplicationContext applicationContext;
    @Autowired
    private MetricDataManager metricDataManager;
    @Autowired
    private ExecuteEngineFactory executeEngineFactory;
    @Autowired
    private CompositeAppMetricDao compositeAppMetricDao;
    @Autowired
    private DebugHelper debugHelper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * @param appMetric       指标
     * @param alarmRule       规则 可以为空
     * @param expectStartTime 预期计算起始时间
     */
    public void executeMetricEngine(AppMetricDTO appMetric, AlarmRuleDTO alarmRule, Date expectStartTime) {

        Long startTime = System.currentTimeMillis();
        if (appMetric == null) {
            throw new ResourceNotFoundException(ResourceEnum.APP_METRIC, 0L);
        }
        AlarmMetricDTO alarmMetricDTO = metricDataManager.getAlarmMetricById(appMetric.getMetricId());
        try {

            String key = String.format(METRIC_EXECUTE_KEY, appMetric.getId());
            String timeStr = redisTemplate.opsForValue().get(key);
            // 只针对固定时间窗口
            if (TimeWindowTypeEnum.DEFAULT == alarmMetricDTO.getTimeWindowType()
                    && !needExecute(timeStr, alarmMetricDTO.getTimeWindow())) {
                return;
            }
            redisTemplate.opsForValue().set(key, Long.toString(System.currentTimeMillis()));
        } catch (Exception e) {
            log.warn("计算执行引擎间隔异常", e);
        }

        AlarmDataSourceDTO dataSource = metricDataManager.getDataSourceById(appMetric.getDatasourceId());
        AppDTO appDTO = metricDataManager.getAppById(appMetric.getAppId());
        // 指标计算
        ExecuteEngine executeEngine = executeEngineFactory.getExecuteEngine(alarmMetricDTO.getSourceType());
        List<MetricExecuteResult> resultsList = null;
        MetricExecuteResult results = null;

        if (alarmMetricDTO.getCollectType() == CollectTypeEnum.SINGLE_METRIC) {
            Metric metric = buildMetric(appMetric, alarmMetricDTO, appDTO, expectStartTime);
            if (alarmRule == null || alarmRule.getContinuousHitTimes() <= 1) {
                results = executeEngine.executeSingleMetric(dataSource, metric);
                if (debugHelper.isDebugMetricId(appMetric.getId())) {
                    executeMetricMDC(appMetric,alarmMetricDTO,alarmRule,expectStartTime,results,resultsList);
                    log.info("execute singleMetric,appMetricId:{}", appMetric.getId());
                }
            } else {
                resultsList = executeEngine
                        .executeMultiSingleMetric(dataSource, metric, alarmMetricDTO.getTimeWindow().getCode(),
                                alarmRule.getContinuousHitTimes());
                if (debugHelper.isDebugMetricId(appMetric.getId())) {
                    executeMetricMDC(appMetric,alarmMetricDTO,alarmRule,expectStartTime,results,resultsList);
                    log.info("execute mulSingleMetric,appMetricId:{}", appMetric.getId());
                }
            }

        } else {
            if (StringUtils.isEmpty(alarmMetricDTO.getFormula())) {
                throw new CompositeFormularException();
            }

            // get single_app_metric_ids
            Map<Long, String> singleAppMetricNameMap = loadSingleMetricsNameByComposite(appMetric.getId());
            if (CollectionUtils.isEmpty(singleAppMetricNameMap)) {
                log.warn("复合指标异常 appMetric:{}", appMetric);
                return;
            }

            // get single_metric_ids
            Map<Long, AppMetricDTO> singleAppMetricMap = metricDataManager.loadAppMetricsByIds(
                    Lists.newArrayList(singleAppMetricNameMap.keySet()));
            if (debugHelper.isDebugMetricId(appMetric.getId())) {
                log.info("singleAppMetricNameMap is {}", JSON.toJSONString(singleAppMetricMap));
            }
            if (CollectionUtils.isEmpty(singleAppMetricMap)) {
                log.warn("复合指标异常 appMetric:{}", appMetric);
                return;
            }

            List<AlarmMetricDTO> alarmMetricList = metricDataManager
                    .loadAlarmMetrics(Lists.newArrayList(singleAppMetricMap.keySet()));
            // get single metric list
            List<Metric> singleMetricList = buildCompositeMetric(appMetric.getId(), appDTO, alarmMetricList,
                    singleAppMetricMap, singleAppMetricNameMap, expectStartTime);
            // get composite metric
            Metric compositeMetric = buildMetric(appMetric, alarmMetricDTO, appDTO, expectStartTime);

            if (alarmRule == null || alarmRule.getContinuousHitTimes() <= 1) {
                results = executeEngine
                        .executeCompositeMetric(dataSource, singleMetricList, compositeMetric);
                if (debugHelper.isDebugMetricId(appMetric.getId())) {
                    executeMetricMDC(appMetric,alarmMetricDTO,alarmRule,expectStartTime,results,resultsList);
                    log.warn("execute compositeMetric,appMetricId:{}",appMetric.getId());
                }
            } else {
                resultsList = executeEngine
                        .executeMultiCompositeMetric(dataSource, singleMetricList, compositeMetric,
                                alarmRule.getContinuousHitTimes());
                if (debugHelper.isDebugMetricId(appMetric.getId())) {
                    executeMetricMDC(appMetric,alarmMetricDTO,alarmRule,expectStartTime,results,resultsList);
                    log.warn("execute multiCompositeMetric,appMetricId:{}",appMetric.getId());
                }
            }

        }

        MetricExecuteEvent executeEvent = new MetricExecuteEvent(appMetric, alarmRule, results, resultsList,
                expectStartTime);
        applicationContext.publishEvent(executeEvent);
        // 指标执行时间超过一分钟 需要报警，优化表达式排查问题
        Long executeEndTime = System.currentTimeMillis();
        Long executeTime = executeEndTime - expectStartTime.getTime();
        if (executeTime > METRIC_CAL_TIME_THRESHOLD) {
            log.error("id:{} 指标执行超时，实际调度时间延迟{}毫秒，运算时间延迟{}毫秒，共延迟{}毫秒", appMetric.getId(),
                    startTime - expectStartTime.getTime(),
                    executeEndTime - startTime, executeTime);
            throw new MetricCalculateTimeOutOfThresholdException(appMetric.getId());
        }

    }

    /**
     * 根据复合指标获取单一指标
     *
     * @param appMetricId
     * @return
     */
    private Map<Long, String> loadSingleMetricsNameByComposite(Long appMetricId) {
        QueryWrapper<CompositeAppMetricPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CompositeAppMetricPO::getCompositeAppMetricId, appMetricId);
        List<CompositeAppMetricPO> compositeAppMetricPOList = compositeAppMetricDao.selectList(queryWrapper);

        Map<Long, String> singleAppMetricNameMap = new HashMap<>();
        compositeAppMetricPOList.forEach(compositeAppMetricPO -> {
            singleAppMetricNameMap
                    .put(compositeAppMetricPO.getSingleAppMetricId(), compositeAppMetricPO.getMetricName());
        });
        return singleAppMetricNameMap;
    }

    /**
     * 构建复合指标
     *
     * @param compositeId
     * @param appDTO
     * @param alarmMetricList
     * @param singleAppMetricMap
     * @param singleAppMetricNameMap
     * @param expectStartTime
     * @return
     */
    private List<Metric> buildCompositeMetric(Long compositeId, AppDTO appDTO, List<AlarmMetricDTO> alarmMetricList,
            Map<Long, AppMetricDTO> singleAppMetricMap, Map<Long, String> singleAppMetricNameMap,
            Date expectStartTime) {
        List<Metric> singleMetricList = new ArrayList<>();
        alarmMetricList.forEach(alarmMetric -> {
            Metric singleMetric = buildMetric(singleAppMetricMap.get(alarmMetric.getId()), alarmMetric, appDTO,
                    expectStartTime);
            singleMetric.setMetricName(
                    singleAppMetricNameMap.get(singleAppMetricMap.get(alarmMetric.getId()).getId()));

            if (debugHelper.isDebugMetricId(compositeId)) {
                log.info("singleMetric is {}", JSON.toJSONString(singleMetric));
            }
            singleMetricList.add(singleMetric);
        });
        return singleMetricList;
    }

    /**
     * 拼装Metric
     *
     * @param appMetric
     * @param alarmMetricDTO
     * @param appDTO
     * @param executeStartTime
     * @return
     */
    private Metric buildMetric(AppMetricDTO appMetric, AlarmMetricDTO alarmMetricDTO,
            AppDTO appDTO, Date executeStartTime) {
        Map extData = JSON.parseObject(alarmMetricDTO.getExtData());

        return Metric.builder()
                .unit(alarmMetricDTO.getUnit())
                .expression(PlaceHolderUtils.replace(alarmMetricDTO.getExpression(), appMetric.getAttrValue()))
                .executeTime(executeStartTime)
                .metricName(alarmMetricDTO.getName())
                .metricExtData(extData)
                .groupType(alarmMetricDTO.getGroupType())
                .timeWindowOffset(alarmMetricDTO.getTimeWindowOffset())
                .timeWindowType(alarmMetricDTO.getTimeWindowType())
                .timeWindow((null != alarmMetricDTO.getTimeWindow()) ? alarmMetricDTO.getTimeWindow().getCode() : null)
                .customTimeWindow(alarmMetricDTO.getCustomTimeWindow())
                .appId(appMetric.getAppId())
                .appMetricId(appMetric.getId())
                .collectId(appMetric.getCollectId())
                .appCode(appDTO.getAppCode())
                .moneAppCode(appDTO.getMoneAppCode())
                .groupField(alarmMetricDTO.getGroupField())
                .formula(alarmMetricDTO.getFormula())
                .build();
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 是否需要执行
     *
     * @param timeStr    上次执行时间戳
     * @param timeWindow 时间窗口
     * @return
     */
    private boolean needExecute(String timeStr, TimeWindowEnum timeWindow) {
        if (null == timeWindow) {
            return true;
        }
        if (StringUtils.isBlank(timeStr)) {
            return true;
        }
        if (timeWindow == TimeWindowEnum.ONE_MINUTE) {
            return true;
        }
        Long timeStamp = Long.valueOf(timeStr);
        long timeInterval = System.currentTimeMillis() - timeStamp;
        // 允许0.5分钟的误差
        return timeInterval >= (timeWindow.getCode() - 0.5) * 60 * 1000;
    }

    /**
     * 执行指标日志设置
     *
     * @return
     */
    private void executeMetricMDC(AppMetricDTO appMetric,AlarmMetricDTO alarmMetricDTO,AlarmRuleDTO alarmRule,Date expectStartTime,MetricExecuteResult results,List<MetricExecuteResult> resultsList){
        MDC.put("app_id", appMetric.getAppId().toString());
        MDC.put("app_metric_id", appMetric.getId().toString());
        MDC.put("source_type", alarmMetricDTO.getSourceType().getDesc());
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String est = sdf.format(expectStartTime);
        MDC.put("expect_start_time",est);
        if (alarmRule == null || alarmRule.getContinuousHitTimes() <= 1) {
            MDC.put("result", JSON.toJSONString(results));
        }else {
            MDC.put("result", JSON.toJSONString(resultsList));
        }
    }
}
