package com.mfw.themis.dependent.elasticsearch.aggQuery;

import com.mfw.themis.dependent.elasticsearch.model.EsAggRequest;
import com.mfw.themis.dependent.elasticsearch.model.EsAggResponse;

/**
 * 聚合接口
 * @author wenhong
 */
public interface EsAggInterface {

    /**
     * 数量聚合
     * @param esAggRequest
     * @param needLog
     * @return
     */
    EsAggResponse aggCount(EsAggRequest esAggRequest, boolean needLog);

    /**
     * 最小值聚合
     * @param esAggRequest
     * @return
     */
    EsAggResponse aggMin(EsAggRequest esAggRequest);

    /**
     * 最大值聚合
     * @param esAggRequest
     * @return
     */
    EsAggResponse aggMax(EsAggRequest esAggRequest);

    /**
     * 累加聚合
     * @param esAggRequest
     * @return
     */
    EsAggResponse aggSum(EsAggRequest esAggRequest);

    /**
     * 平均值聚合
     * @param esAggRequest
     * @return
     */
    EsAggResponse aggAvg(EsAggRequest esAggRequest);

    /**
     * 平均值聚合
     * @param esAggRequest
     * @return
     */
    EsAggResponse aggPercentiles(EsAggRequest esAggRequest);
}
