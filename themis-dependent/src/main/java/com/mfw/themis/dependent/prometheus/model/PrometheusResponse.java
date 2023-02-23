package com.mfw.themis.dependent.prometheus.model;

import java.util.List;
import java.util.Map;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author liuqi
 */
@Data
public class PrometheusResponse {

    private static final String SUCCESS_FLAG = "success";
    private String status;
    private PrometheusResponseData data;

    @Data
    public static class PrometheusResponseData {

        private String resultType;
        private List<PrometheusResponseResult> result;
    }

    @Data
    public static class PrometheusResponseResult {

        private Map<String, String> metric;
        private List<Object> value;
        private List<List<Object>> values;
    }

    public boolean isSuccess() {
        return StringUtils.equals(this.status, SUCCESS_FLAG);
    }
}
