package com.mfw.themis.collector.dubbo.sdk;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author liuqi
 */
@Component
@Activate(group = {Constants.CONSUMER, Constants.PROVIDER})
@Slf4j
public class MfwDubboFilter extends AbstractDubboFilter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        /**
         * 过滤掉monitor  不拦截该请求
         */
        if (invoker.getUrl().hasParameter(Constants.MONITOR_KEY)) {
            return invoker.invoke(invocation);
        }

        long startTime = System.currentTimeMillis();
        Result result = invoker.invoke(invocation);
        long costTime = System.currentTimeMillis() - startTime;

        collectFromResult(invoker, invocation, result, costTime);
        return result;
    }


    @Override
    public String getCodeFromResult(Result result) {
        return null;
    }

    @Override
    public String getMsgFromResult(Result result) {
        return null;
    }

    @Override
    public String getDataFromResult(Result result) {
        return null;
    }
}
