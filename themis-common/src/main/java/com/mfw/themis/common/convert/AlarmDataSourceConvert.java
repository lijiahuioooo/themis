package com.mfw.themis.common.convert;

import com.mfw.themis.common.constant.enums.DataSourceTypeEnum;
import com.mfw.themis.common.model.dto.AlarmDataSourceDTO;
import com.mfw.themis.dao.po.AlarmDataSourcePO;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;

public class AlarmDataSourceConvert {

    public static AlarmDataSourceDTO toDTO(AlarmDataSourcePO alarmDataSource) {
        if (alarmDataSource == null) {
            return null;
        }
        AlarmDataSourceDTO dto = AlarmDataSourceDTO.builder().build();
        BeanUtils.copyProperties(alarmDataSource, dto);
        dto.setType(DataSourceTypeEnum.getByCode(alarmDataSource.getType()));
        return dto;
    }

    public static AlarmDataSourcePO toPO(AlarmDataSourceDTO alarmDataSource) {
        if (alarmDataSource == null) {
            return null;
        }
        AlarmDataSourcePO po = new AlarmDataSourcePO();
        BeanUtils.copyProperties(alarmDataSource, po);
        po.setType(alarmDataSource.getType().getCode());
        return po;
    }

    public static List<AlarmDataSourceDTO> toDTOList(List<AlarmDataSourcePO> dataSourcePOList) {
        List<AlarmDataSourceDTO> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dataSourcePOList)) {
            dataSourcePOList.forEach(dataSourcePO -> list.add(toDTO(dataSourcePO)));
        }
        return list;
    }
}
