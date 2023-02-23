package com.mfw.themis.common.constant.enums;

/**
 * 指标模板标签
 * @author wenhong
 */
public enum AlarmMetricTagEnum {

    INIT("INIT"),
    AOS("AOS"),
    JAVA("JAVA`"),
    K8S("K8S");

    private String value;

    AlarmMetricTagEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
