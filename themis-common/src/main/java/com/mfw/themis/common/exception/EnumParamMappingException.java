package com.mfw.themis.common.exception;

/**
 * @author liuqi
 */
public class EnumParamMappingException extends WebException {

    private static final String ERR_MSG = "无法映射枚举预设类型，value:%d";

    public EnumParamMappingException(Integer code) {
        super(String.format(ERR_MSG, code));
    }
}
