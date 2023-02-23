package com.mfw.themis.portal.controller.advice;

import com.google.common.collect.Lists;
import com.mfw.themis.common.constant.enums.GlobalCodeEnum;
import com.mfw.themis.common.exception.ServiceException;
import com.mfw.themis.common.exception.WebException;
import com.mfw.themis.common.model.ResponseResult;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.stereotype.Component;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author liuqi
 */
@Slf4j
@ControllerAdvice
@Component
public class CommonExceptionHandler {

    private static final List<String> ALTER_ENVS = Lists.newArrayList("prod", "stg");
    @Value("${app.env}")
    private String env;

    @ExceptionHandler(value = ServiceException.class)
    @ResponseBody
    public ResponseResult makeServiceException(ServiceException e) {
        log.error(e.getMessage(), e);
        StringWriter stringWriter = new StringWriter();
        String errorMessage = "[" + env + "]" + stringWriter.toString();

        return ResponseResult.fail(e.getGlobalCode().getCode(), e.getMessage());
    }

    @ExceptionHandler(value = WebException.class)
    @ResponseBody
    public ResponseResult makeWebException(WebException e) {
        log.error(e.getMessage(), e);
        StringWriter stringWriter = new StringWriter();

        return ResponseResult.fail(GlobalCodeEnum.GL_FAIL_9998.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseResult makeFailResponse(Exception e) {
        log.error(e.getMessage(), e);
        StringWriter stringWriter = new StringWriter();
        String errorMessage = "[" + env + "]" + stringWriter.toString();

        return ResponseResult.fail(GlobalCodeEnum.GL_FAIL_9999.getCode(), "内部错误");
    }

    /**
     * 统一处理参数校验错误异常
     *
     * @param response
     * @param e
     * @return
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class, HttpMessageConversionException.class})
    @ResponseBody
    public ResponseResult<?> processValidException(HttpServletResponse response, MethodArgumentNotValidException e) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        List<String> errorStringList = e.getBindingResult().getAllErrors()
                .stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        String errorMessage = String.join("; ", errorStringList);
        response.setContentType("application/json;charset=UTF-8");
        log.error(e.toString() + "_" + e.getMessage(), e);

        return ResponseResult.systemException(GlobalCodeEnum.GL_FAIL_9998.getCode(),
                errorMessage);
    }

}
