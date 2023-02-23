package com.mfw.themis.metric;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.elasticsearch.ElasticSearchRestHealthIndicatorAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.mfw.themis", exclude = ElasticSearchRestHealthIndicatorAutoConfiguration.class)
@EnableDiscoveryClient
@EnableAsync
@EnableCaching
@EnableScheduling
@MapperScan("com.mfw.themis.dao.mapper")
public class ThemisMetricApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThemisMetricApplication.class, args);
    }
}
