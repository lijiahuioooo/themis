package com.mfw.themis.metric.job;

import com.alibaba.fastjson.JSON;
import com.mfw.themis.common.constant.enums.CollectTypeEnum;
import com.mfw.themis.common.constant.enums.DataSourceTypeEnum;
import com.mfw.themis.dao.mapper.AppMetricDao;
import com.mfw.themis.dao.po.AppMetricPO;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.JobHandler;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * prometheus单一指标job
 *
 * @author liuqi
 */
@Component
@JobHandler(value = "singleMetricCalculateJob")
@Slf4j
public class SingleMetricCalculateJob extends CommonCalculateJob {

    @Autowired
    private AppMetricDao appMetricDao;


    @Override
    public ReturnT<String> execute(String s){
        List<AppMetricPO> totalList = appMetricDao
                .selectAllAppMetricByCollectType(CollectTypeEnum.SINGLE_METRIC.getCode(),
                        DataSourceTypeEnum.PROMETHEUS.getCode());
        final Date now = new Date();

        List<AppMetricPO> shardingList = getShardingList(totalList);
        List<Long> shardingIdList = shardingList.stream().map(AppMetricPO::getId).collect(Collectors.toList());
        log.info("success to sharding prometheus metric,sharding size:{}.sharding:{}", shardingList.size(),
                JSON.toJSONString(shardingIdList.subList(0, shardingIdList.size() > 100 ? 100 : shardingIdList.size())));
        if (CollectionUtils.isNotEmpty(shardingList)) {
            executeMetricTask(shardingList, now);
            executeRuleTask(shardingList, shardingIdList, now);
        }
        return ReturnT.SUCCESS;
    }
}
