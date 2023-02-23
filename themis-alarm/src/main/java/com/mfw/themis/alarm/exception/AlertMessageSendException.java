package com.mfw.themis.alarm.exception;

import com.mfw.themis.common.exception.ServiceException;

/**
 * 下发报警异常
 *
 * @author liuqi
 */
public class AlertMessageSendException extends ServiceException {

    private static final String ERR_MSG = "下发报警失败，下发内容：%s,异常信息：%s";

    public AlertMessageSendException(String req, String res) {
        super(String.format(ERR_MSG, req, res));
    }
}
