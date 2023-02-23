package com.mfw.themis.collector.config;

import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ES restHighLevelClient
 *
 * @author wenhong
 */
@Slf4j
@Configuration
public class EsRestHighLevelClientConfigure {

    /**
     * 最大连接数
     */
    private static final int CONNECTION_MAX_TOTAL = 50;
    /**
     * connect timeout 5s
     */
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int CONNECTION_REQUEST_TIMEOUT = 30000;
    private static final int SOCKET_TIMEOUT = 30000;
    /**
     * timeout 60s
     */
    private static final int HTTP_TIMEOUT = 60000;

    private static final String HTTP_SCHEME = "http";
    private static final String PATTERN_IP = "(\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+,?)+";

    @Value("${themis.collector.es.address}")
    private String address;

    @Bean
    RestHighLevelClient restHighLevelClient() {
        RestHighLevelClient client;

        boolean isMatch = Pattern.matches(PATTERN_IP, address);
        if(isMatch){
            client = getClientByIp(address);
        }else{
            client = getClientByHost(address);
        }

        return client;
    }

    /**
     * 通过域名获取客户端
     * @param address  eg: https://es.mafengwo.cn
     * @return
     */
    public static RestHighLevelClient getClientByHost(String address){
        try{
            URL url = new URL(address);

            HttpHost httpHost = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
            RestClientBuilder restClientBuilder = RestClient.builder(httpHost);

            // client config
            setRestClientConfig(restClientBuilder);

            return new RestHighLevelClient(restClientBuilder);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 通过ip:port获取客户端
     * @param address  eg: ip:port,ip:port
     * @return
     */
    public static RestHighLevelClient getClientByIp(String address){
        RestClientBuilder restClientBuilder;

        String[] ipAddress = address.split(",");

        HttpHost[] hosts = Arrays.stream(ipAddress)
                .map(EsRestHighLevelClientConfigure::buildHttpHost)
                .filter(Objects::nonNull)
                .toArray(HttpHost[]::new);

        restClientBuilder = RestClient.builder(hosts);

        // client config
        setRestClientConfig(restClientBuilder);

        return new RestHighLevelClient(restClientBuilder);
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
     * rest client config
     * @param restClientBuilder
     */
    private static void setRestClientConfig(RestClientBuilder restClientBuilder){

        restClientBuilder.setMaxRetryTimeoutMillis(HTTP_TIMEOUT);
        restClientBuilder.setRequestConfigCallback(requestConfig -> {
            requestConfig.setConnectTimeout(CONNECTION_TIMEOUT);
            requestConfig.setSocketTimeout(SOCKET_TIMEOUT);
            requestConfig.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT);
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
     * 设置失败Handler
     * @param restClientBuilder
     */
    private static void setFailureHandler(RestClientBuilder restClientBuilder){
        restClientBuilder.setFailureListener(new RestClient.FailureListener(){
            @Override
            public void onFailure(HttpHost host){
            log.error("Es rest client connect failure, {}", host.toString());
            }
        });
    }

}
