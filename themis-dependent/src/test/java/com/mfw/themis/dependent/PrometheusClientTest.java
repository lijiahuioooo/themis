package com.mfw.themis.dependent;

import com.mfw.themis.dependent.exception.DependentCommunicateException;
import com.mfw.themis.dependent.prometheus.PrometheusClient;
import com.mfw.themis.dependent.prometheus.model.PrometheusRequest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Ignore
public class PrometheusClientTest {

    @Autowired
    PrometheusClient prometheusClient;

    @Test
    public void testClient() {
        PrometheusRequest request = PrometheusRequest.builder()
                .query("up")
                .start("2020-09-03T09:10:00Z")
                .end("2020-09-03T10:10:00Z")
                .step("15s")
                .build();
        try {
            prometheusClient.query("http://10.32.3.237:10902", request);
        } catch (DependentCommunicateException e) {
            e.printStackTrace();
        }
    }
}
