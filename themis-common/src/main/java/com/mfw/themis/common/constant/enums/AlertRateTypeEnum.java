package com.mfw.themis.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

/**
 * 报警方式
 *
 * @author liuqi
 */
public enum AlertRateTypeEnum {
    /**
     * 固定次数
     */
    FIXED_TIMES(1, "固定次数"),
    /**
     * 固定间隔
     */
    FIXED_INTERVAL(2, "固定间隔"),
    /**
     * 递增间隔 5min 10min 15min 30min 30min
     */
    INCREASE_INTERVAL(3, "递增间隔"),
    /**
     * 每次都触发报警
     */
    EVERY_TIME(4, "总是触发报警");
    private int code;
    private String desc;

    AlertRateTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonValue
    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    @JsonCreator
    public static AlertRateTypeEnum getByCode(int codeInt) {
        return Arrays.stream(values()).filter(value -> value.getCode() == codeInt).findFirst().orElse(null);
    }
}
