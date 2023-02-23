package com.mfw.themis.alarm.manager;

import com.mfw.themis.common.constant.enums.CompareTypeEnum;
import com.mfw.themis.common.constant.enums.ResourceEnum;
import com.mfw.themis.common.convert.AlarmRuleConvert;
import com.mfw.themis.common.exception.ResourceNotFoundException;
import com.mfw.themis.common.model.AlertRate;
import com.mfw.themis.common.model.message.AlertMessage;
import com.mfw.themis.common.util.AlertRateUtils;
import com.mfw.themis.common.util.RuleCompareUtils;
import com.mfw.themis.dao.mapper.AlarmLevelDao;
import com.mfw.themis.dao.mapper.AlarmRuleDao;
import com.mfw.themis.dao.mapper.AppDao;
import com.mfw.themis.dao.mapper.AppMetricDao;
import com.mfw.themis.dao.po.AlarmLevelPO;
import com.mfw.themis.dao.po.AlarmRecordPO;
import com.mfw.themis.dao.po.AlarmRulePO;
import com.mfw.themis.dao.po.AppMetricPO;
import com.mfw.themis.dao.po.AppPO;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 报警核心
 *
 * @author liuqi
 */
@Component
public class AlarmCoreManager {

    @Autowired
    private AlarmRuleDao alarmRuleDao;
    @Autowired
    private AppDao appDao;
    @Autowired
    private AppMetricDao appMetricDao;
    @Autowired
    private AlarmLevelDao alarmLevelDao;
    @Autowired
    private AlarmRecordManager alarmRecordManager;
    @Autowired
    private AlarmTouchManager alarmTouchManager;

    @Value("${themis.metric.rule_id}")
    private Long ruleId;

    public void sendAlertMessage(AlertMessage alertMessage) {
        AlarmRulePO alarmRule = alarmRuleDao.selectById(alertMessage.getRuleId());
        if (alarmRule == null) {
            throw new ResourceNotFoundException(ResourceEnum.ALARM_RULE, alertMessage.getRuleId());
        }
        if (StringUtils.isBlank(alertMessage.getAlertContent())) {
            alertMessage.setAlertContent(getDefaultAlarmContent(alarmRule, alertMessage));
        }

        if (alarmRule.getId().equals(ruleId)) {
            // 内部系统报警加锁
            synchronized (this) {
                alarmProcess(alertMessage, alarmRule);
            }
        } else {
            alarmProcess(alertMessage, alarmRule);
        }
    }

    /**
     * 报警处理流程
     *
     * @param alertMessage
     * @param alarmRule
     */
    private void alarmProcess(AlertMessage alertMessage, AlarmRulePO alarmRule) {

        boolean inError = true;
        if (alertMessage.getCurrentMetricValue() != null) {
            inError = RuleCompareUtils
                    .compareRule(alertMessage.getCurrentMetricValue(), AlarmRuleConvert.toDTO(alarmRule));
        }
        if (alertMessage.getCurrentMetricValues() != null) {
            inError = RuleCompareUtils
                    .compareRule(alertMessage.getCurrentMetricValues(), AlarmRuleConvert.toDTO(alarmRule));
        }

        AlarmRecordPO alarmRecord = alarmRecordManager
                .queryExistingAlarm(alarmRule.getAppId(), alertMessage.getRuleId(), alertMessage.getEndPoint());
        if (inError) {
            // 故障中
            if (alarmRecord == null) {
                alarmRecord = alarmRecordManager.createAlarmErrorRecord(alertMessage);
            }
            AlarmLevelPO alarmLevel = alarmLevelDao.selectById(alarmRule.getAlarmLevelId());
            AlertRate alertRate = AlertRateUtils.parseAlertRate(alarmLevel.getAlarmRate());
            if (AlertRateUtils.canAlert(alarmRecord, alertRate, new Date())) {
                // 下发报警
                alarmTouchManager.sendAlertMessage(alertMessage, alarmRecord, true);
                // 更新报警次数&时间&接收人
                alarmRecord.setReceivers(String.join(",",
                        alarmTouchManager.getReceivers(alarmLevel, alarmRule)));
                alarmRecordManager.updateAlertAction(alarmRecord);
            }

            // 数据上报
            alarmMonitor(alarmRule);
        } else if (alarmRecord != null) {
            // 恢复
            alarmRecordManager.closeAlarmRecord(alarmRecord, alertMessage);
            alarmTouchManager.sendAlertMessage(alertMessage, alarmRecord, false);
        }
    }

    /**
     * 数据上报
     *
     * @param alarmRule
     */
    public void alarmMonitor(AlarmRulePO alarmRule) {
        AppPO appPO = appDao.selectById(alarmRule.getAppId());

        Map<String, Object> tags = new HashMap<>();
        tags.put("appCode", appPO.getAppCode());
        tags.put("appName", appPO.getAppName());
        tags.put("ruleId", alarmRule.getId());
        tags.put("ruleName", alarmRule.getRuleName());
        tags.put("appMetricId", alarmRule.getAppMetricId());
        PrometheusMonitor.counter("alarm_statistic", tags);
    }

    /**
     * 获取默认报警文案 文案格式：您的应用「服务名」指标名，当前值 比较方式 阈值
     *
     * @param alarmRule    规则
     * @param alertMessage 报警消息
     * @return
     */
    private String getDefaultAlarmContent(AlarmRulePO alarmRule, AlertMessage alertMessage) {
        AppMetricPO appMetric = appMetricDao.selectById(alarmRule.getAppMetricId());
        AppPO app = appDao.selectById(appMetric.getAppId());
        StringBuilder alertContent = new StringBuilder();
        alertContent.append("您的应用「");
        alertContent.append(app.getAppCode());
        if (StringUtils.isNotBlank(alertMessage.getEndPoint())) {
            alertContent.append(" - ");
            alertContent.append(alertMessage.getEndPoint());
        }
        alertContent.append("」");
        alertContent.append(alarmRule.getRuleName());
        alertContent.append(",");
        if (alertMessage.getCurrentMetricValue() != null) {
            alertContent.append(alertMessage.getCurrentMetricValue());
        }
        if (alertMessage.getCurrentMetricValues() != null) {
            alertContent.append(StringUtils.join(alertMessage.getCurrentMetricValues(), ";"));
        }
        alertContent.append(" ");
        alertContent.append(CompareTypeEnum.getByCode(alarmRule.getCompare()).getDescription());
        alertContent.append(" ");
        alertContent.append(alarmRule.getThreshold());

        return alertContent.toString();
    }


}
