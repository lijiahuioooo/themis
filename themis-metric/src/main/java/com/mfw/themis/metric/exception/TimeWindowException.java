package com.mfw.themis.metric.exception;


import com.mfw.themis.common.exception.ServiceException;

/**
 * @author wenhong
 */
public class TimeWindowException extends ServiceException {

    private static final String CONFIG_EMPTY_ERROR_MSG = "时间窗口异常";
    private static final String ERROR_MSG = "时间窗口异常,指标配置:%s";

    public TimeWindowException() {
        super(CONFIG_EMPTY_ERROR_MSG);
    }

    public TimeWindowException(String compositeExpression) {
        super(String.format(ERROR_MSG, compositeExpression));
    }
}
