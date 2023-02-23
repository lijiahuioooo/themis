package com.mfw.themis.common.constant.enums;

import java.util.Arrays;

/**
 * @author wenhong
 */
public enum ProjectTypeEnum {

    /**
     * java
     */
    JAVA(1, "java"),
    /**
     * php
     */
    PHP(2, "php"),
    /**
     * golang
     */
    GOLANG(3, "golang");

    ProjectTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private int code;
    private String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ProjectTypeEnum getByCode(Integer code) {
        return Arrays.stream(values()).filter(e -> e.getCode() == code).findFirst().orElse(null);
    }
}
