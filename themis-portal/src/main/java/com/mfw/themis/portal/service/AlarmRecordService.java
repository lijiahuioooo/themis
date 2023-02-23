package com.mfw.themis.portal.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mfw.themis.common.model.bo.AlarmRecordBO;
import com.mfw.themis.common.model.dto.AlarmRecordDTO;
import com.mfw.themis.portal.model.dto.QueryAlarmRecordParam;

/**
 * @author wenhong
 */
public interface AlarmRecordService {

    /**
     * 获取记录详情
     * @param id
     * @return
     */
    AlarmRecordDTO queryById(Long id);

    /**
     * 获取应用报警记录列表
     * @param queryAlarmRecordParam
     * @return
     */
    IPage<AlarmRecordBO> queryAlarmRecordPage(QueryAlarmRecordParam queryAlarmRecordParam);
}
