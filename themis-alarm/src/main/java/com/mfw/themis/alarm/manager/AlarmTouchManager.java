package com.mfw.themis.alarm.manager;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import com.mfw.themis.alarm.handler.AlarmExceptionHandler;
import com.mfw.themis.common.constant.enums.AlarmScopeEnum;
import com.mfw.themis.common.constant.enums.AlertChannelEnum;
import com.mfw.themis.common.constant.enums.AppSourceEnum;
import com.mfw.themis.common.constant.enums.MetricTypeEnum;
import com.mfw.themis.common.constant.enums.ResourceEnum;
import com.mfw.themis.common.exception.ResourceNotFoundException;
import com.mfw.themis.common.model.message.AlertMessage;
import com.mfw.themis.common.util.DateFormatUtils;
import com.mfw.themis.dao.mapper.AlarmLevelDao;
import com.mfw.themis.dao.mapper.AlarmMetricDao;
import com.mfw.themis.dao.mapper.AlarmRuleDao;
import com.mfw.themis.dao.mapper.AppDao;
import com.mfw.themis.dao.mapper.AppMetricDao;
import com.mfw.themis.dao.po.AlarmLevelPO;
import com.mfw.themis.dao.po.AlarmMetricPO;
import com.mfw.themis.dao.po.AlarmRecordPO;
import com.mfw.themis.dao.po.AlarmRulePO;
import com.mfw.themis.dao.po.AppMetricPO;
import com.mfw.themis.dao.po.AppPO;
import com.mfw.themis.dependent.alert.AlertClient;
import com.mfw.themis.dependent.alert.model.AlertRequest;
import com.mfw.themis.dependent.alert.model.AlertResponse;
import com.mfw.themis.dependent.aosapp.AosClient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * 报警触达
 *
 * @author liuqi
 */
@Service
@Slf4j
public class AlarmTouchManager {

    /**
     * 报警第10次，才会触发电话报警
     */
    private final static Integer VOICE_SMS_COUNT = 10;
    private final static Set<String> ADMIN_UID = Sets.newHashSet("76252806", "56379418");
    @Autowired
    private AlertClient alertClient;
    @Autowired
    private AosClient aosClient;
    @Autowired
    private AlarmRuleDao alarmRuleDao;
    @Autowired
    private AlarmLevelDao alarmLevelDao;
    @Autowired
    private AlarmMetricDao alarmMetricDao;
    @Autowired
    private AppMetricDao appMetricDao;
    @Autowired
    private AppDao appDao;
    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${ops.metric.alert.employee}")
    private String opsMetricAlertEmployee;
    /**
     * 系统内部错误报警规则
     */
    @Value("${themis.metric.rule_id}")
    private Long systemAlarmRuleId;

    @Autowired
    private AlarmExceptionHandler alarmExceptionHandler;

    public void sendAlertMessage(AlertMessage alertMessage, AlarmRecordPO alarmRecord, boolean inError) {
        AlarmRulePO alarmRule = alarmRuleDao.selectById(alertMessage.getRuleId());
        AlarmLevelPO alarmLevel = alarmLevelDao.selectById(alarmRule.getAlarmLevelId());
        if (alarmLevel == null) {
            throw new ResourceNotFoundException(ResourceEnum.ALARM_LEVEL, alarmRule.getAlarmLevelId());
        }

        if (StringUtils.isBlank(alarmLevel.getAlarmScope())) {
            log.warn("未设置报警范围，level:{}", alarmLevel.getLevel());
            return;
        }

        // 获取报警人信息
        Set<String> employees = getReceivers(alarmLevel, alarmRule);

        AppPO app = appDao.selectById(alarmRule.getAppId());

        List<AlertChannelEnum> alertChannels = parseChannels(alarmLevel);
        String alertTimeStr = DateFormatUtils.formatToRfc3339China(alertMessage.getAlertTime());

        if (alertChannels.contains(AlertChannelEnum.VOICE_SMS) && !alarmRecord.getAlertTimes()
                .equals(VOICE_SMS_COUNT)) {
            // 如果报警渠道包含语言报警，只有在报警等于10次的时候才会触发语音报警
            alertChannels.remove(AlertChannelEnum.VOICE_SMS);
        }

        AlertRequest request = AlertRequest.builder().title(buildTitle(app.getAppCode(), alarmRule.getRuleName()))
                .alertTime(alertTimeStr)
                .channels(alertChannels.stream().map(AlertChannelEnum::getDesc).collect(Collectors.toList()))
                .level(parseLevel(alarmLevel))
                .content(alertMessage.getAlertContent())
                .moreUrl(alertMessage.getMoreUrl())
                .receivers(new ArrayList<>(employees))
                .source(applicationName)
                .state(inError ? "in" : "solve")
                .build();
        sendMessage(request);
    }

    private void sendMessage(AlertRequest request) {
        try {
            AlertResponse response = alertClient.sendAlertMessage(request);
            log.info("sendAlertMessage, request: {}, response : {}", request.toString(), JSON.toJSONString(response));
            if (response != null && !CollectionUtils.isEmpty(response.getFailures())) {
                final String errMsg = response.toString();
                // 判断是否是离职员工发送失败
                response.getFailures().forEach(failure -> {
                    if (!failure.getInfo().equals("员工已离职")) {
                        // 推送告警消息
                        alarmExceptionHandler.uncaughtException("告警发送异常");
                    }
                });
            }
        } catch (Exception e) {
            log.error("sendAlertMessage, request: {}, error : {}", request.toString(), e);
            alarmExceptionHandler.uncaughtException(e.getMessage());
        }
    }

    /**
     * 获取报警人信息
     *
     * @param alarmLevel
     * @param alarmRule
     * @return
     */
    public Set<String> getReceivers(AlarmLevelPO alarmLevel, AlarmRulePO alarmRule) {
        Set<String> employees = new HashSet<>();
        if (alarmRule.getId().equals(systemAlarmRuleId)) {
            return ADMIN_UID;
        }
        List<AlarmScopeEnum> scopeList = Arrays.stream(StringUtils.split(alarmLevel.getAlarmScope(), ","))
                .map(id -> AlarmScopeEnum.getByCode(Integer.valueOf(id))).collect(Collectors.toList());
        for (AlarmScopeEnum scope : scopeList) {
            if (scope == AlarmScopeEnum.RULE_REPORT) {
                employees.addAll(Arrays.asList(StringUtils.split(alarmRule.getContacts(), ",")));
            }
            if (scope == AlarmScopeEnum.APPLICATION_REPORT) {
                AppPO app = appDao.selectById(alarmRule.getAppId());
                if (AppSourceEnum.getByCode(app.getSource()) == AppSourceEnum.AOS) {
                    // 从服务树获取联系人
                    List<String> uids = aosClient.getAppMemberList(app.getAppCode()).getUids();
                    log.info("获取服务树联系人 appCode:{},相关联系人:{}", app.getAppCode(), uids);
                    if (uids.size() > 0) {
                        employees.addAll(uids);
                    }
                } else {
                    employees.addAll(Arrays.asList(StringUtils.split(app.getContacts(), ",")));
                }
            }
        }

        if (StringUtils.isNotBlank(alarmRule.getContacts())) {
            employees.addAll(Arrays.asList(StringUtils.split(alarmRule.getContacts(), ",")));
        }

        if (!alarmRule.getAppMetricId().equals(0L)) {
            AppMetricPO appMetricPO = appMetricDao.selectById(alarmRule.getAppMetricId());
            AlarmMetricPO alarmMetric = alarmMetricDao.selectById(appMetricPO.getMetricId());
            if (alarmMetric.getMetricType().equals(MetricTypeEnum.OPS_METRIC.getCode()) &&
                    StringUtils.isNotBlank(opsMetricAlertEmployee)) {
                employees.addAll(Arrays.asList(StringUtils.split(opsMetricAlertEmployee, ",")));
            }
        }

        return employees.stream().filter(s -> !"0".equals(s)).collect(Collectors.toSet());
    }

    /**
     * 解析报警渠道
     *
     * @param alarmLevel
     * @return
     */
    public List<AlertChannelEnum> parseChannels(AlarmLevelPO alarmLevel) {
        if (alarmLevel == null || StringUtils.isBlank(alarmLevel.getAlarmChannels())) {
            throw new ResourceNotFoundException(ResourceEnum.ALARM_LEVEL, 0L);
        }
        String[] channelIds = StringUtils.split(alarmLevel.getAlarmChannels(), ',');
        return Arrays.stream(channelIds)
                .filter(StringUtils::isNotBlank)
                .map(id -> AlertChannelEnum.getByCode(Integer.valueOf(id)))
                .filter(id -> !Objects.isNull(id))
                .collect(Collectors.toList());
    }

    private String buildTitle(String appCode, String ruleName) {
        return "[" + appCode + "]" + ruleName;
    }

    /**
     * 解析报警等级
     *
     * @param alarmLevel
     * @return
     */
    private String parseLevel(AlarmLevelPO alarmLevel) {
        String level = alarmLevel.getLevel();
        switch (level) {
            case "P0":
                return "critical";
            case "P1":
                return "urgency";
            case "P3":
                return "normal";
            case "P2":
            default:
                return "warning";
        }
    }


}
