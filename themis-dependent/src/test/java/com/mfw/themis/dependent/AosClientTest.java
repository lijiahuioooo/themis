package com.mfw.themis.dependent;

import com.mfw.themis.dependent.aosapp.AosClient;
import com.mfw.themis.dependent.aosapp.model.DepartmentResponse;
import com.mfw.themis.dependent.aosapp.model.MemberResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AosClientTest {

    @Autowired
    AosClient aosClient;

    @Test
    public void testMember() {
        MemberResponse response = aosClient.getAppMemberList("footprint");

        System.out.println(response);
    }

    @Test
    public void testDepartment() {
        DepartmentResponse response = aosClient.getAppDepartment("footprint");

        System.out.println(response.getDepartmentName());
    }
}
