package com.mfw.themis.portal.manager;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mfw.themis.common.constant.GlobalStatusConstants;
import com.mfw.themis.common.constant.enums.AlarmMetricUnitEnum;
import com.mfw.themis.common.constant.enums.CollectTypeEnum;
import com.mfw.themis.common.constant.enums.CustomTimeWindowUnitEnum;
import com.mfw.themis.common.constant.enums.DataSourceTypeEnum;
import com.mfw.themis.common.constant.enums.EnableEnum;
import com.mfw.themis.common.constant.enums.GroupTypeEnum;
import com.mfw.themis.common.constant.enums.ResourceEnum;
import com.mfw.themis.common.constant.enums.TimeWindowEnum;
import com.mfw.themis.common.constant.enums.TimeWindowTypeEnum;
import com.mfw.themis.common.exception.ResourceNotFoundException;
import com.mfw.themis.common.exception.ServiceException;
import com.mfw.themis.common.model.bo.SuggestBO;
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
import com.mfw.themis.portal.model.dto.NewAppMetricDTO;
import com.mfw.themis.portal.model.dto.QueryAlarmMetricSuggestDTO;
import com.mfw.themis.portal.model.dto.QueryAppMetricSuggestDTO;
import com.mfw.themis.portal.service.AlarmMetricService;
import com.mfw.themis.portal.service.AppMetricService;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.Set;

/**
 * @author wenhong
 */
@Component
@Slf4j
public class AppMetricManager {

    @Autowired
    private AppDao appDao;

    @Autowired
    private AppMetricDao appMetricDao;

    @Autowired
    private AlarmMetricDao alarmMetricDao;

    @Autowired
    private AlarmDataSourceDao alarmDataSourceDao;

    @Autowired
    private CompositeAppMetricDao compositeAppMetricDao;

    @Autowired
    private AlarmMetricService alarmMetricService;

    @Autowired
    private AppMetricService appMetricService;

    /**
     * ????????????
     * @param newAppMetricDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Long save(NewAppMetricDTO newAppMetricDTO){
        Long appMetricId = 0L;

        // ??????????????????
        checkCommonFormField(newAppMetricDTO);

        // ??????????????????
        Metric metric = getConcreteInstance(newAppMetricDTO);
        metric.setNewAppMetricDTO(newAppMetricDTO);
        //????????????
        metric.validateForm();
        // ??????
        if(null != newAppMetricDTO.getAppMetricId() && newAppMetricDTO.getAppMetricId() > 0){
            appMetricId = metric.update();
        }else{
            appMetricId = metric.create();
        }

        return appMetricId;
    }

    /**
     * ??????????????????????????????????????????
     * @param newAppMetricDTO
     * @return
     */
    public Metric getConcreteInstance(NewAppMetricDTO newAppMetricDTO){
        switch (DataSourceTypeEnum.getByCode(newAppMetricDTO.getSourceType())){
            case PROMETHEUS:
                return new PrometheusMetric();
            case ELASTIC_SEARCH:
                // ???????????????
                if(null == newAppMetricDTO.getDatasourceId()){
                    QueryWrapper<AlarmDataSourcePO> queryWrapper = new QueryWrapper<>();
                    queryWrapper.lambda().eq(AlarmDataSourcePO::getType, newAppMetricDTO.getSourceType());
                    List<AlarmDataSourcePO> alarmDataSourcePOList = alarmDataSourceDao.selectList(queryWrapper);
                    newAppMetricDTO.setDatasourceId(alarmDataSourcePOList.get(0).getId());
                }
                return new EsMetric();
            default:
                throw new ServiceException("???????????????????????????PROMETHEUS,ELASTIC_SEARCH");
        }
    }

    /**
     * ??????????????????
     * @param newAppMetricDTO
     */
    private void checkCommonFormField(NewAppMetricDTO newAppMetricDTO){
        AppPO appPO = appDao.selectById(newAppMetricDTO.getAppId());
        if (null == appPO) {
            throw new ResourceNotFoundException(ResourceEnum.APP, newAppMetricDTO.getAppId());
        }

        if(null == newAppMetricDTO.getCreater()){
            throw new ServiceException("uid????????????");
        }
    }

    @Data
    class Metric {
        protected NewAppMetricDTO newAppMetricDTO;
        protected Map<String, String> attrValue = new HashMap<>();
        protected List<Map<String, Object>> metricList = new ArrayList<>();

        public void validateForm(){

        }

        public Long create(){
            AppMetricPO appMetricPO = new AppMetricPO();

            appMetricPO.setAppId(newAppMetricDTO.getAppId());
            appMetricPO.setMetricId(newAppMetricDTO.getMetricId());
            appMetricPO.setAttrValue(JSON.toJSONString(attrValue));
            appMetricPO.setDatasourceId(newAppMetricDTO.getDatasourceId());
            appMetricPO.setCreater(newAppMetricDTO.getCreater());
            appMetricPO.setStatus(EnableEnum.ENABLE.getCode());
            appMetricPO.setIsDelete(GlobalStatusConstants.IS_DELETE_DISABLE);
            if (null != newAppMetricDTO.getCollectId()){
                appMetricPO.setCollectId(newAppMetricDTO.getCollectId());
            }

            appMetricDao.insert(appMetricPO);
            return appMetricPO.getId();
        }

        public Long update(){
            AppMetricPO appMetricPO = new AppMetricPO();

            appMetricPO.setId(newAppMetricDTO.getAppMetricId());
            appMetricPO.setAppId(newAppMetricDTO.getAppId());
            appMetricPO.setMetricId(newAppMetricDTO.getMetricId());
            appMetricPO.setAttrValue(JSON.toJSONString(attrValue));
            appMetricPO.setDatasourceId(newAppMetricDTO.getDatasourceId());
            appMetricPO.setStatus(EnableEnum.ENABLE.getCode());
            appMetricPO.setIsDelete(GlobalStatusConstants.IS_DELETE_DISABLE);
            appMetricPO.setMtime(null);
            if (null != newAppMetricDTO.getCollectId()){
                appMetricPO.setCollectId(newAppMetricDTO.getCollectId());
            }

            appMetricDao.updateById(appMetricPO);
            return appMetricPO.getId();
        }
    }

    @Data
    class PrometheusMetric extends Metric {

        @Override
        public void validateForm(){
            if(null == newAppMetricDTO.getMetricId()){
                throw new ServiceException("metricId????????????");
            }

            if(newAppMetricDTO.getCollectType().equals(CollectTypeEnum.COMPOSITE_METRIC.getCode())){
                throw new ServiceException("PROMETHEUS?????????????????????????????????????????????");
            }

            AlarmMetricPO alarmMetricPO = alarmMetricDao.selectById(newAppMetricDTO.getMetricId());
            if(null == alarmMetricPO){
                throw new ResourceNotFoundException(ResourceEnum.APP, newAppMetricDTO.getAppId());
            }

            newAppMetricDTO.getExpressionList().forEach(expression -> {
                if(expression.containsKey("metric") && expression.containsKey("metricValue")){
                    attrValue.put(expression.get("metric").toString(), expression.get("metricValue").toString());
                }
            });

            if (!PlaceHolderUtils.checkContainsStringParams(alarmMetricPO.getExpression(), attrValue)) {
                throw new ServiceException("??????????????????????????????????????????????????????????????????");
            }
        }
    }

    @Data
    class EsMetric extends Metric {

        @Override
        public void validateForm(){

            if(StringUtils.isBlank(newAppMetricDTO.getName())){
                throw new ServiceException("????????????????????????");
            }

            if(newAppMetricDTO.getCollectType().equals(CollectTypeEnum.SINGLE_METRIC.getCode())){
                if(newAppMetricDTO.getTimeWindowType().equals(TimeWindowTypeEnum.DEFAULT.getCode())){
                    if(null == newAppMetricDTO.getTimeWindow() || null == TimeWindowEnum.getByCode(newAppMetricDTO.getTimeWindow())){
                        throw new ServiceException("????????????????????????");
                    }
                }else{
                    if(null == newAppMetricDTO.getCustomTimeWindow()){
                        throw new ServiceException("???????????????????????????????????????");
                    }
                }

                if(null == newAppMetricDTO.getGroupType()
                    || null == GroupTypeEnum.getByCode(newAppMetricDTO.getGroupType())){
                    throw new ServiceException("ELASTIC_SEARCH????????? groupType?????????");
                }

                if(StringUtils.isBlank(newAppMetricDTO.getGroupField()) &&
                        !newAppMetricDTO.getGroupType().equals(GroupTypeEnum.COUNT.getCode()) ) {
                    throw new ServiceException("ELASTIC_SEARCH????????? groupField????????????");
                }

                if(null == newAppMetricDTO.getExpressionList() || newAppMetricDTO.getExpressionList().size() <= 0){
                    throw new ServiceException("????????????????????????");
                }

                newAppMetricDTO.getExpressionList().forEach(expression -> {
                    if(expression.containsKey("metric") && expression.containsKey("metricValue")
                            && StringUtils.isNoneBlank(expression.get("metric").toString())
                            && StringUtils.isNoneBlank(expression.get("metricValue").toString())){
                        attrValue.put(expression.get("metric").toString(), expression.get("metricValue").toString());
                    }
                });

                if(attrValue.size() <= 0){
                    throw new ServiceException("????????????????????????");
                }
            }

            if(newAppMetricDTO.getCollectType().equals(CollectTypeEnum.COMPOSITE_METRIC.getCode())) {
                // ????????????????????????
                metricList = newAppMetricDTO.getMetricList();
                if(null == metricList || metricList.size() < 2){
                    throw new ServiceException("?????????????????????????????????");
                }

                List<Map<String, Object>> formatedMetricList = new ArrayList<>();
                metricList.forEach(singleMetric -> {
                    Map<String, Object> metricItem = new HashMap<>();

                    if(!singleMetric.containsKey("metricName")
                            || StringUtils.isBlank(singleMetric.get("metricName").toString())){
                        throw new ServiceException("????????????????????????????????????");
                    }
                    if(!singleMetric.containsKey("appMetricId")
                            || Long.valueOf(singleMetric.get("appMetricId").toString()) <= 0){
                        throw new ServiceException("???????????????appMetricId????????????");
                    }

                    metricItem.put("metricName", singleMetric.get("metricName"));
                    metricItem.put("appMetricId", singleMetric.get("appMetricId"));

                    formatedMetricList.add(metricItem);
                });
                metricList = formatedMetricList;

                if(StringUtils.isBlank(newAppMetricDTO.getFormula())) {
                    throw new ServiceException("????????????????????????formula????????????");
                }
            }

            final int maxOffsetDay = 30;
            if(null != newAppMetricDTO.getTimeWindowOffset()){
                newAppMetricDTO.getTimeWindowOffset().putIfAbsent("value", 0);
                int offset = Integer.valueOf(newAppMetricDTO.getTimeWindowOffset().get("value").toString());
                int unit = Integer.valueOf(newAppMetricDTO.getTimeWindowOffset().get("unit").toString());

                if(unit == CustomTimeWindowUnitEnum.DAY.getCode()){
                    if(offset > maxOffsetDay){
                        throw new ServiceException("???????????????????????????30???");
                    }
                }
            }

            if(newAppMetricDTO.getTimeWindowType().equals(TimeWindowTypeEnum.CUSTOME.getCode())){
                final String customTimeFormat = "HH:mm:ss";
                checkTimeRange(newAppMetricDTO.getCustomTimeWindow().get("from").toString(),
                        newAppMetricDTO.getCustomTimeWindow().get("to").toString(), customTimeFormat);
            }
        }

        public void checkTimeRange(String startTime, String endTime, String format){
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);

                Date startDate = sdf.parse(startTime);
                Date endDate = sdf.parse(endTime);

                if (startDate.after(endDate)) {
                    throw new ServiceException("????????????????????????????????????");
                }
            } catch (ServiceException e){
                throw new ServiceException(e.getMessage());
            } catch (Exception e) {
                throw new ServiceException("?????????????????????");
            }
        }

        @Override
        public Long create(){
            // ?????? alarmMetric
            AlarmMetricPO alarmMetricPO = new AlarmMetricPO();
            BeanUtils.copyProperties(newAppMetricDTO, alarmMetricPO);
            if(!newAppMetricDTO.getCollectType().equals(CollectTypeEnum.COMPOSITE_METRIC.getCode())) {
                alarmMetricPO.setExpression(JSON.toJSONString(newAppMetricDTO.getExpressionList()));
            }

            if(null != newAppMetricDTO.getTimeWindowOffset()){
                alarmMetricPO.setTimeWindowOffset(JSON.toJSONString(newAppMetricDTO.getTimeWindowOffset()));
            }

            if(newAppMetricDTO.getTimeWindowType().equals(TimeWindowTypeEnum.CUSTOME.getCode())){
                alarmMetricPO.setCustomTimeWindow(JSON.toJSONString(newAppMetricDTO.getCustomTimeWindow()));
            }

            alarmMetricDao.insert(alarmMetricPO);
            newAppMetricDTO.setMetricId(alarmMetricPO.getId());

            // ??????appMetric
            Long appMetricId = super.create();

            if(newAppMetricDTO.getCollectType().equals(CollectTypeEnum.COMPOSITE_METRIC.getCode())) {
                // ????????????????????????
                metricList.forEach(metricItem -> {
                    CompositeAppMetricPO compositeAppMetricPO = new CompositeAppMetricPO();
                    compositeAppMetricPO.setCompositeAppMetricId(appMetricId);
                    compositeAppMetricPO.setMetricName(metricItem.get("metricName").toString());
                    compositeAppMetricPO.setSingleAppMetricId(Long.valueOf(metricItem.get("appMetricId").toString()));

                    Integer id = compositeAppMetricDao.insert(compositeAppMetricPO);
                });
            }

            return appMetricId;
        }

        @Override
        public Long update(){
            AppMetricPO appMetricPO = appMetricDao.selectById(newAppMetricDTO.getAppMetricId());
            AlarmMetricPO alarmMetricPO = alarmMetricDao.selectById(appMetricPO.getMetricId());

            BeanUtils.copyProperties(newAppMetricDTO, alarmMetricPO);
            if(!newAppMetricDTO.getCollectType().equals(CollectTypeEnum.COMPOSITE_METRIC.getCode())) {
                alarmMetricPO.setExpression(JSON.toJSONString(newAppMetricDTO.getExpressionList()));
            }

            if(null != newAppMetricDTO.getTimeWindowOffset()){
                alarmMetricPO.setTimeWindowOffset(JSON.toJSONString(newAppMetricDTO.getTimeWindowOffset()));
            }

            if(newAppMetricDTO.getTimeWindowType().equals(TimeWindowTypeEnum.CUSTOME.getCode())){
                alarmMetricPO.setCustomTimeWindow(JSON.toJSONString(newAppMetricDTO.getCustomTimeWindow()));
            }

            alarmMetricDao.updateById(alarmMetricPO);

            if(alarmMetricPO.getCollectType().equals(CollectTypeEnum.COMPOSITE_METRIC.getCode())) {
                //??????????????????composite_app_metric????????????
                QueryWrapper<CompositeAppMetricPO> wrapper = new QueryWrapper<>();
                wrapper.lambda().eq(CompositeAppMetricPO::getCompositeAppMetricId, newAppMetricDTO.getAppMetricId());

                List<CompositeAppMetricPO> list = compositeAppMetricDao.selectList(wrapper);

                Set<Long> singleAppMetricIds = new HashSet<>();
                newAppMetricDTO.getMetricList().forEach(metricItem -> {
                    singleAppMetricIds.add(Long.valueOf(metricItem.get("appMetricId").toString()));
                });

                Set<Long> existSingleAppMetricIds = new HashSet<>();

                Map<Long, CompositeAppMetricPO> compositeAppMetricPOMap = new HashMap<>();

                //????????????????????????
                list.forEach(compositeAppMetricPO -> {
                    compositeAppMetricPOMap.put(compositeAppMetricPO.getSingleAppMetricId(), compositeAppMetricPO);
                    if(!singleAppMetricIds.contains(compositeAppMetricPO.getSingleAppMetricId())){
                        QueryWrapper<CompositeAppMetricPO> delWrapper = new QueryWrapper<>();
                        delWrapper.lambda().eq(CompositeAppMetricPO::getCompositeAppMetricId, newAppMetricDTO.getAppMetricId());
                        delWrapper.lambda().eq(CompositeAppMetricPO::getSingleAppMetricId, compositeAppMetricPO.getSingleAppMetricId());
                        compositeAppMetricDao.delete(delWrapper);
                    }else{
                        existSingleAppMetricIds.add(compositeAppMetricPO.getSingleAppMetricId());
                    }
                });

                newAppMetricDTO.getMetricList().forEach(metricItem -> {
                    Long singleAppMetricId = Long.valueOf(metricItem.get("appMetricId").toString());

                    CompositeAppMetricPO compositeAppMetricPO;

                    log.info("existSingleAppMetricIds:{}, singleAppMetricId: {}", existSingleAppMetricIds, singleAppMetricId);
                    if(existSingleAppMetricIds.contains(singleAppMetricId)){
                        // ??????
                        compositeAppMetricPO = compositeAppMetricPOMap.get(singleAppMetricId);
                        compositeAppMetricPO.setMetricName(metricItem.get("metricName").toString());
                        compositeAppMetricDao.updateById(compositeAppMetricPO);
                    }else{
                        compositeAppMetricPO = new CompositeAppMetricPO();
                        compositeAppMetricPO.setCompositeAppMetricId(newAppMetricDTO.getAppMetricId());
                        compositeAppMetricPO.setMetricName(metricItem.get("metricName").toString());
                        compositeAppMetricPO.setSingleAppMetricId(Long.valueOf(metricItem.get("appMetricId").toString()));
                        // ??????
                        compositeAppMetricDao.insert(compositeAppMetricPO);
                    }
                });
            }

            // ??????appMetric
            return super.update();
        }
    }

    /**
     * ??????????????? suggest
     * @param appCode
     * @param sourceType
     * @param metricType
     * @param keyword
     * @return
     */
    public List<SuggestBO> appMetricSuggest(
            String appCode,
            Integer sourceType,
            Integer metricType,
            String keyword) {

        List<SuggestBO> suggestBOList = new ArrayList<>();
        if(sourceType.equals(DataSourceTypeEnum.PROMETHEUS.getCode())){
            // prometheus ????????????
            QueryAlarmMetricSuggestDTO queryAlarmMetricSuggestDTO = new QueryAlarmMetricSuggestDTO();
            queryAlarmMetricSuggestDTO.setCollectType(CollectTypeEnum.SINGLE_METRIC.getCode());
            queryAlarmMetricSuggestDTO.setSourceType(DataSourceTypeEnum.PROMETHEUS.getCode());
            if(metricType > 0){
                queryAlarmMetricSuggestDTO.setMetricType(metricType);
            }
            if(StringUtils.isNotBlank(keyword)){
                queryAlarmMetricSuggestDTO.setKeyword(keyword);
            }

            suggestBOList = alarmMetricService.queryMetricSuggest(queryAlarmMetricSuggestDTO);
        }

        if(sourceType.equals(DataSourceTypeEnum.ELASTIC_SEARCH.getCode())){
            if(StringUtils.isBlank(appCode)){
                throw new ServiceException("appCode????????????");
            }

            // es ????????????
            QueryAppMetricSuggestDTO queryAppMetricSuggestDTO = new QueryAppMetricSuggestDTO();
            queryAppMetricSuggestDTO.setCollectType(CollectTypeEnum.SINGLE_METRIC.getCode());
            queryAppMetricSuggestDTO.setAppCode(appCode);
            queryAppMetricSuggestDTO.setKeyword(keyword);
            if(metricType > 0){
                queryAppMetricSuggestDTO.setMetricType(metricType);
            }

            suggestBOList = appMetricService.queryAppMetricSuggestByParam(queryAppMetricSuggestDTO);
        }

        return suggestBOList;
    }
}
