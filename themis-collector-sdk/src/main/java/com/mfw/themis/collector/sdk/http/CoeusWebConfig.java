package com.mfw.themis.collector.sdk.http;

import com.mfw.themis.collector.sdk.MfwCollector;
import java.util.List;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author liuqi
 */
public class CoeusWebConfig implements WebMvcConfigurer {

    private MfwCollector mfwCollector;
    private List<String> includePathPatterns;
    private List<String> excludePathPatterns;


    public CoeusWebConfig(MfwCollector mfwCollector, List<String> includePathPatterns,
            List<String> excludePathPatterns) {
        this.mfwCollector = mfwCollector;
        this.includePathPatterns = includePathPatterns;
        this.excludePathPatterns = excludePathPatterns;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration interceptorRegistration = registry
                .addInterceptor(new CoeusMvcInterceptor(mfwCollector));

        if (includePathPatterns != null && includePathPatterns.size() > 0) {
            interceptorRegistration.addPathPatterns(includePathPatterns);
        }

        if (excludePathPatterns != null && excludePathPatterns.size() > 0) {
            interceptorRegistration.excludePathPatterns(excludePathPatterns);
        }

    }
}
