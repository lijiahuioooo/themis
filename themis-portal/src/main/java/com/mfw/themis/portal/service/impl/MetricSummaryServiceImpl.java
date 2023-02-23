package com.mfw.themis.portal.service.impl;

import com.mfw.themis.common.model.dto.AlarmRuleDTO;
import com.mfw.themis.common.model.dto.AppMetricDTO;
import com.mfw.themis.common.model.dto.MetricSummaryDTO;
import com.mfw.themis.common.model.dto.SaveAlarmMetricDTO;
import com.mfw.themis.portal.service.AlarmMetricService;
import com.mfw.themis.portal.service.AlarmRuleService;
import com.mfw.themis.portal.service.AppMetricService;
import com.mfw.themis.portal.service.MetricSummaryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author guosp
 */
@Service
public class MetricSummaryServiceImpl implements MetricSummaryService {


    @Autowired
    private AlarmMetricService alarmMetricService;

    @Autowired
    private AppMetricService appMetricService;

    @Autowired
    private AlarmRuleService alarmRuleService;

    /**
     * 创建指标
     * todo
     *
     * @param summaryDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MetricSummaryDTO create(MetricSummaryDTO summaryDTO) {
        SaveAlarmMetricDTO saveAlarmMetricDTO = new SaveAlarmMetricDTO();
        BeanUtils.copyProperties(summaryDTO,saveAlarmMetricDTO);
        saveAlarmMetricDTO = alarmMetricService.create(saveAlarmMetricDTO);
        summaryDTO.setMetricId(saveAlarmMetricDTO.getId());

        AppMetricDTO appMetricDTO = new AppMetricDTO();
        BeanUtils.copyProperties(summaryDTO,appMetricDTO);
        appMetricDTO = appMetricService.create(appMetricDTO);
        summaryDTO.setAppMetricId(appMetricDTO.getId());

        AlarmRuleDTO alarmRuleDTO = new AlarmRuleDTO();
        BeanUtils.copyProperties(summaryDTO,alarmRuleDTO);
        alarmRuleDTO = alarmRuleService.create(alarmRuleDTO);
        summaryDTO.setRuleId(alarmRuleDTO.getId());

        return summaryDTO;
    }
}
