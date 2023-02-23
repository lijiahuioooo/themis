package com.mfw.themis.dependent;

import com.mfw.themis.dependent.elasticsearch.EsRestClient;
import com.mfw.themis.dependent.elasticsearch.constant.enums.AggTypeEnum;
import com.mfw.themis.dependent.elasticsearch.constant.enums.DateFieldTypeEnum;
import com.mfw.themis.dependent.elasticsearch.model.EsAggRequest;
import com.mfw.themis.dependent.elasticsearch.model.EsAggRequest.EsAggMetric;
import com.mfw.themis.dependent.elasticsearch.model.EsAggResponse;
import org.apache.commons.lang3.time.DateUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class EsAggQueryTest {

    @Test
    public void aggCount(){
        // set EsAggRequest
        Date date = new Date();
        EsAggRequest esAggRequest = new EsAggRequest();
        esAggRequest.setStartTime(DateUtils.addHours(date, -1));
        esAggRequest.setEndTime(date);

        esAggRequest.setAggType(AggTypeEnum.COUNT);
        esAggRequest.setGroupField("https_response_total");
        esAggRequest.setDateField("datetime");
        esAggRequest.setDateFieldType(DateFieldTypeEnum.DATETIME);
        esAggRequest.setIndexName("mes_*");
        esAggRequest.setTypeName("server_event");

        List<EsAggMetric> esFilterMetricList = new ArrayList<>();
        esFilterMetricList.add(EsAggMetric.builder().metric("app_code").metricValue("ftwl").filterMetricOperator("is").build());
        esFilterMetricList.add(EsAggMetric.builder().metric("event_code").metricValue("http_provider").filterMetricOperator("is").build());
        esFilterMetricList.add(EsAggMetric.builder().metric("attr.path.keyword").metricValue("/order/payValidate").filterMetricOperator("is").build());
        esFilterMetricList.add(EsAggMetric.builder().metric("attr.response_status.keyword").metricValue("true,false").filterMetricOperator("is_one_of").build());

        esAggRequest.setFilterMetrics(esFilterMetricList);

        // aggQuery
        String host = "https://mes-es-traffic.mfwdev.com";
        EsAggResponse esAggResponse = EsRestClient.aggQuery(host, esAggRequest);

        Assert.assertEquals("https_response_total", esAggResponse.getData().getMetric());
    }

    @Test
    @Ignore
    public void aggCountEsV731(){
        // set EsAggRequest
        EsAggRequest esAggRequest = new EsAggRequest();

        esAggRequest.setAggType(AggTypeEnum.COUNT);
        esAggRequest.setGroupField("person_total");
        esAggRequest.setIndexName("accounts");
        esAggRequest.setTypeName("person");
        esAggRequest.setDateField("datetime");
        esAggRequest.setDateFieldType(DateFieldTypeEnum.DATETIME);

        List<EsAggMetric> esFilterMetricList = new ArrayList<>();
        esFilterMetricList.add(EsAggMetric.builder().metric("user").metricValue("张三").filterMetricOperator("is").build());

        esAggRequest.setFilterMetrics(esFilterMetricList);

        // aggQuery
        String host = "127.0.0.1:9200";
        EsAggResponse esAggResponse = EsRestClient.aggQuery(host, esAggRequest);

        Assert.assertEquals("https_response_total", esAggResponse.getData().getMetric());
    }

    @Test
    public void aggAvg(){
        // set EsAggRequest
        Date date = new Date();
        EsAggRequest esAggRequest = new EsAggRequest();
        esAggRequest.setStartTime(DateUtils.addMinutes(date, -1));
        esAggRequest.setEndTime(date);

        esAggRequest.setGroupField("attr.response_time");
        esAggRequest.setAggType(AggTypeEnum.AVG);
        esAggRequest.setIndexName("mes_mdts*");
        esAggRequest.setTypeName("server_event");
        esAggRequest.setDateField("datetime");
        esAggRequest.setDateFieldType(DateFieldTypeEnum.DATETIME);

        List<EsAggMetric> esFilterMetricList = new ArrayList<>();
        esFilterMetricList.add(EsAggMetric.builder().metric("app_code").metricValue("mdts").filterMetricOperator("is").build());
        esFilterMetricList.add(EsAggMetric.builder().metric("event_code").metricValue("http_provider").filterMetricOperator("is").build());

        esAggRequest.setFilterMetrics(esFilterMetricList);

        // aggQuery
        String host = "https://mes-es-traffic.mfwdev.com";
        EsAggResponse esAggResponse = EsRestClient.aggQuery(host, esAggRequest);

        Assert.assertEquals("attr.response_time", esAggResponse.getData().getMetric());
        System.out.println(esAggResponse);
    }

    @Test
    public void aggMin(){
        // set EsAggRequest
        Date date = new Date();
        EsAggRequest esAggRequest = new EsAggRequest();
        esAggRequest.setStartTime(DateUtils.addMinutes(date, -1));
        esAggRequest.setEndTime(date);

        esAggRequest.setGroupField("attr.response_time");
        esAggRequest.setAggType(AggTypeEnum.MIN);
        esAggRequest.setIndexName("mes_mdts*");
        esAggRequest.setTypeName("server_event");
        esAggRequest.setDateField("datetime");
        esAggRequest.setDateFieldType(DateFieldTypeEnum.DATETIME);

        List<EsAggMetric> esFilterMetricList = new ArrayList<>();
        esFilterMetricList.add(EsAggMetric.builder().metric("app_code").metricValue("mdts").filterMetricOperator("is").build());
        esFilterMetricList.add(EsAggMetric.builder().metric("event_code").metricValue("http_provider").filterMetricOperator("is").build());

        esAggRequest.setFilterMetrics(esFilterMetricList);

        // aggQuery
        String host = "https://mes-es-traffic.mfwdev.com";
        EsAggResponse esAggResponse = EsRestClient.aggQuery(host, esAggRequest);

        Assert.assertEquals("attr.response_time", esAggResponse.getData().getMetric());
    }

    @Test
    public void aggMax(){
        // set EsAggRequest
        Date date = new Date();
        EsAggRequest esAggRequest = new EsAggRequest();
        esAggRequest.setStartTime(DateUtils.addMinutes(date, -1));
        esAggRequest.setEndTime(date);

        esAggRequest.setGroupField("attr.response_time");
        esAggRequest.setAggType(AggTypeEnum.MAX);
        esAggRequest.setIndexName("mes_mdts*");
        esAggRequest.setTypeName("server_event");
        esAggRequest.setDateField("datetime");
        esAggRequest.setDateFieldType(DateFieldTypeEnum.DATETIME);

        List<EsAggMetric> esFilterMetricList = new ArrayList<>();
        esFilterMetricList.add(EsAggMetric.builder().metric("app_code").metricValue("mdts").filterMetricOperator("is").build());
        esFilterMetricList.add(EsAggMetric.builder().metric("event_code").metricValue("http_provider").filterMetricOperator("is").build());

        esAggRequest.setFilterMetrics(esFilterMetricList);

        // aggQuery
        String host = "https://mes-es-traffic.mfwdev.com";
        EsAggResponse esAggResponse = EsRestClient.aggQuery(host, esAggRequest);

        Assert.assertEquals("attr.response_time", esAggResponse.getData().getMetric());
    }
}
