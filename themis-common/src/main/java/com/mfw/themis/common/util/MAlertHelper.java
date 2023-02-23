package com.mfw.themis.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class MAlertHelper {

    @Resource
    private RestTemplate restTemplate;
    @Value("${mfw.alert.host:http://malert.mtech.svc.ab}")
    private String mAlertHost;
    @Value("${mfw.alert.uri:/malert/alertByAppCode}")
    private String mAlertUri;

    /**
     * 直接报警上报
     *
     * @param appCode  服务code
     * @param alertMsg 报警内容
     * @return
     */
    public boolean alertEventMap(String appCode, String alertMsg) {
        Map<String, String> eventMap = new HashMap<>();
        eventMap.put("appCode", appCode);
        eventMap.put("alertMsg", alertMsg);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(mAlertHost + mAlertUri, eventMap, String.class);
        JSONObject response = JSON.parseObject(responseEntity.getBody());
        return responseEntity.getStatusCode() == HttpStatus.OK && (Boolean) response.get("isSuccess") && response
                .get("isSuccess").equals(HttpStatus.OK.value());
    }
}
