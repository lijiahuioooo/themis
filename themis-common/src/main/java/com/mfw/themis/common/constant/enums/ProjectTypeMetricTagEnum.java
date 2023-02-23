package com.mfw.themis.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.mfw.themis.common.constant.MetricTagConstant;
import java.util.Arrays;

/**
 * @author guosp
 */

public enum ProjectTypeMetricTagEnum {
    /**
     * JAVA系统指标对应tag -- jvm
     */
    SYSTEM_JAVA(ProjectTypeEnum.JAVA.getCode(), MetricTagConstant.PROJECT_TYPE_SYSTEM_JAVA_TAG),

    /**
     * PHP系统指标对应tag
     */
    SYSTEM_PHP(ProjectTypeEnum.PHP.getCode(), MetricTagConstant.PROJECT_TYPE_SYSTEM_PHP_TAG),

    /**
     * GOLANG指标对应tag
     */
    SYSTEM_GOLANG(ProjectTypeEnum.GOLANG.getCode(), MetricTagConstant.PROJECT_TYPE_SYSTEM_GOLANG_TAG);


    ProjectTypeMetricTagEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private int code;
    private String desc;

    @JsonValue
    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ProjectTypeMetricTagEnum getByCode(Integer code) {
        return Arrays.stream(values()).filter(e -> e.getCode() == code).findFirst().orElse(null);
    }
}
