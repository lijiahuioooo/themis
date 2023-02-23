package com.mfw.themis.common.convert;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.mfw.themis.common.constant.enums.EnableEnum;
import com.mfw.themis.common.model.dto.CollectMetricFieldDTO;
import com.mfw.themis.dao.po.CollectMetricFieldPO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wenhong
 */
@Slf4j
public class CollectMetricFieldConvert {

    public static CollectMetricFieldPO toPO (CollectMetricFieldDTO collectMetricFieldDTO){
        if (collectMetricFieldDTO == null) {
            return null;
        }

        CollectMetricFieldPO po = new CollectMetricFieldPO();
        BeanUtils.copyProperties(collectMetricFieldDTO, po);

        if (null != collectMetricFieldDTO.getTags() && !CollectionUtils.isEmpty(collectMetricFieldDTO.getTags())){
            po.setTags(JSON.toJSONString(collectMetricFieldDTO.getTags()));
        }

        if (null != collectMetricFieldDTO.getFields() && !CollectionUtils.isEmpty(collectMetricFieldDTO.getFields())){
            po.setFields(JSON.toJSONString(collectMetricFieldDTO.getFields()));
        }

        return po;
    }

    public static CollectMetricFieldDTO toDTO (CollectMetricFieldPO collectMetricFieldPO){
        if (collectMetricFieldPO == null) {
            return null;
        }

        CollectMetricFieldDTO dto = new CollectMetricFieldDTO();
        BeanUtils.copyProperties(collectMetricFieldPO, dto);
        dto.setStatus(EnableEnum.getByCode(collectMetricFieldPO.getStatus()));

        try{
            if(StringUtils.isNotBlank(collectMetricFieldPO.getTags())){
                List<CollectMetricFieldDTO.Field> fieldList = JSON.parseArray(collectMetricFieldPO.getTags(), CollectMetricFieldDTO.Field.class);
                dto.setTags(fieldList);
            }

            if(StringUtils.isNotBlank(collectMetricFieldPO.getFields())){
                List<CollectMetricFieldDTO.Field> fieldList = JSON.parseArray(collectMetricFieldPO.getFields(), CollectMetricFieldDTO.Field.class);
                dto.setFields(fieldList);
            }
        }catch (Exception e){
            log.error("CollectMetricFieldDTO.toDTO parse json error.", e);
        }

        return dto;
    }

    public static List<CollectMetricFieldDTO> toDTOList(List<CollectMetricFieldPO> collectMetricFieldPOList) {
        if (CollectionUtils.isEmpty(collectMetricFieldPOList)) {
            return Lists.newArrayList();
        }
        return collectMetricFieldPOList.stream().map(CollectMetricFieldConvert::toDTO).collect(Collectors.toList());
    }
}
