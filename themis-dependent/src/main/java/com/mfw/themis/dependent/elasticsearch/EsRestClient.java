package com.mfw.themis.dependent.elasticsearch;


import com.mfw.themis.dependent.elasticsearch.aggQuery.EsAggQuery;
import com.mfw.themis.dependent.elasticsearch.model.EsAggRequest;
import com.mfw.themis.dependent.elasticsearch.model.EsAggResponse;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.util.DigestUtils;

/**
 * Es Rest Client
 * @author wenhong
 */
@Slf4j
public class EsRestClient {

    /**
     * connection cache
     */
    private static Map<String, RestHighLevelClient> clientHashMap = new ConcurrentHashMap<>();

    /**
     * 最大连接数
     */
    private static final int CONNECTION_MAX_TOTAL = 50;
    /**
     * connect timeout 5s
     */
    private static final int CONNECTION_TIMEOUT = 5000;
    /**
     * timeout 10s
     */
    private static final int HTTP_TIMEOUT = 10000;

    private static final String HTTP_SCHEME = "http";
    private static final String PATTERN_IP = "(\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+,?)+";

    /**
     * 聚合查询
     * @param address  eg: https://es.mafengwo.cn or ip:port,ip:port
     * @param esAggRequest
     * @return
     */
    public static EsAggResponse aggQuery(String address, EsAggRequest esAggRequest) {
        return aggQuery(address, esAggRequest, false);
    }

    public static EsAggResponse aggQuery(String address, EsAggRequest esAggRequest, boolean needLog) {
        RestHighLevelClient restHighLevelClient = getRestClient(address);

        EsAggQuery esAggQuery = new EsAggQuery(restHighLevelClient);

        EsAggResponse esAggResponse;
        switch (esAggRequest.getAggType()) {
            case COUNT:
                esAggResponse = esAggQuery.aggCount(esAggRequest, needLog);
                break;
            case MIN:
                esAggResponse = esAggQuery.aggMin(esAggRequest);
                break;
            case MAX:
                esAggResponse = esAggQuery.aggMax(esAggRequest);
                break;
            case AVG:
                esAggResponse = esAggQuery.aggAvg(esAggRequest);
                break;
            case SUM:
                esAggResponse = esAggQuery.aggSum(esAggRequest);
                break;
            case PERCENT_50:
            case PERCENT_90:
            case PERCENT_95:
            case PERCENT_99:
            case PERCENT_999:
                esAggResponse = esAggQuery.aggPercentiles(esAggRequest);
                break;
            default:
                throw new IllegalArgumentException();
        }

        return esAggResponse;
    }

    /**
     * 获取客户端
     * @param address
     * @return
     */
    private static RestHighLevelClient getRestClient(String address) {
        RestHighLevelClient restHighLevelClient;

        String addressMd5 = DigestUtils.md5DigestAsHex(address.getBytes());

        if (null != clientHashMap.get(addressMd5)) {
            restHighLevelClient = clientHashMap.get(addressMd5);
        } else {
            boolean isMatch = Pattern.matches(PATTERN_IP, address);
            if (isMatch) {
                restHighLevelClient = getClientByIp(address);
            } else {
                restHighLevelClient = getClientByHost(address);
            }

            clientHashMap.put(addressMd5, restHighLevelClient);
        }

        return restHighLevelClient;
    }

    /**
     * 通过域名获取客户端
     * @param address  eg: https://es.mafengwo.cn
     * @return
     */
    public static RestHighLevelClient getClientByHost(String address) {
        try {
            URL url = new URL(address);

            HttpHost httpHost = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
            RestClientBuilder restClientBuilder = RestClient.builder(httpHost);

            // client config
            setRestClientConfig(restClientBuilder);

            return new RestHighLevelClient(restClientBuilder);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 通过ip:port获取客户端
     * @param address  eg: ip:port,ip:port
     * @return
     */
    public static RestHighLevelClient getClientByIp(String address) {
        RestClientBuilder restClientBuilder;

        String[] ipAddress = address.split(",");

        HttpHost[] hosts = Arrays.stream(ipAddress)
                .map(EsRestClient::buildHttpHost)
                .filter(Objects::nonNull)
                .toArray(HttpHost[]::new);

        restClientBuilder = RestClient.builder(hosts);

        // client config
        setRestClientConfig(restClientBuilder);

        return new RestHighLevelClient(restClientBuilder);
    }

    /**
     * rest client config
     * @param restClientBuilder
     */
    private static void setRestClientConfig(RestClientBuilder restClientBuilder) {

        restClientBuilder.setMaxRetryTimeoutMillis(HTTP_TIMEOUT);
        restClientBuilder.setRequestConfigCallback(requestConfig -> {
            requestConfig.setConnectTimeout(CONNECTION_TIMEOUT);
            requestConfig.setSocketTimeout(30000);
            requestConfig.setConnectionRequestTimeout(30000);
            return requestConfig;
        });

        restClientBuilder.setHttpClientConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setMaxConnTotal(CONNECTION_MAX_TOTAL);
            return requestConfigBuilder;
        });

        // set failure handler
        setFailureHandler(restClientBuilder);
    }

    /**
     * 构建HttpHost
     * @param ipPort
     * @return
     */
    private static HttpHost buildHttpHost(String ipPort) {
        String[] address = ipPort.split(":");

        if (address.length == 2) {
            String ip = address[0];
            int port = Integer.parseInt(address[1]);

            return new HttpHost(ip, port, HTTP_SCHEME);
        } else {
            return null;
        }
    }

    /**
     * 设置失败Handler
     * @param restClientBuilder
     */
    private static void setFailureHandler(RestClientBuilder restClientBuilder) {
        restClientBuilder.setFailureListener(new RestClient.FailureListener() {
            @Override
            public void onFailure(HttpHost host) {
                log.error("Es rest client connect failure, {}", host.toString());
            }
        });
    }
}
