package com.mfw.themis.portal.service;

import com.mfw.themis.common.model.dto.AlarmLevelDTO;

import com.mfw.themis.portal.controller.model.AlarmLevelReqDTO;
import java.util.List;

/**
 * @author liuqi
 */
public interface AlarmLevelService {

    /**
     * 报警等级列表
     * @return
     */
    List<AlarmLevelDTO> alarmLevelList();

    /**
     * 创建报警等级
     *
     * @param req
     * @return
     */
    AlarmLevelDTO create(AlarmLevelReqDTO req);

    /**
     * 更新报警等级配置
     *
     * @param req
     */
    Boolean update(AlarmLevelReqDTO req);

    /**
     * 删除报警等级
     *
     * @param id
     */
    Boolean delete(Long id);

}
