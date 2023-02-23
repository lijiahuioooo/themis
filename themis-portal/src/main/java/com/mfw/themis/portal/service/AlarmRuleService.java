package com.mfw.themis.portal.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mfw.themis.common.model.dto.AlarmRuleDTO;
import com.mfw.themis.portal.model.dto.QueryAlarmRuleParam;
import com.mfw.themis.portal.model.dto.QueryAlarmRuleResponse;

/**
 * @author wenhong
 */
public interface AlarmRuleService {

    /**
     * 获取规则详情
     * @param id
     * @return
     */
    AlarmRuleDTO queryById(Long id);

    /**
     * 获取应用报警规则列表
     * @param queryAlarmRuleParam
     * @return
     */
    IPage<QueryAlarmRuleResponse> queryAlarmRulePage(QueryAlarmRuleParam queryAlarmRuleParam);

    /**
     * 创建告警规则
     * @param alarmRuleDTO
     * @return
     */
    AlarmRuleDTO create(AlarmRuleDTO alarmRuleDTO);

    /**
     * 更新规则
     * @param alarmRuleDTO
     * @return
     */
    Integer update(AlarmRuleDTO alarmRuleDTO);

    /**
     * 删除规则
     * @param id
     * @return
     */
    Integer delete(Long id);

    /**
     * 启用/禁用应用指标关系
     * @param id
     * @param status
     * @return
     */
    Integer changeStatus(Long id,Integer status);
}
