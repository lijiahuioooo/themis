package com.mfw.themis.portal.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mfw.themis.common.constant.EngineConstant;
import com.mfw.themis.common.constant.enums.CollectTypeEnum;
import com.mfw.themis.common.constant.enums.DataSourceTypeEnum;
import com.mfw.themis.common.constant.enums.GroupTypeEnum;
import com.mfw.themis.common.constant.enums.ResourceEnum;
import com.mfw.themis.common.convert.AlarmMetricConvert;
import com.mfw.themis.common.exception.ResourceNotFoundException;
import com.mfw.themis.common.exception.ServiceException;
import com.mfw.themis.common.exception.UnAuthenticationException;
import com.mfw.themis.common.exception.WebException;
import com.mfw.themis.common.model.bo.AlarmMetricBO;
import com.mfw.themis.common.model.bo.SuggestBO;
import com.mfw.themis.common.model.dto.AlarmMetricDTO;
import com.mfw.themis.common.model.dto.EsAggMetricDTO;
import com.mfw.themis.common.model.dto.SaveAlarmMetricDTO;
import com.mfw.themis.dao.mapper.AlarmMetricDao;
import com.mfw.themis.dao.mapper.AppMetricDao;
import com.mfw.themis.dao.po.AlarmMetricPO;
import com.mfw.themis.dao.po.AppMetricPO;
import com.mfw.themis.dependent.mfwemployee.EmployeeClient;
import com.mfw.themis.dependent.mfwemployee.model.EmpInfoData;
import com.mfw.themis.dependent.mfwemployee.model.EmpInfoListResponse;
import com.mfw.themis.portal.auth.AdminRoleUtils;
import com.mfw.themis.portal.model.dto.QueryAlarmMetricDTO;
import com.mfw.themis.portal.model.dto.QueryAlarmMetricSuggestDTO;
import com.mfw.themis.portal.service.AlarmMetricService;
import java.util.ArrayList;
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
 * @author guosp
 */
@Service
@Slf4j
public class AlarmMetricServiceImpl implements AlarmMetricService {


    @Autowired
    private AlarmMetricDao alarmMetricDao;

    @Autowired
    private AppMetricDao appMetricDao;

    @Autowired
    private EmployeeClient employeeClient;

    @Autowired
    private AdminRoleUtils adminRoleUtils;

    @Override
    public AlarmMetricBO queryById(Long id) {
        AlarmMetricPO alarmMetricPO = alarmMetricDao.selectById(id);

        return AlarmMetricConvert.toBO(alarmMetricPO);
    }

    @Override
    public IPage<AlarmMetricDTO> queryMetricPage(QueryAlarmMetricDTO queryAlarmMetricDTO) {

        IPage<AlarmMetricPO> dbResult = search(queryAlarmMetricDTO);

        IPage<AlarmMetricDTO> result = new Page<>(queryAlarmMetricDTO.getPage(), queryAlarmMetricDTO.getPageSize());
        BeanUtils.copyProperties(dbResult, result);
        List<AlarmMetricDTO> list = AlarmMetricConvert.toDTOList(dbResult.getRecords());

        setItemEmployeeName(list);

        result.setRecords(list);
        return result;
    }

    /**
     * 设置联系人 创建人姓名
     *
     * @param list
     */
    private void setItemEmployeeName(List<AlarmMetricDTO> list) {
        if (null == list || list.size() <= 0) {
            return;
        }

        Set<Long> uids = list.stream().map(AlarmMetricDTO::getOperator).collect(Collectors.toSet());

        if (uids.size() > 0) {
            EmpInfoListResponse empInfoListResponse = employeeClient.getInfoList(new ArrayList<>(uids));
            Map<Long, EmpInfoData> employeInfoMap = empInfoListResponse.getData().stream()
                    .collect(Collectors.toMap(EmpInfoData::getUid, Function
                            .identity(), (value1, value2) -> value2));

            list.forEach(item -> {
                if (item.getOperator() > 0) {
                    if (employeInfoMap.containsKey(item.getOperator())) {
                        item.setOperatorUname(employeInfoMap.get(item.getOperator()).getName());
                    }
                } else {
                    item.setOperatorUname("");
                }
            });
        }
    }

    /**
     * 指标模板搜索
     *
     * @param queryAlarmMetricDTO
     * @return
     */
    private IPage<AlarmMetricPO> search(QueryAlarmMetricDTO queryAlarmMetricDTO) {
        QueryWrapper<AlarmMetricPO> wrapper = new QueryWrapper<>();

        if (StringUtils.isNotEmpty(queryAlarmMetricDTO.getKeyword())) {
            wrapper.lambda().and(w -> w.like(AlarmMetricPO::getName, queryAlarmMetricDTO.getKeyword())
                    .or().like(AlarmMetricPO::getDescription, queryAlarmMetricDTO.getKeyword()));
        }

        if (null != queryAlarmMetricDTO.getId()) {
            wrapper.lambda().eq(AlarmMetricPO::getId, queryAlarmMetricDTO.getId());
        }
        if (null != queryAlarmMetricDTO.getIsDelete()) {
            wrapper.lambda().eq(AlarmMetricPO::getIsDelete, queryAlarmMetricDTO.getIsDelete());
        }

        if (null != queryAlarmMetricDTO.getMetricType()) {
            wrapper.lambda().eq(AlarmMetricPO::getMetricType, queryAlarmMetricDTO.getMetricType());
        }

        if (null != queryAlarmMetricDTO.getSourceType()) {
            wrapper.lambda().eq(AlarmMetricPO::getSourceType, queryAlarmMetricDTO.getSourceType());
        }

        wrapper.lambda().orderByDesc(AlarmMetricPO::getId);

        Page<AlarmMetricPO> page = new Page<>(queryAlarmMetricDTO.getPage(), queryAlarmMetricDTO.getPageSize());
        return alarmMetricDao.selectPage(page, wrapper);
    }

    /**
     * 指标模板 suggest
     *
     * @param queryAlarmMetricSuggestDTO
     * @return
     */
    @Override
    public List<SuggestBO> queryMetricSuggest(QueryAlarmMetricSuggestDTO queryAlarmMetricSuggestDTO) {
        QueryWrapper<AlarmMetricPO> wrapper = new QueryWrapper<>();
        if (null != queryAlarmMetricSuggestDTO.getCollectType()) {
            wrapper.lambda().eq(AlarmMetricPO::getCollectType, queryAlarmMetricSuggestDTO.getCollectType());
        }
        if (null != queryAlarmMetricSuggestDTO.getSourceType()) {
            wrapper.lambda().eq(AlarmMetricPO::getSourceType, queryAlarmMetricSuggestDTO.getSourceType());
        }

        if (null != queryAlarmMetricSuggestDTO.getMetricType()) {
            wrapper.lambda().eq(AlarmMetricPO::getMetricType, queryAlarmMetricSuggestDTO.getMetricType());
        }

        if (StringUtils.isNoneBlank(queryAlarmMetricSuggestDTO.getKeyword())) {
            wrapper.lambda().like(AlarmMetricPO::getName, queryAlarmMetricSuggestDTO.getKeyword());
        }

        wrapper.lambda().eq(AlarmMetricPO::getIsDelete, 0);

        List<AlarmMetricPO> alarmMetricList = alarmMetricDao.selectList(wrapper);

        return AlarmMetricConvert.toSuggestBOList(alarmMetricList);
    }

    @Override
    public SaveAlarmMetricDTO create(SaveAlarmMetricDTO saveAlarmMetricDTO) {
        if (!adminRoleUtils.checkAdminRole(saveAlarmMetricDTO.getOperator())) {
            throw new UnAuthenticationException();
        }

        checkMetricParams(saveAlarmMetricDTO);

        AlarmMetricDTO alarmMetricDTO = new AlarmMetricDTO();
        BeanUtils.copyProperties(saveAlarmMetricDTO, alarmMetricDTO);

        AlarmMetricPO alarmMetric = AlarmMetricConvert.toPO(alarmMetricDTO);
        alarmMetricDao.insert(alarmMetric);

        saveAlarmMetricDTO.setId(alarmMetric.getId());
        return saveAlarmMetricDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SaveAlarmMetricDTO saveAlarmMetricDTO) {
        if (!adminRoleUtils.checkAdminRole(saveAlarmMetricDTO.getOperator())) {
            throw new UnAuthenticationException();
        }
        checkMetricParams(saveAlarmMetricDTO);
        AlarmMetricPO originAlarmMetricPO = alarmMetricDao.selectById(saveAlarmMetricDTO.getId());
        log.info("update alarmMetric By uid:{}.old alarm:{},new alarm:{}", saveAlarmMetricDTO.getOperator(),
                JSON.toJSONString(originAlarmMetricPO), JSON.toJSONString(saveAlarmMetricDTO));
        if (originAlarmMetricPO == null) {
            throw new ResourceNotFoundException(ResourceEnum.ALARM_METRIC, saveAlarmMetricDTO.getId());
        }
        if (saveAlarmMetricDTO.getExpression() != null) {
            originAlarmMetricPO.setExpression(saveAlarmMetricDTO.getExpression());
        }
        if (saveAlarmMetricDTO.getName() != null) {
            originAlarmMetricPO.setName(saveAlarmMetricDTO.getName());
        }
        if (saveAlarmMetricDTO.getDescription() != null) {
            originAlarmMetricPO.setDescription(saveAlarmMetricDTO.getDescription());
        }
        if (saveAlarmMetricDTO.getTimeWindow() != null) {
            originAlarmMetricPO.setTimeWindow(saveAlarmMetricDTO.getTimeWindow().getCode());
        }
        if (saveAlarmMetricDTO.getMetricTag() != null) {
            originAlarmMetricPO.setMetricTag(StringUtils.join(saveAlarmMetricDTO.getMetricTag(), ","));
        }
        if (saveAlarmMetricDTO.getOperator() != null) {
            originAlarmMetricPO.setOperator(saveAlarmMetricDTO.getOperator());
        }

        return alarmMetricDao.updateById(originAlarmMetricPO) == 1;

    }


    private void checkMetricParams(SaveAlarmMetricDTO saveAlarmMetricDTO) {
        if (saveAlarmMetricDTO.getSourceType().equals(DataSourceTypeEnum.ELASTIC_SEARCH)) {
            if (StringUtils.isBlank(saveAlarmMetricDTO.getGroupField()) &&
                    !saveAlarmMetricDTO.getGroupType().equals(GroupTypeEnum.COUNT)) {
                throw new ServiceException("es数据源 group_field不能为空");
            }

            List<EsAggMetricDTO> esFilterMetricList = JSONObject.parseArray(saveAlarmMetricDTO.getExpression(),
                    EsAggMetricDTO.class);
            if (null != esFilterMetricList) {
                boolean isExistAppCode = false;
                for (EsAggMetricDTO esAggMetricDTO : esFilterMetricList) {
                    if (esAggMetricDTO.getMetric().equals(EngineConstant.PLACEHOLDER_APP_CODE)) {
                        isExistAppCode = true;
                    }
                }
                if (!isExistAppCode) {
                    EsAggMetricDTO esAggMetric = EsAggMetricDTO.builder()
                            .metric(EngineConstant.PLACEHOLDER_APP_CODE)
                            .filterMetricOperator("is")
                            .metricValue("${" + EngineConstant.PLACEHOLDER_APP_CODE + "}")
                            .build();
                    esFilterMetricList.add(esAggMetric);
                }

                saveAlarmMetricDTO.setExpression(JSON.toJSONString(esFilterMetricList));
            } else {
                if (saveAlarmMetricDTO.getCollectType().equals(CollectTypeEnum.SINGLE_METRIC)) {
                    throw new ServiceException("es数据源 单指标表达式不能为空");
                }
            }
        }

        if (saveAlarmMetricDTO.getCollectType().equals(CollectTypeEnum.COMPOSITE_METRIC)) {
            if (StringUtils.isBlank(saveAlarmMetricDTO.getFormula())) {
                throw new ServiceException("复合指标计算公式formula不能为空");
            }
        }
    }

    @Override
    public boolean delete(Long id) {
        AlarmMetricPO originAlarmMetricPO = alarmMetricDao.selectById(id);
        if (originAlarmMetricPO == null) {
            throw new ResourceNotFoundException(ResourceEnum.ALARM_METRIC, id);
        }

        originAlarmMetricPO.setIsDelete(true);

        return alarmMetricDao.updateById(originAlarmMetricPO) == 1;
    }

    /**
     * 删除指标模板
     *
     * @param id
     * @param operatorUid
     * @return
     */
    @Override
    public boolean deleteTpl(Long id, Long operatorUid) {
        AlarmMetricPO alarmMetric = alarmMetricDao.selectById(id);
        if (null == alarmMetric) {
            throw new ResourceNotFoundException(ResourceEnum.ALARM_METRIC, id);
        }

        if (!alarmMetric.getSourceType().equals(DataSourceTypeEnum.PROMETHEUS.getCode())) {
            throw new WebException("非Prometheus数据源模板不允许删除");
        }

        /**
         * 判断是否有已关联的指标
         */
        QueryWrapper<AppMetricPO> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(AppMetricPO::getMetricId, id);
        wrapper.lambda().eq(AppMetricPO::getIsDelete, false);
        List<AppMetricPO> appMetricList = appMetricDao.selectList(wrapper);
        if (null != appMetricList && appMetricList.size() >= 1) {
            throw new WebException("此模板已关联指标不允许删除");
        }

        alarmMetric.setIsDelete(true);
        alarmMetric.setMtime(null);
        alarmMetric.setOperator(operatorUid);

        return alarmMetricDao.updateById(alarmMetric) == 1;
    }
}
