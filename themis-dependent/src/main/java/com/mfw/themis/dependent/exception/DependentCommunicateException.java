package com.mfw.themis.dependent.exception;

/**
 * 第三方依赖通信异常
 *
 * @author liuqi
 */
public class DependentCommunicateException extends Exception {

    public DependentCommunicateException() {
        super();
    }

    public DependentCommunicateException(String message) {
        super(message);
    }
}
