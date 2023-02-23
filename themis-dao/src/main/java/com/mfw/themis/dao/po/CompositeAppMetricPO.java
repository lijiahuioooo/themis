package com.mfw.themis.dao.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 复合指标关系
 * @author wenhong
 */
@TableName("t_composite_app_metric")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompositeAppMetricPO {
    private Long id;
    /**
     * 指标名
     */
    private String metricName;
    /**
     * 复合指标app_metric_id
     */
    private Long compositeAppMetricId;
    /**
     * 单指标app_metric_id
     */
    private Long singleAppMetricId;
    /**
     * 创建时间
     */
    private Date ctime;
    /**
     * 更新时间
     */
    private Date mtime;
}
