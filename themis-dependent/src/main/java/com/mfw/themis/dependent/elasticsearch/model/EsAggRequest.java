package com.mfw.themis.dependent.elasticsearch.model;

import com.mfw.themis.dependent.elasticsearch.constant.enums.AggTypeEnum;
import com.mfw.themis.dependent.elasticsearch.constant.enums.DateFieldTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 聚合查询参数
 * @author wenhong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EsAggRequest {

    /**
     * 索引名
     */
    private String indexName;

    /**
     * 索引type
     */
    private String typeName;

    /**
     * ES 日期查询字段
     */
    private String dateField;

    /**
     * ES 日期查询字段类型
     */
    private DateFieldTypeEnum dateFieldType;

    /**
     * 聚合类型 count min max sum avg
     */
    private AggTypeEnum aggType;

    /**
     * 聚合字段
     */
    private String groupField;

    /**
     * 筛选条件
     */
    private List<EsAggMetric> filterMetrics;

    private Date startTime;

    private Date endTime;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EsAggMetric {
        private String metric;

        private String filterMetricOperator;

        private String metricValue;

        private Boolean fullCondition;
    }

}
