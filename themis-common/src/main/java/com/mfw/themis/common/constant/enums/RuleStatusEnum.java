package com.mfw.themis.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

/**
 * 故障状态
 *
 * @author liuqi
 */

public enum RuleStatusEnum {
    /**
     * 正常
     */
    IN_NORMAL(0, "正常"),
    /**
     * 故障中
     */
    IN_ERROR(1, "故障中"),
    /**
     * 已恢复
     */
    SOLVE(2, "已恢复"),
    /**
     * 超时关闭
     */
    CLOSE(3, "超时关闭");

    RuleStatusEnum(Integer code, String desc) {
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

    @JsonCreator
    public static RuleStatusEnum getByCode(Integer code) {
        return Arrays.stream(values()).filter(e -> e.getCode() == code).findFirst().orElse(null);
    }
}
