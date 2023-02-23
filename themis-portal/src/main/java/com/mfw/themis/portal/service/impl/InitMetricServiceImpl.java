package com.mfw.themis.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mfw.themis.common.constant.GlobalStatusConstants;
import com.mfw.themis.common.constant.MetricParamConstant;
import com.mfw.themis.common.constant.MetricTagConstant;
import com.mfw.themis.common.constant.enums.AppSourceEnum;
import com.mfw.themis.common.constant.enums.EnableEnum;
import com.mfw.themis.common.constant.enums.MetricTypeEnum;
import com.mfw.themis.common.constant.enums.ProjectTypeEnum;
import com.mfw.themis.common.constant.enums.ProjectTypeMetricTagEnum;
import com.mfw.themis.common.constant.enums.ResourceEnum;
import com.mfw.themis.common.convert.AlarmMetricConvert;
import com.mfw.themis.common.exception.ResourceNotFoundException;
import com.mfw.themis.common.exception.ServiceException;
import com.mfw.themis.common.model.dto.AlarmMetricDTO;
import com.mfw.themis.common.model.dto.AppMetricDTO;
import com.mfw.themis.common.util.PlaceHolderUtils;
import com.mfw.themis.dao.mapper.AlarmMetricDao;
import com.mfw.themis.dao.mapper.AppDao;
import com.mfw.themis.dao.mapper.AppMetricDao;
import com.mfw.themis.dao.po.AlarmMetricPO;
import com.mfw.themis.dao.po.AppMetricPO;
import com.mfw.themis.dao.po.AppPO;
import com.mfw.themis.portal.service.AppMetricService;
import com.mfw.themis.portal.service.InitMetricService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * @author guosp
 */
@Slf4j
@Service
public class InitMetricServiceImpl implements InitMetricService {

    @Autowired
    private AppDao appDao;

    @Autowired
    private AlarmMetricDao alarmMetricDao;

    @Autowired
    private AppMetricDao appMetricDao;

    @Autowired
    private AppMetricService appMetricService;

    /**
     * jvm相关指标datasource_id
     */
    @Value("${system.metric.jvm.datasource.id}")
    private Long systemMetricJvmDatasourceId;

    /**
     * aos容器运维指标datasource_id
     */
    @Value("${ops.metric.datasource.id}")
    private Long opsMetricDataSourceId;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean initAllAppMetric(Long appId) {
        AppPO appPO = appDao.selectById(appId);
        if(appPO == null) {
            throw new ResourceNotFoundException(ResourceEnum.APP,appId);
        }

        ProjectTypeEnum projectTypeEnum = ProjectTypeEnum.getByCode(appPO.getProjectType());
        if(projectTypeEnum == null) {
            throw new ServiceException("应用projectType错误");
        }

        AppSourceEnum appSourceEnum = AppSourceEnum.getByCode(appPO.getSource());
        if(appSourceEnum == null) {
            throw new ServiceException("应用source错误");
        }

        switch (projectTypeEnum) {
            case JAVA:
                //系统指标
                initAppMetric(appId,MetricTypeEnum.SYSTEM_METRIC.getCode());

                //应用指标
                initAppMetric(appId,MetricTypeEnum.APPLICATION_METRIC.getCode());

                //运维指标
                initAppMetric(appId,MetricTypeEnum.OPS_METRIC.getCode());

                break;
            default:
                //运维指标
                initAppMetric(appId,MetricTypeEnum.OPS_METRIC.getCode());
                break;
        }

        return true;
    }

    private boolean initAppMetric(Long appId,Integer metricType) {
        AppPO appPO = appDao.selectById(appId);
        if(appPO == null) {
            throw new ResourceNotFoundException(ResourceEnum.APP,appId);
        }

        Long dataSourceId = getDatasourceId(appPO.getProjectType(),metricType);

        List<AlarmMetricPO> alarmMetricList = queryInitMetricList(appPO,metricType);

        for (AlarmMetricPO alarmMetricPO: alarmMetricList) {
            QueryWrapper<AppMetricPO> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(AppMetricPO::getMetricId,alarmMetricPO.getId());
            wrapper.lambda().eq(AppMetricPO::getAppId,appId);
            wrapper.lambda().eq(AppMetricPO::getIsDelete,GlobalStatusConstants.IS_DELETE_DISABLE);

            List<AppMetricPO> appMetricList = appMetricDao.selectList(wrapper);
            if(appMetricList.isEmpty()) {
                AppMetricDTO appMetricDTO = buildAppMetricDTO(appPO,dataSourceId,alarmMetricPO);
                appMetricService.create(appMetricDTO);
                appMetricService.changeStatus(appMetricDTO.getId(), EnableEnum.DISABLE.getCode());
            }
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean syncMetricToApp(Long metricId) {
        AlarmMetricPO alarmMetricPO = alarmMetricDao.selectById(metricId);
        if(alarmMetricPO == null) {
            throw new ResourceNotFoundException(ResourceEnum.ALARM_METRIC,metricId);
        }

        AlarmMetricDTO alarmMetricDTO = AlarmMetricConvert.toDTO(alarmMetricPO);
        if(!alarmMetricDTO.getMetricTag().contains(MetricTagConstant.APP_METRIC_INIT_TAG)) {
            return false;
        }

        List<Integer> projectTypeFilterList = getProjectFilterList(alarmMetricDTO);
        List<Integer> sourceFilterList = getSourceFilterList(alarmMetricDTO);


        List<AppPO> appList = queryAppList(projectTypeFilterList,sourceFilterList);
        for (AppPO appPO: appList) {
            if(!checkAppMetricExists(appPO.getId(),metricId)) {
                Long dataSourceId = getDatasourceId(appPO.getProjectType(),alarmMetricPO.getMetricType());
                if(dataSourceId == null) {
                    throw new ServiceException("datasource error");
                }
                AppMetricDTO appMetricDTO = buildAppMetricDTO(appPO,dataSourceId,alarmMetricPO);
                appMetricService.create(appMetricDTO);
                appMetricService.changeStatus(appMetricDTO.getId(), EnableEnum.ENABLE.getCode());
            }
        }

        return true;
    }

    private List<Integer> getSourceFilterList(AlarmMetricDTO alarmMetricDTO) {
        List<Integer> sourceList = new ArrayList<>();
        if(alarmMetricDTO.getMetricTag().contains(MetricTagConstant.APP_METRIC_AOS_TAG)) {
            sourceList.add(AppSourceEnum.AOS.getCode());
        }

        return sourceList;
    }


    private List<Integer> getProjectFilterList(AlarmMetricDTO alarmMetricDTO) {
        List<Integer> projectTypeList = new ArrayList<>();
        switch (alarmMetricDTO.getMetricType()) {
            case SYSTEM_METRIC:
            case APPLICATION_METRIC:
                if(!alarmMetricDTO.getMetricTag().contains(MetricTagConstant.PROJECT_TYPE_SYSTEM_JAVA_TAG)) {
                    projectTypeList.add(ProjectTypeEnum.JAVA.getCode());
                }
                if(!alarmMetricDTO.getMetricTag().contains(MetricTagConstant.PROJECT_TYPE_SYSTEM_PHP_TAG)) {
                    projectTypeList.add(ProjectTypeEnum.PHP.getCode());
                }
                if(!alarmMetricDTO.getMetricTag().contains(MetricTagConstant.PROJECT_TYPE_SYSTEM_GOLANG_TAG)) {
                    projectTypeList.add(ProjectTypeEnum.GOLANG.getCode());
                }
                break;
            case OPS_METRIC:
                projectTypeList.add(ProjectTypeEnum.JAVA.getCode());
                projectTypeList.add(ProjectTypeEnum.PHP.getCode());
                projectTypeList.add(ProjectTypeEnum.GOLANG.getCode());
                break;
            case BUSINESS_METRIC:
                break;
            default:
                throw new ServiceException("指标类型错误");
        }

        return projectTypeList;
    }

    private boolean checkAppMetricExists(Long appId,Long metricId) {
        QueryWrapper<AppMetricPO> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(AppMetricPO::getMetricId,metricId);
        wrapper.lambda().eq(AppMetricPO::getAppId,appId);
        wrapper.lambda().eq(AppMetricPO::getIsDelete,GlobalStatusConstants.IS_DELETE_DISABLE);

        List<AppMetricPO> appMetricList = appMetricDao.selectList(wrapper);

        return appMetricList.size() > 0;
    }


    /**
     * 获取数据源ID
     * @param projectType
     * @param metricType
     * @return
     */
    private Long getDatasourceId(Integer projectType,Integer metricType) {
        switch (MetricTypeEnum.getByCode(metricType)) {
            case SYSTEM_METRIC:
                if(projectType == ProjectTypeEnum.JAVA.getCode()) {
                    return systemMetricJvmDatasourceId;
                }
            case OPS_METRIC:
                return opsMetricDataSourceId;
            case APPLICATION_METRIC:
                if(projectType == ProjectTypeEnum.JAVA.getCode()) {
                    return systemMetricJvmDatasourceId;
                }
            case BUSINESS_METRIC:
            default:
                break;
        }

        return null;
    }

    /**
     * 获取需要初始化的
     * @param projectTypeList
     * @param sourceList
     * @return
     */
    private List<AppPO> queryAppList(List<Integer> projectTypeList,List<Integer> sourceList) {
        List<AppPO> list = new ArrayList<>();
        if(CollectionUtils.isEmpty(projectTypeList)) {
            return list;
        }

        if(CollectionUtils.isEmpty(sourceList)) {
            return list;
        }

        LambdaQueryWrapper<AppPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppPO::getIsDelete, GlobalStatusConstants.IS_DELETE_DISABLE);
        wrapper.orderByAsc(AppPO::getId);

        wrapper.in(AppPO::getProjectType, projectTypeList);
        wrapper.in(AppPO::getSource, sourceList);

        return appDao.selectList(wrapper);
    }

    private AppMetricDTO buildAppMetricDTO(AppPO appPO,Long datasourceId,AlarmMetricPO alarmMetricPO) {
        AppMetricDTO appMetricDTO = new AppMetricDTO();
        appMetricDTO.setAppId(appPO.getId());
        appMetricDTO.setDatasourceId(datasourceId);
        appMetricDTO.setMetricId(alarmMetricPO.getId());

        List<String> placeHolderKeyList = PlaceHolderUtils.getPlaceHolderKeyList(alarmMetricPO.getExpression());

        if(placeHolderKeyList.contains(MetricParamConstant.METRIC_APP_CODE)) {
            Map<String,String> attrValue = new HashMap<>();
            attrValue.put(MetricParamConstant.METRIC_APP_CODE, appPO.getAppCode());
            appMetricDTO.setAttrValue(attrValue);
        }

        return appMetricDTO;
    }


    private List<AlarmMetricPO> queryInitMetricList(AppPO appPO,Integer metricType) {
        List<AlarmMetricPO> list = new ArrayList<>();
        List<String> tagList = new ArrayList<>();
        tagList.add(MetricTagConstant.APP_METRIC_INIT_TAG);

        //系统指标 需要区分应用类型
        if(metricType == MetricTypeEnum.SYSTEM_METRIC.getCode()) {
            String projectTag = ProjectTypeMetricTagEnum.getByCode(appPO.getProjectType()).getDesc();
            tagList.add(projectTag);
        }

        if(appPO.getSource() == AppSourceEnum.AOS.getCode()) {
            tagList.add(MetricTagConstant.APP_METRIC_AOS_TAG);
        } else {
            //TODO 后续增加其他类型数据源需修改
            return list;
        }
        LambdaQueryWrapper<AlarmMetricPO> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(AlarmMetricPO::getMetricType, metricType);
        wrapper.eq(AlarmMetricPO::getIsDelete, GlobalStatusConstants.IS_DELETE_DISABLE);
        tagList.forEach(
                tag->{
                    wrapper.like(AlarmMetricPO::getMetricTag,tag);
                }
        );

        wrapper.orderByAsc(AlarmMetricPO::getId);

        list = alarmMetricDao.selectList(wrapper);

        return list;
    }

}
