package com.mfw.themis.portal.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mfw.themis.common.model.bo.AppMetricBO;
import com.mfw.themis.common.model.bo.SuggestBO;
import com.mfw.themis.common.model.bo.user.UserAppMetricBO;
import com.mfw.themis.common.model.dto.AppMetricDTO;
import com.mfw.themis.portal.model.dto.QueryAppMetricDTO;
import com.mfw.themis.portal.model.dto.QueryAppMetricSuggestDTO;
import java.util.List;
import java.util.Map;

/**
 * @author wenhong
 */
public interface AppMetricService {

    /**
     * 根据id获取应用指标关系
     *
     * @param id
     * @return
     */
    AppMetricDTO queryById(Long id);

    /**
     * 用户接口 - 指标列表
     *
     * @param queryAppMetricDTO
     * @return
     */
    IPage<UserAppMetricBO> userQueryAppMetricByParam(QueryAppMetricDTO queryAppMetricDTO);

    /**
     * 指标Suggest接口
     *
     * @param queryAppMetricSuggestDTO
     * @return
     */
    List<SuggestBO> queryAppMetricSuggestByParam(QueryAppMetricSuggestDTO queryAppMetricSuggestDTO);

    /**
     * 指标详情
     *
     * @param appMetricId
     * @return
     */
    AppMetricBO appMetricDetail(Long appMetricId);

    /**
     * 指标模板详情
     *
     * @param appMetricId
     * @param sourceType
     * @return
     */
    AppMetricBO appMetricTplDetail(Long appMetricId, Integer sourceType);

    /**
     * 根据id获取应用指标关系
     *
     * @param queryAppMetricDTO
     * @return
     */
    IPage<AppMetricBO> queryAppMetricByParam(QueryAppMetricDTO queryAppMetricDTO);


    /**
     * 绑定应用指标关系
     *
     * @param appMetricDTO
     * @return
     */
    AppMetricDTO create(AppMetricDTO appMetricDTO);

    /**
     * 更新应用指标关系
     *
     * @param appMetricDTO
     * @return
     */
    Integer update(AppMetricDTO appMetricDTO);

    /**
     * 更新应用收集id
     *
     * @param id
     * @param collectId
     * @return
     */
    Integer updateCollectId(Long id, Integer collectId);

    /**
     * 删除应用指标关系
     *
     * @param id
     * @return
     */
    Integer delete(Long id);

    /**
     * 启用/禁用应用指标关系
     *
     * @param id
     * @param status
     * @return
     */
    Integer changeStatus(Long id, Integer status);

    /**
     * grafana看板url
     *
     * @param appCode
     * @return
     */
    Map<String, String> grafanaDashboardUrl(String appCode);

    /**
     * 创建业务自定义指标grafana看板
     *
     * @param appCode
     * @return
     */
    Boolean createGrafanaDashboard(String appCode);

    /**
     * 删除业务自定义指标grafana看板
     *
     * @param appCode
     * @return
     */
    Boolean removeGrafanaDashboard(String appCode);
}
