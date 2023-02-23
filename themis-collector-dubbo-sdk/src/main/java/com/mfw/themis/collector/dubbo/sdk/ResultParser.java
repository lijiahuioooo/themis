package com.mfw.themis.collector.dubbo.sdk;

import com.alibaba.dubbo.rpc.Result;

/**
 * @author liuqi
 */
public interface ResultParser {

    /**
     * 根据result获取返回状态码
     *
     * @param result
     * @return
     */
    String getCodeFromResult(Result result);

    /**
     * 根据result获取描述
     *
     * @param result
     * @return
     */
    String getMsgFromResult(Result result);

    /**
     * 根据result获取返回数据
     *
     * @param result
     * @return
     */
    String getDataFromResult(Result result);
}
