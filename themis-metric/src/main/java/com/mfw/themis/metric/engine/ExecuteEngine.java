package com.mfw.themis.metric.engine;

import com.mfw.themis.common.model.dto.AlarmDataSourceDTO;
import com.mfw.themis.metric.model.Metric;
import com.mfw.themis.metric.model.MetricExecuteResult;
import java.util.List;

/**
 * 指标执行引擎
 *
 * @author liuqi
 */
public interface ExecuteEngine {

    /**
     * 执行单一指标
     *
     * @param dataSource 数据源
     * @param metric     指标
     * @return 指标结果集（单节点or多节点）
     */
    MetricExecuteResult executeSingleMetric(AlarmDataSourceDTO dataSource, Metric metric);

    /**
     * 执行复合指标
     * @param dataSource        数据源
     * @param singleMetrics     单指标
     * @param compositeMetric   复合指标
     * @return
     */
    MetricExecuteResult executeCompositeMetric(AlarmDataSourceDTO dataSource, List<Metric> singleMetrics, Metric compositeMetric);

    /**
     * 连续多次单一执行
     *
     * @param dataSource 数据源
     * @param metric     指标
     * @param timeWindow 时间窗口
     * @param times      执行次数
     * @return 多次指标结果集
     */
    List<MetricExecuteResult> executeMultiSingleMetric(AlarmDataSourceDTO dataSource, Metric metric, Integer timeWindow,
            int times);

    /**
     * 连续多次执行复合指标
     *
     * @param dataSource        数据源
     * @param singleMetrics     单指标
     * @param compositeMetric   复合指标
     * @param times             执行次数
     * @return 多次指标结果集
     */
    List<MetricExecuteResult> executeMultiCompositeMetric(AlarmDataSourceDTO dataSource, List<Metric> singleMetrics,
                                                   Metric compositeMetric,  int times);

}
