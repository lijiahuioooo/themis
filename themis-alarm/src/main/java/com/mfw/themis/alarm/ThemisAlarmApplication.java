package com.mfw.themis.alarm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.elasticsearch.ElasticSearchRestHealthIndicatorAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.mfw.themis", exclude = ElasticSearchRestHealthIndicatorAutoConfiguration.class)
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.mfw.themis.dao.mapper")
public class ThemisAlarmApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThemisAlarmApplication.class, args);
    }

}
