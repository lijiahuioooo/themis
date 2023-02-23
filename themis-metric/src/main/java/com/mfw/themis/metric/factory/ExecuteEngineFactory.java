package com.mfw.themis.metric.factory;

import com.mfw.themis.common.constant.enums.DataSourceTypeEnum;
import com.mfw.themis.common.exception.ServiceException;
import com.mfw.themis.metric.engine.ElasticSearchEngine;
import com.mfw.themis.metric.exception.DataSourceEngineNotSupportException;
import com.mfw.themis.metric.engine.ExecuteEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 指标计算引擎工厂类
 *
 * @author liuqi
 */
@Component
public class ExecuteEngineFactory {

    @Autowired
    private ExecuteEngine prometheusEngine;

    @Autowired
    private ElasticSearchEngine elasticSearchEngine;

    public ExecuteEngine getExecuteEngine(DataSourceTypeEnum dataSourceType) {
        if (dataSourceType == null) {
            throw new ServiceException("指标数据类型为空，无法计算指标");
        }
        switch (dataSourceType) {
            case PROMETHEUS:
                return prometheusEngine;
            case ELASTIC_SEARCH:
                return elasticSearchEngine;
            case MYSQL:
            case INFLUX_DB:
            default:
                throw new DataSourceEngineNotSupportException(dataSourceType);
        }
    }


}
