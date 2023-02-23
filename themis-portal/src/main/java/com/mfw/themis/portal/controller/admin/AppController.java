package com.mfw.themis.portal.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mfw.themis.common.model.ResponseResult;
import com.mfw.themis.common.model.bo.admin.AppSuggestBO;
import com.mfw.themis.common.model.dto.AppDTO;
import com.mfw.themis.portal.model.dto.QueryAppDTO;
import com.mfw.themis.portal.service.AppService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wenhong
 */
@RestController
@RequestMapping(value = "/portal/admin/app/")
@Slf4j
public class AppController {

    @Autowired
    private AppService appService;

    /**
     * 选择应用
     * @param keyword
     * @return
     */
    @GetMapping(value = "sug")
    public ResponseResult<IPage<AppSuggestBO>> suggest(String keyword) {
        QueryAppDTO queryAppDTO = new QueryAppDTO();
        queryAppDTO.setPage(1);
        queryAppDTO.setPageSize(20);

        if(StringUtils.isNotEmpty(keyword)){
            if(keyword.matches("^[0-9]*$")){
                queryAppDTO.setId(Long.valueOf(keyword));
            }else{
                queryAppDTO.setAppName(keyword);
            }
        }

        return ResponseResult.OK(appService.queryAppSuggestPage(queryAppDTO));
    }

    /**
     * 应用列表
     * @param appId
     * @param appName
     * @param moneAppCode
     * @param source
     * @param projectType
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping(value = "list")
    public ResponseResult<IPage<AppDTO>> queryAppPage(
            @RequestParam(value = "appId", defaultValue = "0") Long appId,
            @RequestParam(value = "appName", defaultValue = "") String appName,
            @RequestParam(value = "moneAppCode", defaultValue = "") String moneAppCode,
            @RequestParam(value = "source", defaultValue = "") String source,
            @RequestParam(value = "projectType", required = false) Integer projectType,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        QueryAppDTO queryAppDTO = new QueryAppDTO();
        queryAppDTO.setPage(page);
        queryAppDTO.setPageSize(pageSize);

        if(appId > 0){
            queryAppDTO.setId(appId);
        }

        if(StringUtils.isNotEmpty(appName)){
            queryAppDTO.setAppName(appName);
        }

        if(StringUtils.isNotEmpty(moneAppCode)){
            queryAppDTO.setMoneAppCode(moneAppCode);
        }

        if(StringUtils.isNotEmpty(source)){
            queryAppDTO.setSource(source);
        }

        if(null != projectType){
            queryAppDTO.setProjectType(projectType);
        }

        return ResponseResult.OK(appService.queryAppPage(queryAppDTO));
    }

    /**
     * 应用详情
     * @param appId
     * @return
     */
    @GetMapping(value = "detail")
    public ResponseResult<AppDTO> queryById(Long appId) {
        return ResponseResult.OK(appService.queryById(appId));
    }

    /**
     * 应用创建 or 更新
     * @param req
     * @return
     */
    @PostMapping("save")
    public ResponseResult<Long> save(@RequestBody @Validated AppDTO req) {
        if(null != req.getId()){
            appService.update(req);
            return ResponseResult.OK(req.getId());
        }

        return ResponseResult.OK(appService.open(req));
    }
}