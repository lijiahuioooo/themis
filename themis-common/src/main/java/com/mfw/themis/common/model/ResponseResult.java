package com.mfw.themis.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mfw.themis.common.constant.enums.GlobalCodeEnum;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiangqiao
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@JsonPropertyOrder({"code", "message", "data"})
public class ResponseResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 返回的对象
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
    /**
     * 返回的编码
     */
    private Integer code;
    /**
     * 返回的信息
     */
    private String message;

    /**
     * 成功返回成功响应码
     *
     * @return 响应结果
     */
    public static ResponseResult<String> OK() {
        return packageObject("", GlobalCodeEnum.GL_SUCC_0000);
    }

    /**
     * 成功返回响应数据
     *
     * @param data 返回的数据
     * @param <T>  返回的数据类型
     * @return 响应结果
     */
    public static <T> ResponseResult<T> OK(T data) {
        return packageObject(data, GlobalCodeEnum.GL_SUCC_0000);
    }

    /**
     * 对返回的消息进行包装
     *
     * @param data           返回的数据
     * @param globalCodeEnum 自定义的返回码枚举类型
     * @param <T>            返回的数据类型
     * @return 响应结果
     */
    public static <T> ResponseResult<T> packageObject(T data, GlobalCodeEnum globalCodeEnum) {
        ResponseResult<T> responseResult = new ResponseResult<>();
        responseResult.setCode(globalCodeEnum.getCode());
        responseResult.setMessage(globalCodeEnum.getDesc());
        responseResult.setData(data);
        return responseResult;
    }

    /**
     * 对返回的消息进行打包
     *
     * @param data    返回的数据
     * @param code    返回的状态码
     * @param message 返回的消息
     * @param <T>     返回的数据类型
     * @return 响应结果
     */
    public static <T> ResponseResult<T> packageObject(T data, Integer code, String message) {
        ResponseResult<T> responseResult = new ResponseResult<>();
        responseResult.setCode(code);
        responseResult.setMessage(message);
        responseResult.setData(data);
        return responseResult;
    }


    /**
     * 系统发生异常不可用时返回
     *
     * @param <T> 返回的数据类型
     * @return 响应结果
     */
    public static <T> ResponseResult<T> systemException() {
        return packageObject(null, GlobalCodeEnum.GL_FAIL_9999);
    }

    /**
     * 发现可感知的系统异常时返回
     *
     * @param globalCodeEnum
     * @param <T>
     * @return
     */
    public static <T> ResponseResult<T> systemException(GlobalCodeEnum globalCodeEnum) {
        return packageObject(null, globalCodeEnum);
    }

    public static <T> ResponseResult<T> fail(Integer code, String desc) {
        ResponseResult<T> responseResult = new ResponseResult<>();
        responseResult.setCode(code);
        responseResult.setMessage(desc);
        return responseResult;
    }

    public boolean isOK() {
        return code.equals(GlobalCodeEnum.GL_SUCC_0000.getCode());
    }

    /**
     * 自定义系统异常信息
     *
     * @param code
     * @param message 自定义消息
     * @param <T>
     * @return
     */
    public static <T> ResponseResult<T> systemException(Integer code, String message) {
        return packageObject(null, code, message);
    }
}
