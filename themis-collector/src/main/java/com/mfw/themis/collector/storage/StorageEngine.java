package com.mfw.themis.collector.storage;

import com.mfw.themis.common.model.message.CollectorMessage;

/**
 * @author wenhong
 */
public interface StorageEngine {

    /**
     * 数据校验
     * @param message
     */
    void validate(CollectorMessage message);

    /**
     * 数据加工处理
     * @param message
     */
    void process(CollectorMessage message);

}
