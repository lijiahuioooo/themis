package com.mfw.themis.collector.factory;

import com.mfw.themis.collector.exception.MetricStorageException;
import com.mfw.themis.collector.storage.ElasticSearchStorageEngine;
import com.mfw.themis.collector.storage.StorageEngine;
import com.mfw.themis.common.constant.enums.DataSourceTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wenhong
 */
@Component
public class StorageEngineFactory {

    @Autowired
    private ElasticSearchStorageEngine elasticSearchStorage;

    /**
     * 获取存储引擎
     *
     * @param dataSourceTypeEnum
     * @return
     */
    public StorageEngine getStorageEngine(DataSourceTypeEnum dataSourceTypeEnum) {
        switch (dataSourceTypeEnum) {
            case ELASTIC_SEARCH:
                return elasticSearchStorage;
            default:
                throw new MetricStorageException("未知的存储引擎");
        }
    }

}
