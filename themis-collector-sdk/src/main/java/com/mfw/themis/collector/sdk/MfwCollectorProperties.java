package com.mfw.themis.collector.sdk;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 数据上报配置
 *
 * @author wenhong
 */
@ConfigurationProperties(prefix = "mfw.collector")
public class MfwCollectorProperties {

    private String address;
    private String appCode;
    private HttpCollect httpCollect = new HttpCollect();

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public HttpCollect getHttpCollect() {
        return httpCollect;
    }

    public void setHttpCollect(HttpCollect httpCollect) {
        this.httpCollect = httpCollect;
    }

    public static class HttpCollect {

        private boolean enable = false;
        private List<String> includePathPatterns;
        private List<String> excludePathPatterns;

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public List<String> getIncludePathPatterns() {
            return includePathPatterns;
        }

        public void setIncludePathPatterns(List<String> includePathPatterns) {
            this.includePathPatterns = includePathPatterns;
        }

        public List<String> getExcludePathPatterns() {
            return excludePathPatterns;
        }

        public void setExcludePathPatterns(List<String> excludePathPatterns) {
            this.excludePathPatterns = excludePathPatterns;
        }
    }
}
