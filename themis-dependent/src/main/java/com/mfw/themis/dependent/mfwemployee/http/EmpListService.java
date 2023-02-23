package com.mfw.themis.dependent.mfwemployee.http;

import com.mfw.themis.dependent.mfwemployee.model.EmpInfoListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author wenhong
 */
@FeignClient(name = "employeeInfoList", url = "${mfw.employee.host:}/emp/list_byuid")
public interface EmpListService {

    /**
     * 批量获取员工信息
     * @param uids
     * @param token
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, headers = "Accept=application/json;Content-Type=application/json",
            produces = "application/x-www-form-urlencoded;charset=UTF-8", consumes = "application/json")
    EmpInfoListResponse getEmpInfoList(@RequestParam("uids[]") List<Long> uids, @RequestParam("token") String token);
}
