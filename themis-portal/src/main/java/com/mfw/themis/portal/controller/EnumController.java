package com.mfw.themis.portal.controller;

import com.mfw.themis.common.constant.enums.AlarmMetricTagEnum;
import com.mfw.themis.common.constant.enums.AlarmMetricUnitEnum;
import com.mfw.themis.common.constant.enums.AppSourceEnum;
import com.mfw.themis.common.constant.enums.CollectTypeEnum;
import com.mfw.themis.common.constant.enums.CompareTypeEnum;
import com.mfw.themis.common.constant.enums.CustomTimeWindowUnitEnum;
import com.mfw.themis.common.constant.enums.DataSourceTypeEnum;
import com.mfw.themis.common.constant.enums.EnableEnum;
import com.mfw.themis.common.constant.enums.FilterMetricOperatorEnum;
import com.mfw.themis.common.constant.enums.GroupTypeEnum;
import com.mfw.themis.common.constant.enums.MetricTypeEnum;
import com.mfw.themis.common.constant.enums.ProjectTypeEnum;
import com.mfw.themis.common.constant.enums.RuleStatusEnum;
import com.mfw.themis.common.constant.enums.TimeWindowEnum;
import com.mfw.themis.common.model.ResponseResult;
import com.mfw.themis.common.model.dto.AlarmLevelDTO;
import com.mfw.themis.common.util.EnumUtils;
import com.mfw.themis.portal.service.AlarmLevelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 枚举接口
 * @author wenhong
 */
@RestController
@RequestMapping(value = "/portal/enum/")
@Slf4j
public class EnumController {

    @Autowired
    private AlarmLevelService alarmLevelService;

    /**
     * 指标枚举接口
     * @see <a href=https://yapi.mfwdev.com/project/1951/interface/api/28961>指标枚举接口</a>
     */
    @GetMapping("metric")
    public ResponseResult<Map<String, Object>> metricEnum(){
        String methodIdName = "getCode";
        String methodValueName = "getDesc";

        Map<String, Object> resMap = new HashMap<>();

        resMap.put("metricType", EnumUtils.enumToMapList(
                MetricTypeEnum.class, methodIdName, methodValueName));

        resMap.put("collectType", EnumUtils.enumToMapList(
                CollectTypeEnum.class, methodIdName, methodValueName));

        resMap.put("collectTypePrometheus", EnumUtils.enumToMapList(
                CollectTypeEnum.class, methodIdName, methodValueName)
                .stream().filter(item -> {
                    // 1:单一指标
                    return item.get("id").toString().equals("1");
                })
                .collect(Collectors.toList()));

        List<Map<String, Object>> datasource = EnumUtils.enumToMapList(
                DataSourceTypeEnum.class, methodIdName, methodValueName)
                .stream().filter(item -> {
                    // 2:prometheus  3:ES
                    return item.get("id").toString().equals("2") || item.get("id").toString().equals("3");
                })
                .collect(Collectors.toList());

        resMap.put("datasource", datasource);

        resMap.put("timeWindow", EnumUtils.enumToMapList(
                TimeWindowEnum.class, methodIdName, methodValueName));

        resMap.put("timeWindowOffsetUnit", EnumUtils.enumToMapList(
                CustomTimeWindowUnitEnum.class, methodIdName, methodValueName));

        resMap.put("metricUnit", EnumUtils.enumToMapList(
                AlarmMetricUnitEnum.class, methodIdName, methodValueName));

        resMap.put("groupType", EnumUtils.enumToMapList(
                GroupTypeEnum.class, methodIdName, methodValueName));

        resMap.put("metricOperatorEnum", EnumUtils.enumToMapList(
                FilterMetricOperatorEnum.class, "getValue", "getDescription"));

        return ResponseResult.OK(resMap);
    }

    /**
     * 应用枚举接口
     * @see <a href=https://yapi.mfwdev.com/project/1951/interface/api/28973>应用枚举接口</a>
     */
    @GetMapping("app")
    public ResponseResult<Map<String, Object>> appEnum(){
        String methodIdName = "getCode";
        String methodValueName = "getDesc";

        Map<String, Object> resMap = new HashMap<>();

        resMap.put("source", EnumUtils.enumToMapList(
                AppSourceEnum.class, methodIdName, methodValueName));

        resMap.put("projectType", EnumUtils.enumToMapList(
                ProjectTypeEnum.class, methodIdName, methodValueName));

        return ResponseResult.OK(resMap);
    }

    /**
     * 报警列表枚举接口
     * @see <a href=https://yapi.mfwdev.com/project/1951/interface/api/29161>报警列表接口</a>
     */
    @GetMapping("alarmRule")
    public ResponseResult<Map<String, Object>> alarmRuleEnum(){
        String methodIdName = "getCode";
        String methodValueName = "getDesc";

        Map<String, Object> resMap = new HashMap<>();

        List<AlarmLevelDTO> alarmLevelDTOList = alarmLevelService.alarmLevelList();
        List<Map<String, Object>> alarmLevelList = new ArrayList<>();
        alarmLevelDTOList.forEach(alarmLevelDTO -> {
            Map<String, Object> alarmLevel = new HashMap<>();
            alarmLevel.put("id", alarmLevelDTO.getId());
            alarmLevel.put("value", alarmLevelDTO.getLevel());
            alarmLevelList.add(alarmLevel);

        });
        resMap.put("alarmLevel", alarmLevelList);

        resMap.put("enable", EnumUtils.enumToMapList(
                EnableEnum.class, methodIdName, methodValueName));

        resMap.put("compareTypeEnum", EnumUtils.enumToMapList(
                CompareTypeEnum.class, "getCode", "getDescription"));

        return ResponseResult.OK(resMap);
    }

    /**
     * 报警记录枚举接口
     * @see <a href=https://yapi.mfwdev.com/project/1951/interface/api/29289>报警记录枚举接口</a>
     * @return
     */
    @GetMapping("alarmRecord")
    public ResponseResult<Map<String, Object>> alarmRecordEnum(){
        String methodIdName = "getCode";
        String methodValueName = "getDesc";

        Map<String, Object> resMap = new HashMap<>();

        resMap.put("ruleStatusEnum", EnumUtils.enumToMapList(
                RuleStatusEnum.class, methodIdName, methodValueName));

        return ResponseResult.OK(resMap);
    }

    /**
     * 指标模板枚举接口
     * @see <a href=https://yapi.mfwdev.com/project/1951/interface/api/30041>指标枚举接口</a>
     */
    @GetMapping("alarmMetric")
    public ResponseResult<Map<String, Object>> alarmMetricEnum(){
        String methodIdName = "getCode";
        String methodValueName = "getDesc";

        Map<String, Object> resMap = new HashMap<>();

        resMap.put("metricType", EnumUtils.enumToMapList(
                MetricTypeEnum.class, methodIdName, methodValueName));

        resMap.put("collectType", EnumUtils.enumToMapList(
                CollectTypeEnum.class, methodIdName, methodValueName)
                .stream().filter(item -> {
                    // 1:单一指标
                    return item.get("id").toString().equals("1");
                })
                .collect(Collectors.toList()));

        List<Map<String, Object>> datasource = EnumUtils.enumToMapList(
                DataSourceTypeEnum.class, methodIdName, methodValueName)
                .stream().filter(item -> {
                    // 2:prometheus
                    return item.get("id").toString().equals("2");
                })
                .collect(Collectors.toList());

        resMap.put("datasource", datasource);

        resMap.put("timeWindow", EnumUtils.enumToMapList(
                TimeWindowEnum.class, methodIdName, methodValueName));

        resMap.put("metricUnit", EnumUtils.enumToMapList(
                AlarmMetricUnitEnum.class, methodIdName, methodValueName));

        resMap.put("groupType", EnumUtils.enumToMapList(
                GroupTypeEnum.class, methodIdName, methodValueName));

        resMap.put("metricTag", AlarmMetricTagEnum.values());

        return ResponseResult.OK(resMap);
    }
}
