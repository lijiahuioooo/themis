package com.mfw.themis.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.mfw.themis.common.exception.EnumParamMappingException;

import java.util.Arrays;

/**
 * app来源
 *
 * @author wenhong
 */
public enum AppSourceEnum {

    /**
     * themis
     */
    THEMIS(0, "themis"),
    /**
     * aos
     */
    AOS(1, "aos");

    AppSourceEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private int code;
    private String desc;

    @JsonValue
    public int getCode() {
        return this.code;
    }

    public String getDesc() {
        return desc;
    }

    @JsonCreator
    public static AppSourceEnum getByCode(Integer code) {
        return Arrays.stream(values()).filter(e -> e.getCode() == code).findFirst()
                .orElseThrow(() -> new EnumParamMappingException(code));
    }
}
