package com.mfw.themis.portal.controller;

import com.mfw.themis.collector.sdk.MfwCollector;
import com.mfw.themis.collector.sdk.MfwCollectorRequest;
import com.mfw.themis.common.model.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author wenhong
 */
@RestController
@Slf4j
public class IndexController {

    @Autowired
    private MfwCollector mfwCollector;

    @GetMapping(value = "/")
    public ResponseResult<String> index() {
        return ResponseResult.OK("OK");
    }

    @GetMapping(value="/test")
    public ResponseResult<String> test() {

        MfwCollectorRequest request = new MfwCollectorRequest();
        request.setAppCode("honey-account");
        request.setMetric("http_response_time");

        Random random = new Random(999);
        Map<String, Object> insertItem = new HashMap<>();
        insertItem.put("api_name", "getInfo");
        insertItem.put("http_status", 200);
        insertItem.put("response_time", Double.valueOf(String.valueOf(random.nextInt(1000))));

        request.setData(insertItem);

        mfwCollector.report(request);

        return ResponseResult.OK("done");
    }

}