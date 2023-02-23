package com.mfw.themis.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mfw.themis.dao.mapper.mes.MesAlarmRuleDao;
import com.mfw.themis.dao.po.mes.AlarmRulePO;
import com.mfw.themis.portal.model.dto.mes.QueryAlarmRuleDTO;
import com.mfw.themis.portal.service.MesAlarmRuleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MesAlarmRuleServiceImpl implements MesAlarmRuleService {

    @Autowired
    private MesAlarmRuleDao mesAlarmRuleDao;

    /**
     * 获取大交通报警规则列表
     * @param queryAlarmRuleDTO
     * @return
     */
    @Override
    public IPage<AlarmRulePO> queryPageByAppCode(QueryAlarmRuleDTO queryAlarmRuleDTO){

        QueryWrapper<AlarmRulePO> wrapper = new QueryWrapper<>();

        wrapper.lambda().eq(AlarmRulePO::getAppCode, queryAlarmRuleDTO.getAppCode());

        Page<AlarmRulePO> page = new Page<>(queryAlarmRuleDTO.getPage(), queryAlarmRuleDTO.getPageSize());
        IPage<AlarmRulePO> dbResult = mesAlarmRuleDao.selectPage(page, wrapper);

        IPage<AlarmRulePO> result = new Page<>(queryAlarmRuleDTO.getPage(), queryAlarmRuleDTO.getPageSize());
        BeanUtils.copyProperties(dbResult, result);

        return result;
    }
}
