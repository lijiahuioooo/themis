package com.mfw.themis.common.constant.enums;

/**
 * @author liuqi
 */

public enum ResourceEnum {
    /**
     * 报警记录
     */
    ALARM_RECODE(1, "报警记录"),
    /**
     * 报警规则
     */
    ALARM_RULE(2, "报警规则"),
    /**
     * 应用
     */
    APP(3, "应用"),
    /**
     * 应用指标
     */
    APP_METRIC(4, "应用指标"),
    /**
     * 报警等级
     */
    ALARM_LEVEL(5, "报警等级"),
    /**
     * 指标
     */
    ALARM_METRIC(6, "指标"),
    /**
     * 数据源
     */
    DATASOURCE(7, "数据源"),
    /**
     * 上报事件
     */
    COLLECT_METRIC(8, "上报事件");

    ResourceEnum(Integer code, String desc) {
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

}
