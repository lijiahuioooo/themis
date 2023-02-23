package com.mfw.themis.common.convert;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.mfw.themis.common.constant.enums.AlertRateTypeEnum;
import com.mfw.themis.common.model.AlertRate;
import com.mfw.themis.common.model.dto.AlarmLevelDTO;
import com.mfw.themis.dao.po.AlarmLevelPO;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import java.util.List;

/**
 * @author liuqi
 */
public class AlarmLevelConvert {

    public static AlarmLevelDTO toDTO(AlarmLevelPO alarmLevel) {
        if (alarmLevel == null) {
            return null;
        }
        AlarmLevelDTO dto = AlarmLevelDTO.builder().build();
        BeanUtils.copyProperties(alarmLevel, dto);

        AlertRate alertRate = JSONObject.parseObject(alarmLevel.getAlarmRate(), AlertRate.class);
        dto.setAlarmRate(alertRate);

        return dto;
    }

    public static AlarmLevelPO toPO(AlarmLevelDTO alarmLevel) {
        if (alarmLevel == null) {
            return null;
        }
        AlarmLevelPO po = new AlarmLevelPO();
        BeanUtils.copyProperties(alarmLevel, po);

        Map<String, Object> rate = new HashMap<>();
        rate.put("rateType", alarmLevel.getAlarmRate().getRateType().getCode());
        rate.put("rateInterval", alarmLevel.getAlarmRate().getRateInterval());
        rate.put("rateTimes", alarmLevel.getAlarmRate().getRateTimes());

        po.setAlarmRate(JSONObject.toJSONString(rate));

        return po;
    }

    public static List<AlarmLevelDTO> toDTOList(List<AlarmLevelPO> alarmLevelPOList) {
        if (CollectionUtils.isEmpty(alarmLevelPOList)) {
            return Lists.newArrayList();
        }
        return alarmLevelPOList.stream().map(AlarmLevelConvert::toDTO).collect(Collectors.toList());
    }
}
