package com.mfw.themis.collector.manager;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * @author liuqi
 */
@Component
@Slf4j
public class IndexManager {

    @Value("${themis.collector.es.address}")
    private String address;
    private static final String PATTERN_IP = "(\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+,?)+";
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    private static final Settings settings = Settings.builder()
            .put("index.routing.allocation.require.box_type", "hot")
            .put("number_of_replicas", 1)
            .put("number_of_shards", 2)
            .build();

    /**
     * 如果没有创建索引
     * @param indexName
     */
    public void checkIndex(String indexName) {
        try {
            boolean isMatch = Pattern.matches(PATTERN_IP, address);
            if (isMatch) {
                address = Arrays.stream(address.split(",")).map(s -> "http://" + s).findAny().orElse("");
            }
            ResponseEntity<Map> res = restTemplate.getForEntity(address + "/" + indexName + "/_mappings", Map.class);
        } catch (HttpClientErrorException e) {
            try {
                CreateIndexRequest createIndexReq = new CreateIndexRequest().settings(settings).index(indexName);
                CreateIndexResponse response = restHighLevelClient.indices().create(createIndexReq);
            } catch (Exception ex) {
                log.error("create Index error", ex);
            }
        } catch (Exception e) {
            log.error("create Index error.address:" + address, e);
        }
    }
}
