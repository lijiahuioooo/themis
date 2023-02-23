package com.mfw.themis.common.config;

import java.util.concurrent.TimeUnit;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CommonConfig {

    @Bean
    public RestTemplate initRestTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
        return new RestTemplate(clientHttpRequestFactory);
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.httpclient")
    public HttpClientProperties httpClientProperties() {
        return new HttpClientProperties();
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(HttpClientProperties properties) {

        HttpClient httpClient = HttpClientBuilder.create().setMaxConnTotal(properties.getMaxTotalConnect())
                .setMaxConnPerRoute(properties.getMaxConnectPerRoute())
                .evictExpiredConnections()
                .evictIdleConnections(5000, TimeUnit.MILLISECONDS).build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(properties.getConnectTimeout());
        factory.setReadTimeout(properties.getReadTimeout());
        factory.setConnectionRequestTimeout(properties.getConnectionRequestTimeout());
        return factory;
    }
}
