package com.mfw.themis.common.exception;

import com.mfw.themis.common.constant.enums.GlobalCodeEnum;
import lombok.Data;

/**
 * 业务层异常
 *
 * @author liuqi
 */
public class ServiceException extends RuntimeException {

    private GlobalCodeEnum globalCode = GlobalCodeEnum.GL_FAIL_9999;

    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, GlobalCodeEnum globalCodeEnum) {
        super(message);
        this.globalCode = globalCodeEnum;
    }

    public ServiceException(String message, Throwable e) {
        super(message, e);
    }

    public ServiceException(String message, GlobalCodeEnum globalCodeEnum, Throwable e) {
        super(message, e);
        this.globalCode = globalCodeEnum;
    }

    public GlobalCodeEnum getGlobalCode() {
        return globalCode;
    }
}
