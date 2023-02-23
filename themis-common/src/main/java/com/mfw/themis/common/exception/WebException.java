package com.mfw.themis.common.exception;

import com.mfw.themis.common.constant.enums.GlobalCodeEnum;
import lombok.Data;

/**
 * 业务层异常
 *
 * @author liuqi
 */
public class WebException extends RuntimeException {

    private GlobalCodeEnum globalCode = GlobalCodeEnum.GL_FAIL_9995;

    public WebException() {
        super();
    }

    public WebException(String message) {
        super(message);
    }

    public WebException(String message, GlobalCodeEnum globalCode) {
        super(message);
        this.globalCode = globalCode;
    }

    public WebException(String message, Throwable e) {
        super(message, e);
    }

    public WebException(String message, GlobalCodeEnum globalCode, Throwable e) {
        super(message, e);
        this.globalCode = globalCode;
    }

    public GlobalCodeEnum getGlobalCode() {
        return globalCode;
    }
}
