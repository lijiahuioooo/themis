package com.mfw.themis.collector.sdk.grpc;

import static com.mfw.themis.common.constant.SdkCollectFields.ENDPOINT;
import static com.mfw.themis.common.constant.SdkCollectFields.GRPC_EVENT;
import static com.mfw.themis.common.constant.SdkCollectFields.GRPC_METHOD;
import static com.mfw.themis.common.constant.SdkCollectFields.GRPC_PARAM;
import static com.mfw.themis.common.constant.SdkCollectFields.LOCAL_ADDR;
import static com.mfw.themis.common.constant.SdkCollectFields.REMOTE_ADDR;
import static com.mfw.themis.common.constant.SdkCollectFields.RT;
import static com.mfw.themis.common.constant.SdkCollectFields.SUCCESS;
import static io.grpc.Grpc.TRANSPORT_ATTR_LOCAL_ADDR;
import static io.grpc.Grpc.TRANSPORT_ATTR_REMOTE_ADDR;

import com.mfw.themis.collector.sdk.MfwCollector;
import com.mfw.themis.collector.sdk.MfwCollectorRequest;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

public class MfwGrpcInterceptor implements ServerInterceptor {

    private MfwCollector mfwCollector;

    public MfwGrpcInterceptor(MfwCollector mfwCollector) {
        this.mfwCollector = mfwCollector;
    }

    @Override
    public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(
                next.startCall(call, headers)) {
            private long startTime = 0; //处理开始时间
            private ReqT request;

            @Override
            public void onComplete() {
                //记录请求参数及耗时
                long rt = System.currentTimeMillis() - startTime;
                MfwCollectorRequest report = new MfwCollectorRequest();
                report.setMetric(GRPC_EVENT);
                report.addField(ENDPOINT, call.getMethodDescriptor().getFullMethodName());
                report.addField(GRPC_METHOD, call.getMethodDescriptor().getFullMethodName());
                report.addField(GRPC_PARAM, request.toString());
                report.addField(LOCAL_ADDR, call.getAttributes().get(TRANSPORT_ATTR_LOCAL_ADDR));
                report.addField(REMOTE_ADDR, call.getAttributes().get(TRANSPORT_ATTR_REMOTE_ADDR));
                report.addField(RT, rt);
                report.addField(SUCCESS, true);
                mfwCollector.report(report);
                super.onComplete();
            }

            @Override
            public void onMessage(ReqT message) {
                startTime = System.currentTimeMillis();
                request = message;
                super.onMessage(message);
            }

        };
    }
}
