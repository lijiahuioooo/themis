package com.mfw.themis.collector.sdk;

import com.mafengwo.msp.commons.datacarrier.DataCarrier;
import com.mafengwo.msp.commons.datacarrier.buffer.BufferStrategy;
import com.mfw.themis.collector.sdk.dubbo.MfwCollectorDubboConfiguration;
import com.mfw.themis.collector.sdk.grpc.MfwCollectorGrpcConfiguration;
import com.mfw.themis.collector.sdk.http.MfwCollectHttpConfiguration;
import io.grpc.ManagedChannel;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;

/**
 * 自动加载配置  mfw.collector.address
 * @author wenhong
 */
@Configuration
@EnableConfigurationProperties(MfwCollectorProperties.class)
@Import({MfwCollectorDubboConfiguration.class, MfwCollectHttpConfiguration.class, MfwCollectorGrpcConfiguration.class})
public class MfwCollectorAutoConfiguration {

    private final static Logger log = LoggerFactory.getLogger(MfwCollectorAutoConfiguration.class);

    private ManagedChannel managedChannel;
    private MfwCollectorProperties mfwCollectorProperties;

    public MfwCollectorAutoConfiguration(MfwCollectorProperties properties) {
        this.mfwCollectorProperties = properties;
    }

    @Bean
    public MfwCollector mfwCollector() {

        if (StringUtils.isEmpty(mfwCollectorProperties.getAddress())) {
            throw new CollectorCreateException("can`t find {mfw.collector.address}");
        }
        if (StringUtils.isEmpty(mfwCollectorProperties.getAppCode())) {
            throw new CollectorCreateException("can`t find {mfw.collector.appCode}");
        }

        log.info("start to init collector ManagedChannel. properties {}", mfwCollectorProperties.getAddress());
        ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue(CommonConstant.THREAD_POOL_QUEUE_SIZE);
        ExecutorService executor = new ThreadPoolExecutor(CommonConstant.THREAD_POOL_SIZE,
                CommonConstant.THREAD_POOL_SIZE, 60, TimeUnit.SECONDS, blockingQueue,
                new CollectorThreadFactory("collector-grpc-pool"), new CustomRejectedExecutionHandler());
        this.managedChannel = NettyChannelBuilder
                .forAddress(mfwCollectorProperties.getAddress(), CommonConstant.GRPC_PORT)
                .negotiationType(
                        NegotiationType.PLAINTEXT)
                .executor(executor).build();

        log.info("finish to init collector ManagedChannel.");

        DataCarrier<MfwCollectorRequest> dataCarrier = new DataCarrier<>("themis", CommonConstant.DATA_CHANNEL,
                CommonConstant.DATE_CHANNEL_BUFFER);
        dataCarrier.setBufferStrategy(BufferStrategy.IF_POSSIBLE);
        dataCarrier.consume(new AsynRequestConsumer(this.managedChannel), CommonConstant.CONSUMER_THREAD);
        return new MfwCollector(dataCarrier, mfwCollectorProperties.getAppCode());
    }

    static class CustomRejectedExecutionHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            log.warn(" collector grpc thread pool is full, rejecting the task");
        }
    }

    @PreDestroy
    public synchronized void close() {
        if (!managedChannel.isTerminated()) {
            managedChannel.shutdown();
        }

        try {
            final long waitLimit = System.currentTimeMillis() + 60 * 1000;
            do {
                log.info("Awaiting mfw collector channel shutdown: {}", managedChannel);
            } while (System.currentTimeMillis() < waitLimit && !managedChannel.awaitTermination(1, TimeUnit.SECONDS));


        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            log.info("We got interrupted - Speeding up shutdown mfw collector process");
        } finally {
            if (!managedChannel.isTerminated()) {
                log.info(" mfw collector Channel not terminated yet - force shutdown now: {} ", managedChannel);
                managedChannel.shutdown();
            }
        }

        log.debug(" mfw collector Channel is closed.");
    }

    static class CollectorCreateException extends BeanCreationException {

        private static final String ERR_MSG = "Failed to init mfwCollector.";

        public CollectorCreateException(String msg) {
            super(ERR_MSG + msg);
        }
    }
}
