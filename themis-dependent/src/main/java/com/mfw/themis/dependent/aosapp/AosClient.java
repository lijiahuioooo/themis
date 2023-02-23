package com.mfw.themis.dependent.aosapp;

import com.mfw.themis.dependent.aosapp.model.DepartmentResponse;
import com.mfw.themis.dependent.aosapp.model.MemberResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 系统部应用用户列表，接口文档 https://yapi.mfwdev.com/project/841/interface/api/14829
 *
 * @author wenhong
 */
@FeignClient(name = "aosapp", url = "${themis.alarm.aosapp.host:}")
public interface AosClient {

    @GetMapping("/api/v1/servicetree/app/{appCode}/user")
    MemberResponse getAppMemberList(@PathVariable("appCode") String appCode);

    @GetMapping("/api/v1/servicetree/app/{appCode}")
    DepartmentResponse getAppDepartment(@PathVariable("appCode") String appCode);
}
