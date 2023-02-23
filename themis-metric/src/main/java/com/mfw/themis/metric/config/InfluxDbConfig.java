//package com.mfw.themis.metric.config;
//
//import org.apache.commons.lang3.StringUtils;
//import org.influxdb.BatchOptions;
//import org.influxdb.InfluxDB;
//import org.influxdb.InfluxDBFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @author liuqi
// */
//@Configuration
//public class InfluxDbConfig {
//
//    @Value("${spring.influxDb.url:}")
//    private String influxDbUrl;
//    @Value("${spring.influxDb.username:}")
//    private String username;
//    @Value("${spring.influxDb.password:}")
//    private String password;
//    @Value("${spring.influxDb.database:}")
//    private String database;
//
//    @Bean
//    public InfluxDB influxDBConfig() {
//        if (StringUtils.isBlank(influxDbUrl)) {
//            return null;
//        }
//
//        InfluxDB influxDB = InfluxDBFactory.connect(influxDbUrl, username, password);
//        influxDB.enableBatch(BatchOptions.DEFAULTS);
//        influxDB.setDatabase(database);
//        return influxDB;
//    }
//
//}
