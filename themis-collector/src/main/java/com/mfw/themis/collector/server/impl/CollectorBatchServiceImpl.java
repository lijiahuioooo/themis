package com.mfw.themis.collector.server.impl;

import com.mfw.themis.collector.exception.MetricValidateException;
import com.mfw.themis.collector.factory.StorageEngineFactory;
import com.mfw.themis.collector.server.CollectorBatchService;
import com.mfw.themis.collector.storage.StorageEngine;
import com.mfw.themis.common.constant.enums.DataSourceTypeEnum;
import com.mfw.themis.common.model.message.CollectorMessage;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 批量上报
 * @author wenhong
 */
@Slf4j
@Service
public class CollectorBatchServiceImpl implements CollectorBatchService {

    @Autowired
    private StorageEngineFactory storageEngineFactory;

    /**
     * 批量上报
     * @param collectorMessageList
     * @return
     */
    @Override
    public Boolean batchReport(List<CollectorMessage> collectorMessageList) {

        log.info("messageList count: {}", collectorMessageList.size());
        collectorMessageList.forEach(collectorMessage -> {
            try {
                StorageEngine storageEngine = storageEngineFactory.getStorageEngine(DataSourceTypeEnum.ELASTIC_SEARCH);

                // 设置uuid
                collectorMessage.set_collectorId(UUID.randomUUID().toString());

                Long t1 = System.currentTimeMillis();
                storageEngine.validate(collectorMessage);
                Long t2 = System.currentTimeMillis();
                storageEngine.process(collectorMessage);
                Long t3 = System.currentTimeMillis();

                log.info(" appCode:{},metric:{} validate take {}ms, process take {}ms", collectorMessage.getAppCode(),
                        collectorMessage.getMetric(), t2 - t1, t3 - t2);
            } catch (MetricValidateException e) {
//                log.warn("batchReport collector validate failed, message: " + collectorMessage.toString(), e);
            }

        });

        return true;
    }

}
