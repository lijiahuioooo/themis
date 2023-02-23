package com.mfw.themis.collector.server;

import com.mfw.coeus.producer.sdk.MfwMessageResponse;
import com.mfw.themis.collector.message.producer.CollectorMessageProducer;
import com.mfw.themis.common.constant.enums.GlobalCodeEnum;
import com.mfw.themis.common.model.message.CollectorMessage;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.springboot.autoconfigure.grpc.server.GrpcService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * 数据收集GRPC服务
 * @author wenhong
 */
@Slf4j
@GrpcService
public class CollectorService extends CollectorServiceGrpc.CollectorServiceImplBase {

    @Autowired
    private CollectorMessageProducer collectorMessageProducer;

    @Override
    public StreamObserver<CollectorServiceOuterClass.MfwCollectorReq> reportMessage(
            StreamObserver<CollectorServiceOuterClass.MfwCollectorRes> responseObserver){

        return new StreamObserver<CollectorServiceOuterClass.MfwCollectorReq>() {

            @Override
            public void onNext(CollectorServiceOuterClass.MfwCollectorReq request) {
                CollectorMessage collectorMessage = new CollectorMessage();
                BeanUtils.copyProperties(request, collectorMessage);

                collectorMessage.set_collectorId(UUID.randomUUID().toString());

                MfwMessageResponse response = collectorMessageProducer.sendCollectorMessage(collectorMessage);
                if(null == response){
                    log.error("Mfw Collector 消息写入服务无响应");
                    return;
                }
            }

            @Override
            public void onError(Throwable e) {
                log.error("mfw collector 上报数据服务异常", e);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(CollectorServiceOuterClass.MfwCollectorRes.newBuilder()
                        .setErrorCode(GlobalCodeEnum.GL_SUCC_0000.getCode().toString()).build());
                responseObserver.onCompleted();
            }
        };

    }

}
