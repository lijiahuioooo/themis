package com.mfw.themis.dependent.mfwemployee.http;

import com.mfw.themis.dependent.mfwemployee.model.EmpTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author wenhong
 */
@FeignClient(name = "employeeToken", url = "https://admin.mafengwo.cn/mbasebiz/apiauth/api/getClientToken")
public interface EmpTokenService {

    /**
     * 获取token
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, headers = "Accept=application/json;Content-Type=application/json",
            produces = "application/x-www-form-urlencoded;charset=UTF-8", consumes = "application/json")
    EmpTokenResponse getToken(@RequestParam("appKey") String appKey, @RequestParam("appSecret") String appSecret);

}
