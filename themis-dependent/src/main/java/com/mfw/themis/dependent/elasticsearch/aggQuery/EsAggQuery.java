package com.mfw.themis.dependent.elasticsearch.aggQuery;

import com.mfw.themis.dependent.elasticsearch.constant.enums.FilterMetricOperatorEnum;
import com.mfw.themis.dependent.elasticsearch.model.EsAggRequest;
import com.mfw.themis.dependent.elasticsearch.model.EsAggResponse;
import com.mfw.themis.dependent.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.ParsedAvg;
import org.elasticsearch.search.aggregations.metrics.max.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.max.ParsedMax;
import org.elasticsearch.search.aggregations.metrics.min.MinAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.min.ParsedMin;
import org.elasticsearch.search.aggregations.metrics.percentiles.ParsedPercentiles;
import org.elasticsearch.search.aggregations.metrics.percentiles.Percentile;
import org.elasticsearch.search.aggregations.metrics.percentiles.Percentiles;
import org.elasticsearch.search.aggregations.metrics.percentiles.PercentilesAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.ParsedSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.ParsedValueCount;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

/**
 * es聚合查询
 * @author wenhong
 */
@Slf4j
public class EsAggQuery implements EsAggInterface {

    /**
     * ES RestHighLevelClient
     */
    private RestHighLevelClient restHighLevelClient;

    /**
     * 构造函数
     * @param restHighLevelClient
     */
    public EsAggQuery(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    /**
     * 数量聚合
     * @param esAggRequest
     * @return
     */
    @Override
    public EsAggResponse aggCount(EsAggRequest esAggRequest, boolean needLog) {

        EsAggResponse esAggResponse = new EsAggResponse();

        SearchSourceBuilder sourceBuilder = buildSearchSource(esAggRequest);
        ValueCountAggregationBuilder aggBuilder = AggregationBuilders.count("count_id")
                .field("_id");
        sourceBuilder.aggregation(aggBuilder);

        SearchRequest searchRequest = createSearchRequest(esAggRequest, sourceBuilder);
        if (needLog) {
            log.info("es aggCount query: {}", sourceBuilder.toString());
        }
        Long aggValue;
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);

            Aggregations aggregations = searchResponse.getAggregations();
            if (null == aggregations) {
                aggValue = 0L;
            } else {
                ParsedValueCount internalValue = (ParsedValueCount) aggregations.asList().get(0);
                aggValue = internalValue.getValue();
                if (Double.isInfinite(aggValue)) {
                    aggValue = 0L;
                }
            }

            if (needLog) {
                log.info("es aggCount response: {}", searchResponse.toString());
            }
            esAggResponse.setStatus(EsAggResponse.SUCCESS_FLAG);
            esAggResponse.setData(EsAggResponse.ResponseData
                    .builder()
                    .metric(esAggRequest.getGroupField())
                    .value(aggValue.doubleValue())
                    .build());
        } catch (Exception e) {
            log.error("[error] es aggAvg error.", e);
            esAggResponse.setStatus(EsAggResponse.FAIL_FLAG);
        }

        return esAggResponse;
    }

    /**
     * 最大值聚合
     * @param esAggRequest
     * @return
     */
    @Override
    public EsAggResponse aggMax(EsAggRequest esAggRequest) {
        EsAggResponse esAggResponse = new EsAggResponse();

        SearchSourceBuilder sourceBuilder = buildSearchSource(esAggRequest);
        MaxAggregationBuilder aggBuilder = AggregationBuilders.max(esAggRequest.getGroupField())
                .field(esAggRequest.getGroupField());
        sourceBuilder.aggregation(aggBuilder);

        SearchRequest searchRequest = createSearchRequest(esAggRequest, sourceBuilder);

        log.debug("es aggMax query: {}", sourceBuilder.toString());

        Double aggValue;
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);

            Aggregations aggregations = searchResponse.getAggregations();
            if (null == aggregations) {
                aggValue = 0.0;
            } else {
                ParsedMax internalMax = (ParsedMax) aggregations.asList().get(0);
                aggValue = internalMax.getValue();
                if (Double.isInfinite(aggValue)) {
                    aggValue = 0.0;
                }
            }

            esAggResponse.setStatus(EsAggResponse.SUCCESS_FLAG);
            esAggResponse.setData(EsAggResponse.ResponseData
                    .builder()
                    .metric(esAggRequest.getGroupField())
                    .value(aggValue)
                    .build());
        } catch (Exception e) {
            log.error("[error] es aggMax error: ", e);
            esAggResponse.setStatus(EsAggResponse.FAIL_FLAG);
        }

        return esAggResponse;
    }

    /**
     * 最小值聚合
     * @param esAggRequest
     * @return
     */
    @Override
    public EsAggResponse aggMin(EsAggRequest esAggRequest) {
        EsAggResponse esAggResponse = new EsAggResponse();

        SearchSourceBuilder sourceBuilder = buildSearchSource(esAggRequest);
        MinAggregationBuilder aggBuilder = AggregationBuilders.min(esAggRequest.getGroupField())
                .field(esAggRequest.getGroupField());
        sourceBuilder.aggregation(aggBuilder);

        SearchRequest searchRequest = createSearchRequest(esAggRequest, sourceBuilder);

        log.debug("es aggMin query: {}", sourceBuilder.toString());

        Double aggValue;
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);

            Aggregations aggregations = searchResponse.getAggregations();
            if (null == aggregations) {
                aggValue = 0.0;
            } else {
                ParsedMin internalMin = (ParsedMin) aggregations.asList().get(0);
                aggValue = internalMin.getValue();
                if (Double.isInfinite(aggValue)) {
                    aggValue = 0.0;
                }
            }

            esAggResponse.setStatus(EsAggResponse.SUCCESS_FLAG);
            esAggResponse.setData(EsAggResponse.ResponseData
                    .builder()
                    .metric(esAggRequest.getGroupField())
                    .value(aggValue)
                    .build());
        } catch (Exception e) {
            log.error("[error] es aggMin error: ", e);
            esAggResponse.setStatus(EsAggResponse.FAIL_FLAG);
        }

        return esAggResponse;
    }

    /**
     * 累加聚合
     * @param esAggRequest
     * @return
     */
    @Override
    public EsAggResponse aggSum(EsAggRequest esAggRequest) {
        EsAggResponse esAggResponse = new EsAggResponse();

        SearchSourceBuilder sourceBuilder = buildSearchSource(esAggRequest);
        SumAggregationBuilder aggBuilder = AggregationBuilders.sum(esAggRequest.getGroupField())
                .field(esAggRequest.getGroupField());
        sourceBuilder.aggregation(aggBuilder);

        SearchRequest searchRequest = createSearchRequest(esAggRequest, sourceBuilder);

        log.debug("es aggSum query: {}", sourceBuilder.toString());

        Double aggValue;
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);

            Aggregations aggregations = searchResponse.getAggregations();
            if (null == aggregations) {
                aggValue = 0.0;
            } else {
                ParsedSum internalSum = (ParsedSum) aggregations.asList().get(0);
                aggValue = internalSum.getValue();
                if (Double.isInfinite(aggValue)) {
                    aggValue = 0.0;
                }
            }

            esAggResponse.setStatus(EsAggResponse.SUCCESS_FLAG);
            esAggResponse.setData(EsAggResponse.ResponseData
                    .builder()
                    .metric(esAggRequest.getGroupField())
                    .value(aggValue)
                    .build());
        } catch (Exception e) {
            log.error("[error] es aggSum error: ", e);
            esAggResponse.setStatus(EsAggResponse.FAIL_FLAG);
        }

        return esAggResponse;
    }

    /**
     * 平均值聚合
     * @param esAggRequest
     * @return
     */
    @Override
    public EsAggResponse aggAvg(EsAggRequest esAggRequest) {
        EsAggResponse esAggResponse = new EsAggResponse();

        SearchSourceBuilder sourceBuilder = buildSearchSource(esAggRequest);
        AvgAggregationBuilder aggBuilder = AggregationBuilders.avg(esAggRequest.getGroupField())
                .field(esAggRequest.getGroupField());
        sourceBuilder.aggregation(aggBuilder);

        SearchRequest searchRequest = createSearchRequest(esAggRequest, sourceBuilder);

        log.debug("es aggAvg query: {}", sourceBuilder.toString());
        Double aggValue;
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            log.debug("es aggAvg response: {}", searchResponse.toString());

            Aggregations aggregations = searchResponse.getAggregations();
            if (null == aggregations) {
                aggValue = 0.0;
            } else {
                ParsedAvg internalAvg = (ParsedAvg) aggregations.asList().get(0);
                aggValue = internalAvg.getValue();
                if (Double.isInfinite(aggValue)) {
                    aggValue = 0.0;
                }
            }

            esAggResponse.setStatus(EsAggResponse.SUCCESS_FLAG);
            esAggResponse.setData(EsAggResponse.ResponseData
                    .builder()
                    .metric(esAggRequest.getGroupField())
                    .value(aggValue)
                    .build());
        } catch (Exception e) {
            log.error("[error] es aggAvg error: ", e);
            esAggResponse.setStatus(EsAggResponse.FAIL_FLAG);
        }

        return esAggResponse;
    }

    /**
     * 百分比聚合
     * @param esAggRequest
     * @return
     */
    @Override
    public EsAggResponse aggPercentiles(EsAggRequest esAggRequest) {
        EsAggResponse esAggResponse = new EsAggResponse();

        SearchSourceBuilder sourceBuilder = buildSearchSource(esAggRequest);
        PercentilesAggregationBuilder aggBuilder = AggregationBuilders.percentiles(esAggRequest.getGroupField())
                .field(esAggRequest.getGroupField()).percentiles(Double.parseDouble(esAggRequest.getAggType().getValue()));
        
        sourceBuilder.aggregation(aggBuilder);
        
        SearchRequest searchRequest = createSearchRequest(esAggRequest, sourceBuilder);
        
        log.debug("es aggPercentiles query: {}", sourceBuilder.toString());
        Double aggValue;
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            log.debug("es aggPercentiles response: {}", searchResponse.toString());

            Percentiles aggregations = searchResponse.getAggregations().get(esAggRequest.getGroupField());
            if (null == aggregations) {
                aggValue = 0.0;
            } else {
                aggValue= aggregations.percentile(Double.parseDouble(esAggRequest.getAggType().getValue()));
                if (Double.isInfinite(aggValue)) {
                    aggValue = 0.0;
                }
            }
            esAggResponse.setStatus(EsAggResponse.SUCCESS_FLAG);
            esAggResponse.setData(EsAggResponse.ResponseData
                    .builder()
                    .metric(esAggRequest.getGroupField())
                    .value(aggValue)
                    .build());
        } catch (Exception e) {
            log.error("[error] es aggPercentiles error: ", e);
            esAggResponse.setStatus(EsAggResponse.FAIL_FLAG);
        }

        return esAggResponse;
    }

    /**
     * 构造ES SearchSourceBuilder 对象
     * @param esAggRequest
     * @return
     */
    private SearchSourceBuilder buildSearchSource(EsAggRequest esAggRequest) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = buildBoolQuery(esAggRequest);

        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.size(0);
        return sourceBuilder;
    }

    /**
     * 构造ES BoolQueryBuilder 对象
     * @param esAggRequest
     * @return
     */
    private BoolQueryBuilder buildBoolQuery(EsAggRequest esAggRequest) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (null != esAggRequest.getStartTime() && null != esAggRequest.getEndTime()) {
            boolQueryBuilder.must(buildTimeQuery(esAggRequest));
        }

        esAggRequest.getFilterMetrics().forEach(filterMetric -> {

            final String metric = filterMetric.getMetric();
            final String filterMetricOperator = filterMetric.getFilterMetricOperator();
            final String metricValue = filterMetric.getMetricValue();
            if (StringUtils.isAnyBlank(metric, filterMetricOperator, metricValue)) {
                return;
            }

            if (FilterMetricOperatorEnum.IS.getValue().equals(filterMetricOperator)) {
                boolQueryBuilder.must(this.createMatchQueryBuilder(metric, metricValue));
            } else if (FilterMetricOperatorEnum.IS_NOT.getValue().equals(filterMetricOperator)) {
                boolQueryBuilder.mustNot(this.createMatchQueryBuilder(metric, metricValue));
            } else if (FilterMetricOperatorEnum.IS_ONE_OF.getValue().equals(filterMetricOperator)
                    || FilterMetricOperatorEnum.IS_NOT_ONE_OF.getValue().equals(filterMetricOperator)) {

                final String[] metricValueArr = StringUtils.split(metricValue, ",");
                if (ArrayUtils.isEmpty(metricValueArr)) {
                    return;
                }
                BoolQueryBuilder subBoolQueryBuilder = QueryBuilders.boolQuery();
                for (String oneOfTheMetricValue : metricValueArr) {
                    subBoolQueryBuilder.should(this.createMatchQueryBuilder(metric, oneOfTheMetricValue));
                }
                if (FilterMetricOperatorEnum.IS_ONE_OF.getValue().equals(filterMetricOperator)) {
                    boolQueryBuilder.must(subBoolQueryBuilder);
                } else {
                    boolQueryBuilder.mustNot(subBoolQueryBuilder);
                }
            } else if (FilterMetricOperatorEnum.GT.getValue().equals(filterMetricOperator)
                    || FilterMetricOperatorEnum.GTE.getValue().equals(filterMetricOperator)
                    || FilterMetricOperatorEnum.LT.getValue().equals(filterMetricOperator)
                    || FilterMetricOperatorEnum.LTE.getValue().equals(filterMetricOperator)) {
                if (!StringUtils.isNumeric(metricValue)) {
                    return;
                }

                final int metricValueNumber = NumberUtils.toInt(metricValue);
                RangeQueryBuilder filterMetricRangeQueryBuilder = QueryBuilders.rangeQuery(metric);
                if (FilterMetricOperatorEnum.GT.getValue().equals(filterMetricOperator)) {
                    filterMetricRangeQueryBuilder.gt(metricValueNumber);
                } else if (FilterMetricOperatorEnum.GTE.getValue().equals(filterMetricOperator)) {
                    filterMetricRangeQueryBuilder.gte(metricValueNumber);
                } else if (FilterMetricOperatorEnum.LT.getValue().equals(filterMetricOperator)) {
                    filterMetricRangeQueryBuilder.lt(metricValueNumber);
                } else if (FilterMetricOperatorEnum.LTE.getValue().equals(filterMetricOperator)) {
                    filterMetricRangeQueryBuilder.lte(metricValueNumber);
                }
                boolQueryBuilder.must(filterMetricRangeQueryBuilder);
            } else if (FilterMetricOperatorEnum.LIKE.getValue().equals(filterMetricOperator)) {
                final WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery(
                        metric, StringUtils.replace(metricValue, "%", "*"));
                boolQueryBuilder.must(wildcardQueryBuilder);
            }

        });

        return boolQueryBuilder;
    }

    /**
     * 时间查询
     * @param esAggRequest
     * @return
     */
    private RangeQueryBuilder buildTimeQuery(EsAggRequest esAggRequest) {
        RangeQueryBuilder dateTimeRangeQueryBuilder;

        switch (esAggRequest.getDateFieldType()) {
            case TIMESTAMP:
                // 时间戳 单位毫秒
                dateTimeRangeQueryBuilder = QueryBuilders.rangeQuery(esAggRequest.getDateField())
                        .from(esAggRequest.getStartTime().getTime())
                        .to(esAggRequest.getEndTime().getTime());
                break;
            case DATETIME:
                // 日期时间
                dateTimeRangeQueryBuilder = QueryBuilders.rangeQuery(esAggRequest.getDateField())
                        .from(DateTimeUtil
                                .date2Str(esAggRequest.getStartTime(), DateTimeUtil.YYYY_MM_DD_T_HH_MM_SS_XXX_PATTERN))
                        .to(DateTimeUtil
                                .date2Str(esAggRequest.getEndTime(), DateTimeUtil.YYYY_MM_DD_T_HH_MM_SS_XXX_PATTERN));
                break;
            default:
                throw new IllegalArgumentException("unkown dateFieldType");
        }

        return dateTimeRangeQueryBuilder;
    }

    /**
     * 构造ES QueryBuilder 对象
     * @param metric
     * @param metricValue
     * @return
     */
    private QueryBuilder createMatchQueryBuilder(String metric, Object metricValue) {
        return QueryBuilders.matchPhraseQuery(metric, metricValue);
    }

    /**
     * 创建ES SearchRequest 对象
     * @param esAggRequest
     * @param sourceBuilder
     * @return
     */
    private SearchRequest createSearchRequest(EsAggRequest esAggRequest, SearchSourceBuilder sourceBuilder) {
        SearchRequest searchRequest = new SearchRequest();

        searchRequest.source(sourceBuilder);
        searchRequest.indices(esAggRequest.getIndexName());
        searchRequest.types(esAggRequest.getTypeName());

        return searchRequest;
    }

}

