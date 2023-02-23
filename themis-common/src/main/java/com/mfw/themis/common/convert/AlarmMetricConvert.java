package com.mfw.themis.common.convert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.mfw.themis.common.constant.enums.AlarmMetricUnitEnum;
import com.mfw.themis.common.constant.enums.CollectTypeEnum;
import com.mfw.themis.common.constant.enums.DataSourceTypeEnum;
import com.mfw.themis.common.constant.enums.GroupTypeEnum;
import com.mfw.themis.common.constant.enums.MetricTypeEnum;
import com.mfw.themis.common.constant.enums.TimeWindowEnum;
import com.mfw.themis.common.constant.enums.TimeWindowTypeEnum;
import com.mfw.themis.common.model.CustomTimeWindow;
import com.mfw.themis.common.model.TimeWindowOffset;
import com.mfw.themis.common.model.bo.AlarmMetricBO;
import com.mfw.themis.common.model.bo.SuggestBO;
import com.mfw.themis.common.model.dto.AlarmMetricDTO;
import com.mfw.themis.dao.po.AlarmMetricPO;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;


/**
 * @author guosp
 */
public class AlarmMetricConvert {

    public static AlarmMetricDTO toDTO(AlarmMetricPO alarmMetric) {
        if (alarmMetric == null) {
            return null;
        }
        AlarmMetricDTO dto = AlarmMetricDTO.builder().build();
        BeanUtils.copyProperties(alarmMetric, dto);
        dto.setCollectType(CollectTypeEnum.getByCode(alarmMetric.getCollectType()));
        dto.setTimeWindowType(TimeWindowTypeEnum.getByCode(alarmMetric.getTimeWindowType()));
        dto.setTimeWindow(TimeWindowEnum.getByCode(alarmMetric.getTimeWindow()));
        dto.setMetricType(MetricTypeEnum.getByCode(alarmMetric.getMetricType()));
        dto.setSourceType(DataSourceTypeEnum.getByCode(alarmMetric.getSourceType()));
        dto.setGroupType(GroupTypeEnum.getByCode(alarmMetric.getGroupType()));
        dto.setUnit(AlarmMetricUnitEnum.getByCode(alarmMetric.getMetricUnit()));

        try{
            Object jsonObj = JSON.parse(alarmMetric.getExpression());
            if(jsonObj instanceof JSONObject){
                dto.setExpressionJson(JSON.parseObject(alarmMetric.getExpression()));
            }else if(jsonObj instanceof JSONArray){
                dto.setExpressionJson(JSON.parseArray(alarmMetric.getExpression()));
            }
        }catch (Exception e){
            dto.setExpressionJson(alarmMetric.getExpression());
        }

        // 自定义时间窗口
        if(alarmMetric.getTimeWindowType().equals(TimeWindowTypeEnum.CUSTOME.getCode())){
            CustomTimeWindow timeWindow = JSONObject.parseObject(alarmMetric.getCustomTimeWindow(), CustomTimeWindow.class);
            dto.setCustomTimeWindow(timeWindow);
        }
        // 时间窗口偏移量
        if(StringUtils.isNotBlank(alarmMetric.getTimeWindowOffset())){
            TimeWindowOffset timeWindowOffset = JSONObject.parseObject(alarmMetric.getTimeWindowOffset(), TimeWindowOffset.class);
            dto.setTimeWindowOffset(timeWindowOffset);
        }

        if (StringUtils.isNotBlank(alarmMetric.getMetricTag())) {
            dto.setMetricTag(Arrays.asList(StringUtils.split(alarmMetric.getMetricTag(), ",")));
        }
        return dto;
    }

    public static AlarmMetricBO toBO(AlarmMetricPO alarmMetric) {
        if (alarmMetric == null) {
            return null;
        }
        AlarmMetricBO bo = AlarmMetricBO.builder().build();
        BeanUtils.copyProperties(toDTO(alarmMetric),bo );

        return bo;
    }

    public static AlarmMetricPO toPO(AlarmMetricDTO alarmMetricDTO) {
        if (alarmMetricDTO == null) {
            return null;
        }
        AlarmMetricPO po = new AlarmMetricPO();
        BeanUtils.copyProperties(alarmMetricDTO, po);
        po.setCollectType(alarmMetricDTO.getCollectType().getCode());
        po.setTimeWindow(alarmMetricDTO.getTimeWindow().getCode());
        po.setMetricType(alarmMetricDTO.getMetricType().getCode());
        po.setSourceType(alarmMetricDTO.getSourceType().getCode());
        if (alarmMetricDTO.getMetricTag() != null) {
            po.setMetricTag(String.join(",", alarmMetricDTO.getMetricTag()));
        }

        if (alarmMetricDTO.getGroupType() != null) {
            po.setGroupType(alarmMetricDTO.getGroupType().getCode());
        }
        po.setMetricUnit(alarmMetricDTO.getUnit().getCode());
        return po;
    }

    public static List<AlarmMetricDTO> toDTOList(List<AlarmMetricPO> metricPOList) {
        if (CollectionUtils.isEmpty(metricPOList)) {
            return Lists.newArrayList();
        }
        return metricPOList.stream().map(AlarmMetricConvert::toDTO).collect(Collectors.toList());
    }

    public static SuggestBO toSuggestBO(AlarmMetricPO alarmMetric) {
        if (alarmMetric == null) {
            return null;
        }

        SuggestBO bo = SuggestBO.builder().build();
        BeanUtils.copyProperties(alarmMetric, bo);

        return bo;
    }

    public static List<SuggestBO> toSuggestBOList(List<AlarmMetricPO> metricPOList){
        if (CollectionUtils.isEmpty(metricPOList)) {
            return Lists.newArrayList();
        }
        return metricPOList.stream().map(AlarmMetricConvert::toSuggestBO).collect(Collectors.toList());
    }
}
