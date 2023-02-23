package com.mfw.themis.metric.job;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mfw.themis.common.constant.enums.CollectTypeEnum;
import com.mfw.themis.common.constant.enums.DataSourceTypeEnum;
import com.mfw.themis.common.constant.enums.EnableEnum;
import com.mfw.themis.common.convert.AlarmRuleConvert;
import com.mfw.themis.common.convert.AppMetricConvert;
import com.mfw.themis.common.model.dto.AppMetricDTO;
import com.mfw.themis.dao.mapper.AlarmRuleDao;
import com.mfw.themis.dao.mapper.AppMetricDao;
import com.mfw.themis.dao.po.AlarmRulePO;
import com.mfw.themis.dao.po.AppMetricPO;
import com.mfw.themis.metric.config.ExecutorConfig;
import com.mfw.themis.metric.manager.MetricExecuteManager;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * es单一指标job
 *
 * @author wenhong
 */
@Component
@JobHandler(value = "esSingleMetricCalculateJob")
@Slf4j
public class EsSingleMetricCalculateJob extends CommonCalculateJob {

    @Autowired
    private AppMetricDao appMetricDao;

    @Autowired
    private AlarmRuleDao alarmRuleDao;
    @Autowired
    private ExecutorConfig executorConfig;
    @Autowired
    private MetricExecuteManager executeManager;

    @Override
    public ReturnT<String> execute(String s){
        List<AppMetricPO> totalList = appMetricDao
                .selectAllAppMetricByCollectType(CollectTypeEnum.SINGLE_METRIC.getCode(),
                        DataSourceTypeEnum.ELASTIC_SEARCH.getCode());
        final Date now = new Date();

        List<AppMetricPO> shardingList = getShardingList(totalList);
        List<Long> shardingIdList = shardingList.stream().map(AppMetricPO::getId).collect(Collectors.toList());
        log.info("success to sharding es metric,sharding size:{}.sharding:{}", shardingList.size(),
                JSON.toJSONString(shardingIdList.subList(0, shardingIdList.size()>100 ? 100 : shardingIdList.size())));
        if (CollectionUtils.isNotEmpty(shardingList)) {
            executeRuleTask(shardingList, shardingIdList, now);
        }
        return ReturnT.SUCCESS;
    }

    /**
     * 执行连续规则任务
     *
     * @param shardingList   分片数据
     * @param shardingIdList 分片id
     * @param executeDate    执行时间
     */
    @Override
    protected void executeRuleTask(List<AppMetricPO> shardingList, List<Long> shardingIdList, Date executeDate) {

        // 获取所有连续执行大于1的规则
        LambdaQueryWrapper<AlarmRulePO> queryWrapper = new LambdaQueryWrapper<AlarmRulePO>()
                .in(AlarmRulePO::getAppMetricId, shardingIdList)
                .eq(AlarmRulePO::getStatus, EnableEnum.ENABLE.getCode());
        List<AlarmRulePO> alarmRuleList = alarmRuleDao.selectList(queryWrapper);

        if (CollectionUtils.isNotEmpty(alarmRuleList)) {
            log.info("execute es rule metric job. size:{}", alarmRuleList.size());
            Map<Long, AppMetricDTO> appMetricMap = shardingList.stream().map(AppMetricConvert::toDTO).collect(
                    Collectors.toMap(AppMetricDTO::getId, e -> e));
            for (AlarmRulePO alarmRule : alarmRuleList) {
                executorConfig.getMetricExecutorService()
                        .submit(() -> executeManager.executeMetricEngine(appMetricMap.get(alarmRule.getAppMetricId()),
                                AlarmRuleConvert.toDTO(alarmRule), executeDate));
            }
        }
    }
}
