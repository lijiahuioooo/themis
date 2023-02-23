package com.mfw.themis.common.constant.enums;

/**
 * 设置DB数据源
 * @author wenhong
 */
public enum DBTypeEnum {

    /**
     * themis db
     */
    THEMIS_ALARM("themis_alarm"),

    /**
     * 大交通 db
     */
    TRAFFIC_ALARM("traffic_alarm");

    private String value;

    DBTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
