package com.mfw.themis.metric.exception;


import com.mfw.themis.common.exception.ServiceException;

/**
 * @author liuqi
 */
public class CompositeFormularException extends ServiceException {

    private static final String CONFIG_EMPTY_ERROR_MSG = "复合指标未配置";
    private static final String ERROR_MSG = "复合指标获取异常,指标配置:%s";

    public CompositeFormularException() {
        super(CONFIG_EMPTY_ERROR_MSG);
    }

    public CompositeFormularException(String compositeExpression) {
        super(String.format(ERROR_MSG, compositeExpression));
    }
}
