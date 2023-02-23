package com.mfw.themis.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

/**
 * 主动上报的数据类型
 *
 * @author liuqi
 */
public enum CollectFieldTypeEnum {
    /**
     * 长整型
     */
    LONG("long"),
    /**
     * 浮点数
     */
    DOUBLE("double"),
    /**
     * 字符串
     */
    STRING("string");

    private String name;

    CollectFieldTypeEnum(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
    public static CollectFieldTypeEnum getByCode(String name) {
        return Arrays.stream(values()).filter(e -> StringUtils.equals(e.getName(), name)).findFirst().orElse(null);
    }
}
