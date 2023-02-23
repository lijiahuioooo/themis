package com.mfw.themis.metric.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.mfw.themis.common.constant.enums.EnableEnum;
import com.mfw.themis.common.convert.AlarmRuleConvert;
import com.mfw.themis.common.convert.AppMetricConvert;
import com.mfw.themis.common.model.dto.AppMetricDTO;
import com.mfw.themis.dao.mapper.AlarmRuleDao;
import com.mfw.themis.dao.po.AlarmRulePO;
import com.mfw.themis.dao.po.AppMetricPO;
import com.mfw.themis.metric.config.ExecutorConfig;
import com.mfw.themis.metric.manager.MetricExecuteManager;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.util.ShardingUtil;
import com.xxl.job.core.util.ShardingUtil.ShardingVO;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 指标计算job
 *
 * @author liuqi
 */
@Slf4j
public abstract class CommonCalculateJob extends IJobHandler {

    @Autowired
    protected AlarmRuleDao alarmRuleDao;
    @Autowired
    protected ExecutorConfig executorConfig;
    @Autowired
    protected MetricExecuteManager executeManager;

    /**
     * 获取分片任务
     *
     * @param totalList
     * @return
     */
    protected List<AppMetricPO> getShardingList(List<AppMetricPO> totalList) {
        if (CollectionUtils.isEmpty(totalList)) {
            return totalList;
        }
        ShardingVO shardingVO = ShardingUtil.getShardingVo();
        List<AppMetricPO> shardingList = Lists.newArrayList();
        for (int i = 0; i < totalList.size(); i++) {
            int shardingIndex = (int) (totalList.get(i).getId() % shardingVO.getTotal());
            if (shardingIndex == shardingVO.getIndex()) {
                shardingList.add(totalList.get(i));
            }
        }
        return shardingList;
    }

    /**
     * 执行指标任务
     *
     * @param shardingList 分片数据
     * @param executeDate  执行时间
     */
    protected void executeMetricTask(List<AppMetricPO> shardingList, Date executeDate) {
        for (AppMetricPO appMetric : shardingList) {
            executorConfig.getMetricExecutorService().execute(
                    () -> executeManager.executeMetricEngine(AppMetricConvert.toDTO(appMetric), null, executeDate));
        }
    }

    /**
     * 执行连续规则任务
     *
     * @param shardingList   分片数据
     * @param shardingIdList 分片id
     * @param executeDate    执行时间
     */
    protected void executeRuleTask(List<AppMetricPO> shardingList, List<Long> shardingIdList, Date executeDate) {

        // 获取所有连续执行大于1的规则
        LambdaQueryWrapper<AlarmRulePO> queryWrapper = new LambdaQueryWrapper<AlarmRulePO>()
                .in(AlarmRulePO::getAppMetricId, shardingIdList)
                .eq(AlarmRulePO::getStatus, EnableEnum.ENABLE.getCode())
                .eq(AlarmRulePO::getIsDelete,false)
                .gt(AlarmRulePO::getContinuousHitTimes, 1);
        List<AlarmRulePO> alarmRuleList = alarmRuleDao.selectList(queryWrapper);

        if (CollectionUtils.isNotEmpty(alarmRuleList)) {
            log.info("execute rule metric job. size:{}", alarmRuleList.size());
            Map<Long, AppMetricDTO> appMetricMap = shardingList.stream().map(AppMetricConvert::toDTO).collect(
                    Collectors.toMap(AppMetricDTO::getId, e -> e));
            for (AlarmRulePO alarmRule : alarmRuleList) {
                executorConfig.getRuleExecutorService()
                        .submit(() -> executeManager.executeMetricEngine(appMetricMap.get(alarmRule.getAppMetricId()),
                                AlarmRuleConvert.toDTO(alarmRule), executeDate));
            }
        }
    }
}
