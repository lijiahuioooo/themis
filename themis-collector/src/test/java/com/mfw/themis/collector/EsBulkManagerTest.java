package com.mfw.themis.collector;

import com.mfw.themis.collector.manager.EsBulkManager;
import com.mfw.themis.collector.manager.IndexManager;
import com.mfw.themis.collector.model.EsBulkRequest;
import org.checkerframework.checker.units.qual.A;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
//@Ignore
public class EsBulkManagerTest {

    @Autowired
    private EsBulkManager esBulkManager;
    @Autowired
    private IndexManager indexManager;

    @Test
    public void bulkInsertTest(){

        Random random = new Random(999);

        for(int i=0; i < 160; i++){
            EsBulkRequest esBulkRequest = new EsBulkRequest();
            esBulkRequest.setIndex("themis_test_20210624");
            esBulkRequest.setType("themis_event");

            Map<String, Object> insertItem = new HashMap<>();
            insertItem.put("api_name", "getInfo");
            insertItem.put("http_status", 200);
            insertItem.put("response_time", Double.valueOf(String.valueOf(random.nextInt(1000))));

            esBulkRequest.setData(insertItem);
            esBulkRequest.setUniqId(UUID.randomUUID().toString());

            esBulkManager.bulkInsert(esBulkRequest);
        }

        try{
            Thread.sleep(30000L);
        }catch (Exception e){}
    }

    @Test
    public void checkIndexTest(){
        indexManager.checkIndex("themis_test_20210624");
    }
}
