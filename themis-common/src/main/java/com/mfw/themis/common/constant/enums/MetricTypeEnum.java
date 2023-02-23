package com.mfw.themis.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

/**
 * @author guosp
 */
public enum MetricTypeEnum {
    /**
     * 系统指标(例：jvm相关数据)
     */
    SYSTEM_METRIC(1, "系统指标"),

    /**
     * 应用指标(例：qps相关数据)
     */
    APPLICATION_METRIC(2, "应用指标"),

    /**
     * 业务指标
     */
    BUSINESS_METRIC(3, "业务指标"),

    /**
     * 运维指标(例：容器相关数据)
     */
    OPS_METRIC(4, "运维指标");

    MetricTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private int code;
    private String desc;

    @JsonValue
    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    @JsonCreator
    public static MetricTypeEnum getByCode(Integer code) {
        return Arrays.stream(values()).filter(e -> e.getCode() == code).findFirst().orElse(null);
    }
}
