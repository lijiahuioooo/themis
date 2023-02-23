package com.mfw.themis.portal.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mfw.themis.common.constant.AlarmRuleTemplateConstants;
import com.mfw.themis.common.constant.GlobalStatusConstants;
import com.mfw.themis.common.constant.enums.CompareTypeEnum;
import com.mfw.themis.common.constant.enums.EnableEnum;
import com.mfw.themis.common.constant.enums.ResourceEnum;
import com.mfw.themis.common.exception.ResourceNotFoundException;
import com.mfw.themis.common.exception.ServiceException;
import com.mfw.themis.common.model.dto.AlarmRuleDTO;
import com.mfw.themis.common.model.dto.AlarmRuleTemplateDTO;
import com.mfw.themis.dao.mapper.AlarmLevelDao;
import com.mfw.themis.dao.mapper.AlarmRuleDao;
import com.mfw.themis.dao.mapper.AppDao;
import com.mfw.themis.dao.mapper.AppMetricDao;
import com.mfw.themis.dao.po.AlarmRulePO;
import com.mfw.themis.dao.po.AppMetricPO;
import com.mfw.themis.dao.po.AppPO;
import com.mfw.themis.portal.service.AlarmRuleService;
import com.mfw.themis.portal.service.InitRuleService;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author guosp
 */
@Slf4j
@Service
public class InitRuleServiceImpl implements InitRuleService {

    @Autowired
    private AppDao appDao;

    @Autowired
    private AlarmRuleDao alarmRuleDao;

    @Autowired
    private AppMetricDao appMetricDao;

    @Autowired
    private AlarmRuleService alarmRuleService;

    @Autowired
    private AlarmLevelDao alarmLevelDao;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean initAlarmRule(Long appId) {
        List<AlarmRuleTemplateDTO> templateList = getAlarmRuleTemplateList();
        if(templateList == null) {
            return false;
        }
        AppPO appPO = appDao.selectById(appId);
        if(appPO == null) {
            throw new ResourceNotFoundException(ResourceEnum.APP,appId);
        }

        for (AlarmRuleTemplateDTO alarmRuleTemplate : templateList) {
            AppMetricPO appMetricPO = getAppMetricPO(alarmRuleTemplate.getMetricId(),appId);
            if(appMetricPO != null && !checkExists(appMetricPO,appId)) {
                AlarmRuleDTO alarmRuleDTO = buildAlarmRuleDTO(alarmRuleTemplate,appPO,appMetricPO);
                alarmRuleDTO = alarmRuleService.create(alarmRuleDTO);
                alarmRuleService.changeStatus(alarmRuleDTO.getId(), EnableEnum.ENABLE.getCode());
            }
        }
        

        return true;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean syncRuleToApp(Long metricId) {
        AlarmRuleTemplateDTO alarmRuleTemplate = getAlarmRuleTemplateByMetricId(metricId);
        if(alarmRuleTemplate == null) {
            throw new ServiceException("告警模版不存在");
        }

        List<AppPO> list = queryAppList();
        for (AppPO appPO: list) {
            AppMetricPO appMetricPO = getAppMetricPO(metricId,appPO.getId());
            if(appMetricPO != null && !checkExists(appMetricPO,appPO.getId())) {
                AlarmRuleDTO alarmRuleDTO = buildAlarmRuleDTO(alarmRuleTemplate,appPO,appMetricPO);
                alarmRuleDTO = alarmRuleService.create(alarmRuleDTO);
                alarmRuleService.changeStatus(alarmRuleDTO.getId(), EnableEnum.ENABLE.getCode());
            }
        }

        return true;
    }


    /**
     * 获取app liist
     * @return
     */
    private List<AppPO> queryAppList() {
        LambdaQueryWrapper<AppPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppPO::getIsDelete, GlobalStatusConstants.IS_DELETE_DISABLE);

        return appDao.selectList(wrapper);
    }

    private AppMetricPO getAppMetricPO(Long metricId,Long appId) {
        LambdaQueryWrapper<AppMetricPO> appMetricWrapper = new LambdaQueryWrapper<>();
        appMetricWrapper.eq(AppMetricPO::getAppId,appId);
        appMetricWrapper.eq(AppMetricPO::getMetricId,metricId);
        appMetricWrapper.eq(AppMetricPO::getIsDelete,GlobalStatusConstants.IS_DELETE_DISABLE);

        return appMetricDao.selectOne(appMetricWrapper);
    }

    private boolean checkExists(AppMetricPO appMetricPO, Long appId) {
        LambdaQueryWrapper<AlarmRulePO> alarmRuleWrapper = new LambdaQueryWrapper<>();
        alarmRuleWrapper.eq(AlarmRulePO::getAppId,appId);
        alarmRuleWrapper.eq(AlarmRulePO::getAppMetricId,appMetricPO.getId());
        AlarmRulePO alarmRulePO = alarmRuleDao.selectOne(alarmRuleWrapper);

        return alarmRulePO != null;
    }

    private AlarmRuleDTO buildAlarmRuleDTO(AlarmRuleTemplateDTO alarmRuleTemplateDTO,AppPO appPO,AppMetricPO appMetricPO) {
        AlarmRuleDTO alarmRuleDTO = new AlarmRuleDTO();

        BeanUtils.copyProperties(alarmRuleTemplateDTO, alarmRuleDTO);
        alarmRuleDTO.setContacts(appPO.getContacts());
        alarmRuleDTO.setCompare(CompareTypeEnum.getByCode(alarmRuleTemplateDTO.getCompare()));
        alarmRuleDTO.setAppMetricId(appMetricPO.getId());
        alarmRuleDTO.setAppId(appPO.getId());
        alarmRuleDTO.setStatus(EnableEnum.ENABLE);

        return alarmRuleDTO;
    }


    private List<AlarmRuleTemplateDTO> getAlarmRuleTemplateList() {
        String templateListConfig = AlarmRuleTemplateConstants.AUTO_INIT_TEMPLATE_LIST;
        return JSONArray.parseArray(templateListConfig, AlarmRuleTemplateDTO.class);
    }


    private AlarmRuleTemplateDTO getAlarmRuleTemplateByMetricId(Long metricId) {
        List<AlarmRuleTemplateDTO> alarmRuleTemplateList = getAlarmRuleTemplateList();
        for (AlarmRuleTemplateDTO alarmRuleTemplate: alarmRuleTemplateList) {
            if(alarmRuleTemplate.getMetricId().equals(metricId)) {
                return alarmRuleTemplate;
            }
        }

        return null;
    }
}
