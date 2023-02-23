package com.mfw.themis.alarm.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mfw.themis.common.constant.enums.RuleStatusEnum;
import com.mfw.themis.dao.mapper.AlarmRecordDao;
import com.mfw.themis.dao.po.AlarmRecordPO;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author wenhong
 */
@Component
@JobHandler("finishUnCloseRecordJob")
public class FinishUnCloseRecordJob extends IJobHandler {

    @Autowired
    private AlarmRecordDao alarmRecordDao;

    @Override
    public ReturnT<String> execute(String param) {

        // 获取一小时未结束的记录，主动进行关闭
        LambdaQueryWrapper<AlarmRecordPO> wapper = new LambdaQueryWrapper<AlarmRecordPO>()
                .le(AlarmRecordPO::getMtime, DateUtils.addHours(new Date(), -1))
                .eq(AlarmRecordPO::getStatus, RuleStatusEnum.IN_ERROR.getCode());

        List<AlarmRecordPO> alarmRecordPOList = alarmRecordDao.selectList(wapper);

        if(null != alarmRecordPOList){
            for(AlarmRecordPO alarmRecordPO: alarmRecordPOList){
                alarmRecordPO.setStatus(RuleStatusEnum.CLOSE.getCode());
                alarmRecordPO.setEndTime(new Date());
                alarmRecordPO.setMtime(null);
                alarmRecordDao.updateById(alarmRecordPO);
            }
        }

        return ReturnT.SUCCESS;
    }
}
