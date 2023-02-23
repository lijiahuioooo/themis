package com.mfw.themis.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

/**
 * @author guosp
 */
public enum CollectTypeEnum {
    /**
     * 收集类型 - 单一指标
     */
    SINGLE_METRIC(1, "单一指标"),

    /**
     * 收集类型 - 复合指标
     */
    COMPOSITE_METRIC(2, "复合指标");

    CollectTypeEnum(Integer code, String desc) {
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
    public static CollectTypeEnum getByCode(Integer code) {
        return Arrays.stream(values()).filter(e -> e.getCode() == code).findFirst().orElse(null);
    }
}
