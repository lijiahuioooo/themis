package com.mfw.themis.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mfw.themis.common.constant.enums.CompareTypeEnum;
import com.mfw.themis.common.constant.enums.EnableEnum;
import com.mfw.themis.common.constant.enums.ResourceEnum;
import com.mfw.themis.common.convert.AlarmRuleConvert;
import com.mfw.themis.common.exception.ResourceNotFoundException;
import com.mfw.themis.common.exception.ServiceException;
import com.mfw.themis.common.model.bo.AlarmRecordBO;
import com.mfw.themis.common.model.dto.AlarmRuleDTO;
import com.mfw.themis.dao.mapper.AlarmLevelDao;
import com.mfw.themis.dao.mapper.AlarmRuleDao;
import com.mfw.themis.dao.mapper.AppDao;
import com.mfw.themis.dao.mapper.AppMetricDao;
import com.mfw.themis.dao.po.AlarmLevelPO;
import com.mfw.themis.dao.po.AlarmMetricPO;
import com.mfw.themis.dao.po.AlarmRulePO;
import com.mfw.themis.dao.po.AppMetricPO;
import com.mfw.themis.dao.po.AppPO;
import com.mfw.themis.dao.po.union.AppMetricUnionPO;
import com.mfw.themis.dao.po.union.QueryAppMetricPO;
import com.mfw.themis.dependent.mfwemployee.EmployeeClient;
import com.mfw.themis.dependent.mfwemployee.model.EmpInfoData;
import com.mfw.themis.dependent.mfwemployee.model.EmpInfoListResponse;
import com.mfw.themis.portal.model.dto.QueryAlarmRuleParam;
import com.mfw.themis.portal.model.dto.QueryAlarmRuleResponse;
import com.mfw.themis.portal.service.AlarmRuleService;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author wenhong
 */
@Slf4j
@Service
public class AlarmRuleServiceImpl implements AlarmRuleService {

    @Autowired
    private AlarmRuleDao alarmRuleDao;

    @Autowired
    private AppDao appDao;

    @Autowired
    private EmployeeClient employeeClient;

    @Autowired
    private AlarmLevelDao alarmLevelDao;

    @Autowired
    private AppMetricDao appMetricDao;

    @Override
    public AlarmRuleDTO queryById(Long id) {
        return AlarmRuleConvert.toDTO(alarmRuleDao.selectById(id));
    }

    @Override
    public IPage<QueryAlarmRuleResponse> queryAlarmRulePage(QueryAlarmRuleParam queryAlarmRuleParam) {
        QueryWrapper<AlarmRulePO> wrapper = new QueryWrapper<>();

        if (StringUtils.isNotEmpty(queryAlarmRuleParam.getKeyword())) {
            wrapper.lambda().and(w -> w.like(AlarmRulePO::getRuleName, queryAlarmRuleParam.getKeyword()).or()
                    .eq(AlarmRulePO::getId, queryAlarmRuleParam.getKeyword()));
        }
        if (queryAlarmRuleParam.getAppId() != null) {
            wrapper.lambda().eq(AlarmRulePO::getAppId, queryAlarmRuleParam.getAppId());
        }
        if (queryAlarmRuleParam.getCompare() != null) {
            wrapper.lambda().eq(AlarmRulePO::getCompare, queryAlarmRuleParam.getCompare());
        }
        if (queryAlarmRuleParam.getStatus() != null) {
            wrapper.lambda().eq(AlarmRulePO::getStatus, queryAlarmRuleParam.getStatus());
        }
        if (queryAlarmRuleParam.getAlarmLevelId() != null) {
            wrapper.lambda().eq(AlarmRulePO::getAlarmLevelId, queryAlarmRuleParam.getAlarmLevelId());
        }
        wrapper.lambda().eq(AlarmRulePO::getIsDelete, false);

        wrapper.lambda().orderByDesc(AlarmRulePO::getId);

        Page<AlarmRulePO> page = new Page<>(queryAlarmRuleParam.getPage(), queryAlarmRuleParam.getPageSize());
        IPage<AlarmRulePO> dbResult = alarmRuleDao.selectPage(page, wrapper);

        IPage<QueryAlarmRuleResponse> result = new Page<>(queryAlarmRuleParam.getPage(),
                queryAlarmRuleParam.getPageSize());
        BeanUtils.copyProperties(dbResult, result);
        List<QueryAlarmRuleResponse> list = dbResult.getRecords().stream().map(this::convertToResponse)
                .collect(Collectors.toList());

        // set metricName
        setAlarmRuleMetricInfo(list);
        // set operator uname
        setItemEmployeeName(list);

        result.setRecords(list);

        return result;
    }

    /**
     * 设置关联指标名称
     * @param list
     */
    private void setAlarmRuleMetricInfo(List<QueryAlarmRuleResponse> list){
        if(null == list || list.size() <= 0){
            return;
        }

        List<Long> appMetricIds = list.stream().map(QueryAlarmRuleResponse::getAppMetricId).collect(Collectors.toList());

        QueryAppMetricPO queryAppMetricPO = QueryAppMetricPO.builder().appMetricIds(appMetricIds).build();

        IPage<AppMetricUnionPO> appMetricUnionPOPage = appMetricDao.selectAppMetricPageByParam(
                new Page<>(1, 100), queryAppMetricPO);

        Map<Long, AppMetricUnionPO> appMetricUnionPOMap = appMetricUnionPOPage.getRecords()
                .stream().collect(
                        Collectors.toMap(AppMetricUnionPO::getId, Function.identity()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        list.forEach(queryAlarmRuleResponse -> {
            if(!queryAlarmRuleResponse.getAlwaysEffective()){
                queryAlarmRuleResponse.setStartEffectiveTime(parseTimeHHmmss(queryAlarmRuleResponse.getStartEffectiveTime()));
                queryAlarmRuleResponse.setEndEffectiveTime(parseTimeHHmmss(queryAlarmRuleResponse.getEndEffectiveTime()));
            }

            if(appMetricUnionPOMap.containsKey(queryAlarmRuleResponse.getAppMetricId())){
                queryAlarmRuleResponse.setMetricName(
                        appMetricUnionPOMap.get(queryAlarmRuleResponse.getAppMetricId()).getName());
            }
        });

    }

    /**
     * 时分秒解析函数
     * @param dateTime
     * @return
     */
    private String parseTimeHHmmss(String dateTime){
        LocalTime localTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        try{
            if(StringUtils.isNotBlank(dateTime)){
                String[] dateTimeArr = dateTime.split(" ");
                if(dateTimeArr.length > 1){
                    localTime = LocalTime.parse(dateTimeArr[1]);
                }else{
                    localTime = LocalTime.parse(dateTimeArr[0]);
                }

                return localTime.format(formatter);
            }
        }catch (Exception e){
            log.error("parseTimeHHmmss datetime:{} error:{}", dateTime, e);
        }

        return "";
    }

    /**
     * 设置联系人 创建人姓名
     * @param list
     */
    private void setItemEmployeeName(List<QueryAlarmRuleResponse> list){
        if(null == list || list.size() <= 0){
            return;
        }

        Set<Long> uids = list.stream().map(QueryAlarmRuleResponse::getOperator).collect(Collectors.toSet());

        if(uids.size() > 0){
            EmpInfoListResponse empInfoListResponse = employeeClient.getInfoList(new ArrayList<>(uids));
            Map<Long, EmpInfoData> employeInfoMap = empInfoListResponse.getData().stream().collect(
                    Collectors.toMap(EmpInfoData::getUid, Function.identity(), (value1, value2) -> value2));

            list.forEach(item -> {
                if(item.getOperator() > 0){
                    if(employeInfoMap.containsKey(item.getOperator())){
                        item.setOperatorUname(employeInfoMap.get(item.getOperator()).getName());
                    }
                }else{
                    item.setOperatorUname("");
                }
            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AlarmRuleDTO create(AlarmRuleDTO alarmRuleDTO) {
        AlarmRulePO po = AlarmRuleConvert.toPO(alarmRuleDTO);

        checkResourceExists(alarmRuleDTO);
        if(alarmRuleDTO.getStatus().getCode() == EnableEnum.ENABLE.getCode()){
            po.setStatus(EnableEnum.ENABLE.getCode());
        }else{
            po.setStatus(EnableEnum.DISABLE.getCode());
        }

        po.setCreater(alarmRuleDTO.getOperator());
        po.setOperator(alarmRuleDTO.getOperator());
        po.setIsDelete(false);

        alarmRuleDao.insert(po);

        return AlarmRuleConvert.toDTO(po);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer update(AlarmRuleDTO alarmRuleDTO) {
        AlarmRulePO alarmRulePO = alarmRuleDao.selectById(alarmRuleDTO.getId());
        if (alarmRulePO == null) {
            throw new ResourceNotFoundException(ResourceEnum.ALARM_RULE, alarmRuleDTO.getId());
        }
        checkResourceExists(alarmRuleDTO);
        alarmRulePO.setOperator(alarmRuleDTO.getOperator());
        return alarmRuleDao.updateById(AlarmRuleConvert.toPO(alarmRuleDTO));
    }

    private void checkResourceExists(AlarmRuleDTO alarmRuleDTO) {
        AppMetricPO appMetricPO = appMetricDao.selectById(alarmRuleDTO.getAppMetricId());

        AppPO appPO = appDao.selectById(alarmRuleDTO.getAppId());

        // Themismetric自身服务的不做校验，需要保持为0的场景
        if(!appPO.getAppCode().equals("themismetric-msp")){
            if (alarmRuleDTO.getAppMetricId() != null && appMetricPO == null) {
                throw new ResourceNotFoundException(ResourceEnum.APP_METRIC, alarmRuleDTO.getAppMetricId());
            }

            // 校验appid是否一致
            if(!appPO.getId().equals(appMetricPO.getAppId())){
                throw new ServiceException("appMetric appid 不一致");
            }
        }

        if (alarmRuleDTO.getAppId() != null && appPO == null) {
            throw new ResourceNotFoundException(ResourceEnum.APP, alarmRuleDTO.getAppId());
        }

        AlarmLevelPO alarmLevelPO = alarmLevelDao.selectById(alarmRuleDTO.getAlarmLevelId());
        if (alarmRuleDTO.getAlarmLevelId() != null && alarmLevelPO == null) {
            throw new ResourceNotFoundException(ResourceEnum.ALARM_LEVEL, alarmRuleDTO.getAppId());
        }
    }

    @Override
    public Integer delete(Long id) {
        AlarmRulePO alarmRulePO = alarmRuleDao.selectById(id);
        if (alarmRulePO == null) {
            throw new ResourceNotFoundException(ResourceEnum.ALARM_RULE, id);
        }

        // 逻辑删除
        alarmRulePO.setIsDelete(true);

        return alarmRuleDao.updateById(alarmRulePO);
    }

    @Override
    public Integer changeStatus(Long id, Integer status) {
        AlarmRulePO alarmRulePO = alarmRuleDao.selectById(id);
        if (alarmRulePO == null) {
            throw new ResourceNotFoundException(ResourceEnum.ALARM_RULE, id);
        }

        alarmRulePO.setStatus(status);

        return alarmRuleDao.updateById(alarmRulePO);
    }

    private QueryAlarmRuleResponse convertToResponse(AlarmRulePO alarmRule) {
        QueryAlarmRuleResponse response = new QueryAlarmRuleResponse();
        BeanUtils.copyProperties(alarmRule, response);
        response.setStatus(EnableEnum.getByCode(alarmRule.getStatus()));
        response.setCompare(CompareTypeEnum.getByCode(alarmRule.getCompare()));
        if (StringUtils.isNotBlank(alarmRule.getContacts())) {
            EmpInfoListResponse empInfoListResponse = employeeClient.getInfoList(
                    Arrays.stream(alarmRule.getContacts().split(",")).filter(StringUtils::isNotBlank).map(Long::valueOf)
                            .collect(
                                    Collectors.toList()));
            response.setContactMembers(empInfoListResponse.getData());

        }
        return response;
    }
}
