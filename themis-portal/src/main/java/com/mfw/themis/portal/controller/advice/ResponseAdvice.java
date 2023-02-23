package com.mfw.themis.portal.controller.advice;

import com.alibaba.fastjson.JSON;
import com.mfw.themis.common.model.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * rest controller @RequestBody response advice
 * @author wenhong
 */
@Slf4j
@RestControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        // 仅打印POST请求的返回
        if(HttpMethod.POST.equals(request.getMethod())){
            if(body instanceof ResponseResult){
                log.info("[请求返回] ===> {}", JSON.toJSONString(body));
            }
        }

        return body;
    }
}
