package com.mfw.themis.metric.config;

import com.mfw.themis.metric.handler.MyUncaughtExceptionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author liuqi
 */
@Component
public class ExecutorConfig {

    @Autowired
    private MyUncaughtExceptionHandler exceptionHandler;
    private AtomicInteger metricThreadCount = new AtomicInteger();
    private AtomicInteger ruleThreadCount = new AtomicInteger();
    /**
     * 指标任务线程池
     */
    private final ExecutorService metricExecutorService = new ThreadPoolExecutor(64, 128, 5000L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(8000), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("themis-metric-cal-" + metricThreadCount.incrementAndGet());
            t.setDaemon(true);
            t.setUncaughtExceptionHandler(exceptionHandler);
            return t;
        }
    }, new DiscardOldestPolicy());

    /**
     * 规则任务线程池
     */
    private final ExecutorService ruleExecutorService = new ThreadPoolExecutor(16, 32, 5000L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(8000), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("themis-rule-cal-" + ruleThreadCount.incrementAndGet());
            t.setDaemon(true);
            t.setUncaughtExceptionHandler(exceptionHandler);
            return t;
        }
    }, new DiscardOldestPolicy());

    public ExecutorService getMetricExecutorService() {
        return metricExecutorService;
    }

    public ExecutorService getRuleExecutorService() {
        return ruleExecutorService;
    }
}
