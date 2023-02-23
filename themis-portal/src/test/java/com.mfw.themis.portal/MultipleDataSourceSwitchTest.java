package com.mfw.themis.portal;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mfw.themis.dao.po.mes.AlarmRulePO;
import com.mfw.themis.portal.model.dto.mes.QueryAlarmRuleDTO;
import com.mfw.themis.portal.service.AppMetricService;
import com.mfw.themis.portal.service.MesAlarmRuleService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MultipleDataSourceSwitchTest {

    @Autowired
    private AppMetricService appMetricService;

    @Autowired
    private MesAlarmRuleService mesAlarmRuleService;


    @Test
    public void MultipleThemisSource(){

        appMetricService.queryById(9L);

        QueryAlarmRuleDTO queryAlarmRuleDTO = QueryAlarmRuleDTO.builder()
                .appCode("frisk")
                .page(0L).pageSize(10L).build();


        IPage<AlarmRulePO> result = mesAlarmRuleService.queryPageByAppCode(queryAlarmRuleDTO);

        log.info("mes alarmRule queryPageByAppCode: {}", result);

        appMetricService.queryById(14L);
    }

}
