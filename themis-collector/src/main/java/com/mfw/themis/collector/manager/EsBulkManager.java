package com.mfw.themis.collector.manager;

import com.mfw.themis.collector.model.EsBulkRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * es 批量处理
 *
 * @author wenhong
 */
@Slf4j
@Component
public class EsBulkManager {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    private BulkProcessor bulkProcessor;

    @Value("${themis.collector.es.bulk.num}")
    private Integer bulkNum;

    private static final Long BULK_BYTE_SIZE = 1L;
    private static final Integer BULK_CONCURRENT_REQUEST_NUM = 30;
    private static final Long BULK_FLUSH_INTERVAL = 10L;
    private static final Integer BULK_RETRY_NUM = 3;
    private static final Long BULK_BACKOFF_SECOND = 1L;

    private BulkProcessor.Listener listener = new BulkProcessor.Listener() {
        @Override
        public void beforeBulk(long executionId, BulkRequest request) {
        }

        @Override
        public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
            List<String> failureMessage = Arrays.stream(response.getItems())
                    .filter(item -> item.getFailure() != null)
                    .map(BulkItemResponse::getFailureMessage).collect(
                            Collectors.toList());
            if (!CollectionUtils.isEmpty(failureMessage)) {
                log.error("ES bulk write failure.failureMessage:{}", failureMessage);
            }
        }

        @Override
        public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
            log.error("Es bulk after failure... ", failure);
        }
    };

    @PostConstruct
    private void init() {
        BulkProcessor.Builder builder = BulkProcessor.builder(restHighLevelClient::bulkAsync, listener);

        builder.setBulkActions(bulkNum);
        builder.setBulkSize(new ByteSizeValue(BULK_BYTE_SIZE, ByteSizeUnit.MB));
        builder.setConcurrentRequests(BULK_CONCURRENT_REQUEST_NUM);
        builder.setFlushInterval(TimeValue.timeValueSeconds(BULK_FLUSH_INTERVAL));
        builder.setBackoffPolicy(
                BackoffPolicy.constantBackoff(TimeValue.timeValueSeconds(BULK_BACKOFF_SECOND), BULK_RETRY_NUM));

        bulkProcessor = builder.build();
    }

    /**
     * 批量插入
     *
     * @param esBulkRequest
     */
    public void bulkInsert(EsBulkRequest esBulkRequest) {
        bulkProcessor.add(new IndexRequest(esBulkRequest.getIndex(), esBulkRequest.getType(), esBulkRequest.getUniqId())
                .opType("create")
                .source(esBulkRequest.getData(), XContentType.JSON));
    }
}
