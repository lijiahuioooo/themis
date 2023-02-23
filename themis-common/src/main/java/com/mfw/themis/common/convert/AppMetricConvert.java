package com.mfw.themis.common.convert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.mfw.themis.common.constant.enums.CollectTypeEnum;
import com.mfw.themis.common.constant.enums.CustomTimeWindowUnitEnum;
import com.mfw.themis.common.constant.enums.DataSourceTypeEnum;
import com.mfw.themis.common.constant.enums.EnableEnum;
import com.mfw.themis.common.constant.enums.TimeWindowTypeEnum;
import com.mfw.themis.common.model.CustomTimeWindow;
import com.mfw.themis.common.model.TimeWindowOffset;
import com.mfw.themis.common.model.bo.AppMetricBO;
import com.mfw.themis.common.model.bo.SuggestBO;
import com.mfw.themis.common.model.bo.user.UserAppMetricBO;
import com.mfw.themis.common.model.dto.AppMetricDTO;
import com.mfw.themis.common.util.PlaceHolderUtils;
import com.mfw.themis.dao.po.AppMetricPO;
import com.mfw.themis.dao.po.union.AppMetricUnionPO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;


/**
 * @author guosp
 */
@Slf4j
public class AppMetricConvert {

    public static AppMetricDTO toDTO(AppMetricPO appMetricPO) {
        if (appMetricPO == null) {
            return null;
        }
        AppMetricDTO appMetricDTO = AppMetricDTO.builder().build();
        BeanUtils.copyProperties(appMetricPO, appMetricDTO);
        appMetricDTO.setStatus(EnableEnum.getByCode(appMetricPO.getStatus()));

        if (StringUtils.isNotBlank(appMetricPO.getAttrValue())) {
            try {
                JSONObject jsonObject = JSON.parseObject(appMetricPO.getAttrValue());
                Map<String, String> attrValue = new HashMap<>(jsonObject.size());
                jsonObject.forEach((k, v) -> attrValue.put(k, Objects.toString(v)));
                appMetricDTO.setAttrValue(attrValue);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        return appMetricDTO;
    }

    public static AppMetricPO toPO(AppMetricDTO appMetricDTO) {
        if (appMetricDTO == null) {
            return null;
        }
        AppMetricPO po = new AppMetricPO();
        BeanUtils.copyProperties(appMetricDTO, po);
        if (null != appMetricDTO.getStatus()) {
            po.setStatus(appMetricDTO.getStatus().getCode());
        }
        if (null != appMetricDTO.getAttrValue() && !appMetricDTO.getAttrValue().isEmpty()) {
            po.setAttrValue(JSON.toJSONString(appMetricDTO.getAttrValue()));
        }

        return po;
    }

    public static List<AppMetricDTO> toDTOList(List<AppMetricPO> apps) {
        List<AppMetricDTO> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(apps)) {
            apps.forEach(appMetricPO -> list.add(toDTO(appMetricPO)));
        }
        return list;
    }

    public static AppMetricBO toAppMetricBO(AppMetricUnionPO po) {
        if (po == null) {
            return null;
        }

        AppMetricBO appMetricBO = AppMetricBO.builder().build();
        appMetricBO.setTimeWindowType(TimeWindowTypeEnum.getByCode(po.getTimeWindowType()));
        BeanUtils.copyProperties(po, appMetricBO);

        // set expression list
        if(po.getSourceType().equals(DataSourceTypeEnum.PROMETHEUS.getCode())){
            List<Map<String, Object>> expressionList = new ArrayList<>();
            List<String> placeHolderKeyList = PlaceHolderUtils.getPlaceHolderKeyList(appMetricBO.getExpression());
            if(StringUtils.isNotEmpty(appMetricBO.getAttrValue())){
                try{
                    Map<String, Object> attrMap = new HashMap<>();
                    JSONObject jsonObject = JSON.parseObject(appMetricBO.getAttrValue());
                    placeHolderKeyList.forEach(placeHolder -> {
                        if(jsonObject.containsKey(placeHolder)){
                            attrMap.put("metric", placeHolder);
                            attrMap.put("filterMetricOperator", "is");
                            attrMap.put("metricValue", jsonObject.get(placeHolder));
                        }
                    });

                    expressionList.add(attrMap);
                    appMetricBO.setExpressionList(expressionList);
                }catch (Exception e){
                    log.error("parse attr_value error, attr_value:{}, error:{}", appMetricBO.getAttrValue(), e);
                }
            }else{
                Map<String, Object> existMap = new HashMap<>();
                placeHolderKeyList.forEach(placeHolder -> {
                    if(existMap.containsKey(placeHolder)){
                        return;
                    }

                    Map<String, Object> attrMap = new HashMap<>();
                    existMap.put(placeHolder, "");
                    attrMap.put("metric", placeHolder);
                    attrMap.put("filterMetricOperator", "is");
                    attrMap.put("metricValue", "");
                    expressionList.add(attrMap);
                });

                appMetricBO.setExpressionList(expressionList);
            }
        }


        if(po.getSourceType().equals(DataSourceTypeEnum.ELASTIC_SEARCH.getCode())
            && po.getCollectType().equals(CollectTypeEnum.SINGLE_METRIC.getCode())){

            try{
                if(StringUtils.isNotBlank(appMetricBO.getExpression())){
                    List jsonArray = JSON.parseArray(appMetricBO.getExpression());
                    appMetricBO.setExpressionList(jsonArray);
                }

            }catch (Exception e){
                log.error("parse expression error, expression:{}, error:{}", appMetricBO.getExpression(), e);
            }
        }

        if(StringUtils.isNotBlank(po.getTimeWindowOffset())){
            appMetricBO.setTimeWindowOffset(JSONObject.parseObject(po.getTimeWindowOffset(), TimeWindowOffset.class));
        }else{
            TimeWindowOffset timeWindowOffset = new TimeWindowOffset();
            timeWindowOffset.setUnit(CustomTimeWindowUnitEnum.DAY.getCode());
            timeWindowOffset.setValue(0);
            appMetricBO.setTimeWindowOffset(timeWindowOffset);
        }

        if(StringUtils.isNotBlank(po.getCustomTimeWindow())){
            appMetricBO.setCustomTimeWindow(JSONObject.parseObject(po.getCustomTimeWindow(), CustomTimeWindow.class));
        }

        return appMetricBO;
    }

    public static SuggestBO toAppMetricSuggestBO(AppMetricUnionPO po){
        if (po == null) {
            return null;
        }

        SuggestBO suggestBO = SuggestBO.builder()
                .id(po.getId())
                .name("["+po.getId()+"]"+po.getName())
                .build();
        return suggestBO;
    }

    public static List<AppMetricBO> toAppMetricBOList(List<AppMetricUnionPO> apps) {

        if (CollectionUtils.isEmpty(apps)) {
            return Lists.newArrayList();
        }
        return apps.stream().map(AppMetricConvert::toAppMetricBO).collect(Collectors.toList());
    }

    public static UserAppMetricBO toUserAppMetricBO(AppMetricUnionPO po) {
        if (po == null) {
            return null;
        }
        UserAppMetricBO userAppMetricBO = UserAppMetricBO.builder().build();
        BeanUtils.copyProperties(po, userAppMetricBO);

        return userAppMetricBO;
    }

    public static List<UserAppMetricBO> toUserAppMetricBOList(List<AppMetricUnionPO> apps) {

        if (CollectionUtils.isEmpty(apps)) {
            return Lists.newArrayList();
        }
        return apps.stream().map(AppMetricConvert::toUserAppMetricBO).collect(Collectors.toList());
    }

    public static List<SuggestBO> toAppMetricSuggestBOList(List<AppMetricUnionPO> apps) {

        if (CollectionUtils.isEmpty(apps)) {
            return Lists.newArrayList();
        }
        return apps.stream().map(AppMetricConvert::toAppMetricSuggestBO).collect(Collectors.toList());
    }
}
