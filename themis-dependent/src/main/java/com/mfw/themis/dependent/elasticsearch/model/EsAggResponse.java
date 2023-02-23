package com.mfw.themis.dependent.elasticsearch.model;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 聚合查询结果
 * @author wenhong
 */
@Data
public class EsAggResponse {
    public static final String FAIL_FLAG = "fail";
    public static final String SUCCESS_FLAG = "success";
    private String status;
    private ResponseData data;

    @Data
    @Builder
    public static class ResponseData {

        private String metric;
        private Double value;
    }

    public boolean isSuccess() {
        return StringUtils.equals(this.status, SUCCESS_FLAG);
    }
}
