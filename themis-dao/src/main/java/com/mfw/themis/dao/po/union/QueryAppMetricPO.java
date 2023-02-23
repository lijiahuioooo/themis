package com.mfw.themis.dao.po.union;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author guosp
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryAppMetricPO {

    private Long appMetricId;

    private List<Long> appMetricIds;
    /**
     * 应用
     */
    private String appCode;

    private Long appId;

    private Long metricId;

    private Integer metricType;

    private Integer collectType;

    private String name;

    private Long datasourceId;

    private Integer datasourceType;

    /**
     * 是否删除
     */
    private Boolean isDelete;

    /**
     * 当前页
     */
    private Long page;

    /**
     * 每页个数
     */
    private Long pageSize;
}
