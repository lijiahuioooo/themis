package com.mfw.themis.portal.controller.admin;

import com.mfw.themis.common.model.ResponseResult;
import com.mfw.themis.common.model.dto.AlarmLevelDTO;
import com.mfw.themis.portal.controller.model.AlarmLevelReqDTO;
import com.mfw.themis.portal.service.AlarmLevelService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuqi
 */
@RestController
@RequestMapping(value = "/portal/admin/alarmLevel")
@Slf4j
public class AdminAlarmLevelController {

    @Autowired
    private AlarmLevelService alarmLevelService;

    @GetMapping(value = "/queryList")
    public ResponseResult<List<AlarmLevelDTO>> queryList() {
        return ResponseResult.OK(alarmLevelService.alarmLevelList());
    }


    @PostMapping("/create")
    public ResponseResult<AlarmLevelDTO> create(@RequestBody AlarmLevelReqDTO req) {
        return ResponseResult.OK(alarmLevelService.create(req));
    }

    @PostMapping("/update")
    public ResponseResult<Boolean> update(@RequestBody AlarmLevelReqDTO req) {
        Boolean res = alarmLevelService.update(req);
        return ResponseResult.OK(res);
    }

    @PostMapping("/delete")
    public ResponseResult<Boolean> delete(Long id) {
        Boolean isDel = alarmLevelService.delete(id);
        return ResponseResult.OK(isDel);
    }
}
