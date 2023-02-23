package com.mfw.themis.portal.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mfw.themis.common.model.bo.AlarmMetricBO;
import com.mfw.themis.common.model.bo.SuggestBO;
import com.mfw.themis.common.model.dto.AlarmMetricDTO;
import com.mfw.themis.common.model.dto.AlarmMetricSuggestDTO;
import com.mfw.themis.common.model.dto.SaveAlarmMetricDTO;
import com.mfw.themis.portal.model.dto.QueryAlarmMetricDTO;
import com.mfw.themis.portal.model.dto.QueryAlarmMetricSuggestDTO;

import java.util.List;


/**
 * @author guosp
 */
public interface AlarmMetricService {

    /**
     * 获取指标
     *
     * @param id
     * @return
     */
    AlarmMetricBO queryById(Long id);

    /**
     * 指标列表查询
     * @param req
     * @return
     */
    IPage<AlarmMetricDTO> queryMetricPage(QueryAlarmMetricDTO req);

    /**
     * 指标模板 suggest
     * @param queryAlarmMetricSuggestDTO
     * @return
     */
    List<SuggestBO> queryMetricSuggest(QueryAlarmMetricSuggestDTO queryAlarmMetricSuggestDTO);

    /**
     * 创建指标
     *
     * @param alarmMetricDTO
     * @return
     */
    SaveAlarmMetricDTO create(SaveAlarmMetricDTO alarmMetricDTO);

    /**
     * 更新指标
     *
     * @param alarmMetricDTO
     * @return
     * */
    boolean update(SaveAlarmMetricDTO alarmMetricDTO);

    /**
     * 删除指标
     *
     * @param id
     * @return
     */
    boolean delete(Long id);

    /**
     * 删除指标模板
     *
     * @param id
     * @param operatorUid
     * @return
     */
    boolean deleteTpl(Long id, Long operatorUid);

}
