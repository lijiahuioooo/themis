package com.mfw.themis.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mfw.themis.dao.mapper.mes.MesAlarmRuleDao;
import com.mfw.themis.dao.po.mes.AlarmRulePO;
import com.mfw.themis.portal.service.MoneRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MoneRuleServiceImpl implements MoneRuleService {

    @Autowired
    private MesAlarmRuleDao mesAlarmRuleDao;

    /**
     * 禁用报警规则
     * @param appCode
     * @return
     */
    @Override
    public Integer disableRuleByAppCode(String appCode){
        QueryWrapper<AlarmRulePO> alarmRulePOQueryWrapper = new QueryWrapper<>();

        alarmRulePOQueryWrapper.lambda().eq(AlarmRulePO::getAppCode, appCode);
        alarmRulePOQueryWrapper.lambda().eq(AlarmRulePO::getIsEnable, 1);

        List<AlarmRulePO> alarmRulePOList = mesAlarmRuleDao.selectList(alarmRulePOQueryWrapper);

        if(null == alarmRulePOList){
            return 0;
        }


        alarmRulePOList.forEach(alarmRulePO -> {
            alarmRulePO.setIsEnable(false);
            mesAlarmRuleDao.updateById(alarmRulePO);
        });

        return alarmRulePOList.size();
    }

    /**
     * 启用报警规则
     * @param appCode
     * @return
     */
    @Override
    public Integer enableRuleByAppCode(String appCode){
        QueryWrapper<AlarmRulePO> alarmRulePOQueryWrapper = new QueryWrapper<>();

        alarmRulePOQueryWrapper.lambda().eq(AlarmRulePO::getAppCode, appCode);
        alarmRulePOQueryWrapper.lambda().eq(AlarmRulePO::getIsEnable, 0);

        List<AlarmRulePO> alarmRulePOList = mesAlarmRuleDao.selectList(alarmRulePOQueryWrapper);

        if(null == alarmRulePOList){
            return 0;
        }


        alarmRulePOList.forEach(alarmRulePO -> {
            alarmRulePO.setIsEnable(true);
            mesAlarmRuleDao.updateById(alarmRulePO);
        });

        return alarmRulePOList.size();
    }
}
