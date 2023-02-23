package com.mfw.themis.alarm.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mfw.themis.common.constant.enums.ResourceEnum;
import com.mfw.themis.common.constant.enums.RuleStatusEnum;
import com.mfw.themis.common.exception.ResourceNotFoundException;
import com.mfw.themis.common.model.message.AlertMessage;
import com.mfw.themis.dao.mapper.AlarmLevelDao;
import com.mfw.themis.dao.mapper.AlarmRecordDao;
import com.mfw.themis.dao.mapper.AlarmRuleDao;
import com.mfw.themis.dao.po.AlarmLevelPO;
import com.mfw.themis.dao.po.AlarmRecordPO;
import com.mfw.themis.dao.po.AlarmRulePO;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 报警记录
 *
 * @author liuqi
 */
@Component
public class AlarmRecordManager {


    @Autowired
    private AlarmRuleDao alarmRuleDao;
    @Autowired
    private AlarmRecordDao alarmRecordDao;
    @Autowired
    private AlarmLevelDao alarmLevelDao;

    /**
     * 查询故障中的报警记录
     *
     * @param appId    应用id
     * @param ruleId   规则id
     * @param endPoint 节点
     * @return
     */
    public AlarmRecordPO queryExistingAlarm(Long appId, Long ruleId, String endPoint) {
        LambdaQueryWrapper<AlarmRecordPO> wapper = new LambdaQueryWrapper<AlarmRecordPO>()
                .eq(AlarmRecordPO::getAppId, appId)
                .eq(AlarmRecordPO::getRuleId, ruleId)
                .eq(AlarmRecordPO::getStatus, RuleStatusEnum.IN_ERROR.getCode());

        if (StringUtils.isNotEmpty(endPoint)) {
            wapper.eq(AlarmRecordPO::getEndPoint, endPoint);
        }

        wapper.orderByDesc(AlarmRecordPO::getId);
        List<AlarmRecordPO> alarmRecordPOList = alarmRecordDao.selectList(wapper);
        if (null != alarmRecordPOList && alarmRecordPOList.size() > 0) {
            return alarmRecordPOList.get(0);
        }

        return null;
    }

    /**
     * 创建故障报警记录
     *
     * @param alertMessage
     */
    @Transactional(rollbackFor = Exception.class)
    public AlarmRecordPO createAlarmErrorRecord(AlertMessage alertMessage) {
        AlarmRulePO alarmRule = alarmRuleDao.selectById(alertMessage.getRuleId());
        if (alarmRule == null) {
            throw new ResourceNotFoundException(ResourceEnum.ALARM_RULE, alertMessage.getRuleId());
        }

        // 防止重复写入
        AlarmRecordPO alarmRecordPO = queryExistingAlarm(
                alarmRule.getAppId(), alertMessage.getRuleId(), alertMessage.getEndPoint());
        if (null != alarmRecordPO) {
            return alarmRecordPO;
        }

        AlarmLevelPO alarmLevel = alarmLevelDao.selectById(alarmRule.getAlarmLevelId());
        AlarmRecordPO po = new AlarmRecordPO();
        po.setAppId(alarmRule.getAppId());
        po.setLevel(alarmLevel.getLevel());
        po.setRuleId(alarmRule.getId());
        po.setStartTime(alertMessage.getAlertTime());
        po.setStatus(RuleStatusEnum.IN_ERROR.getCode());
        po.setEndPoint(alertMessage.getEndPoint());
        po.setAlertTimes(0);
        po.setContent(alertMessage.getAlertContent());
        alarmRecordDao.insert(po);
        return po;
    }

    /**
     * 故障恢复，关闭告警记录
     *
     * @param po
     * @param alertMessage
     */
    @Transactional(rollbackFor = Exception.class)
    public void closeAlarmRecord(AlarmRecordPO po, AlertMessage alertMessage) {
        po.setEndTime(alertMessage.getAlertTime());
        po.setStatus(RuleStatusEnum.SOLVE.getCode());
        po.setMtime(new Date());
        alarmRecordDao.updateById(po);
    }

    /**
     * 更新报警次数
     *
     * @param po
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateAlertAction(AlarmRecordPO po) {
        po.setAlertTimes(po.getAlertTimes() == null ? 1 : po.getAlertTimes() + 1);
        po.setLastAlertTime(new Date());
        po.setMtime(new Date());
        alarmRecordDao.updateById(po);
    }

}
