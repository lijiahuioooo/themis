package com.mfw.themis.metric.exception;

import com.mfw.themis.common.constant.enums.DataSourceTypeEnum;
import com.mfw.themis.common.exception.ServiceException;

/**
 * @author liuqi
 */
public class DataSourceEngineNotSupportException extends ServiceException {

    private static final String ERROR_MSG = "该数据源类型的指标计算无法支持，数据源类型:%s";

    private static final String ES_DOC_INDEX_ERROR = "数据源，ES索引名称配置错误，索引名称:%s";


    public DataSourceEngineNotSupportException(DataSourceTypeEnum dataSourceType) {
        super(String.format(ERROR_MSG, dataSourceType.getDesc()));
    }

    public DataSourceEngineNotSupportException(String esDocIndex) {
        super(String.format(ES_DOC_INDEX_ERROR, esDocIndex));
    }
}
