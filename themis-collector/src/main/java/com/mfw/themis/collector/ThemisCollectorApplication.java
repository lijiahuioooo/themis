package com.mfw.themis.collector;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.elasticsearch.ElasticSearchRestHealthIndicatorAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 数据收集
 *
 * @author wenhong
 */
@SpringBootApplication(scanBasePackages = "com.mfw.themis", exclude = {ElasticSearchRestHealthIndicatorAutoConfiguration.class,
        RedisAutoConfiguration.class})
@EnableDiscoveryClient
@EnableAsync
@EnableCaching
@MapperScan("com.mfw.themis.dao.mapper")
public class ThemisCollectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThemisCollectorApplication.class, args);
    }
}
