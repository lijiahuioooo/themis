package com.mfw.themis.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.mfw.themis.common.exception.EnumParamMappingException;
import java.util.Arrays;

/**
 * 数据源类型
 *
 * @author liuqi
 */
public enum DataSourceTypeEnum {
    /**
     * mysql
     */
    MYSQL(1, "mysql"),
    /**
     * premotheus
     */
    PROMETHEUS(2, "prometheus"),
    /**
     * ES
     */
    ELASTIC_SEARCH(3, "ES"),
    /**
     * influxDB
     */
    INFLUX_DB(4, "influxDB");

    DataSourceTypeEnum(Integer code, String desc) {
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
    public static DataSourceTypeEnum getByCode(Integer code) {
        return Arrays.stream(values()).filter(e -> e.getCode() == code).findFirst()
                .orElseThrow(() -> new EnumParamMappingException(code));
    }
}
