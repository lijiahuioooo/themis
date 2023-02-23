package com.mfw.themis.collector.sdk.http;

import static com.mfw.themis.common.constant.SdkCollectFields.ENDPOINT;
import static com.mfw.themis.common.constant.SdkCollectFields.HTTP_EVENT;
import static com.mfw.themis.common.constant.SdkCollectFields.HTTP_METHOD;
import static com.mfw.themis.common.constant.SdkCollectFields.HTTP_RESPONSE_SIZE;
import static com.mfw.themis.common.constant.SdkCollectFields.HTTP_STATUS;
import static com.mfw.themis.common.constant.SdkCollectFields.HTTP_THREAD;
import static com.mfw.themis.common.constant.SdkCollectFields.HTTP_URL;
import static com.mfw.themis.common.constant.SdkCollectFields.LOCAL_ADDR;
import static com.mfw.themis.common.constant.SdkCollectFields.REMOTE_ADDR;
import static com.mfw.themis.common.constant.SdkCollectFields.RT;
import static com.mfw.themis.common.constant.SdkCollectFields.SUCCESS;

import com.mfw.themis.collector.sdk.MfwCollector;
import com.mfw.themis.collector.sdk.MfwCollectorRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author liuqi
 */
public class CoeusMvcInterceptor implements HandlerInterceptor {

    private static final ThreadLocal<Long> TIME_THREADLOCAL = ThreadLocal.withInitial(System::currentTimeMillis);
    private MfwCollector mfwCollector;

    public CoeusMvcInterceptor(MfwCollector mfwCollector) {
        this.mfwCollector = mfwCollector;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        TIME_THREADLOCAL.set(System.currentTimeMillis());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            @Nullable ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            @Nullable Exception ex) throws Exception {
        long executeTime = System.currentTimeMillis() - TIME_THREADLOCAL.get();
        MfwCollectorRequest report = new MfwCollectorRequest();
        report.setMetric(HTTP_EVENT);
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            String basePath = "";
            RequestMapping basePathRequestMapping = handlerMethod.getMethodAnnotation(RequestMapping.class);
            if (basePathRequestMapping != null) {
                if (basePathRequestMapping.value().length > 0) {
                    basePath = basePathRequestMapping.value()[0];
                } else if (basePathRequestMapping.path().length > 0) {
                    basePath = basePathRequestMapping.path()[0];
                }
            }
            report.addField(ENDPOINT, basePath);
        }
        report.addField(RT, executeTime);
        report.addField(REMOTE_ADDR, request.getRemoteAddr());
        report.addField(LOCAL_ADDR, request.getLocalAddr());

        report.addField(HTTP_URL, request.getRequestURI());
        report.addField(HTTP_METHOD, request.getMethod());
        report.addField(HTTP_STATUS, response.getStatus());
        report.addField(SUCCESS, response.getStatus() == 200);
        report.addField(HTTP_THREAD, Thread.currentThread().getName());
        report.addField(HTTP_RESPONSE_SIZE, response.getBufferSize());

        mfwCollector.report(report);
        TIME_THREADLOCAL.remove();
    }

}
