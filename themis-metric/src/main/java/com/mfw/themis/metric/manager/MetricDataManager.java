package com.mfw.themis.metric.manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mfw.themis.common.convert.AlarmDataSourceConvert;
import com.mfw.themis.common.convert.AlarmMetricConvert;
import com.mfw.themis.common.convert.AppConvert;
import com.mfw.themis.common.convert.AppMetricConvert;
import com.mfw.themis.common.model.dto.AlarmDataSourceDTO;
import com.mfw.themis.common.model.dto.AlarmMetricDTO;
import com.mfw.themis.common.model.dto.AppDTO;
import com.mfw.themis.common.model.dto.AppMetricDTO;
import com.mfw.themis.dao.mapper.AlarmDataSourceDao;
import com.mfw.themis.dao.mapper.AlarmMetricDao;
import com.mfw.themis.dao.mapper.AppDao;
import com.mfw.themis.dao.mapper.AppMetricDao;
import com.mfw.themis.dao.po.AlarmDataSourcePO;
import com.mfw.themis.dao.po.AlarmMetricPO;
import com.mfw.themis.dao.po.AppMetricPO;
import com.mfw.themis.dao.po.AppPO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author liuqi
 */
@Slf4j
@Component
public class MetricDataManager implements InitializingBean {

    private Map<Long, AppDTO> appMap = new ConcurrentHashMap<>();
    private Map<Long, AlarmMetricDTO> alarmMetricMap = new ConcurrentHashMap<>();
    private Map<Long, AppMetricDTO> appMetricMap = new ConcurrentHashMap<>();
    private Map<Long, AlarmDataSourceDTO> datasourceMap = new ConcurrentHashMap<>();

    @Autowired
    private AlarmDataSourceDao alarmDataSourceDao;
    @Autowired
    private AlarmMetricDao alarmMetricDao;
    @Autowired
    private AppDao appDao;
    @Autowired
    private AppMetricDao appMetricDao;


    @Override
    public void afterPropertiesSet() throws Exception {
        loadCacheData();
    }

    @Scheduled(fixedDelay = 5 * 60 * 1000L)
    public void refreshCacheData() {
        try {
            loadCacheData();
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
    }

    public AppDTO getAppById(Long id) {
        return appMap.get(id);
    }

    public AlarmMetricDTO getAlarmMetricById(Long id) {
        return alarmMetricMap.get(id);
    }

    public AppMetricDTO getAppMetricById(Long id) {
        return appMetricMap.get(id);
    }

    public AlarmDataSourceDTO getDataSourceById(Long id) {
        return datasourceMap.get(id);
    }

    /**
     * 根据id获取appMetricPO
     *
     * @param appMetricIds
     * @return
     */
    public Map<Long, AppMetricDTO> loadAppMetricsByIds(List<Long> appMetricIds) {
        Map<Long, AppMetricDTO> singleAppMetricMap = Maps.newHashMap();
        appMetricIds.forEach(appMetricId -> {
            if (singleAppMetricMap.containsKey(appMetricId)) {
                singleAppMetricMap.put(appMetricId, appMetricMap.get(appMetricId));
            }
        });
        return singleAppMetricMap;
    }

    /**
     * 根据id获取alarmMetricPO
     *
     * @param alarmMetricIds
     * @return
     */
    public List<AlarmMetricDTO> loadAlarmMetrics(List<Long> alarmMetricIds) {
        List<AlarmMetricDTO> alarmMetricList = Lists.newArrayList();
        alarmMetricIds.forEach(id -> {
            if (alarmMetricMap.containsKey(id)) {
                alarmMetricList.add(alarmMetricMap.get(id));
            }
        });
        return alarmMetricList;
    }

    private void loadCacheData() {
        List<AlarmDataSourcePO> alarmDataSourceList = alarmDataSourceDao.selectList(new QueryWrapper<>());
        datasourceMap = alarmDataSourceList.stream().map(AlarmDataSourceConvert::toDTO)
                .collect(Collectors.toConcurrentMap(AlarmDataSourceDTO::getId, a -> a));

        List<AppPO> appList = appDao.selectList(new QueryWrapper<>());
        appMap = appList.stream().map(AppConvert::toDTO)
                .collect(Collectors.toConcurrentMap(AppDTO::getId, a -> a));

        List<AppMetricPO> appMetricList = appMetricDao.selectList(new QueryWrapper<>());
        appMetricMap = appMetricList.stream().map(AppMetricConvert::toDTO)
                .collect(Collectors.toConcurrentMap(AppMetricDTO::getId, a -> a));

        List<AlarmMetricPO> alarmMetricList = alarmMetricDao.selectList(new QueryWrapper<>());
        alarmMetricMap = alarmMetricList.stream().map(AlarmMetricConvert::toDTO)
                .collect(Collectors.toConcurrentMap(AlarmMetricDTO::getId, a -> a));
    }


}
