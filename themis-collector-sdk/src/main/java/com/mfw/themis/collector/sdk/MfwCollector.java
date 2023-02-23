package com.mfw.themis.collector.sdk;

import com.mafengwo.msp.commons.datacarrier.DataCarrier;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Mfw 业务数据上报收集器
 *
 * @author wenhong
 */
public class MfwCollector {

    private final static Logger log = LoggerFactory.getLogger(MfwCollector.class);

    private DataCarrier<MfwCollectorRequest> producer;
    private String appCode;

    public MfwCollector(DataCarrier<MfwCollectorRequest> producer, String appCode) {
        this.producer = producer;
        this.appCode = appCode;
    }

    /**
     * 上报数据
     *
     * @param request
     * @return
     */
    public void report(MfwCollectorRequest request) {

        if (StringUtils.isEmpty(request.getMetric())) {
            log.error("send to MfwCollector fail. metric is empty");
            return;
        }

        Map<String, Object> properties = request.getData();
        if (null == properties || properties.size() <= 0) {
            log.error("send to MfwCollector fail. data is empty");
            return;
        }
        // 设置appCode
        request.setAppCode(appCode);
        // 设置上报时间戳
        request.setTimestamp(System.currentTimeMillis());

        //异步数据上报
        producer.produce(request);
    }

}
