package com.mfw.themis.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

/**
 * 聚合方式
 *
 * @author liuqi
 */
public enum GroupTypeEnum {
    /**
     * 无
     */
    NONE(0, "无",""),
    /**
     * 个数
     */
    COUNT(1, "个数", ""),
    /**
     * 求和
     */
    SUM(2, "求和", ""),
    /**
     * 平均值
     */
    AVG(3, "平均值", ""),
    /**
     * 最小值
     */
    MIN(4, "最小值", ""),
    /**
     * 最大值
     */
    MAX(5, "最大值", ""),
    /**
     * 百分比50
     */
    PERCENT_50(6, "TP50","50.0"),
    /**
     * 百分比90
     */
    PERCENT_90(7,  "TP90","90.0"),
    /**
     * 百分比95
     */
    PERCENT_95(8, "TP95","95.0"),
    /**
     * 百分比99
     */
    PERCENT_99(9, "TP99","99.0"),
    /**
     * 百分比99.9
     */
    PERCENT_999(10, "TP999","99.9");
    

    private int code;
    private String desc;
    private String value;

    GroupTypeEnum(Integer code, String desc, String value) {
        this.code = code;
        this.desc = desc;
        this.value = value;
    }

    @JsonValue
    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
    
    public String getValue(){ return value; }

    @JsonCreator
    public static GroupTypeEnum getByCode(int codeInt) {
        return Arrays.stream(values()).filter(value -> value.getCode() == codeInt).findFirst().orElse(null);
    }
}
