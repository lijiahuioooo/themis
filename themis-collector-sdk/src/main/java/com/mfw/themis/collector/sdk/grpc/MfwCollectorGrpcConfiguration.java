package com.mfw.themis.collector.sdk.grpc;

import com.mfw.themis.collector.sdk.MfwCollector;
import io.grpc.ServerInterceptor;
import net.devh.springboot.autoconfigure.grpc.server.GlobalServerInterceptorConfigurer;
import net.devh.springboot.autoconfigure.grpc.server.GlobalServerInterceptorRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * grpc默认配置
 *
 * @author liuqi
 */
@Configuration
@ConditionalOnClass({ServerInterceptor.class, GlobalServerInterceptorRegistry.class})
public class MfwCollectorGrpcConfiguration {

    @Bean
    public MfwGrpcInterceptor mfwGrpcInterceptor(MfwCollector mfwCollector) {
        return new MfwGrpcInterceptor(mfwCollector);
    }

    @Bean
    public GlobalServerInterceptorConfigurer globalTraceServerInterceptorConfigurerAdapter(MfwGrpcInterceptor mfwGrpcInterceptor) {
        return registry -> registry.addServerInterceptors(mfwGrpcInterceptor);
    }
}