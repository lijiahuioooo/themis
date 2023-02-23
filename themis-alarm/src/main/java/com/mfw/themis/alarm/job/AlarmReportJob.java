package com.mfw.themis.alarm.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mfw.themis.common.util.email.EmailParam;
import com.mfw.themis.common.util.email.EmailUtils;
import com.mfw.themis.dao.mapper.AlarmRecordDao;
import com.mfw.themis.dao.mapper.AlarmRuleDao;
import com.mfw.themis.dao.mapper.AppDao;
import com.mfw.themis.dao.po.AlarmRecordPO;
import com.mfw.themis.dao.po.AlarmRulePO;
import com.mfw.themis.dao.po.AppPO;
import com.mfw.themis.dependent.mfwemployee.EmployeeClient;
import com.mfw.themis.dependent.mfwemployee.model.EmpInfoListResponse;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 发送报警报告
 *
 * @author wenhong
 */
@Slf4j
@Component
@JobHandler("alarmReportJob")
public class AlarmReportJob extends IJobHandler {

    private static final Long RECORD_PAGE_SIZE = 500L;

    @Autowired
    private AlarmRecordDao alarmRecordDao;

    @Autowired
    private AppDao appDao;

    @Autowired
    private AlarmRuleDao alarmRuleDao;

    @Autowired
    private EmployeeClient employeeClient;

    @Value("${app.env}")
    private String appEnv;


    /**
     * 管理员名单
     */
    private final Set<String> adminEmailTo = Sets.newHashSet(
            "wenhong@mafengwo.com", "liuqi2@mafengwo.com", "lijiahui@mafengwo.com");

    @Override
    public ReturnT<String> execute(String arg) {

        // 获取当前时间前一周
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        Date endTime = calendar.getTime();

        calendar.add(Calendar.DATE, -6);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Date startTime = calendar.getTime();

        processReport(startTime, endTime);

        return ReturnT.SUCCESS;
    }

    /**
     * 报告处理主逻辑
     *
     * @param startTime
     * @param endTime
     */
    private void processReport(Date startTime, Date endTime) {
        Long page = 1L;

        Map<Long, RuleReportRecord> ruleIdMap = new HashMap<>();
        Map<String, Set<String>> uidAppCodeMap = new HashMap<>();
        while (true) {
            List<AlarmRecordPO> alarmRecordList = getRecordList(startTime, endTime, page);
            if (null == alarmRecordList || alarmRecordList.size() <= 0) {
                break;
            }
            page++;

            Set<Long> appIds = alarmRecordList.stream().map(AlarmRecordPO::getAppId).collect(Collectors.toSet());

            List<AppPO> appList = getAppByIds(appIds);
            Map<Long, AppPO> appIdMap = appList.stream().collect(Collectors.toMap(AppPO::getId, Function.identity()));

            alarmRecordList.forEach(r -> {
                if (ruleIdMap.containsKey(r.getRuleId())) {
                    RuleReportRecord ruleReportRecord = ruleIdMap.get(r.getRuleId());
                    ruleReportRecord.setAlertTimes(ruleReportRecord.getAlertTimes() + r.getAlertTimes());
                    long lastTime = (r.getEndTime().getTime() - r.getStartTime().getTime());
                    ruleReportRecord.setAlertLastTime(ruleReportRecord.getAlertLastTime() + lastTime);
                } else {
                    RuleReportRecord ruleReportRecord = new RuleReportRecord();
                    ruleReportRecord.setAppId(r.getAppId());
                    ruleReportRecord.setAppCode(appIdMap.get(r.getAppId()).getAppCode());
                    ruleReportRecord.setRuleId(r.getRuleId());
                    ruleReportRecord.setAlertTimes(r.getAlertTimes());
                    long lastTime = (r.getEndTime().getTime() - r.getStartTime().getTime());
                    ruleReportRecord.setAlertLastTime(lastTime);
                    ruleIdMap.put(r.getRuleId(), ruleReportRecord);
                }
                String receivers = r.getReceivers();
                Arrays.asList(receivers.split(",")).forEach(uid -> {
                    Set<String> appCodes;
                    if (uidAppCodeMap.containsKey(uid)) {
                        uidAppCodeMap.get(uid).add(appIdMap.get(r.getAppId()).getAppCode());
                    } else {
                        appCodes = new HashSet<>();
                        appCodes.add(appIdMap.get(r.getAppId()).getAppCode());
                    }
                });

            });
        }

        //补充rule相关字段
        List<AlarmRulePO> alarmRuleList = getRuleByIds(ruleIdMap.keySet());
        Map<Long, AlarmRulePO> rulePOMap = alarmRuleList.stream().collect(Collectors.toMap(AlarmRulePO::getId, e -> e));
        ruleIdMap.forEach((k, v) -> {
            AlarmRulePO rule = rulePOMap.get(v.getRuleId());
            v.setRuleName(rule.getRuleName());
            v.setAppMetricId(rule.getAppMetricId());
            v.setThreshold(rule.getThreshold());
        });

        String title = getReportTypeName(startTime, endTime);
        // 发送报告给管理员
        sendEmailForAdmin(title, ruleIdMap);

        // 发送报告给用户
        sendEmailForUser(title, ruleIdMap, uidAppCodeMap);
    }

    /**
     * 发送报告给管理员
     *
     * @param reportTypeNam
     */
    private void sendEmailForAdmin(String reportTypeNam, Map<Long, RuleReportRecord> ruleIdMap) {

        String message = createReportHtml(ruleIdMap);

        EmailParam emailParam = new EmailParam();
        emailParam.setSenderNick("服务质量平台");
        emailParam.setEmailTo(StringUtils.join(adminEmailTo, ","));
        emailParam.setSubject(reportTypeNam);
        emailParam.setEmailContent(message);

        log.info("管理员报警报告：{}", emailParam.toString());

        EmailUtils.sendEmail(emailParam);
    }

    /**
     * 发送报告给用户
     *
     * @param reportTypeNam
     */
    private void sendEmailForUser(String reportTypeNam, Map<Long, RuleReportRecord> ruleIdMap,
            Map<String, Set<String>> uidAppCodeMap) {

        /**
         * 用户邮箱批量获取
         */
        Map<String, String> uidEmailMap = getUserEmail(uidAppCodeMap);

        for (Map.Entry<String, Set<String>> entry : uidAppCodeMap.entrySet()) {
            String uid = entry.getKey();

            if (!uidEmailMap.containsKey(uid)) {
                log.error("报警报告，uid: {} 未获取到邮箱地址", uid);
                continue;
            }

            String emailAddress = uidEmailMap.get(uid);

            /**
             * 离职员工不发送
             */
            if (emailAddress.contains("_leave")) {
                continue;
            }

            /**
             * 如果是管理员不再发送了
             */
            if (adminEmailTo.contains(emailAddress)) {
                continue;
            }

            String message = createReportHtml(ruleIdMap);

            EmailParam emailParam = new EmailParam();
            emailParam.setSenderNick("服务质量平台");
            emailParam.setEmailTo(emailAddress);
            emailParam.setSubject(reportTypeNam);
            emailParam.setEmailContent(message);

            if (appEnv.equals("prod")) {
                EmailUtils.sendEmail(emailParam);
                log.info("用户报警报告：{}", emailParam.toString());
            }
        }
    }

    /**
     * 获取用户邮箱地址
     */
    private Map<String, String> getUserEmail(Map<String, Set<String>> uidAppCodeMap) {
        Set<String> uids = uidAppCodeMap.keySet();
        Map<String, String> uidEmailMap = Maps.newHashMap();
        Lists.partition(new ArrayList<>(uids), 30).forEach(uidList -> {

            EmpInfoListResponse empInfoListResponse = employeeClient.getInfoList(
                    uidList.stream().map(Long::valueOf).collect(Collectors.toList()));

            if (null == empInfoListResponse) {
                log.error("报警报告，获取员工邮箱失败, uids: {}", uids);
                return;
            }

            empInfoListResponse.getData().forEach(empInfoData -> {
                uidEmailMap.put(empInfoData.getUid().toString(), empInfoData.getEmail());
            });
        });
        return uidEmailMap;
    }

    /**
     * 生成报告内容
     *
     * @param ruleIdMap
     * @return
     */
    private String createReportHtml(Map<Long, RuleReportRecord> ruleIdMap) {
        // 按照appCode维度聚合
        Map<String, RuleReportRecord> appRecordMap = ruleIdMap.values().stream()
                .collect(Collectors.toMap(RuleReportRecord::getAppCode, e -> e, (a, b) -> {
                    RuleReportRecord c = new RuleReportRecord();
                    BeanUtils.copyProperties(a, c);
                    c.setAlertLastTime(a.getAlertLastTime() + b.getAlertLastTime());
                    c.setAlertTimes(a.getAlertTimes() + b.getAlertTimes());
                    return c;
                }));
        // 根据报警次数排序
        List<RuleReportRecord> sortAppRecordList = appRecordMap.values().stream().sorted((a, b) ->
                b.getAlertTimes() - a.getAlertTimes()).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder("<html>");
        sb.append("<head>");
        sb.append("<style>");
        sb.append(
                ".table-d table{ font-family: verdana,arial,sans-serif;font-size:11px;color:#333333;border-width: 1px;border-color: #666666;border-collapse: collapse;}");
        sb.append(
                ".table-d table td{ border-width: 1px;padding: 8px;border-style: solid;border-color: #666666;background-color: #ffffff;}");
        sb.append(
                ".table-d table th{border-width: 1px;padding: 8px;border-style: solid;border-color: #666666;background-color: #dedede; }");
        sb.append("</style>");
        sb.append("</head>");

        sb.append("<body>");

        sb.append("<div class=\"table-d\">");
        sb.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"0\"");
        sb.append("<tr>");
        sb.append("<th>").append("应用").append("</th>").append("<th>").append("报警个数").append("</th>").append("<th>")
                .append("持续时间").append("</th>");
        sb.append("</tr>");
        for (RuleReportRecord appRecord : sortAppRecordList) {
            long lastTimes = appRecord.getAlertLastTime() / 1000 / 60;
            sb.append("<tr>");
            sb.append("<td>").append(appRecord.getAppCode()).append("</td>");
            sb.append("<td>").append(appRecord.getAlertTimes()).append("</td>");
            sb.append("<td>").append(lastTimes).append(" 分</td>");
            sb.append("</tr>");
        }

        sb.append("</table>");
        sb.append("</div>");

        sb.append("<br/>");

        sb.append("<div class=\"table-d\">");
        sb.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"0\"");
        sb.append("<tr>");
        sb.append("<th>").append("应用").append("</th>");
        sb.append("<th>").append("规则ID").append("</th>");
        sb.append("<th>").append("规则名称").append("</th>");
        sb.append("<th>").append("指标ID").append("</th>");
        sb.append("<th>").append("阈值").append("</th>");
        sb.append("<th>").append("报警个数").append("</th>");
        sb.append("<th>").append("持续时间").append("</th>");
        sb.append("</tr>");
        // 根据appId和报警次数排序
        List<RuleReportRecord> sortRecordList = ruleIdMap.values().stream()
                .sorted((a, b) -> {
                    long compare = a.getAppId() - b.getAppId();
                    if (compare > 0) {
                        return 1;
                    } else if (compare < 0) {
                        return -1;
                    } else {
                        return b.getAlertTimes() - a.getAlertTimes();
                    }
                })
                .collect(Collectors.toList());
        for (RuleReportRecord record : sortRecordList) {
            Long ruleId = record.getRuleId();
            RuleReportRecord rule = ruleIdMap.get(ruleId);
            sb.append("<tr>");
            sb.append("<td>").append(record.getAppCode()).append("</td>");
            sb.append("<td>").append(ruleId).append("</td>");
            sb.append("<td>").append(Optional.ofNullable(rule.getRuleName()).orElse(""))
                    .append("</td>");
            sb.append("<td>").append(Optional.ofNullable(rule.getAppMetricId()).orElse(0L))
                    .append("</td>");
            sb.append("<td>").append(Optional.ofNullable(rule.getThreshold()).orElse(""))
                    .append("</td>");
            sb.append("<td>").append(record.getAlertTimes()).append("</td>");
            long lastTimes = rule.getAlertLastTime() / 1000 / 60;
            sb.append("<td>").append(lastTimes).append(" 分</td>");
            sb.append("</tr>");

        }

        sb.append("</table>");

        sb.append("</div>");

        sb.append("</body>");

        sb.append("</html>");

        return sb.toString();
    }

    /**
     * 获取报警记录
     *
     * @param startTime
     * @param endTime
     * @param page
     * @return
     */
    private List<AlarmRecordPO> getRecordList(Date startTime, Date endTime, Long page) {
        QueryWrapper<AlarmRecordPO> queryWrapper = new QueryWrapper<>();

        queryWrapper.lambda().ge(AlarmRecordPO::getStartTime, startTime);
        queryWrapper.lambda().le(AlarmRecordPO::getEndTime, endTime);

        Page<AlarmRecordPO> pageReq = new Page<>(page, RECORD_PAGE_SIZE);
        IPage<AlarmRecordPO> dbResult = alarmRecordDao.selectPage(pageReq, queryWrapper);

        return dbResult.getRecords();
    }

    private List<AlarmRulePO> getRuleByIds(Set<Long> ruleIds) {
        QueryWrapper<AlarmRulePO> alarmRuleQueryWrapper = new QueryWrapper<>();
        alarmRuleQueryWrapper.lambda().in(AlarmRulePO::getId, ruleIds);
        return alarmRuleDao.selectList(alarmRuleQueryWrapper);
    }

    private List<AppPO> getAppByIds(Set<Long> appIds) {
        QueryWrapper<AppPO> appQueryWrapper = new QueryWrapper<>();
        appQueryWrapper.lambda().in(AppPO::getId, appIds);
        return appDao.selectList(appQueryWrapper);
    }

    /**
     * 获取报警title
     *
     * @param startTime
     * @param endTime
     * @return
     */
    private String getReportTypeName(Date startTime, Date endTime) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String reportTypeName = "报警汇总周报" + simpleDateFormat.format(startTime) + "-" + simpleDateFormat.format(endTime);

        if (!appEnv.equals("prod")) {
            reportTypeName = "[DEV]" + reportTypeName;
        }
        return reportTypeName;
    }

    @Data
    class RuleReportRecord {

        private Long appId;
        private String appCode;
        private Long ruleId;
        private String ruleName;
        private String threshold;
        private Long appMetricId;
        private Integer alertTimes;
        // 持续时间
        private Long alertLastTime;
    }

}
