package com.mfw.themis.common.exception;

import com.mfw.themis.common.constant.enums.GlobalCodeEnum;

/**
 * @author liuqi
 */
public class UnAuthenticationException extends WebException {

    private static final String ERROR_MESSAGE = "您不是管理员，无法修改指标模板";

    public UnAuthenticationException() {
        super(ERROR_MESSAGE, GlobalCodeEnum.GL_FAIL_NO_AUTH);
    }
}
