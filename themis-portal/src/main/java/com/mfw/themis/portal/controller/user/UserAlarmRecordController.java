package com.mfw.themis.portal.controller.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mfw.themis.common.constant.enums.RuleStatusEnum;
import com.mfw.themis.common.exception.ServiceException;
import com.mfw.themis.common.model.ResponseResult;
import com.mfw.themis.common.model.bo.AlarmRecordBO;
import com.mfw.themis.common.util.DateFormatUtils;
import com.mfw.themis.portal.model.dto.QueryAlarmRecordParam;
import com.mfw.themis.portal.service.AlarmRecordService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping(value = "/portal/user")
public class UserAlarmRecordController {

    @Autowired
    private AlarmRecordService alarmRecordService;

    /**
     * 报警列表
     * @param appId
     * @param startTime
     * @param endTime
     * @param status
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/alarmRecord/list")
    public ResponseResult<IPage<AlarmRecordBO>> queryMetricPage(
            @RequestParam(value = "appId", defaultValue = "0") Long appId,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        QueryAlarmRecordParam queryAlarmRecordParam = new QueryAlarmRecordParam();
        queryAlarmRecordParam.setPage(page);
        queryAlarmRecordParam.setPageSize(pageSize);

        if(appId <= 0){
            throw new ServiceException("appId不能为空");
        }
        queryAlarmRecordParam.setAppId(appId);

        if(null != status && null != RuleStatusEnum.getByCode(status)){
            queryAlarmRecordParam.setStatus(status);
        }

        try{
            if(StringUtils.isNotBlank(startTime)){
                queryAlarmRecordParam.setStartTime(DateUtils.parseDate(startTime, "yyyy-MM-dd HH:mm:ss"));
            }
            if(StringUtils.isNotBlank(endTime)){
                queryAlarmRecordParam.setEndTime(DateUtils.parseDate(endTime, "yyyy-MM-dd HH:mm:ss"));
            }
        }catch (Exception e){
            throw new ServiceException("日期格式不正确");
        }

        return ResponseResult.OK(alarmRecordService.queryAlarmRecordPage(queryAlarmRecordParam));
    }


}
