package com.mfw.themis.dependent.mfwemployee.http;

import com.mfw.themis.dependent.mfwemployee.model.EmpInfoListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author wenhong
 */
@FeignClient(name = "employeeSuggest", url = "${mfw.employee.host:}/emp/suggest_list")
public interface EmpSugService {

    /**
     * 员工模糊搜索
     * @param word
     * @param token
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, headers = "Accept=application/json;Content-Type=application/json",
            produces = "application/x-www-form-urlencoded;charset=UTF-8", consumes = "application/json")
    EmpInfoListResponse getEmpSugList(@RequestParam("word") String word, @RequestParam("token") String token);
}
