package com.mfw.themis.common.convert;

import com.google.common.collect.Lists;
import com.mfw.themis.common.constant.enums.CompareTypeEnum;
import com.mfw.themis.common.constant.enums.EnableEnum;
import com.mfw.themis.common.model.bo.AlarmRecordBO;
import com.mfw.themis.common.model.dto.AlarmRecordDTO;
import com.mfw.themis.dao.po.AlarmRecordPO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wenhong
 */
public class AlarmRecordConvert {

    public static AlarmRecordDTO toDTO(AlarmRecordPO alarmRecord) {
        if (alarmRecord == null) {
            return null;
        }
        AlarmRecordDTO alarmRecordDTO = AlarmRecordDTO.builder().build();
        BeanUtils.copyProperties(alarmRecord, alarmRecordDTO);

        return alarmRecordDTO;
    }

    public static AlarmRecordPO toPO(AlarmRecordDTO alarmRecord) {
        if (alarmRecord == null) {
            return null;
        }
        AlarmRecordPO po = new AlarmRecordPO();
        BeanUtils.copyProperties(alarmRecord, po);

        return po;
    }

    public static AlarmRecordBO toBO(AlarmRecordPO alarmRecord) {
        if (alarmRecord == null) {
            return null;
        }
        AlarmRecordBO bo = new AlarmRecordBO();
        BeanUtils.copyProperties(alarmRecord, bo);

        return bo;
    }


    public static List<AlarmRecordDTO> toDTOList(List<AlarmRecordPO> metricPOList) {
        if (CollectionUtils.isEmpty(metricPOList)) {
            return Lists.newArrayList();
        }
        return metricPOList.stream().map(AlarmRecordConvert::toDTO).collect(Collectors.toList());
    }

    public static List<AlarmRecordBO> toBOList(List<AlarmRecordPO> poList) {
        if (CollectionUtils.isEmpty(poList)) {
            return Lists.newArrayList();
        }
        return poList.stream().map(AlarmRecordConvert::toBO).collect(Collectors.toList());
    }
}
