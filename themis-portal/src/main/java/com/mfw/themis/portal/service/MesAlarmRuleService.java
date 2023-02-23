package com.mfw.themis.portal.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mfw.themis.portal.model.dto.mes.QueryAlarmRuleDTO;
import com.mfw.themis.dao.po.mes.AlarmRulePO;

/**
 * @author wenhong
 */
public interface MesAlarmRuleService {

    /**
     * 获取大交通报警规则列表
     * @param queryAlarmRuleDTO
     * @return
     */
    IPage<AlarmRulePO> queryPageByAppCode(QueryAlarmRuleDTO queryAlarmRuleDTO);
}
