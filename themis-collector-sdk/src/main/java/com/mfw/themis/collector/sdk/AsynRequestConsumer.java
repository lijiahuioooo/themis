package com.mfw.themis.collector.sdk;

import com.mafengwo.msp.commons.datacarrier.consumer.IConsumer;
import com.mfw.themis.collector.server.CollectorServiceGrpc;
import com.mfw.themis.collector.server.CollectorServiceOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * 异步消费
 *
 * @author wenhong
 */
public class AsynRequestConsumer implements IConsumer<MfwCollectorRequest> {

    private final static Logger log = LoggerFactory.getLogger(AsynRequestConsumer.class);

    private ManagedChannel managedChannel;

    public AsynRequestConsumer(ManagedChannel managedChannel) {
        this.managedChannel = managedChannel;
    }

    @Override
    public void init() {
    }

    @Override
    public void consume(List<MfwCollectorRequest> list) {
        log.debug("mfw collector consume info, items: {}", list);

        //GRPC 流式消费
        StreamObserver<CollectorServiceOuterClass.MfwCollectorRes> responseData = new StreamObserver<CollectorServiceOuterClass.MfwCollectorRes>() {
            @Override
            public void onNext(CollectorServiceOuterClass.MfwCollectorRes mfwCollectorRes) {
                if (!StringUtils.isEmpty(mfwCollectorRes.getErrorMessage())) {
                    log.error("mfw collector consume response error: {}", mfwCollectorRes.toString());
                }
            }

            @Override
            public void onError(Throwable t) {
                log.error("mfw collector consume response error", t);
            }

            @Override
            public void onCompleted() {
                log.debug("mfw collector consume response complete");
            }
        };

        try {
            StreamObserver<CollectorServiceOuterClass.MfwCollectorReq> requestStreamObserver = CollectorServiceGrpc
                    .newStub(managedChannel).reportMessage(responseData);

            list.forEach(request -> {
                CollectorServiceOuterClass.MfwCollectorReqOrBuilder mfwCollectorReqOrBuilder = CollectorServiceOuterClass.MfwCollectorReq
                        .newBuilder()
                        .setAppCode(request.getAppCode())
                        .setMetric(request.getMetric())
                        .setTimestamp(request.getTimestamp());

                /**
                 * object 转字符串
                 */
                Map<String, Object> properties = request.getData();
                if (properties == null) {
                    log.warn("report error, properties is null. metric:{}", request.getMetric());
                } else {
                    for (Map.Entry<String, Object> entry : properties.entrySet()) {
                        if (entry.getValue() != null) {
                            ((CollectorServiceOuterClass.MfwCollectorReq.Builder) mfwCollectorReqOrBuilder)
                                    .putData(entry.getKey(), entry.getValue().toString());
                        }
                    }

                    requestStreamObserver
                            .onNext(((CollectorServiceOuterClass.MfwCollectorReq.Builder) mfwCollectorReqOrBuilder)
                                    .build());
                }
            });

            requestStreamObserver.onCompleted();
        } catch (Exception e) {
            log.error("mfw collector consume error", e);
        }
    }

    @Override
    public void onError(List<MfwCollectorRequest> list, Throwable t) {
        log.error("mfw collector consume error", t);
    }

    @Override
    public void onExit() {
        log.info("mfw collector consume exit");
    }

}
