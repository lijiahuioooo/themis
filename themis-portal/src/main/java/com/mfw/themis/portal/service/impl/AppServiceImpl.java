package com.mfw.themis.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mfw.themis.common.constant.SdkCollectFields;
import com.mfw.themis.common.constant.enums.DefaultMetricEnum;
import com.mfw.themis.common.constant.enums.EnableEnum;
import com.mfw.themis.common.constant.enums.ResourceEnum;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mfw.themis.common.convert.AppConvert;
import com.mfw.themis.common.exception.ResourceNotFoundException;
import com.mfw.themis.common.exception.ServiceException;
import com.mfw.themis.common.model.bo.admin.AppSuggestBO;
import com.mfw.themis.common.model.dto.AppDTO;
import com.mfw.themis.common.model.dto.CollectMetricFieldDTO;
import com.mfw.themis.common.model.dto.CollectMetricFieldDTO.Field;
import com.mfw.themis.dao.mapper.AppDao;
import com.mfw.themis.dao.mapper.CollectMetricFieldDao;
import com.mfw.themis.dao.po.AppPO;
import com.mfw.themis.dao.po.union.QueryAppPO;
import com.mfw.themis.dependent.aosapp.AosClient;
import com.mfw.themis.dependent.mfwemployee.EmployeeClient;
import com.mfw.themis.dependent.mfwemployee.model.EmpInfoData;
import com.mfw.themis.dependent.mfwemployee.model.EmpInfoListResponse;
import com.mfw.themis.portal.event.AppCreateEvent;
import com.mfw.themis.portal.model.dto.QueryAppDTO;
import com.mfw.themis.portal.service.AppService;

import com.mfw.themis.portal.service.CollectMetricFieldService;
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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author wenhong
 */
@Slf4j
@Service
public class AppServiceImpl implements AppService, ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Autowired
    private AppDao appDao;

    @Autowired
    private EmployeeClient employeeClient;

    @Autowired
    private AosClient aosClient;
    

    /**
     * 应用列表查询
     *
     * @param queryAppDTO
     * @return
     */
    @Override
    public IPage<AppDTO> queryAppPage(QueryAppDTO queryAppDTO) {

        QueryAppPO queryAppPO = new QueryAppPO();
        BeanUtils.copyProperties(queryAppDTO, queryAppPO);

        IPage<AppPO> dbResult = appDao.selectAppPage(
                new Page<>(queryAppDTO.getPage(), queryAppDTO.getPageSize()),
                queryAppPO
        );

        IPage<AppDTO> result = new Page<>(queryAppDTO.getPage(), queryAppDTO.getPageSize());
        BeanUtils.copyProperties(dbResult, result);

        List<AppDTO> list = AppConvert.toDTOList(dbResult.getRecords());

        // 设置联系人 创建人姓名
        setItemEmployeeName(list);

        result.setRecords(list);
        return result;
    }

    /**
     * 设置联系人 创建人姓名
     * @param list
     */
    private void setItemEmployeeName(List<AppDTO> list){
        Set<Long> uids = new HashSet<>();
        list.forEach(item -> {
            if(item.getCreater() > 0){
                uids.add(item.getCreater());
            }
            if(StringUtils.isNotEmpty(item.getContacts())){
                uids.addAll(Arrays.stream(StringUtils.split(item.getContacts(), ",")).map(uid -> Long.valueOf(uid)).collect(Collectors.toList()));
            }
        });

        if(uids.size() > 0){
            System.out.println(uids);
            EmpInfoListResponse empInfoListResponse = employeeClient.getInfoList(new ArrayList<>(uids));
            Map<Long, EmpInfoData> employeInfoMap = empInfoListResponse.getData().stream().collect(Collectors.toMap(
                    EmpInfoData::getUid, Function.identity(), (value1, value2) -> value2));

            System.out.println(employeInfoMap);
            list.forEach(item -> {
                if(employeInfoMap.containsKey(item.getCreater())){
                    item.setCreaterUname(employeInfoMap.get(item.getCreater()).getName());
                }

                List<Map<Long, String>> contactList = new ArrayList<>();
                if(StringUtils.isNotEmpty(item.getContacts())){
                    Arrays.asList(StringUtils.split(item.getContacts(), ",")).forEach(uid -> {
                        Map<Long, String> contactUser = new HashMap<>();
                        if(employeInfoMap.containsKey(Long.valueOf(uid))){
                            contactUser.put(Long.valueOf(uid), employeInfoMap.get(Long.valueOf(uid)).getName());
                        }else{
                            contactUser.put(Long.valueOf(uid), "");
                        }

                        contactList.add(contactUser);
                    });

                    item.setContactList(contactList);
                }
            });
        }
    }

    /**
     * 应用suggest
     * @param queryAppDTO
     * @return
     */
    @Override
    public IPage<AppSuggestBO> queryAppSuggestPage(QueryAppDTO queryAppDTO){
        QueryAppPO queryAppPO = new QueryAppPO();
        BeanUtils.copyProperties(queryAppDTO, queryAppPO);

        IPage<AppPO> dbResult = appDao.selectAppPage(
                new Page<>(queryAppDTO.getPage(), queryAppDTO.getPageSize()),
                queryAppPO
        );

        IPage<AppSuggestBO> result = new Page<>(queryAppDTO.getPage(), queryAppDTO.getPageSize());
        List<AppSuggestBO> list = AppConvert.toSuggestBOList(dbResult.getRecords());

        result.setRecords(list);
        return result;

    }

    @Override
    public AppDTO queryById(Long id) {
        AppPO appPO = appDao.selectById(id);
        if (appPO == null) {
            return null;
        }

        AppDTO appDTO = AppConvert.toDTO(appPO);

        setItemEmployeeName(Arrays.asList(appDTO));

        return appDTO;
    }

    /**
     * 根据appCode获取应用
     * @param appCode
     * @return
     */
    @Override
    public AppDTO queryByAppCode(String appCode){
        QueryWrapper<AppPO> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(AppPO::getAppCode, appCode);

        return AppConvert.toDTO(appDao.selectOne(wrapper));
    }

    @Transactional(rollbackFor = Exception.class)
    public AppDTO create(AppDTO appDTO) {
        // 判断应用是否存在
        QueryWrapper<AppPO> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(AppPO::getAppCode, appDTO.getAppCode());

        AppPO existAppPO = appDao.selectOne(wrapper);
        if (existAppPO != null) {
            throw new ServiceException("appCode已存在,appCode:" + appDTO.getAppCode());
        }

        // 获取部门信息
        if(null == appDTO.getDepartment()){
            appDTO.setDepartment(aosClient.getAppDepartment(appDTO.getAppCode()).getDepartmentName());
        }

        AppPO appPO = AppConvert.toPO(appDTO);
        appDao.insert(appPO);
        appDTO.setId(appPO.getId());
        AppCreateEvent appCreateEvent = new AppCreateEvent(appPO.getId());
        applicationContext.publishEvent(appCreateEvent);

        return appDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(AppDTO appDTO) {
        AppPO appPO = appDao.selectById(appDTO.getId());
        if (appPO == null) {
            throw new ResourceNotFoundException(ResourceEnum.APP, appDTO.getId());
        }

        AppPO appUpdatePO = AppConvert.toPO(appDTO);
        appUpdatePO.setCtime(null);
        appUpdatePO.setMtime(null);

        return appDao.updateById(appUpdatePO) > 0;
    }

    /**
     * 服务开通
     * @param appDTO
     * @return
     */
    @Override
    public Long open(AppDTO appDTO){

        // 判断是否已开通
        AppDTO app = queryByAppCode(appDTO.getAppCode());
        if(null == app){
            // 开通服务
            AppDTO appResDTO = create(appDTO);
            return appResDTO.getId();
        }

        // 已开通重置开通状态
        if(app.getIsDelete()){
            AppPO appPO = appDao.selectById(app.getId());
            appPO.setIsDelete(false);
            appPO.setOperator(appDTO.getOperator());
            appPO.setMtime(null);
            appDao.updateById(appPO);
        }

        return app.getId();
    }

    /**
     * 服务关闭
     * @param appDTO
     * @return
     */
    @Override
    public Long close(AppDTO appDTO){
        // 判断是否已开通
        AppDTO app = queryByAppCode(appDTO.getAppCode());
        if(null == app || app.getIsDelete()){
            return 0L;
        }

        // 逻辑删除
        AppPO appPO = appDao.selectById(app.getId());
        appPO.setIsDelete(true);
        if(null != appDTO.getOperator()){
            appPO.setOperator(appDTO.getOperator());
        }

        appPO.setMtime(null);

        appDao.updateById(appPO);

        return appPO.getId();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
    
}
