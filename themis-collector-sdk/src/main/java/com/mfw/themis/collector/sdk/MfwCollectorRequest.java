package com.mfw.themis.collector.sdk;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据上报请求参数
 * @author wenhong
 */

public class MfwCollectorRequest {

    /**
     * appCode
     */
    private String appCode;
    /**
     * 上报事件指标
     */
    private String metric;
    /**
     * 时间戳，单位毫秒
     */
    private Long timestamp;
    /**
     * 上报自定义字段
     */
    private Map<String, Object> data = new ConcurrentHashMap<>();

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public void addField(String key, Object val) {
        data.put(key, val);
    }
}
