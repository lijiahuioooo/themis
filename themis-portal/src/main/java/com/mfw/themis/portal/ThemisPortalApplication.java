package com.mfw.themis.portal;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.elasticsearch.ElasticSearchRestHealthIndicatorAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication(scanBasePackages = "com.mfw.themis",
        exclude = {DataSourceAutoConfiguration.class, ElasticSearchRestHealthIndicatorAutoConfiguration.class})
@ServletComponentScan
@EnableDiscoveryClient
@EnableWebMvc
@MapperScan("com.mfw.themis.dao.mapper")
@EnableAsync
public class ThemisPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThemisPortalApplication.class, args);
    }

}
