package com.mfw.themis.metric.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mfw.themis.common.constant.enums.ResourceEnum;
import com.mfw.themis.common.convert.AppMetricConvert;
import com.mfw.themis.common.exception.ResourceNotFoundException;
import com.mfw.themis.common.exception.WebException;
import com.mfw.themis.common.model.dto.AppMetricDTO;
import com.mfw.themis.dao.mapper.AlarmMetricDao;
import com.mfw.themis.dao.mapper.AppMetricDao;
import com.mfw.themis.dao.po.AlarmMetricPO;
import com.mfw.themis.dao.po.AppMetricPO;
import com.mfw.themis.metric.manager.MetricExecuteManager;
import com.mfw.themis.metric.service.MetricExecuteService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MetricExecuteServiceImpl implements MetricExecuteService {

    @Autowired
    private AlarmMetricDao alarmMetricDao;

    @Autowired
    private AppMetricDao appMetricDao;

    @Autowired
    private MetricExecuteManager metricExecuteManager;

    /**
     * 指标计算
     * @param appMetricId
     * @return
     */
    @Override
    public Long metricExecute(Long appMetricId){

        AppMetricPO appMetricPO = appMetricDao.selectById(appMetricId);
        if (appMetricPO == null) {
            throw new ResourceNotFoundException(ResourceEnum.APP_METRIC, appMetricId);
        }

        AlarmMetricPO alarmMetricPO = alarmMetricDao.selectById(appMetricPO.getMetricId());
        if (alarmMetricPO == null) {
            throw new ResourceNotFoundException(ResourceEnum.ALARM_METRIC, appMetricPO.getMetricId());
        }

        AppMetricDTO appMetricDTO = AppMetricConvert.toDTO(appMetricPO);

        Long startTime = System.currentTimeMillis();
        metricExecuteManager.executeMetricEngine(appMetricDTO, null, new Date());

        return System.currentTimeMillis() - startTime;
    }
}
