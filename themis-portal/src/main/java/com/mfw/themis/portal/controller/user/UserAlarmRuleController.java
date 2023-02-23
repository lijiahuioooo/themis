package com.mfw.themis.portal.controller.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mfw.themis.common.constant.enums.EnableEnum;
import com.mfw.themis.common.model.ResponseResult;
import com.mfw.themis.common.model.dto.AlarmRuleDTO;
import com.mfw.themis.portal.model.dto.QueryAlarmRuleParam;
import com.mfw.themis.portal.model.dto.QueryAlarmRuleResponse;
import com.mfw.themis.portal.service.AlarmRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author guosp
 */
@RestController
@RequestMapping(value = "/portal/user")
public class UserAlarmRuleController {

    @Autowired
    private AlarmRuleService alarmRuleService;

    @GetMapping(value = "/alarmRule/{id}")
    public ResponseResult<AlarmRuleDTO> queryById(@PathVariable Long id) {
        return ResponseResult.OK(alarmRuleService.queryById(id));
    }

    @GetMapping(value = "/alarmRules")
    public ResponseResult<IPage<QueryAlarmRuleResponse>> queryMetricPage(@Validated QueryAlarmRuleParam req) {
        return ResponseResult.OK(alarmRuleService.queryAlarmRulePage(req));
    }

    @PostMapping("/alarmRule/create")
    public ResponseResult<AlarmRuleDTO> create(@RequestBody @Validated AlarmRuleDTO req) {
        return ResponseResult.OK(alarmRuleService.create(req));
    }


    @PostMapping("/alarmRule/update")
    public ResponseResult<Integer> update(@RequestBody @Validated AlarmRuleDTO req) {
        return ResponseResult.OK(alarmRuleService.update(req));
    }

    @PostMapping(value = "/alarmRule/delete")
    public ResponseResult<Integer> delete(@RequestBody AlarmRuleDTO req) {
        return ResponseResult.OK(alarmRuleService.delete(req.getId()));
    }

    @PostMapping(value = "/alarmRule/enable")
    public ResponseResult<Integer> enable(@RequestBody AlarmRuleDTO req) {
        return ResponseResult.OK(alarmRuleService.changeStatus(
                req.getId(), EnableEnum.ENABLE.getCode()));
    }

    @PostMapping(value = "/alarmRule/disable")
    public ResponseResult<Integer> disable(@RequestBody AlarmRuleDTO req) {
        return ResponseResult.OK(alarmRuleService.changeStatus(
                req.getId(), EnableEnum.DISABLE.getCode()));
    }
}