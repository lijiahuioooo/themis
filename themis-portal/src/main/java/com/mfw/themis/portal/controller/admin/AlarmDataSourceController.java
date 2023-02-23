package com.mfw.themis.portal.controller.admin;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mfw.themis.common.model.ResponseResult;
import com.mfw.themis.common.model.dto.AlarmDataSourceDTO;
import com.mfw.themis.portal.model.dto.QueryAlarmDataSourceDTO;
import com.mfw.themis.portal.service.AlarmDataSourceService;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping(value = "/portal/admin")
@Slf4j
public class AlarmDataSourceController {

    @Autowired
    private AlarmDataSourceService alarmDataSourceService;

    @GetMapping(value = "/datasources")
    public ResponseResult<IPage<AlarmDataSourceDTO>> queryDataSourcePage(
            @Validated QueryAlarmDataSourceDTO req) {
        log.info("Enter method queryDataSourcePage. req:{}", req);
        ResponseResult<IPage<AlarmDataSourceDTO>> result = ResponseResult
                .OK(alarmDataSourceService.queryDataSourcePage(req));
        log.info("End method queryDataSourcePage. response:{}", JSON.toJSONString(result));
        return result;
    }

    @GetMapping(value = "/datasource/{id}")
    public ResponseResult<AlarmDataSourceDTO> queryById(@PathVariable Long id) {
        log.info("Enter method queryById. id:{}", id);
        ResponseResult<AlarmDataSourceDTO> result = ResponseResult.OK(alarmDataSourceService.queryById(id));
        log.info("End method queryById. response:{}", JSON.toJSONString(result));
        return result;
    }

    @PostMapping("/datasource/create")
    public ResponseResult<AlarmDataSourceDTO> create(@RequestBody @Validated AlarmDataSourceDTO req) {
        log.info("Enter method create. req:{}", JSON.toJSONString(req));
        ResponseResult<AlarmDataSourceDTO> result = ResponseResult.OK(alarmDataSourceService.create(req));
        log.info("End method create. res:{}", JSON.toJSONString(result));
        return result;
    }

    @PostMapping("/datasource/update")
    public ResponseResult<Boolean> update(@RequestBody AlarmDataSourceDTO req) {
        log.info("Enter method update. req:{}", JSON.toJSONString(req));
        ResponseResult<Boolean> result = ResponseResult.OK(alarmDataSourceService.update(req));
        log.info("End method update. res:{}", JSON.toJSONString(result));
        return result;
    }

    @PostMapping("/datasource/delete")
    public ResponseResult<Boolean> delete(@RequestBody AlarmDataSourceDTO req) {
        log.info("Enter method delete. req:{}", req);
        ResponseResult<Boolean> result = ResponseResult.OK(alarmDataSourceService.delete(req.getId()));
        log.info("End method update. res:{}", JSON.toJSONString(result));
        return result;
    }
}
