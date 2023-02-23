package com.mfw.themis.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.mfw.themis.common.constant.SdkCollectFields;
import com.mfw.themis.common.constant.enums.DefaultMetricEnum;
import com.mfw.themis.common.constant.enums.EnableEnum;
import com.mfw.themis.common.model.dto.CollectMetricFieldDTO;
import com.mfw.themis.common.model.dto.CollectMetricFieldDTO.Field;
import com.mfw.themis.dao.mapper.AppDao;
import com.mfw.themis.dao.mapper.CollectMetricFieldDao;
import com.mfw.themis.dao.po.AppPO;
import com.mfw.themis.dao.po.CollectMetricFieldPO;
import com.mfw.themis.portal.service.CollectMetricFieldService;
import com.mfw.themis.portal.service.DefaultMetricService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wangxudong
 * @description
 * @date 2021-12-28 14
 */
@Slf4j
@Service
public class DefaultMetricServiceImpl implements DefaultMetricService {
    @Autowired
    private CollectMetricFieldService collectMetricFieldService;

    @Autowired
    private AppDao appDao;

    @Autowired
    private CollectMetricFieldDao collectMetricFieldDao;

    @Override
    public int creatDefaultMetric(String appCode, DefaultMetricEnum defaultMetricEnum) {
        CollectMetricFieldDTO collectMetricFieldDTO = new CollectMetricFieldDTO();
        collectMetricFieldDTO.setAppCode(appCode);
        collectMetricFieldDTO.setMetric(defaultMetricEnum.getValue());
        collectMetricFieldDTO.setStatus(EnableEnum.ENABLE);
        collectMetricFieldDTO.setIsDelete(false);
        if(defaultMetricEnum == DefaultMetricEnum.DEFAULT_HTTP_METRIC){
            Map<String, List<Field>> map = defaultHTTPMetricFieldBuilder();
            collectMetricFieldDTO.setTags(map.get("tags"));
            collectMetricFieldDTO.setFields(map.get("fields"));
        }
        if(defaultMetricEnum == DefaultMetricEnum.DEFAULT_DUBBO_METRIC){
            Map<String,List<Field>> map = defaultDubboMetricFieldBuilder();
            collectMetricFieldDTO.setTags(map.get("tags"));
            collectMetricFieldDTO.setFields(map.get("fields"));
        }

        return collectMetricFieldService.create(collectMetricFieldDTO);
    }

    @Override
    public void refreshDefaultMetric() {
        QueryWrapper<AppPO> wrapper = new QueryWrapper<>();
        QueryWrapper<CollectMetricFieldPO> metricFieldPOQueryWrapper = new QueryWrapper<>();
        List<AppPO> list = appDao.selectList(wrapper.eq("is_delete", false));
        for(AppPO appPO:list){
            List<CollectMetricFieldPO> collectMetricFieldPOList = collectMetricFieldDao.selectList(metricFieldPOQueryWrapper.eq("app_code", appPO.getAppCode()));
            List<String> metricList = collectMetricFieldPOList.stream().map(CollectMetricFieldPO::getMetric).collect(
                    Collectors.toList());
            if (!metricList.contains(DefaultMetricEnum.DEFAULT_HTTP_METRIC.getValue())){
                creatDefaultMetric(appPO.getAppCode(),DefaultMetricEnum.DEFAULT_HTTP_METRIC);
            }
            if(!metricList.contains(DefaultMetricEnum.DEFAULT_DUBBO_METRIC.getValue())){
                creatDefaultMetric(appPO.getAppCode(),DefaultMetricEnum.DEFAULT_DUBBO_METRIC);
            }
        }

    }

    public Map<String,List<Field>> defaultHTTPMetricFieldBuilder(){
        Map<String,List<Field>> map = new HashMap<>();
        List<Field> tagList = new ArrayList<>();
        List<Field> fieldsList = new ArrayList<>();

        Field endPoint = new Field(SdkCollectFields.ENDPOINT,"string","endpoint");
        Field rt = new Field(SdkCollectFields.RT,"long","执行时间");
        Field remoteAddr = new Field(SdkCollectFields.REMOTE_ADDR,"string","远程地址");
        Field localAddr = new Field(SdkCollectFields.LOCAL_ADDR,"string","本地地址");
        Field url = new Field(SdkCollectFields.HTTP_URL,"string","路径");
        Field method = new Field(SdkCollectFields.HTTP_METHOD,"string","方法");
        Field status = new Field(SdkCollectFields.HTTP_STATUS,"long","状态");
        Field threadName = new Field(SdkCollectFields.HTTP_THREAD,"string","线程");
        Field responseSize = new Field(SdkCollectFields.HTTP_RESPONSE_SIZE,"long","返回结果大小");

        tagList.add(endPoint);
        tagList.add(remoteAddr);
        tagList.add(localAddr);
        tagList.add(url);
        tagList.add(method);
        tagList.add(status);
        tagList.add(threadName);

        fieldsList.add(rt);
        fieldsList.add(responseSize);

        map.put("tags", tagList);
        map.put("fields", fieldsList);
        return map;
    }

    public Map<String,List<Field>> defaultDubboMetricFieldBuilder(){
        Map<String,List<Field>> map = new HashMap<>();
        List<Field> tagList = new ArrayList<>();
        List<Field> fieldsList = new ArrayList<>();

        Field app = new Field(SdkCollectFields.DUBBO_APPLICATION,"string","应用名");
        Field service = new Field(SdkCollectFields.DUBBO_SERVICE,"string","服务名");
        Field className = new Field(SdkCollectFields.DUBBO_CLASS,"string","类全路径");
        Field dubboMethod = new Field(SdkCollectFields.DUBBO_METHOD,"string","方法");
        Field dubboVersion = new Field(SdkCollectFields.DUBBO_VERSION,"string","版本");
        Field dubboGroup = new Field(SdkCollectFields.DUBBO_GROUP,"string","群组");
        Field dubboResponseTime = new Field(SdkCollectFields.DUBBO_RESPONSE_TIME,"long","响应时间");
        Field arguments = new Field(SdkCollectFields.DUBBO_ARGUMENT,"string","参数配置");
        Field dubboCount = new Field(SdkCollectFields.DUBBO_COUNT,"long","计数");
        Field dubboStatus = new Field(SdkCollectFields.DUBBO_STATUS,"long","请求状态");
        Field dubboCode = new Field(SdkCollectFields.DUBBO_CODE,"string","code");
        Field dubboMsg = new Field(SdkCollectFields.DUBBO_MSG,"string","返回信息");

        tagList.add(app);
        tagList.add(service);
        tagList.add(className);
        tagList.add(dubboMethod);
        tagList.add(dubboVersion);
        tagList.add(dubboGroup);
        tagList.add(arguments);
        tagList.add(dubboStatus);
        tagList.add(dubboCode);
        tagList.add(dubboMsg);

        fieldsList.add(dubboResponseTime);
        fieldsList.add(dubboCount);

        map.put("tags", tagList);
        map.put("fields", fieldsList);
        return map;
    }
    
    
}
