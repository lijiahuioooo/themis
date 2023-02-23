package com.mfw.themis.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

public enum DefaultMetricEnum {
    /**
     * 默认HTTP规则
     */
    DEFAULT_HTTP_METRIC(0, "default_http_event"),
    /**
     * 默认Dubbo规则
     */
    DEFAULT_DUBBO_METRIC(1, "default_dubbo_event"),
    /**
     * GRPC默认规则
     */
    DEFAULT_GRPC_METRIC(2, "default_grpc_event");

    DefaultMetricEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }
    private int code;
    private String value;

    @JsonValue
    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    @JsonCreator
    public static DefaultMetricEnum getByCode(Integer code) {
        return Arrays.stream(values()).filter(e -> e.getCode() == code).findFirst().orElse(null);
    }
    
}
