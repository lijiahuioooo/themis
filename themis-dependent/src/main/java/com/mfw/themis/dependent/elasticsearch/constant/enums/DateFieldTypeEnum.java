package com.mfw.themis.dependent.elasticsearch.constant.enums;

import java.util.Arrays;

/**
 * 日期字段类型
 *
 * @author wenhong
 */
public enum DateFieldTypeEnum {

    /**
     * 日期时间
     */
    DATETIME(1, "日期时间"),
    /**
     * 时间戳，单位毫秒
     */
    TIMESTAMP(2, "时间戳");

    private int code;
    private String desc;

    DateFieldTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static DateFieldTypeEnum getByCode(int codeInt) {
        return Arrays.stream(values()).filter(value -> value.getCode() == codeInt).findFirst().orElse(null);
    }
}
