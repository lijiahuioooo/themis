package com.mfw.themis.common.model.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 数据上报消息
 * @author wenhong
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollectorMessage {

    /**
     * SDK内部生成用来标识唯一健
     */
    private String _collectorId;

    /**
     * 应用appCode
     */
    private String appCode;

    /**
     * 上报事件名
     */
    private String metric;

    /**
     * 客户端ip
     */
    private String ip;

    /**
     * 上报时间戳，单位秒
     */
    private Long timestamp;

    /**
     * 自定义数据
     */
    private Map<String, Object> data;
}
