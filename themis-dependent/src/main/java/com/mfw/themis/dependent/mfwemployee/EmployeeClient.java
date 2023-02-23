package com.mfw.themis.dependent.mfwemployee;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mfw.themis.dependent.mfwemployee.http.EmpListService;
import com.mfw.themis.dependent.mfwemployee.http.EmpService;
import com.mfw.themis.dependent.mfwemployee.http.EmpSugService;
import com.mfw.themis.dependent.mfwemployee.http.EmpTokenService;
import com.mfw.themis.dependent.mfwemployee.model.EmpInfoListResponse;
import com.mfw.themis.dependent.mfwemployee.model.EmpInfoResponse;
import com.mfw.themis.dependent.mfwemployee.model.EmpTokenResponse;
import feign.codec.DecodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 获取马蜂窝员工信息
 * @see <a href=https://wiki.mafengwo.cn/pages/viewpage.action?pageId=39432953>接入文档</a>
 * @author wenhong
 */
@Component
@Slf4j
public class EmployeeClient {

    private final String appKey = "dc848dc47c26315e345486a14c18b6c2";
    private final String appSecret = "29cc2c157c2e9b06b6a6ae19535e9dec";

    @Autowired
    private EmpTokenService empTokenService;

    @Autowired
    private EmpService empService;

    @Autowired
    private EmpListService empListService;

    @Autowired
    private EmpSugService empSugService;

    /**
     * 本地缓存token
     */
    private final static String CACHE_TOKEN_KEY = "mfw_employee_token";
    private Cache<String, String> localCache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(5000, TimeUnit.SECONDS)
            .build();

    /**
     * 获取token
     * @return
     */
    private String getToken() {
        String token = "";
        try{
            token = localCache.get(CACHE_TOKEN_KEY, () -> {
                EmpTokenResponse empTokenResponse = empTokenService.getToken(appKey, appSecret);
                return empTokenResponse.getToken();
            });

        }catch (Exception e){
            log.error("employeeClient localCache error, {}", e);
        }

        return token;
    }

    /**
     * 获取单个员工信息
     * @param uid
     * @return
     */
    public EmpInfoResponse getInfo(Long uid){
        try{
            String token = getToken();

            return empService.getEmpInfo(uid, token);
        }catch (DecodeException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 批量获取员工信息
     * @param uids
     * @return
     */
    public EmpInfoListResponse getInfoList(List<Long> uids){
        try{
            String token = getToken();
            return empListService.getEmpInfoList(uids, token);
        }catch (DecodeException e){
            return null;
        }
    }

    /**
     * 员工模糊搜索
     * @param word
     * @return
     */
    public EmpInfoListResponse getSugList(String word){
        try{
            String token = getToken();
            return empSugService.getEmpSugList(word, token);
        }catch (DecodeException e){
            return null;
        }
    }

}
