package com.mfw.themis.collector.sdk.dubbo;

import com.alibaba.dubbo.rpc.Filter;
import com.mfw.themis.collector.sdk.MfwCollector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * dubbo默认配置
 *
 * @author liuqi
 */
@Configuration
@ConditionalOnClass({Filter.class})
public class MfwCollectorDubboConfiguration {

    @Bean
    public MfwDubboFilter mfwDubboFilter(MfwCollector mfwCollector) {
        return new MfwDubboFilter(mfwCollector);
    }
}