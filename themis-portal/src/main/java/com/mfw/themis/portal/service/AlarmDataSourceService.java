package com.mfw.themis.portal.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mfw.themis.common.model.dto.AlarmDataSourceDTO;
import com.mfw.themis.portal.model.dto.QueryAlarmDataSourceDTO;
import com.mfw.themis.portal.model.dto.QueryAlarmMetricDTO;


/**
 * @author guosp
 */
public interface AlarmDataSourceService {

    /**
     * 获取
     *
     * @param id
     * @return
     */
    AlarmDataSourceDTO queryById(Long id);

    /**
     * 应用列表查询
     * @param req
     * @return
     */
    IPage<AlarmDataSourceDTO> queryDataSourcePage(QueryAlarmDataSourceDTO req);

    /**
     * 创建指标
     *
     * @param alarmDataSourceDTO
     * @return
     */
    AlarmDataSourceDTO create(AlarmDataSourceDTO alarmDataSourceDTO);

    /**
     * 更新指标
     *
     * @param alarmDataSourceDTO
     * @return
     * */
    boolean update(AlarmDataSourceDTO alarmDataSourceDTO);

    /**
     * 删除指标
     *
     * @param id
     * @return
     */
    boolean delete(Long id);

}
