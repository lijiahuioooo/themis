package com.mfw.themis.common.convert;

import com.google.common.collect.Lists;
import com.mfw.themis.common.constant.enums.CompareTypeEnum;
import com.mfw.themis.common.constant.enums.EnableEnum;
import com.mfw.themis.common.model.dto.AlarmRuleDTO;
import com.mfw.themis.dao.po.AlarmRulePO;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;

/**
 * @author liuqi
 */
public class AlarmRuleConvert {

    public static AlarmRuleDTO toDTO(AlarmRulePO alarmRule) {
        if (alarmRule == null) {
            return null;
        }
        AlarmRuleDTO alarmRuleDTO = AlarmRuleDTO.builder().build();
        BeanUtils.copyProperties(alarmRule, alarmRuleDTO);
        alarmRuleDTO.setStatus(EnableEnum.getByCode(alarmRule.getStatus()));

        alarmRuleDTO.setCompare(CompareTypeEnum.getByCode(alarmRule.getCompare()));
        return alarmRuleDTO;
    }

    public static AlarmRulePO toPO(AlarmRuleDTO alarmRule) {
        if (alarmRule == null) {
            return null;
        }
        AlarmRulePO po = new AlarmRulePO();
        BeanUtils.copyProperties(alarmRule, po);
        if (null != alarmRule.getStatus()) {
            po.setStatus(alarmRule.getStatus().getCode());
        }
        if (null != alarmRule.getCompare()) {
            po.setCompare(alarmRule.getCompare().getCode());
        }
        return po;
    }

    public static List<AlarmRuleDTO> toDTOList(List<AlarmRulePO> metricPOList) {
        if (CollectionUtils.isEmpty(metricPOList)) {
            return Lists.newArrayList();
        }
        return metricPOList.stream().map(AlarmRuleConvert::toDTO).collect(Collectors.toList());
    }
}
