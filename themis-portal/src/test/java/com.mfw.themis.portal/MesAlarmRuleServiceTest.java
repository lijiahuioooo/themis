package com.mfw.themis.portal;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mfw.themis.dao.po.mes.AlarmRulePO;
import com.mfw.themis.portal.model.dto.mes.QueryAlarmRuleDTO;
import com.mfw.themis.portal.service.MesAlarmRuleService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MesAlarmRuleServiceTest {

    @Autowired
    private MesAlarmRuleService mesAlarmRuleService;

    @Test
    public void queryPageByAppCode(){
        QueryAlarmRuleDTO queryAlarmRuleDTO = QueryAlarmRuleDTO.builder()
                .appCode("frisk")
                .page(0L).pageSize(10L).build();


        IPage<AlarmRulePO> result = mesAlarmRuleService.queryPageByAppCode(queryAlarmRuleDTO);

        log.info("mes alarmRule queryPageByAppCode: {}", result);
    }

}
