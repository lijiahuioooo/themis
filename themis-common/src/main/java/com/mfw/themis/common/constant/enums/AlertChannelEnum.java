package com.mfw.themis.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

/**
 * 报警渠道
 *
 * @author liuqi
 */
public enum AlertChannelEnum {
    /**
     * 邮箱
     */
    EMAIL(1, "email"),
    /**
     * sms
     */
    SMS(2, "sms"),
    /**
     * 微信
     */
    WECHAT(3, "mobile_work"),
    /**
     * 语音短信
     */
    VOICE_SMS(4, "voice_sms");

    AlertChannelEnum(Integer code, String desc) {
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

    @JsonCreator
    public static AlertChannelEnum getByCode(Integer code) {
        return Arrays.stream(values()).filter(e -> e.getCode() == code).findFirst().orElse(null);
    }
}