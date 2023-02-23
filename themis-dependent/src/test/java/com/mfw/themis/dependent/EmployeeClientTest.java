package com.mfw.themis.dependent;

import com.mfw.themis.dependent.mfwemployee.EmployeeClient;
import com.mfw.themis.dependent.mfwemployee.model.EmpInfoListResponse;
import com.mfw.themis.dependent.mfwemployee.model.EmpInfoResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class EmployeeClientTest {

    @Autowired
    private EmployeeClient employeeClient;

    @Test
    public void getInfoByUid(){
        Long uid = 56379418L;

        EmpInfoResponse empInfoResponse = employeeClient.getInfo(uid);
        Assert.assertEquals(empInfoResponse.getData().getUid(), uid);
    }

    @Test
    public void getInfoByUids(){
        Long uid = 56379418L;
        List<Long> uids = new ArrayList<>();
        uids.add(uid);

        EmpInfoListResponse empInfoListResponse = employeeClient.getInfoList(uids);
        Assert.assertEquals(empInfoListResponse.getData().get(1).getUid(), uid);
    }

    @Test
    public void getSuggestList(){
        Long uid = 56379418L;
        String word = "周文洪";

        EmpInfoListResponse empInfoListResponse = employeeClient.getSugList(word);
        Assert.assertEquals(empInfoListResponse.getData().get(0).getUid(), uid);
    }
}
