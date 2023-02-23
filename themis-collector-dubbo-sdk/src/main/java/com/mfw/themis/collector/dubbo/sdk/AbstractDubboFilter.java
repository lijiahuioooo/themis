package com.mfw.themis.collector.dubbo.sdk;

import static com.mfw.themis.collector.sdk.CommonConstant.THEMIS_APPLICATION_NAME;
import static com.mfw.themis.common.constant.SdkCollectFields.DUBBO_APPLICATION;
import static com.mfw.themis.common.constant.SdkCollectFields.DUBBO_CLASS;
import static com.mfw.themis.common.constant.SdkCollectFields.DUBBO_CODE;
import static com.mfw.themis.common.constant.SdkCollectFields.DUBBO_COUNT;
import static com.mfw.themis.common.constant.SdkCollectFields.DUBBO_GROUP;
import static com.mfw.themis.common.constant.SdkCollectFields.DUBBO_METHOD;
import static com.mfw.themis.common.constant.SdkCollectFields.DUBBO_MSG;
import static com.mfw.themis.common.constant.SdkCollectFields.DUBBO_PARAM;
import static com.mfw.themis.common.constant.SdkCollectFields.DUBBO_SERVICE;
import static com.mfw.themis.common.constant.SdkCollectFields.DUBBO_STATUS;
import static com.mfw.themis.common.constant.SdkCollectFields.DUBBO_STATUS_EXCEPTION;
import static com.mfw.themis.common.constant.SdkCollectFields.DUBBO_STATUS_SUCCESS;
import static com.mfw.themis.common.constant.SdkCollectFields.DUBBO_VERSION;
import static com.mfw.themis.common.constant.SdkCollectFields.RT;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.fastjson.JSON;
import com.mfw.themis.collector.sdk.MfwCollector;
import com.mfw.themis.collector.sdk.MfwCollectorRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liuqi
 */
@Slf4j
public abstract class AbstractDubboFilter implements Filter, ResultParser {

    private static final int ARGUMENT_MAX_LENGTH = 500;
    private MfwCollector mfwCollector;


    /**
     * 上报dubbo请求数据
     *
     * @param invoker
     * @param invocation
     * @param result
     * @param rt         响应时间
     */
    void collectFromResult(Invoker<?> invoker, Invocation invocation, Result result, Long rt) {
        String arguments = JSON.toJSONString(invocation.getArguments());
        String method = invocation.getMethodName();
        String path = invoker.getUrl().getPath();
        String application = invoker.getUrl().getParameter(Constants.APPLICATION_KEY);
        String service = invoker.getInterface().getName();
        String group = invoker.getUrl().getParameter(Constants.GROUP_KEY);
        String version = invoker.getUrl().getParameter(Constants.VERSION_KEY);

        Map<String, Object> dubboEvent = new HashMap<>();
        dubboEvent.put(DUBBO_APPLICATION, application);
        dubboEvent.put(DUBBO_SERVICE, service);
        dubboEvent.put(DUBBO_CLASS, path);
        dubboEvent.put(DUBBO_METHOD, method);
        dubboEvent.put(DUBBO_VERSION, version);
        dubboEvent.put(DUBBO_GROUP, group);
        dubboEvent.put(RT, rt);
        if (arguments != null && arguments.length() > ARGUMENT_MAX_LENGTH) {
            arguments = arguments.substring(0, ARGUMENT_MAX_LENGTH);
        }
        dubboEvent.put(DUBBO_PARAM, arguments);
        dubboEvent.put(DUBBO_COUNT, 1);
        if (result.getException() == null) {
            dubboEvent.put(DUBBO_STATUS, DUBBO_STATUS_SUCCESS);
        } else {
            dubboEvent.put(DUBBO_STATUS, DUBBO_STATUS_EXCEPTION);
        }

        String code = getCodeFromResult(result);
        if (code != null) {
            dubboEvent.put(DUBBO_CODE, code);
        }
        String msg = getMsgFromResult(result);
        if (msg != null) {
            dubboEvent.put(DUBBO_MSG, msg);
        }
        String appCode = SpringContextSupport.getProperty(THEMIS_APPLICATION_NAME);

        MfwCollectorRequest collector = new MfwCollectorRequest();
        collector.setMetric(getMetricByApplication(appCode));
        collector.setTimestamp(System.currentTimeMillis());
        collector.setAppCode(appCode);
        collector.setData(dubboEvent);
        if (mfwCollector == null) {
            mfwCollector = SpringContextSupport.getBean(MfwCollector.class);
        }
        if (mfwCollector != null) {
            mfwCollector.report(collector);
        } else {
            log.info("spring context is not ready...");
        }
    }


    private String getMetricByApplication(String appCode) {
        return appCode + "_dubbo_event";
    }

}
