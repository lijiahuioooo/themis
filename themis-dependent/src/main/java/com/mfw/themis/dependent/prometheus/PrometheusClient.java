package com.mfw.themis.dependent.prometheus;

import com.alibaba.fastjson.JSON;
import com.mfw.themis.dependent.exception.DependentCommunicateException;
import com.mfw.themis.dependent.prometheus.model.PrometheusRequest;
import com.mfw.themis.dependent.prometheus.model.PrometheusResponse;
import com.mfw.themis.dependent.prometheus.model.PrometheusResponse.PrometheusResponseData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * prometheus客户端
 *
 * @author liuqi
 */
@Component
@Slf4j
public class PrometheusClient {

    private static final String QUERY_PATH = "/api/v1/query";
    private static final String QUERY_RANGE_PATH = "/api/v1/query_range";

    @Autowired
    private RestTemplate restTemplate;

    public PrometheusResponseData query(String host, PrometheusRequest request) throws DependentCommunicateException {
        return queryFromPrometheus(host + QUERY_PATH, convertToParam(request));
    }

    public PrometheusResponseData queryRange(String host, PrometheusRequest request)
            throws DependentCommunicateException {
        return queryFromPrometheus(host + QUERY_RANGE_PATH, convertToParam(request));
    }

    private PrometheusResponseData queryFromPrometheus(String url, MultiValueMap<String, Object> postParameters)
            throws DependentCommunicateException {
        if (StringUtils.isBlank(url)) {
            throw new RuntimeException();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> param = new HttpEntity<>(postParameters, headers);
        PrometheusResponse responseResult = null;
        try {
            responseResult = restTemplate.postForObject(url, param, PrometheusResponse.class);
            if (responseResult == null || !responseResult.isSuccess()) {
                throw new DependentCommunicateException();
            }
        } catch (Exception e) {
            log.error(String.format("query prometheus error.url:%s,request:%s", url, JSON.toJSONString(postParameters)),
                    e);
            throw new DependentCommunicateException(e.getMessage());
        }

        return responseResult.getData();
    }

    private MultiValueMap<String, Object> convertToParam(PrometheusRequest request) {
        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
        if (StringUtils.isNotBlank(request.getQuery())) {
            postParameters.add("query", request.getQuery());
        }
        if (StringUtils.isNotBlank(request.getStart())) {
            postParameters.add("start", request.getStart());
        }
        if (StringUtils.isNotBlank(request.getEnd())) {
            postParameters.add("end", request.getEnd());
        }
        if (StringUtils.isNotBlank(request.getStep())) {
            postParameters.add("step", request.getStep());
        }
        return postParameters;
    }

}
