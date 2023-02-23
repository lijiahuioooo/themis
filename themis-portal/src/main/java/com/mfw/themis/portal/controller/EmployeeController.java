package com.mfw.themis.portal.controller;

import com.mfw.themis.common.model.ResponseResult;
import com.mfw.themis.dependent.mfwemployee.EmployeeClient;
import com.mfw.themis.dependent.mfwemployee.model.EmpInfoListResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wenhong
 */
@RestController
@RequestMapping(value = "/portal/employee/")
public class EmployeeController {

    @Autowired
    private EmployeeClient employeeClient;

    @GetMapping(value = "sug")
    public ResponseResult<Object> suggest(String keyword) {
        return ResponseResult.OK(employeeClient.getSugList(keyword).getData());
    }

}