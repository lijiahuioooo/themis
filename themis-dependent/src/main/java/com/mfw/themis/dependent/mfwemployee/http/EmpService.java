package com.mfw.themis.dependent.mfwemployee.http;

import com.mfw.themis.dependent.mfwemployee.model.EmpInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author wenhong
 */
@FeignClient(name = "employeeInfo", url = "${mfw.employee.host:}/emp/byuid")
public interface EmpService {

    /**
     * 获取员工信息
     * @param uid
     * @param token
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, headers = "Accept=application/json;Content-Type=application/json",
            produces = "application/x-www-form-urlencoded;charset=UTF-8", consumes = "application/json")
    EmpInfoResponse getEmpInfo(@RequestParam("uid") Long uid, @RequestParam("token") String token);
}
