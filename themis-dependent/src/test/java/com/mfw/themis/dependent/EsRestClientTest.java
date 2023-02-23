package com.mfw.themis.dependent;

import com.mfw.themis.dependent.elasticsearch.EsRestClient;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Pattern;

@SpringBootTest
public class EsRestClientTest {

    @Test
    public void ipHostTest(){
        String patternIp = "(\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+,?)+";

        String address = "127.0.0.1:9200";
        boolean isMatch = Pattern.matches(patternIp, address);
        Assert.assertTrue(isMatch);

        address = "127.0.0.1:9200,127.0.0.2:9200";
        isMatch = Pattern.matches(patternIp, address);
        Assert.assertTrue(isMatch);

        address = "https://es.mafengwo.cn";
        isMatch = Pattern.matches(patternIp, address);
        Assert.assertFalse(isMatch);
    }

}
