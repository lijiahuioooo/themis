package com.mfw.themis.collector.manager;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Sets;
import com.mfw.themis.common.constant.enums.EnableEnum;
import com.mfw.themis.common.convert.CollectMetricFieldConvert;
import com.mfw.themis.common.model.dto.CollectMetricFieldDTO;
import com.mfw.themis.common.model.dto.CollectMetricFieldDTO.Field;
import com.mfw.themis.dao.mapper.AppDao;
import com.mfw.themis.dao.mapper.CollectMetricFieldDao;
import com.mfw.themis.dao.po.AppPO;
import com.mfw.themis.dao.po.CollectMetricFieldPO;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 初始化规则数据
 *
 * @author wenhong
 */
@EnableScheduling
@Component
@Slf4j
public class CollectorMetricManager {

    private Map<String, CollectMetricFieldDTO> collectMetricFieldDtoMap = new ConcurrentHashMap<>();

    @Autowired
    private CollectMetricFieldDao collectMetricFieldDao;

    @Autowired
    private AppDao appDao;

    private static final String DUBBO_METRIC_KEY = "_dubbo_event";

    /**
     * 根据appCode metric 获取缓存的字段信息
     *
     * @param appCode
     * @param metric
     * @return
     */
    public CollectMetricFieldDTO getItem(String appCode, String metric) {
        String cacheKey = generateCollectMetricFieldKey(appCode, metric);
        if (collectMetricFieldDtoMap.containsKey(cacheKey)) {
            return collectMetricFieldDtoMap.get(cacheKey);
        }
        log.warn("未找到缓存配置，cacheKey:{}", cacheKey);

        // 判断是否是dubbo服务，自动生成上报事件，15s生效
        this.autoInitDubboMetricField(appCode, metric);

        return null;
    }

    /**
     * 启动加载上报事件配置 每15s重新加载上报事件配置
     */
    @Scheduled(fixedDelay = 15000)
    @PostConstruct
    public void initMetricData() {
        QueryWrapper<CollectMetricFieldPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CollectMetricFieldPO::getIsDelete, 0);
        queryWrapper.lambda().eq(CollectMetricFieldPO::getStatus, EnableEnum.ENABLE.getCode());

        List<CollectMetricFieldPO> collectMetricFieldList = collectMetricFieldDao.selectList(queryWrapper);

        if (null == collectMetricFieldList) {
            return;
        }

        Set<String> oldKeySet = Sets.newHashSet(collectMetricFieldDtoMap.keySet());

        collectMetricFieldList.forEach(collectMetricField -> {
            CollectMetricFieldDTO dto = CollectMetricFieldConvert.toDTO(collectMetricField);
            if (null != dto) {
                String key = generateCollectMetricFieldKey(dto.getAppCode(), dto.getMetric());
                collectMetricFieldDtoMap.put(key, dto);
                oldKeySet.remove(key);
            } else {
                log.warn("collectMetricField convert null, {}", collectMetricField.toString());
            }
        });

        oldKeySet.forEach(key -> collectMetricFieldDtoMap.remove(key));

    }

    public Map<String, CollectMetricFieldDTO> getCurrentConfig() {
        return collectMetricFieldDtoMap;
    }

    private String generateCollectMetricFieldKey(String appCode, String metricName) {
        return appCode + metricName;
    }

    /**
     * 针对dubbo服务自动生成上报事件
     * @param appCode
     * @param metric
     */
    public void autoInitDubboMetricField(String appCode, String metric){
        if(!metric.contains(DUBBO_METRIC_KEY)){
            return;
        }

        // 判断应用是否存在
        QueryWrapper<AppPO> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(AppPO::getAppCode, appCode);
        AppPO existApp = appDao.selectOne(wrapper);
        if (existApp == null) {
            return;
        }

        String tagsJson = "["
                + "{\"desc\":\"应用名称\",\"metric\":\"dubbo_application_name\",\"type\":\"string\"},"
                + "{\"desc\":\"服务名称\",\"metric\":\"dubbo_service_name\",\"type\":\"string\"},"
                + "{\"desc\":\"类名\",\"metric\":\"dubbo_class_name\",\"type\":\"string\"},"
                + "{\"desc\":\"方法\",\"metric\":\"dubbo_method_name\",\"type\":\"string\"},"
                + "{\"desc\":\"版本\",\"metric\":\"dubbo_version\",\"type\":\"string\"},"
                + "{\"desc\":\"请求组\",\"metric\":\"dubbo_group\",\"type\":\"string\"},"
                + "{\"desc\":\"请求状态\",\"metric\":\"dubbo_status\",\"type\":\"long\"},"
                + "{\"desc\":\"响应码\",\"metric\":\"dubbo_code\",\"type\":\"string\"},"
                + "{\"desc\":\"响应描述\",\"metric\":\"dubbo_msg\",\"type\":\"string\"},"
                + "{\"desc\":\"请求参数\",\"metric\":\"dubbo_argument\",\"type\":\"string\"}"
                + "]";

        String fieldJson = "["
                + "{\"desc\":\"请求次数\",\"metric\":\"count\",\"type\":\"long\"},"
                + "{\"desc\":\"响应时间\",\"metric\":\"dubbo_rt\",\"type\":\"long\"}"
                + "]";

        CollectMetricFieldDTO dto = new CollectMetricFieldDTO();
        dto.setDescription("dubbo接口事件");
        dto.setAppCode(appCode);
        dto.setMetric(metric);

        List<Field> tags = JSONArray.parseArray(tagsJson, Field.class);
        dto.setTags(tags);

        List<Field> fields = JSONArray.parseArray(fieldJson, Field.class);
        dto.setFields(fields);
        dto.setIsDelete(false);

        QueryWrapper<CollectMetricFieldPO> queryWrapper = new QueryWrapper<>();

        // 检查metric唯一性
        queryWrapper.lambda().eq(CollectMetricFieldPO::getAppCode, appCode);
        queryWrapper.lambda().eq(CollectMetricFieldPO::getMetric, metric);

        List<CollectMetricFieldPO> collectList = collectMetricFieldDao.selectList(queryWrapper);
        if(null == collectList || collectList.size() <= 0){
            CollectMetricFieldPO po = CollectMetricFieldConvert.toPO(dto);
            po.setStatus(EnableEnum.ENABLE.getCode());
            // 重复插入报错忽略
            try{
                collectMetricFieldDao.insert(po);
            }catch (Exception e){}
        }
    }
}
