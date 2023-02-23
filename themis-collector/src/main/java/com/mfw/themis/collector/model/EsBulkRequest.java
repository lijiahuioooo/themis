package com.mfw.themis.collector.model;

import java.util.Map;
import lombok.Data;

/**
 * 批量请求参数
 *
 * @author wenhong
 */
@Data
public class EsBulkRequest {

    /**
     * 索引index
     */
    private String index;
    /**
     * 索引type
     */
    private String type;

    /**
     * 唯一健，非必填
     */
    private String uniqId;

    /**
     * 数据字段
     */
    private Map<String, Object> data;
}
