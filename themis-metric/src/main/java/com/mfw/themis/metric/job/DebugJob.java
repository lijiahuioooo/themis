//package com.mfw.themis.metric.job;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.mfw.themis.common.convert.AlarmRuleConvert;
//import com.mfw.themis.common.convert.AppMetricConvert;
//import com.mfw.themis.common.model.dto.AlarmRuleDTO;
//import com.mfw.themis.common.model.dto.AppMetricDTO;
//import com.mfw.themis.dao.mapper.AppMetricDao;
//import com.mfw.themis.dao.po.AlarmRulePO;
//import com.mfw.themis.dao.po.AppMetricPO;
//import com.xxl.job.core.biz.model.ReturnT;
//import com.xxl.job.core.handler.annotation.JobHandler;
//import java.util.Date;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
///**
// * @author liuqi
// */
//@Component
//@JobHandler(value = "DebugJob")
//@Slf4j
//public class DebugJob extends CommonCalculateJob {
//
//    @Autowired
//    private AppMetricDao appMetricDao;
//
//    /**
//     *
//     * @param s  {"id":xxx,"startTime":"2021-10-10 10:10:10"}
//     * @return
//     * @throws Exception
//     */
//    @Override
//    public ReturnT<String> execute(String s) throws Exception {
//        JSONObject jsonObject = JSON.parseObject(s);
//        Long appMetricId = jsonObject.getLong("id");
//        Date executeDate = jsonObject.getDate("startTime");
//        AppMetricPO po = appMetricDao.selectById(appMetricId);
//        LambdaQueryWrapper<AlarmRulePO> queryWrapper = new LambdaQueryWrapper<AlarmRulePO>()
//                .eq(AlarmRulePO::getAppMetricId, appMetricId);
//        AlarmRulePO rulePO = alarmRuleDao.selectOne(queryWrapper);
//
//        AppMetricDTO appMetric = AppMetricConvert.toDTO(po);
//        AlarmRuleDTO rule = AlarmRuleConvert.toDTO(rulePO);
//        executorConfig.getRuleExecutorService()
//                .submit(() -> executeManager.executeMetricEngine(appMetric, rule, executeDate));
//        return ReturnT.SUCCESS;
//    }
//}
