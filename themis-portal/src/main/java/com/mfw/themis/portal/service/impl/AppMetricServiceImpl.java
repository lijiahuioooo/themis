package com.mfw.themis.portal.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mfw.themis.common.constant.GlobalStatusConstants;
import com.mfw.themis.common.constant.enums.CollectTypeEnum;
import com.mfw.themis.common.constant.enums.DataSourceTypeEnum;
import com.mfw.themis.common.constant.enums.EnableEnum;
import com.mfw.themis.common.constant.enums.MetricTypeEnum;
import com.mfw.themis.common.constant.enums.ResourceEnum;
import com.mfw.themis.common.convert.AppMetricConvert;
import com.mfw.themis.common.exception.ResourceNotFoundException;
import com.mfw.themis.common.exception.ServiceException;
import com.mfw.themis.common.exception.WebException;
import com.mfw.themis.common.model.bo.AppMetricBO;
import com.mfw.themis.common.model.bo.SuggestBO;
import com.mfw.themis.common.model.bo.user.UserAppMetricBO;
import com.mfw.themis.common.model.dto.AppMetricDTO;
import com.mfw.themis.common.model.dto.CompositeAppMetricDTO;
import com.mfw.themis.common.util.PlaceHolderUtils;
import com.mfw.themis.dao.mapper.AlarmDataSourceDao;
import com.mfw.themis.dao.mapper.AlarmMetricDao;
import com.mfw.themis.dao.mapper.AppDao;
import com.mfw.themis.dao.mapper.AppMetricDao;
import com.mfw.themis.dao.mapper.CompositeAppMetricDao;
import com.mfw.themis.dao.po.AlarmDataSourcePO;
import com.mfw.themis.dao.po.AlarmMetricPO;
import com.mfw.themis.dao.po.AppMetricPO;
import com.mfw.themis.dao.po.AppPO;
import com.mfw.themis.dao.po.CompositeAppMetricPO;
import com.mfw.themis.dao.po.union.AppMetricUnionPO;
import com.mfw.themis.dao.po.union.QueryAppMetricPO;
import com.mfw.themis.dependent.mfwemployee.EmployeeClient;
import com.mfw.themis.dependent.mfwemployee.model.EmpInfoData;
import com.mfw.themis.dependent.mfwemployee.model.EmpInfoListResponse;
import com.mfw.themis.portal.manager.GrafanaManager;
import com.mfw.themis.portal.model.dto.QueryAppMetricDTO;
import com.mfw.themis.portal.model.dto.QueryAppMetricSuggestDTO;
import com.mfw.themis.portal.service.AppMetricService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
public class AppMetricServiceImpl implements AppMetricService {

    @Autowired
    private AppMetricDao appMetricDao;

    @Autowired
    private AlarmMetricDao alarmMetricDao;

    @Autowired
    private AlarmDataSourceDao alarmDataSourceDao;

    @Autowired
    private CompositeAppMetricDao compositeAppMetricDao;

    @Autowired
    private AppDao appDao;

    @Autowired
    private EmployeeClient employeeClient;

    @Autowired
    private GrafanaManager grafanaManager;

    private static final String YING_YONG = "https://grafana.mfwdev.com/d/BgiQ4sBGz/ying-yong";
    private static final String XI_TONG = "https://grafana.mfwdev.com/d/3nGh7OdWk/jvm";
    private static final String RONG_QI = "https://grafana.mfwdev.com/d/OJjUh5BMk/rong-qi";

    /**
     * 用户 - 指标列表
     *
     * @param queryAppMetricDTO
     * @return
     */
    @Override
    public IPage<UserAppMetricBO> userQueryAppMetricByParam(QueryAppMetricDTO queryAppMetricDTO) {

        QueryAppMetricPO queryAppMetricPO = new QueryAppMetricPO();
        BeanUtils.copyProperties(queryAppMetricDTO, queryAppMetricPO);

        IPage<AppMetricUnionPO> dbResult = appMetricDao
                .selectAppMetricPageByParam(new Page<>(queryAppMetricDTO.getPage(),
                                queryAppMetricDTO.getPageSize()),
                        queryAppMetricPO);

        IPage<UserAppMetricBO> result = new Page<>(queryAppMetricDTO.getPage(), queryAppMetricDTO.getPageSize());
        BeanUtils.copyProperties(dbResult, result);

        List<UserAppMetricBO> metricList = AppMetricConvert.toUserAppMetricBOList(dbResult.getRecords());

        metricList.forEach(metric -> {
            CollectTypeEnum collectTypeEnum = CollectTypeEnum.getByCode(metric.getCollectType());
            metric.setCollectTypeDesc(null != collectTypeEnum ? collectTypeEnum.getDesc() : "");

            MetricTypeEnum metircTypeEnum = MetricTypeEnum.getByCode(metric.getMetricType());
            metric.setMetricTypeDesc(null != metircTypeEnum ? metircTypeEnum.getDesc() : "");
        });

        // 设置创建人姓名
        setItemEmployeeName(metricList);

        result.setRecords(metricList);

        return result;
    }

    /**
     * 指标Suggest接口
     *
     * @param queryAppMetricSuggestDTO
     * @return
     */
    @Override
    public List<SuggestBO> queryAppMetricSuggestByParam(QueryAppMetricSuggestDTO queryAppMetricSuggestDTO) {

        QueryAppMetricPO queryAppMetricPO = new QueryAppMetricPO();
        BeanUtils.copyProperties(queryAppMetricSuggestDTO, queryAppMetricPO);

        if (StringUtils.isNotBlank(queryAppMetricSuggestDTO.getKeyword())) {
            queryAppMetricPO.setName(queryAppMetricSuggestDTO.getKeyword());
        }

        List<AppMetricUnionPO> result = appMetricDao.selectAppMetricSuggestPageByParam(queryAppMetricPO);

        List<SuggestBO> metricList = AppMetricConvert.toAppMetricSuggestBOList(result);

        return metricList;
    }

    /**
     * 指标详情
     *
     * @param appMetricId
     * @return
     */
    @Override
    public AppMetricBO appMetricDetail(Long appMetricId) {
        QueryAppMetricPO queryAppMetricPO = new QueryAppMetricPO();

        QueryAppMetricDTO queryAppMetricDTO = QueryAppMetricDTO.builder().appMetricId(appMetricId).build();
        BeanUtils.copyProperties(queryAppMetricDTO, queryAppMetricPO);

        IPage<AppMetricUnionPO> dbResult = appMetricDao
                .selectAppMetricPageByParam(new Page<>(1, 1), queryAppMetricPO);

        if (null == dbResult.getRecords() || dbResult.getRecords().size() <= 0) {
            throw new ResourceNotFoundException(ResourceEnum.APP_METRIC, appMetricId);
        }

        AppMetricBO metricBO = AppMetricConvert.toAppMetricBO(dbResult.getRecords().get(0));

        // 复合指标
        if (metricBO.getCollectType().equals(CollectTypeEnum.COMPOSITE_METRIC.getCode())) {
            // 获取单指标
            QueryWrapper<CompositeAppMetricPO> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(CompositeAppMetricPO::getCompositeAppMetricId, appMetricId);
            List<CompositeAppMetricPO> compositeAppMetricPOList = compositeAppMetricDao.selectList(queryWrapper);

            List<Long> singleAppMetricIds = new ArrayList<>();
            Map<Long, String> singleAppMetricNameMap = new HashMap<>();
            compositeAppMetricPOList.forEach(compositeAppMetricPO -> {
                singleAppMetricIds.add(compositeAppMetricPO.getSingleAppMetricId());
                singleAppMetricNameMap
                        .put(compositeAppMetricPO.getSingleAppMetricId(), compositeAppMetricPO.getMetricName());
            });

            QueryAppMetricPO singleQueryAppMetricPO = QueryAppMetricPO.builder().appMetricIds(singleAppMetricIds)
                    .build();
            IPage<AppMetricUnionPO> singleMetricResult = appMetricDao
                    .selectAppMetricPageByParam(new Page<>(1, 10), singleQueryAppMetricPO);
            if (singleMetricResult.getSize() == 0) {
                metricBO.setMetricList(new ArrayList<>());
            } else {
                List<AppMetricBO> metricBOList = AppMetricConvert.toAppMetricBOList(singleMetricResult.getRecords());
                // 设置指标编码
                metricBOList.forEach(singleMetricBO -> {
                    singleMetricBO.setMetricName(singleAppMetricNameMap.get(singleMetricBO.getId()));
                });

                metricBO.setMetricList(metricBOList);
            }
        }

        return metricBO;
    }

    /**
     * 指标模板详情
     *
     * @param appMetricId
     * @param sourceType
     * @return
     */
    @Override
    public AppMetricBO appMetricTplDetail(Long appMetricId, Integer sourceType) {

        if (sourceType.equals(DataSourceTypeEnum.PROMETHEUS.getCode())) {
            AlarmMetricPO alarmMetricPO = alarmMetricDao.selectById(appMetricId);

            AppMetricUnionPO appMetricUnionPO = new AppMetricUnionPO();
            BeanUtils.copyProperties(alarmMetricPO, appMetricUnionPO);

            return AppMetricConvert.toAppMetricBO(appMetricUnionPO);
        }

        return appMetricDetail(appMetricId);
    }

    @Override
    public AppMetricDTO queryById(Long id) {
        AppMetricDTO appMetricDTO = AppMetricConvert.toDTO(appMetricDao.selectById(id));
        List<CompositeAppMetricPO> list = getCompositeMetricList(id);
        List<CompositeAppMetricDTO> compositeDTOList = new ArrayList<>();
        list.forEach(compositeAppMetricPO -> {
            CompositeAppMetricDTO compositeAppMetricDTO = new CompositeAppMetricDTO();
            BeanUtils.copyProperties(compositeAppMetricPO, compositeAppMetricDTO);
            compositeDTOList.add(compositeAppMetricDTO);
        });
        appMetricDTO.setCompositeAppMetricList(compositeDTOList);

        return appMetricDTO;
    }

    @Override
    public IPage<AppMetricBO> queryAppMetricByParam(QueryAppMetricDTO queryAppMetricDTO) {

        QueryAppMetricPO queryAppMetricPO = new QueryAppMetricPO();
        BeanUtils.copyProperties(queryAppMetricDTO, queryAppMetricPO);

        IPage<AppMetricUnionPO> dbResult = appMetricDao
                .selectAppMetricPageByParam(new Page<>(queryAppMetricDTO.getPage(),
                                queryAppMetricDTO.getPageSize()),
                        queryAppMetricPO);

        IPage<AppMetricBO> result = new Page<>(queryAppMetricDTO.getPage(), queryAppMetricDTO.getPageSize());
        BeanUtils.copyProperties(dbResult, result);

        List<AppMetricBO> list = AppMetricConvert.toAppMetricBOList(dbResult.getRecords());
        result.setRecords(list);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppMetricDTO create(AppMetricDTO appMetricDTO) {
        AppPO appPO = appDao.selectById(appMetricDTO.getAppId());
        if (appPO == null) {
            throw new ResourceNotFoundException(ResourceEnum.APP, appMetricDTO.getAppId());
        }

        AlarmMetricPO alarmMetricPO = alarmMetricDao.selectById(appMetricDTO.getMetricId());
        if (alarmMetricPO == null) {
            throw new ResourceNotFoundException(ResourceEnum.ALARM_METRIC, appMetricDTO.getMetricId());
        }

        AlarmDataSourcePO alarmDataSourcePO = alarmDataSourceDao.selectById(appMetricDTO.getDatasourceId());
        if (alarmDataSourcePO == null) {
            throw new ResourceNotFoundException(ResourceEnum.DATASOURCE, appMetricDTO.getDatasourceId());
        }

        checkParams(appMetricDTO, alarmMetricPO);

        QueryWrapper<AppMetricPO> wrapper = new QueryWrapper<>();

        wrapper.lambda().eq(AppMetricPO::getAppId, appMetricDTO.getAppId());
        wrapper.lambda().eq(AppMetricPO::getMetricId, appMetricDTO.getMetricId());
        wrapper.lambda().eq(AppMetricPO::getDatasourceId, appMetricDTO.getDatasourceId());
        wrapper.lambda().eq(AppMetricPO::getAttrValue, JSON.toJSONString(appMetricDTO.getAttrValue()));

        wrapper.lambda().orderByDesc(AppMetricPO::getId);
        AppMetricPO existAppMetricPO = appMetricDao.selectOne(wrapper);
        if (existAppMetricPO != null && existAppMetricPO.getIsDelete()
                .equals(GlobalStatusConstants.IS_DELETE_DISABLE)) {
            throw new ServiceException("应用指标关系已存在 ");
        }

        AppMetricPO appMetricPO = AppMetricConvert.toPO(appMetricDTO);
        appMetricPO.setStatus(EnableEnum.DISABLE.getCode());
        appMetricPO.setIsDelete(GlobalStatusConstants.IS_DELETE_DISABLE);

        if (existAppMetricPO == null) {
            appMetricDao.insert(appMetricPO);
        } else {
            //软删除后重新添加需要修改删除状态
            appMetricPO.setId(existAppMetricPO.getId());
            appMetricPO.setIsDelete(GlobalStatusConstants.IS_DELETE_DISABLE);
            appMetricDao.updateById(appMetricPO);
        }

        //复合指标保存单指标和复合指标关系,注：保存关系之前不能给appMetricDTO.id赋值
        saveCompositeMetricList(appMetricDTO, appMetricPO.getId(), alarmMetricPO);
        BeanUtils.copyProperties(appMetricPO, appMetricDTO);

        return appMetricDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer update(AppMetricDTO appMetricDTO) {
        AppMetricPO originAppMetricPO = appMetricDao.selectById(appMetricDTO.getId());
        if (originAppMetricPO == null) {
            throw new ResourceNotFoundException(ResourceEnum.APP_METRIC, appMetricDTO.getId());
        }
        AppMetricPO appMetricPO = AppMetricConvert.toPO(appMetricDTO);

        AlarmMetricPO alarmMetricPO = alarmMetricDao.selectById(appMetricDTO.getMetricId());

        checkParams(appMetricDTO, alarmMetricPO);

        saveCompositeMetricList(appMetricDTO, appMetricPO.getId(), alarmMetricPO);

        return appMetricDao.updateById(appMetricPO);
    }

    @Override
    public Integer delete(Long id) {

        AppMetricPO appMetricPO = appMetricDao.selectById(id);
        if (appMetricPO == null) {
            throw new ResourceNotFoundException(ResourceEnum.APP_METRIC, id);
        }

        // 逻辑删除
        appMetricPO.setIsDelete(true);
        appMetricPO.setMtime(null);

        return appMetricDao.updateById(appMetricPO);
    }

    @Override
    public Integer changeStatus(Long id, Integer status) {
        AppMetricPO appMetricPO = appMetricDao.selectById(id);
        if (appMetricPO == null) {
            throw new ResourceNotFoundException(ResourceEnum.APP_METRIC, id);
        }

        appMetricPO.setStatus(status);

        return appMetricDao.updateById(appMetricPO);
    }

    private void checkParams(AppMetricDTO appMetricDTO, AlarmMetricPO alarmMetricPO) {
        if (alarmMetricPO.getCollectType().equals(CollectTypeEnum.COMPOSITE_METRIC.getCode())) {
            if (CollectionUtils.isEmpty(appMetricDTO.getCompositeAppMetricList())) {
                throw new ServiceException("复合指标指标列表不能为空");
            }

            appMetricDTO.getCompositeAppMetricList().stream().forEach(compositeMetricDTO -> {
                AppMetricPO appMetricPO = appMetricDao.selectById(compositeMetricDTO.getSingleAppMetricId());
                if (appMetricPO == null) {
                    throw new ServiceException("单指标不存在");
                }
                if (StringUtils.isBlank(compositeMetricDTO.getMetricName())) {
                    throw new ServiceException("单指标名称不能为空");
                }
            });
        } else {
            if (!PlaceHolderUtils
                    .checkContainsStringParams(alarmMetricPO.getExpression(), appMetricDTO.getAttrValue())) {
                throw new ServiceException("表达式中的占位符不匹配，请补全占位符所对应值");
            }
        }

    }

    private void saveCompositeMetricList(AppMetricDTO appMetricDTO, Long compositeMetricId,
            AlarmMetricPO alarmMetricPO) {
        if (!alarmMetricPO.getCollectType().equals(CollectTypeEnum.COMPOSITE_METRIC.getCode())) {
            //非复合指标无需保存关联关系
            return;
        }

        //更新复合指标需先删除历史关系记录
        if (appMetricDTO.getId() != null) {
            List<CompositeAppMetricPO> list = getCompositeMetricList(appMetricDTO.getId());

            //若本次提交的不包含已存在的，则删除...
            for (CompositeAppMetricPO compositeAppMetricPO : list) {
                boolean isExist = false;
                for (CompositeAppMetricDTO compositeAppMetricDTO : appMetricDTO.getCompositeAppMetricList()) {
                    if (compositeAppMetricDTO.getId().equals(compositeAppMetricPO.getId())) {
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    compositeAppMetricDao.deleteById(compositeAppMetricPO.getId());
                }
            }
        }

        for (CompositeAppMetricDTO compositeAppMetricDTO : appMetricDTO.getCompositeAppMetricList()) {
            CompositeAppMetricPO compositeAppMetricPO = CompositeAppMetricPO.builder()
                    .id(compositeAppMetricDTO.getId())
                    .compositeAppMetricId(compositeMetricId)
                    .singleAppMetricId(compositeAppMetricDTO.getSingleAppMetricId())
                    .metricName(compositeAppMetricDTO.getMetricName())
                    .build();
            if (compositeAppMetricDTO.getId() == null) {
                compositeAppMetricDao.insert(compositeAppMetricPO);
            } else {
                compositeAppMetricDao.updateById(compositeAppMetricPO);
            }
            compositeAppMetricDTO.setId(compositeAppMetricPO.getId());
        }
    }

    public List<CompositeAppMetricPO> getCompositeMetricList(Long compositeMetricId) {
        QueryWrapper<CompositeAppMetricPO> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(CompositeAppMetricPO::getCompositeAppMetricId, compositeMetricId);

        return compositeAppMetricDao.selectList(wrapper);
    }

    /**
     * 设置联系人 创建人姓名
     *
     * @param list
     */
    private void setItemEmployeeName(List<UserAppMetricBO> list) {
        Set<Long> uids = new HashSet<>();
        list.forEach(item -> {
            if (item.getCreater() > 0) {
                uids.add(item.getCreater());
            }
        });

        if (uids.size() > 0) {
            EmpInfoListResponse empInfoListResponse = employeeClient.getInfoList(new ArrayList<>(uids));
            if (null == empInfoListResponse.getData()) {
                list.forEach(item -> {
                    item.setCreaterUname("");
                });

                return;
            }

            Map<Long, EmpInfoData> employeInfoMap = empInfoListResponse.getData().stream()
                    .collect(Collectors.toMap(EmpInfoData::getUid, Function.identity(), (value1, value2) -> value2));

            list.forEach(item -> {
                if (employeInfoMap.containsKey(item.getCreater())) {
                    item.setCreaterUname(employeInfoMap.get(item.getCreater()).getName());
                } else {
                    item.setCreaterUname("");
                }
            });
        } else {
            list.forEach(item -> {
                item.setCreaterUname("");
            });
        }
    }

    /**
     * 更新应用收集id
     *
     * @param id
     * @param collectId
     * @return
     */
    @Override
    public Integer updateCollectId(Long id, Integer collectId) {
        AppMetricPO appMetricPO = appMetricDao.selectById(id);
        if (appMetricPO == null) {
            throw new ResourceNotFoundException(ResourceEnum.APP_METRIC, id);
        }

        appMetricPO.setCollectId(collectId);
        appMetricPO.setMtime(null);

        return appMetricDao.updateById(appMetricPO);

    }

    /**
     * 业务自定义指标同步grafana看板
     *
     * @param appCode
     * @return
     */
    @Override
    public Boolean createGrafanaDashboard(String appCode) {
        QueryWrapper<AppPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(AppPO::getAppCode, appCode);
        AppPO app = appDao.selectOne(queryWrapper);
        if (null == app) {
            throw new WebException("appCode 不存在");
        }

        if(StringUtils.isNotBlank(app.getMoneAppCode())){
            throw new WebException("从Mone迁移的应用暂不支持此功能");
        }

        QueryAppMetricPO queryAppMetric = QueryAppMetricPO
                .builder()
                .page(1L)
                .pageSize(1000L)
                .appCode(appCode)
                .metricType(MetricTypeEnum.BUSINESS_METRIC.getCode())
                .collectType(CollectTypeEnum.SINGLE_METRIC.getCode())
                .datasourceType(DataSourceTypeEnum.ELASTIC_SEARCH.getCode())
                .build();

        IPage<AppMetricUnionPO> dbResult = appMetricDao.selectAppMetricPageByParam(
                new Page<>(queryAppMetric.getPage(), queryAppMetric.getPageSize()), queryAppMetric);

        if (null == dbResult || dbResult.getSize() <= 0) {
            throw new WebException("应用还没有定义业务指标");
        }

        List<AppMetricUnionPO> appMetricUnionList = dbResult.getRecords()
                .stream()
                .filter(i -> i.getCollectId() > 0)
                .collect(Collectors.toList());

        if (appMetricUnionList.size() <= 0) {
            throw new WebException("业务指标未关联上报事件");
        }

        String grafanaUid = grafanaManager.createDashBoard(appCode, appMetricUnionList);
        if (null != grafanaUid) {
            app.setGrafanaUid(grafanaUid);
            app.setMtime(null);
            appDao.updateById(app);
        }

        return true;
    }

    /**
     * 删除业务自定义指标grafana看板
     *
     * @param appCode
     * @return
     */
    @Override
    public Boolean removeGrafanaDashboard(String appCode) {
        QueryWrapper<AppPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(AppPO::getAppCode, appCode);
        AppPO app = appDao.selectOne(queryWrapper);
        if (null == app) {
            throw new WebException("appCode 不存在");
        }

        return grafanaManager.removeDashboard(appCode);
    }

    /**
     * grafana看板url
     *
     * @param appCode
     * @return
     */
    @Override
    public Map<String, String> grafanaDashboardUrl(String appCode) {
        QueryWrapper<AppPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(AppPO::getAppCode, appCode);
        AppPO app = appDao.selectOne(queryWrapper);
        if (null == app) {
            throw new WebException("appCode 不存在");
        }

        Map<String, String> dashboard = new HashMap<>();
        dashboard.put("application", YING_YONG + "?var-application=" + appCode);
        dashboard.put("system", XI_TONG + "?var-application=" + appCode);
        dashboard.put("operation", RONG_QI + "?var-application=" + appCode);
        dashboard.put("business", "");
        if (StringUtils.isNotBlank(app.getGrafanaUid())) {
            dashboard.put("business", grafanaManager.getGrafanaUrlByUid(app.getGrafanaUid(), appCode));
        }

        return dashboard;
    }
}
