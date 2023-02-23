package com.mfw.themis.dependent.prometheus.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author liuqi
 */
@Data
@Builder
public class PrometheusRequest {

    private String query;
    //    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private String start;
    //    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private String end;
    private String step;


}
