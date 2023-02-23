package com.mfw.themis.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

/**
 * @author liuqi
 */
public enum TimeWindowEnum {
    /**
     * 1分钟
     */
    ONE_MINUTE(1, "1分钟"),
    /**
     * 三分钟
     */
    THREE_MINUTE(3, "3分钟"),
    /**
     * 5分钟
     */
    FIVE_MINUTE(5, "5分钟"),

    /**
     * 10分钟
     */
    TEN_MINUTE(10, "10分钟"),

    /**
     * 1小时
     */
    ONE_HOUR(60, "1小时"),

    /**
     * 1天
     */
    ONE_DAY(1440, "1天"),

    /**
     * 3天
     */
    THREE_DAY(4320, "3天");

    TimeWindowEnum(Integer code, String desc) {
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
    public static TimeWindowEnum getByCode(Integer code) {
        return Arrays.stream(values()).filter(e -> e.getCode() == code).findFirst().orElse(null);
    }
}
