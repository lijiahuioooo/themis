package com.mfw.themis.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

/**
 * @author wenhong
 */
public enum CustomTimeWindowUnitEnum {
    /**
     * 天
     */
    DAY(1, "天"),
    /**
     * 小时
     */
    HOUR(2, "小时"),
    /**
     * 分钟
     */
    MINUTE(3, "分钟");

    CustomTimeWindowUnitEnum(Integer code, String desc) {
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
    public static CustomTimeWindowUnitEnum getByCode(Integer code) {
        return Arrays.stream(values()).filter(e -> e.getCode() == code).findFirst().orElse(null);
    }
}
