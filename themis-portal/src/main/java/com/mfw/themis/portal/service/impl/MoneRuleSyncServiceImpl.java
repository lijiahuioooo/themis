package com.mfw.themis.portal.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mfw.themis.common.constant.EngineConstant;
import com.mfw.themis.common.constant.GlobalStatusConstants;
import com.mfw.themis.common.constant.MetricTagConstant;
import com.mfw.themis.common.constant.enums.AlarmMetricUnitEnum;
import com.mfw.themis.common.constant.enums.CollectTypeEnum;
import com.mfw.themis.common.constant.enums.CompareTypeEnum;
import com.mfw.themis.common.constant.enums.DataSourceTypeEnum;
import com.mfw.themis.common.constant.enums.EnableEnum;
import com.mfw.themis.common.constant.enums.FilterMetricOperatorEnum;
import com.mfw.themis.common.constant.enums.GroupTypeEnum;
import com.mfw.themis.common.constant.enums.MetricTypeEnum;
import com.mfw.themis.common.constant.enums.TimeWindowEnum;
import com.mfw.themis.common.exception.ServiceException;
import com.mfw.themis.common.model.dto.AlarmRuleDTO;
import com.mfw.themis.common.model.dto.AppMetricDTO;
import com.mfw.themis.common.model.dto.EsAggMetricDTO;
import com.mfw.themis.common.model.dto.SaveAlarmMetricDTO;
import com.mfw.themis.common.util.PlaceHolderUtils;
import com.mfw.themis.dao.mapper.AlarmMetricDao;
import com.mfw.themis.dao.mapper.AlarmRuleDao;
import com.mfw.themis.dao.mapper.AppDao;
import com.mfw.themis.dao.mapper.AppMetricDao;
import com.mfw.themis.dao.mapper.mes.MesAlarmRuleDao;
import com.mfw.themis.dao.po.AlarmMetricPO;
import com.mfw.themis.dao.po.AppMetricPO;
import com.mfw.themis.dao.po.AppPO;
import com.mfw.themis.dao.po.mes.AlarmRulePO;
import com.mfw.themis.portal.event.MoneRuleSyncEvent;
import com.mfw.themis.portal.service.AlarmMetricService;
import com.mfw.themis.portal.service.AlarmRuleService;
import com.mfw.themis.portal.service.AppMetricService;
import com.mfw.themis.portal.service.MesAlarmRuleService;
import com.mfw.themis.portal.service.MoneRuleSyncService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @author guosp
 */
@Slf4j
@Service
public class MoneRuleSyncServiceImpl implements MoneRuleSyncService, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private AppDao appDao;

    @Autowired
    private AlarmMetricDao alarmMetricDao;

    @Autowired
    private AppMetricDao appMetricDao;

    @Autowired
    private AlarmRuleDao alarmRuleDao;

    @Autowired
    private MesAlarmRuleDao mesAlarmRuleDao;

    @Autowired
    private AlarmMetricService alarmMetricService;

    @Autowired
    private AppMetricService appMetricService;

    @Autowired
    private AlarmRuleService alarmRuleService;

    @Autowired
    private MesAlarmRuleService mesAlarmRuleService;

    /**
     * aos容器运维指标datasource_id
     */
    @Value("${mone.metric.datasource.id}")
    private Long moneMetricDataSourceId;


    /**
     * 同步mone告警规则
     *
     * @param appId
     * @return
     */
    @Override
    public boolean syncAppRule(Long appId) {
        AppPO appPO = appDao.selectById(appId);
        if(null == appPO) {
            throw new ServiceException("appId不存在");
        }

        MoneRuleSyncEvent moneRuleSyncEvent = new MoneRuleSyncEvent(appId);
        applicationContext.publishEvent(moneRuleSyncEvent);

        return true;
    }


    /**
     * 同步mone告警规则
     *
     * @param appId
     * @return
     */
    @Override
    public boolean consumeSyncAppRule(Long appId) {
        AppPO appPO = appDao.selectById(appId);
        if(null == appPO) {
            throw new ServiceException("appId不存在");
        }

        String appCode = StringUtils.isNotEmpty(appPO.getMoneAppCode()) ? appPO.getMoneAppCode() : appPO.getAppCode();

        List<AlarmRulePO> list = getRuleByAppCodeFromMone(appCode);
        List<Long> failRuleIdList = new ArrayList<>();
        list.forEach(trafficAlarmRule -> {
            if(!syncOneRule(trafficAlarmRule,appPO)) {
                failRuleIdList.add(trafficAlarmRule.getId());
            }
        });

        return true;
    }

    private boolean syncOneRule(AlarmRulePO trafficAlarmRule,AppPO appPO) {
        //暂时不处理复合指标
        if(null == trafficAlarmRule.getMetricType()
                || trafficAlarmRule.getMetricType() == CollectTypeEnum.COMPOSITE_METRIC.getCode()) {
            return  false;
        }

        Long appId = appPO.getId();

        //替换占位符
        List<EsAggMetricDTO> esFilterMetricList = JSONObject.parseArray(trafficAlarmRule.getFilterMetric(),
                EsAggMetricDTO.class);

        // 过滤指标配置为空的条件
        esFilterMetricList = (null == esFilterMetricList) ? new ArrayList<>() : esFilterMetricList.stream()
                .filter((elm)-> StringUtils.isNotEmpty(elm.getMetric()) && StringUtils.isNotEmpty(elm.getMetricValue()))
                .collect(Collectors.toList());

        //mone指标增加attr前缀
        esFilterMetricList.forEach(esAggMetricDTO -> {
            esAggMetricDTO.setMetric("attr."+esAggMetricDTO.getMetric());
        });


        Map<String,String> attrValue = new HashMap<>();

        esFilterMetricList.add(EsAggMetricDTO.builder().filterMetricOperator(FilterMetricOperatorEnum.IS.getValue())
                .metric(EngineConstant.PLACEHOLDER_APP_CODE)
                .metricValue(appPO.getAppCode()).build());

        esFilterMetricList.add(EsAggMetricDTO.builder().filterMetricOperator(FilterMetricOperatorEnum.IS.getValue())
                .metric(EngineConstant.PLACEHOLDER_EVENT_CODE)
                .metricValue(trafficAlarmRule.getEventCode()).build());

        esFilterMetricList.forEach(esAggMetricDTO -> {
            attrValue.put(esAggMetricDTO.getMetric(),esAggMetricDTO.getMetricValue());
            esAggMetricDTO.setMetricValue("${"+esAggMetricDTO.getMetric()+"}");
        });

        String expression = PlaceHolderUtils.replace(JSON.toJSONString(esFilterMetricList), attrValue);

        trafficAlarmRule.setName(convertRuleName(trafficAlarmRule.getName(),expression));

        //创建t_alarm_metric
        Long alarmMetricId = createAlarmMetric(trafficAlarmRule,expression);
        if(alarmMetricId == null) {
            return  false;
        }
        Long appMetricId = createAppMetric(appId,alarmMetricId,attrValue);
        if(appMetricId == null) {
            return false;
        }

        Long ruleId = createAlarmRule(trafficAlarmRule,appPO,appMetricId);
        if(ruleId == null) {
            return false;
        }

        return true;
    }

    private Long createAlarmMetric(AlarmRulePO trafficAlarmRule, String expression) {
//        Long alarmMetricId = getExistsAlarmMetricId(esFilterMetricList,trafficAlarmRule.getTimeWindow());
        Long alarmMetricId = null;
        if(alarmMetricId == null) {
            SaveAlarmMetricDTO saveAlarmMetricDTO = SaveAlarmMetricDTO.builder()
                    .name(trafficAlarmRule.getName())
                    .expression(expression)
                    .description(trafficAlarmRule.getRuleDesc())
                    .sourceType(DataSourceTypeEnum.ELASTIC_SEARCH)
                    .metricType(MetricTypeEnum.BUSINESS_METRIC)
                    .collectType(CollectTypeEnum.SINGLE_METRIC)
                    .metricTag(Arrays.asList(MetricTagConstant.METRIC_SOURCE_MONE_TAG).stream().collect(Collectors.toList()))
                    .groupType(GroupTypeEnum.getByCode(trafficAlarmRule.getType()))
                    .groupField("attr."+trafficAlarmRule.getMetric())
                    .formula(null)
                    .unit(AlarmMetricUnitEnum.NONE)
                    .build();

            TimeWindowEnum timeWindow = TimeWindowEnum.getByCode(trafficAlarmRule.getTimeWindow());
            if(timeWindow == null) {
                return null;
            } else {
                saveAlarmMetricDTO.setTimeWindow(timeWindow);
            }

            GroupTypeEnum groupType = GroupTypeEnum.getByCode(trafficAlarmRule.getType());
            if(groupType == null) {
                return null;
            } else {
                saveAlarmMetricDTO.setGroupType(groupType);
            }

            if(trafficAlarmRule.getMetric() == null) {
                return null;
            }

            saveAlarmMetricDTO = alarmMetricService.create(saveAlarmMetricDTO);
            alarmMetricId = saveAlarmMetricDTO.getId();
        }

        return alarmMetricId;
    }

    public String convertRuleName(String name,String expression) {
        Integer maxStrLength = 30;
        String endWithPoint = "...";
        switch (name) {
            case "调用服务响应时间":
                return getAttrValue(expression,"attr.method",maxStrLength,"")+" 方法响应时间";

            case "调用服务响应状态为false":
                return getAttrValue(expression,"attr.method",maxStrLength,"")+" 方法响应状态为false";

            case "http对外接口响应时间":
                return "接口响应时间 "+getAttrValue(expression,"attr.path",maxStrLength,endWithPoint);

            case "http对外接口返回状态不是200":
                return "接口返回状态不是200 " + getAttrValue(expression,"attr.path",maxStrLength,endWithPoint);

            case "http响应状态为false":
                return "接口响应状态为false " + getAttrValue(expression,"attr.path",maxStrLength,endWithPoint);

            case "对外服务响应时间":
                return getAttrValue(expression,"attr.method",maxStrLength,"")+" 对外服务响应时间";

            case "对外服务响应状态为false":
                return getAttrValue(expression,"attr.method",maxStrLength,"")+" 对外服务响应状态为false";

            default:
                return name;
        }
    }

    /**
     * 从expression获取指定属性值
     * @param expression
     * @param attr 需要查询的字符串
     * @param maxLength 允许返回的最大长度
     * @param endWith 超过最大长度后已特定字符结尾，例如:...,不需要则传null 或空字符串
     * @return
     */
    private String getAttrValue(String expression,String attr,Integer maxLength,String endWith) {
        List<EsAggMetricDTO> esFilterMetricList = JSON.parseArray(expression,EsAggMetricDTO.class);

        String attrValue = "";
        for (EsAggMetricDTO esAggMetricDTO: esFilterMetricList) {
            if(attr.equals(esAggMetricDTO.getMetric())) {
                attrValue = esAggMetricDTO.getMetricValue();
                break;
            }
        }

        if(attrValue.length() <= maxLength) {
            return attrValue;
        }

        if(StringUtils.isEmpty(endWith)) {
            return attrValue.substring(0,maxLength);
        }

        return attrValue.substring(0,maxLength - endWith.length()) + endWith;

    }


    private Long createAppMetric(Long appId,Long alarmMetricId,Map<String,String> attrValue) {
        //创建t_app_metric
        Long appMetricId = getExistsAppMetricId(appId,alarmMetricId,attrValue);
        if(appMetricId == null) {
            AppMetricDTO appMetricDTO = AppMetricDTO.builder()
                    .appId(appId)
                    .metricId(alarmMetricId)
                    .datasourceId(moneMetricDataSourceId)
                    .attrValue(attrValue)
                    .build();

            appMetricDTO = appMetricService.create(appMetricDTO);
            appMetricId = appMetricDTO.getId();
            appMetricService.changeStatus(appMetricId, EnableEnum.ENABLE.getCode());
        }

        return appMetricId;
    }

    private Long createAlarmRule(AlarmRulePO trafficAlarmRule,AppPO appPO,Long appMetricId) {
        Long appId = appPO.getId();
        Long ruleId = getExistsRuleId(appId,appMetricId);
        if(ruleId == null) {
            AlarmRuleDTO alarmRuleDTO = AlarmRuleDTO.builder()
                    .appId(appId)
                    .appMetricId(appMetricId)
                    .ruleName(trafficAlarmRule.getName())
                    .threshold(String.valueOf(trafficAlarmRule.getThreshold()))
                    .startEffectiveTime(trafficAlarmRule.getStartEffectiveTime())
                    .endEffectiveTime(trafficAlarmRule.getEndEffectiveTime())
                    .alwaysEffective(trafficAlarmRule.getIsAlwaysEffective())
                    .continuousHitTimes(trafficAlarmRule.getContinuousHitTimes())
                    .alarmLevelId(getAlarmLevelId(trafficAlarmRule))
                    .contacts(appPO.getContacts())
                    .status(EnableEnum.ENABLE)
                    .build();
            if(StringUtils.isNotBlank(trafficAlarmRule.getOperatorId())) {
                alarmRuleDTO.setCreater(Long.parseLong(trafficAlarmRule.getOperatorId()));
                alarmRuleDTO.setOperator(Long.parseLong(trafficAlarmRule.getOperatorId()));

            }

            CompareTypeEnum compareTypeEnum = CompareTypeEnum.getByCode(trafficAlarmRule.getCompare());
            if(compareTypeEnum == null) {
                return null;
            } else {
                alarmRuleDTO.setCompare(compareTypeEnum);
            }

            alarmRuleDTO = alarmRuleService.create(alarmRuleDTO);
            ruleId = alarmRuleDTO.getId();
            alarmRuleService.changeStatus(ruleId, EnableEnum.ENABLE.getCode());
        }

        return ruleId;
    }



    private List<AlarmRulePO> getRuleByAppCodeFromMone(String appCode) {
        QueryWrapper<AlarmRulePO> wrapper = new QueryWrapper<>();

        wrapper.lambda().eq(AlarmRulePO::getAppCode, appCode);
        wrapper.lambda().eq(AlarmRulePO::getIsDelete, GlobalStatusConstants.IS_DELETE_DISABLE);
        wrapper.lambda().eq(AlarmRulePO::getIsEnable, EnableEnum.ENABLE.getCode());

        return mesAlarmRuleDao.selectList(wrapper);
    }

    private Long getExistsAlarmMetricId(List<EsAggMetricDTO> esFilterMetricList, Integer timeWindow) {
        QueryWrapper<AlarmMetricPO> wrapper = new QueryWrapper();
        wrapper.lambda().eq(AlarmMetricPO::getExpression, JSON.toJSONString(esFilterMetricList));
        wrapper.lambda().eq(AlarmMetricPO::getTimeWindow, timeWindow);
        wrapper.lambda().eq(AlarmMetricPO::getCollectType, CollectTypeEnum.SINGLE_METRIC.getCode());

        List<AlarmMetricPO> alarmMetricPOList = alarmMetricDao.selectList(wrapper);
        if(alarmMetricPOList.size() > 0){
            return alarmMetricPOList.get(0).getId();
        }

        return null;
    }

    private Long getExistsAppMetricId(Long appId, Long alarmMetricId,Map<String,String> attrValue) {
        QueryWrapper<AppMetricPO> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(AppMetricPO::getAppId,appId);
        wrapper.lambda().eq(AppMetricPO::getMetricId,alarmMetricId);
        wrapper.lambda().eq(AppMetricPO::getAttrValue,JSON.toJSONString(attrValue));
        List<AppMetricPO> list = appMetricDao.selectList(wrapper);
        if(CollectionUtils.isEmpty(list)) {
            return null;
        }

        return list.get(0).getId();
    }

    private Long getExistsRuleId(Long appId, Long appMetricId) {
        QueryWrapper<com.mfw.themis.dao.po.AlarmRulePO> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(com.mfw.themis.dao.po.AlarmRulePO::getAppMetricId,appMetricId);
        wrapper.lambda().eq(com.mfw.themis.dao.po.AlarmRulePO::getAppId,appId);

        List<com.mfw.themis.dao.po.AlarmRulePO> list = alarmRuleDao.selectList(wrapper);
        if(CollectionUtils.isEmpty(list)) {
            return null;
        }

        return list.get(0).getId();
    }

    private Long getAlarmLevelId(AlarmRulePO alarmRulePO) {
        return 3L;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 设置alarmMetric数据
     * @param appId
     * @return
     */
    @Override
    public boolean refreshAlarmMetricItem(Long appId){
        QueryWrapper<AppMetricPO> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(AppMetricPO::getAppId,appId);
        List<AppMetricPO> list = appMetricDao.selectList(wrapper);
        if(CollectionUtils.isEmpty(list)) {
            return true;
        }

        for (AppMetricPO appMetricPO : list) {
            // 检查关联的alarmMetricId
            QueryWrapper<AppMetricPO> dupMetricWrapper = new QueryWrapper<>();
            dupMetricWrapper.lambda().eq(AppMetricPO::getMetricId, appMetricPO.getMetricId());

            Integer metricCount = appMetricDao.selectCount(dupMetricWrapper);

            AlarmMetricPO alarmMetricPO = alarmMetricDao.selectById(appMetricPO.getMetricId());
            // 仅处理es的单指标
            if(!alarmMetricPO.getSourceType().equals(DataSourceTypeEnum.ELASTIC_SEARCH.getCode())){
                continue;
            }
            if(!alarmMetricPO.getCollectType().equals(CollectTypeEnum.SINGLE_METRIC.getCode())){
                continue;
            }

            if(metricCount == 1){
                replaceExpression(appMetricPO, alarmMetricPO);
                alarmMetricDao.updateById(alarmMetricPO);
            }else{
                // 新建一条alarmMetric，重新关联id
                AlarmMetricPO alarmMetricPO1 = new AlarmMetricPO();

                alarmMetricPO.setId(null);
                BeanUtils.copyProperties(alarmMetricPO, alarmMetricPO1);
                replaceExpression(appMetricPO, alarmMetricPO1);
                alarmMetricDao.insert(alarmMetricPO1);

                appMetricPO.setMetricId(alarmMetricPO1.getId());
                appMetricDao.updateById(appMetricPO);
            }

        }

        return true;
    }

    private void replaceExpression(AppMetricPO appMetricPO, AlarmMetricPO alarmMetricPO){
        try {
            JSONObject jsonObject = JSON.parseObject(appMetricPO.getAttrValue());

            Map<String, String> attrValue = new HashMap<>(jsonObject.size());
            for (Map.Entry<String, Object> entry: jsonObject.entrySet()) {
                attrValue.put(entry.getKey(), Objects.toString(entry.getValue()));
            }

            alarmMetricPO.setExpression(PlaceHolderUtils.replace(alarmMetricPO.getExpression(), attrValue));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 清除alarmMetric数据
     * @param appId
     * @return
     */
    @Override
    public boolean cleanAlarmMetricItem(Long appId){
        QueryWrapper<AppMetricPO> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(AppMetricPO::getAppId,appId);
        List<AppMetricPO> list = appMetricDao.selectList(wrapper);
        if(CollectionUtils.isEmpty(list)) {
            return true;
        }

        list.forEach(appMetricPO -> {
            AlarmMetricPO alarmMetricPO = alarmMetricDao.selectById(appMetricPO.getMetricId());
            // 仅处理es的单指标
            if(!alarmMetricPO.getSourceType().equals(DataSourceTypeEnum.ELASTIC_SEARCH.getCode())){
                return;
            }
            if(!alarmMetricPO.getCollectType().equals(CollectTypeEnum.SINGLE_METRIC.getCode())){
                return;
            }

            // 删除alarmMetric
            alarmMetricPO.setIsDelete(true);
            alarmMetricDao.updateById(alarmMetricPO);

            // 删除报警规则
            QueryWrapper<com.mfw.themis.dao.po.AlarmRulePO> rulePOQueryWrapper = new QueryWrapper<>();
            rulePOQueryWrapper.lambda().eq(com.mfw.themis.dao.po.AlarmRulePO::getAppId, appId);
            rulePOQueryWrapper.lambda().eq(com.mfw.themis.dao.po.AlarmRulePO::getAppMetricId, appMetricPO.getId());

            com.mfw.themis.dao.po.AlarmRulePO alarmRulePO = alarmRuleDao.selectOne(rulePOQueryWrapper);
            if(alarmRulePO != null){
                alarmRulePO.setIsDelete(true);
                alarmRuleDao.updateById(alarmRulePO);
            }

            // 删除appMetric
            appMetricPO.setIsDelete(true);
            appMetricDao.updateById(appMetricPO);
        });

        return true;
    }


    /**
     * 设置alarmMetric数据
     * @param appId
     * @return
     */
    @Override
    public boolean refreshMetricName(Long appId){
        List<AppMetricPO> list = getAppMetricPoListByAppId(appId);
        if(CollectionUtils.isEmpty(list)) {
            return true;
        }

        for (AppMetricPO appMetricPO : list) {
            // 检查关联的alarmMetricId
            Integer metricCount = getMetricRelationCount(appMetricPO.getMetricId());

            AlarmMetricPO alarmMetricPO = alarmMetricDao.selectById(appMetricPO.getMetricId());

            // 仅处理es的单指标
            if(!alarmMetricPO.getSourceType().equals(DataSourceTypeEnum.ELASTIC_SEARCH.getCode())){
                continue;
            }
            if(!alarmMetricPO.getCollectType().equals(CollectTypeEnum.SINGLE_METRIC.getCode())){
                continue;
            }

            if(metricCount > 1) {
                continue;
            }

            String originName = alarmMetricPO.getName();
            String convertName = convertRuleName(alarmMetricPO.getName(),alarmMetricPO.getExpression());
            if(!convertName.equals(alarmMetricPO.getName())) {
                alarmMetricPO.setName(convertName);
                alarmMetricDao.updateById(alarmMetricPO);

                QueryWrapper<com.mfw.themis.dao.po.AlarmRulePO> alarmRuleWrapper = new QueryWrapper<>();
                alarmRuleWrapper.lambda().eq(com.mfw.themis.dao.po.AlarmRulePO::getAppMetricId, appMetricPO.getId());

                List<com.mfw.themis.dao.po.AlarmRulePO> alarmRulePOList = alarmRuleDao.selectList(alarmRuleWrapper);
                if( alarmRulePOList.size() != 1) {
                    continue;
                }

                com.mfw.themis.dao.po.AlarmRulePO alarmRulePO = alarmRulePOList.get(0);
                if(alarmRulePO.getRuleName().equals(originName)) {
                    alarmRulePO.setRuleName(convertName);
                    alarmRuleDao.updateById(alarmRulePO);
                }
            }
        }

        return true;
    }

    private List<AppMetricPO> getAppMetricPoListByAppId(Long appId) {
        QueryWrapper<AppMetricPO> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(AppMetricPO::getAppId,appId);

        return appMetricDao.selectList(wrapper);
    }

    private Integer getMetricRelationCount(Long metricId) {
        QueryWrapper<AppMetricPO> dupMetricWrapper = new QueryWrapper<>();
        dupMetricWrapper.lambda().eq(AppMetricPO::getMetricId, metricId);
        return appMetricDao.selectCount(dupMetricWrapper);
    }
}
