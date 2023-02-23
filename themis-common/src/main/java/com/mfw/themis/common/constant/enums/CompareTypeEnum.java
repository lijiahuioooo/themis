package com.mfw.themis.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

/**
 * 比较枚举
 *
 * @author liuqi
 */
public enum CompareTypeEnum {
    /**
     * 大于
     */
    GT(1, ">", "大于"),
    /**
     * 小于
     */
    LT(2, "<", "小于"),
    /**
     * 等于
     */
    EQ(3, "==", "等于"),
    /**
     * 大于等于
     */
    GT_EQ(4, ">=", "大于等于"),
    /**
     * 小于等于
     */
    LT_EQ(5, "<=", "小于等于"),
    /**
     * 不等于
     */
    NON_EQ(6, "!=", "不等于"),

    EXIST(7, "存在", "存在"),

    NOT_EXIST(8, "不存在", "不存在");
    Integer code;
    String expression;
    String description;

    CompareTypeEnum(Integer code, String expression, String description) {
        this.code = code;
        this.expression = expression;
        this.description = description;
    }

    @JsonValue
    public Integer getCode() {
        return code;
    }

    public String getExpression() {
        return expression;
    }

    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static CompareTypeEnum getByCode(int code) {
        return Arrays.stream(values()).filter(e -> e.getCode() == code).findFirst().orElse(null);

    }
}
