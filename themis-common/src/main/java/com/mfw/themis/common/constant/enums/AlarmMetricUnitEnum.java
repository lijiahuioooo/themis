package com.mfw.themis.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

/**
 * @author liuqi
 */
public enum AlarmMetricUnitEnum {
    /**
     * 无单位
     */
    NONE(0, "无单位"),
    /**
     * 单位 MB
     */
    MB(1, "M"),
    /**
     * 单位GB
     */
    GB(2, "G"),
    /**
     * 百分比 例如99.22  保留小数点两位
     */
    PERCENTAGE(3, "百分比"),
    /**
     * 字符串
     */
    STRING(4, "字符串"),

    /**
     * 毫秒
     */
    MILL_SECOND(5, "毫秒");

    AlarmMetricUnitEnum(Integer code, String desc) {
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
    public static AlarmMetricUnitEnum getByCode(Integer code) {
        return Arrays.stream(values()).filter(e -> e.getCode() == code).findFirst().orElse(null);
    }
}
