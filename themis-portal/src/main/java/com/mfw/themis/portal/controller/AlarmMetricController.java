package com.mfw.themis.portal.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mfw.themis.common.model.ResponseResult;
import com.mfw.themis.common.model.bo.AlarmMetricBO;
import com.mfw.themis.common.model.dto.AlarmMetricDTO;
import com.mfw.themis.common.model.dto.SaveAlarmMetricDTO;
import com.mfw.themis.portal.model.dto.QueryAlarmMetricDTO;
import com.mfw.themis.portal.service.AlarmMetricService;
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
@RequestMapping(value = "/portal/alarmMetric")
@Slf4j
public class AlarmMetricController {

    @Autowired
    private AlarmMetricService alarmMetricService;

    @PostMapping(value = "/queryMetricPage")
    public ResponseResult<IPage<AlarmMetricDTO>> queryMetricPage(@RequestBody @Validated QueryAlarmMetricDTO req) {

        ResponseResult<IPage<AlarmMetricDTO>> result = ResponseResult.OK(alarmMetricService.queryMetricPage(req));
        return result;
    }

    @GetMapping(value = "/queryById")
    public ResponseResult<AlarmMetricBO> queryById(Long id) {
        ResponseResult<AlarmMetricBO> result = ResponseResult.OK(alarmMetricService.queryById(id));
        return result;
    }

    @PostMapping("/create")
    public ResponseResult<SaveAlarmMetricDTO> create(@RequestBody @Validated SaveAlarmMetricDTO req) {
        ResponseResult<SaveAlarmMetricDTO> result = ResponseResult.OK(alarmMetricService.create(req));
        return result;
    }

    @PostMapping("/update")
    public ResponseResult<Boolean> update(@RequestBody SaveAlarmMetricDTO req) {
        ResponseResult<Boolean> result = ResponseResult.OK(alarmMetricService.update(req));
        return result;
    }

    @PostMapping("/delete")
    public ResponseResult<Boolean> delete(Long id) {
        ResponseResult<Boolean> result = ResponseResult.OK(alarmMetricService.delete(id));
        return result;
    }
}
