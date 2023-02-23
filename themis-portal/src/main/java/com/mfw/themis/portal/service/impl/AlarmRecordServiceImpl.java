package com.mfw.themis.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mfw.themis.common.constant.enums.RuleStatusEnum;
import com.mfw.themis.common.convert.AlarmRecordConvert;
import com.mfw.themis.common.model.bo.AlarmRecordBO;
import com.mfw.themis.common.model.dto.AlarmRecordDTO;
import com.mfw.themis.dao.mapper.AlarmRecordDao;
import com.mfw.themis.dao.mapper.AlarmRuleDao;
import com.mfw.themis.dao.po.AlarmRecordPO;
import com.mfw.themis.dao.po.AlarmRulePO;
import com.mfw.themis.dependent.mfwemployee.EmployeeClient;
import com.mfw.themis.dependent.mfwemployee.model.EmpInfoData;
import com.mfw.themis.dependent.mfwemployee.model.EmpInfoListResponse;
import com.mfw.themis.portal.model.dto.QueryAlarmRecordParam;
import com.mfw.themis.portal.service.AlarmRecordService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AlarmRecordServiceImpl implements AlarmRecordService {

    @Autowired
    private AlarmRecordDao alarmRecordDao;

    @Autowired
    private AlarmRuleDao alarmRuleDao;

    @Autowired
    private EmployeeClient employeeClient;

    /**
     * 获取记录详情
     * @param id
     * @return
     */
    @Override
    public AlarmRecordDTO queryById(Long id){
        return AlarmRecordConvert.toDTO(alarmRecordDao.selectById(id));
    }

    /**
     * 获取应用报警记录列表
     * @param queryAlarmRecordParam
     * @return
     */
    @Override
    public IPage<AlarmRecordBO> queryAlarmRecordPage(QueryAlarmRecordParam queryAlarmRecordParam){
        QueryWrapper<AlarmRecordPO> queryWrapper = new QueryWrapper<>();

        if (queryAlarmRecordParam.getAppId() != null) {
            queryWrapper.lambda().eq(AlarmRecordPO::getAppId, queryAlarmRecordParam.getAppId());
        }

        if(null != queryAlarmRecordParam.getStatus()){
            queryWrapper.lambda().eq(AlarmRecordPO::getStatus, queryAlarmRecordParam.getStatus());
        }

        if(null != queryAlarmRecordParam.getStartTime()){
            queryWrapper.lambda().ge(AlarmRecordPO::getStartTime, queryAlarmRecordParam.getStartTime());
        }

        if(null != queryAlarmRecordParam.getEndTime()){
            queryWrapper.lambda().le(AlarmRecordPO::getEndTime, queryAlarmRecordParam.getEndTime());
        }

        queryWrapper.lambda().orderByDesc(AlarmRecordPO::getId);

        Page<AlarmRecordPO> page = new Page<>(queryAlarmRecordParam.getPage(), queryAlarmRecordParam.getPageSize());
        IPage<AlarmRecordPO> dbResult = alarmRecordDao.selectPage(page, queryWrapper);

        IPage<AlarmRecordBO> result = new Page<>(queryAlarmRecordParam.getPage(),
                queryAlarmRecordParam.getPageSize());

        BeanUtils.copyProperties(dbResult, result);
        List<AlarmRecordBO> list = AlarmRecordConvert.toBOList(dbResult.getRecords());

        // 设置员工姓名
        if(null != list){
            setItemEmployeeName(list);
            setItemRuleName(list);
        }

        result.setRecords(list);

        return result;
    }

    /**
     * 设置规则名称
     * @param list
     */
    private void setItemRuleName(List<AlarmRecordBO> list){
        Set<Long> ruleIds = list.stream().map(AlarmRecordBO::getRuleId).collect(Collectors.toSet());
        if(ruleIds.size() <= 0){
            return;
        }

        QueryWrapper<AlarmRulePO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(AlarmRulePO::getId, ruleIds);

        List<AlarmRulePO> alarmRulePOList = alarmRuleDao.selectList(queryWrapper);
        if(null == alarmRulePOList){
            return;
        }

        Map<Long, AlarmRulePO> alarmRulePOMap = alarmRulePOList
                .stream().collect(Collectors.toMap(AlarmRulePO::getId, Function.identity()));

        list.forEach(item -> {
            item.setRuleName(alarmRulePOMap.containsKey(item.getRuleId()) ? alarmRulePOMap.get(item.getRuleId()).getRuleName() : "");
            item.setStatusDesc(RuleStatusEnum.getByCode(item.getStatus()).getDesc());
        });
    }

    /**
     * 设置联系人 创建人姓名
     * @param list
     */
    private void setItemEmployeeName(List<AlarmRecordBO> list){
        Set<Long> uids = new HashSet<>();
        list.forEach(item -> {
            if(StringUtils.isNotEmpty(item.getReceivers())){
                uids.addAll(Arrays.stream(StringUtils.split(item.getReceivers(), ",")).map(uid -> Long.valueOf(uid)).collect(Collectors.toList()));
            }
        });

        if(uids.size() > 0){
            EmpInfoListResponse empInfoListResponse = employeeClient.getInfoList(new ArrayList<>(uids));
            Map<Long, EmpInfoData> employeInfoMap = empInfoListResponse.getData().stream()
                    .collect(Collectors.toMap(EmpInfoData::getUid, Function.identity(), (value1, value2) -> value2));

            list.forEach(item -> {

                List<Map<String, String>> contactList = new ArrayList<>();
                if(StringUtils.isNotEmpty(item.getReceivers())){
                    Map<Long, String> existUser = new HashMap<>();

                    Arrays.asList(StringUtils.split(item.getReceivers(), ",")).forEach(uid -> {
                        Map<String, String> contactUser = new HashMap<>();

                        if(existUser.containsKey(Long.valueOf(uid))){
                            return;
                        }

                        existUser.put(Long.valueOf(uid), uid);
                        contactUser.put("uid", uid);
                        if(employeInfoMap.containsKey(Long.valueOf(uid))){
                            contactUser.put("name", employeInfoMap.get(Long.valueOf(uid)).getName());
                        }else{
                            contactUser.put("name", "");
                        }

                        contactList.add(contactUser);
                    });

                    item.setReceiverList(contactList);
                }
            });
        }
    }
}
