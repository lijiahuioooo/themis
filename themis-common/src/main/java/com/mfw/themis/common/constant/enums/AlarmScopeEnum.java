package com.mfw.themis.common.constant.enums;

import java.util.Arrays;

/**
 * @author liuqi
 */
public enum AlarmScopeEnum {
    /**
     * 规则报警
     */
    RULE_REPORT(1, "rule_report"),
    /**
     * 应用报警
     */
    APPLICATION_REPORT(2, "application_report");

    AlarmScopeEnum(Integer code, String desc) {
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

    public static AlarmScopeEnum getByCode(Integer code) {
        return Arrays.stream(values()).filter(e -> e.getCode() == code).findFirst().orElse(null);
    }
}
