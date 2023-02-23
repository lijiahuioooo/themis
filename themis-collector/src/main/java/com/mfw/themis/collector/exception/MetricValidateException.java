package com.mfw.themis.collector.exception;

import com.mfw.themis.common.exception.ServiceException;

/**
 * 上报数据校验
 *
 * @author wenhong
 */
public class MetricValidateException extends ServiceException {

    private static final String CONFIG_EMPTY_ERROR_MSG = "上报事件字段校验失败";
    private static final String ERROR_MSG = "上报事件字段校验失败,上报数据:%s";

    public MetricValidateException() {
        super(CONFIG_EMPTY_ERROR_MSG);
    }

    public MetricValidateException(String msg) {
        super(String.format(ERROR_MSG, msg));
    }

}
