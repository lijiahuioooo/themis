package com.mfw.themis.portal.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mfw.themis.common.model.dto.CollectMetricFieldDTO;
import com.mfw.themis.portal.model.dto.QueryCollectMetricFieldParam;

/**
 * 上报事件服务接口
 * @author wenhong
 */
public interface CollectMetricFieldService {

    /**
     * 事件列表
     * @param queryCollectMetricFieldParam
     * @return
     */
    IPage<CollectMetricFieldDTO> collectMetricFieldList(QueryCollectMetricFieldParam queryCollectMetricFieldParam);

    /**
     * 创建上报事件字段
     * @param collectMetricFieldDTO
     * @return
     */
    Integer create(CollectMetricFieldDTO collectMetricFieldDTO);

    /**
     * 更新上报事件字段
     * @param collectMetricFieldDTO
     * @return
     */
    Integer update(CollectMetricFieldDTO collectMetricFieldDTO);

    /**
     * 删除上报事件字段
     * @param id
     * @param operator
     * @return
     */
    Integer delete(Integer id, Long operator);

    /**
     * 启用/禁用上报事件
     * @param id
     * @param status
     * @param operator
     * @return
     */
    Integer changeStatus(Integer id, Integer status, Long operator);

}
