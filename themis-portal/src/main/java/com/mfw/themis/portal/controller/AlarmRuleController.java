package com.mfw.themis.portal.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mfw.themis.common.constant.enums.EnableEnum;
import com.mfw.themis.common.model.ResponseResult;
import com.mfw.themis.common.model.dto.AlarmRuleDTO;
import com.mfw.themis.portal.model.dto.QueryAlarmRuleParam;
import com.mfw.themis.portal.model.dto.QueryAlarmRuleResponse;
import com.mfw.themis.portal.service.AlarmRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author guosp
 */
@RestController
@RequestMapping(value = "/portal/alarmRule")
@Slf4j
public class AlarmRuleController {

    @Autowired
    private AlarmRuleService alarmRuleService;

    @GetMapping(value = "/queryById")
    public ResponseResult<AlarmRuleDTO> queryById(Long id) {
        ResponseResult<AlarmRuleDTO> result = ResponseResult.OK(alarmRuleService.queryById(id));
        return result;
    }

    @PostMapping(value = "/queryMetricPage")
    public ResponseResult<IPage<QueryAlarmRuleResponse>> queryMetricPage(@RequestBody @Validated QueryAlarmRuleParam req) {
        ResponseResult<IPage<QueryAlarmRuleResponse>> result = ResponseResult.OK(alarmRuleService.queryAlarmRulePage(req));
        return result;
    }

    @PostMapping("/create")
    public ResponseResult<AlarmRuleDTO> create(@RequestBody @Validated AlarmRuleDTO req) {
        ResponseResult<AlarmRuleDTO> result = ResponseResult.OK(alarmRuleService.create(req));
        return result;
    }


    @PostMapping("/update")
    public ResponseResult<Integer> update(@RequestBody AlarmRuleDTO req) {
        ResponseResult<Integer> result = ResponseResult.OK(alarmRuleService.update(req));
        return result;
    }

    @PostMapping(value = "/delete")
    public ResponseResult<Integer> delete(Long id) {
        ResponseResult<Integer> result = ResponseResult.OK(alarmRuleService.delete(id));
        return result;
    }

    @PostMapping(value = "/enable")
    public ResponseResult<Boolean> enable(Long id) {
        alarmRuleService.changeStatus(id, EnableEnum.ENABLE.getCode());
        return ResponseResult.OK(true);
    }

    @PostMapping(value = "/disable")
    public ResponseResult<Boolean> disable(Long id) {
        alarmRuleService.changeStatus(
                id, EnableEnum.DISABLE.getCode());
        return ResponseResult.OK(true);
    }
}