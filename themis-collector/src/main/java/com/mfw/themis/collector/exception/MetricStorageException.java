package com.mfw.themis.collector.exception;

import com.mfw.themis.common.exception.ServiceException;

/**
 * 上报数据校验
 *
 * @author wenhong
 */
public class MetricStorageException extends ServiceException {

    private static final String CONFIG_EMPTY_ERROR_MSG = "上报数据存储失败";
    private static final String ERROR_MSG = "上报数据存储失败,%s";

    public MetricStorageException() {
        super(CONFIG_EMPTY_ERROR_MSG);
    }

    public MetricStorageException(String msg) {
        super(String.format(ERROR_MSG, msg));
    }

}
