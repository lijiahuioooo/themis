package com.mfw.themis.collector.sdk.http;

import com.mfw.themis.collector.sdk.MfwCollector;
import com.mfw.themis.collector.sdk.MfwCollectorProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author liuqi
 */
@Configuration
@ConditionalOnClass({HandlerInterceptor.class})
public class MfwCollectHttpConfiguration {

    @Bean
    @ConditionalOnProperty(name = "mfw.collector.httpCollect.enable", havingValue = "true")
    public CoeusWebConfig webConfig(MfwCollector mfwCollector, MfwCollectorProperties properties) {
        return new CoeusWebConfig(mfwCollector, properties.getHttpCollect().getIncludePathPatterns(),
                properties.getHttpCollect().getExcludePathPatterns());
    }
}
