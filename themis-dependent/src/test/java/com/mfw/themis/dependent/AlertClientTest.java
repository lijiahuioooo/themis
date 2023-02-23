package com.mfw.themis.dependent;

import com.mfw.themis.dependent.alert.AlertClient;
import com.mfw.themis.dependent.alert.model.AlertRequest;
import com.mfw.themis.dependent.alert.model.AlertResponse;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Ignore
public class AlertClientTest {

    @Autowired
    AlertClient alertClient;

    @Test
    public void testAlertClient() {
        AlertRequest req = new AlertRequest();
        req.setTitle("测试报警");
        req.setContent("测试");
        req.setState("in");
        req.setLevel("warning");
        req.setAlertTime("2020-07-08T08:00:13Z");
        req.setReceivers(Lists.newArrayList("2610"));
        AlertResponse response = alertClient.sendAlertMessage(req);
        Assert.assertEquals(0, response.getFailures().size());
    }
}
