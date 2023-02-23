package com.mfw.themis.common.convert;

import com.google.common.collect.Lists;
import com.mfw.themis.common.constant.enums.AppSourceEnum;
import com.mfw.themis.common.constant.enums.ProjectTypeEnum;
import com.mfw.themis.common.model.bo.admin.AppSuggestBO;
import com.mfw.themis.common.model.dto.AppDTO;
import com.mfw.themis.dao.po.AppPO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liuqi
 */
public class AppConvert {

    public static AppDTO toDTO(AppPO app) {
        if (app == null) {
            return null;
        }
        AppDTO appDTO = AppDTO.builder().build();
        BeanUtils.copyProperties(app, appDTO);

        if(appDTO.getSource() > 0){
            appDTO.setSourceDesc(AppSourceEnum.getByCode(appDTO.getSource()).getDesc());
        }
        if(appDTO.getProjectType() > 0){
            appDTO.setProjectTypeDesc(ProjectTypeEnum.getByCode(appDTO.getProjectType()).getDesc());
        }

        return appDTO;
    }

    public static AppPO toPO(AppDTO app) {
        if (app == null) {
            return null;
        }
        AppPO po = new AppPO();
        BeanUtils.copyProperties(app, po);
        return po;
    }

    public static AppSuggestBO toSuggestBO(AppPO app){
        if (app == null) {
            return null;
        }
        AppSuggestBO bo = new AppSuggestBO();
        BeanUtils.copyProperties(app, bo);
        return bo;
    }

    public static List<AppDTO> toDTOList(List<AppPO> apps) {
        List<AppDTO> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(apps)) {
            apps.forEach(app -> list.add(toDTO(app)));
        }
        return list;
    }

    public static List<AppSuggestBO> toSuggestBOList(List<AppPO> apps) {
        if (CollectionUtils.isEmpty(apps)) {
            return Lists.newArrayList();
        }

        return apps.stream().map(AppConvert::toSuggestBO).collect(Collectors.toList());
    }
}
