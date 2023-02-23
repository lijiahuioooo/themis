package com.mfw.themis.portal.controller;

import com.mfw.themis.common.constant.FieldTipConstant;
import com.mfw.themis.common.constant.enums.AlarmMetricUnitEnum;
import com.mfw.themis.common.constant.enums.AppSourceEnum;
import com.mfw.themis.common.constant.enums.CollectTypeEnum;
import com.mfw.themis.common.constant.enums.DataSourceTypeEnum;
import com.mfw.themis.common.constant.enums.FilterMetricOperatorEnum;
import com.mfw.themis.common.constant.enums.GroupTypeEnum;
import com.mfw.themis.common.constant.enums.MetricTypeEnum;
import com.mfw.themis.common.constant.enums.ProjectTypeEnum;
import com.mfw.themis.common.constant.enums.TimeWindowEnum;
import com.mfw.themis.common.model.ResponseResult;
import com.mfw.themis.common.util.EnumUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Tip字段提示接口
 * @author wenhong
 */
@RestController
@RequestMapping(value = "/portal/tip/")
@Slf4j
public class TipController {

    /**
     * 指标Tip接口
     * @see <a href=https://yapi.mfwdev.com/project/1951/interface/api/28961>指标Tip接口</a>
     */
    @GetMapping("metric")
    public ResponseResult<Map<String, String>> metricTip(){
        Map<String, String> metricTip = new HashMap<>();
        metricTip.put("sourceType", FieldTipConstant.TIP_SOURCE_TYPE);
        metricTip.put("metricType", FieldTipConstant.TIP_METRIC_TYPE);
        metricTip.put("collectType", FieldTipConstant.TIP_COLLECT_TYPE);
        metricTip.put("collectId", FieldTipConstant.TIP_COLLECT_ID);

        return ResponseResult.OK(metricTip);
    }

    /**
     * 报警规则Tip接口
     * @see <a href=https://yapi.mfwdev.com/project/1951/interface/api/29221>报警规则Tip接口</a>
     * @return
     */
    @GetMapping("alarmRule")
    public ResponseResult<Map<String, String>> alarmRuleTip(){
        Map<String, String> metricTip = new HashMap<>();
        metricTip.put("alarmLevel", FieldTipConstant.TIP_ALARM_LEVEL);

        return ResponseResult.OK(metricTip);
    }

    /**
     * 上报事件Tip接口
     * @see <a href=https://yapi.mfwdev.com/project/1951/interface/api/29653>指标Tip接口</a>
     */
    @GetMapping("collect/report")
    public ResponseResult<Map<String, String>> collectReportTip(){
        Map<String, String> metricTip = new HashMap<>();
        metricTip.put("metric", FieldTipConstant.TIP_COLLECT_METRIC);
        metricTip.put("tag", FieldTipConstant.TIP_COLLECT_TAG);
        metricTip.put("field", FieldTipConstant.TIP_COLLECT_FIELD);

        return ResponseResult.OK(metricTip);
    }

    /**
     * 指标模板Tip接口
     * @see <a href=https://yapi.mfwdev.com/project/1951/interface/api/29653>指标模板Tip接口</a>
     */
    @GetMapping("alarmMetric")
    public ResponseResult<Map<String, String>> alarmMetricTip(){
        Map<String, String> metricTip = new HashMap<>();
        metricTip.put("sourceType", FieldTipConstant.TIP_SOURCE_TYPE);
        metricTip.put("metricType", FieldTipConstant.TIP_METRIC_TYPE);
        metricTip.put("collectType", FieldTipConstant.TIP_COLLECT_TYPE);
        metricTip.put("metricTag", FieldTipConstant.TIP_METRIC_TAG);

        return ResponseResult.OK(metricTip);
    }
}
