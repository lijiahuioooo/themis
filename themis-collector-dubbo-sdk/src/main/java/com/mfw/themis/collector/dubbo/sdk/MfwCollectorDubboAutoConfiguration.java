package com.mfw.themis.collector.dubbo.sdk;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liuqi
 */
@Configuration
public class MfwCollectorDubboAutoConfiguration {

    @Bean
    public SpringContextSupport springContextSupport() {
        return new SpringContextSupport();
    }

}
