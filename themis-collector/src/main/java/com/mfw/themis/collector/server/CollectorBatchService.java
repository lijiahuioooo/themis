package com.mfw.themis.collector.server;

import com.mfw.themis.common.model.message.CollectorMessage;
import java.util.List;

/**
 * @author wenhong
 */
public interface CollectorBatchService {

    /**
     * 批量上报
     * @param collectorMessageList
     * @return
     */
    Boolean batchReport(List<CollectorMessage> collectorMessageList);
}
