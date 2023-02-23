package com.mfw.themis.portal.controller;

import com.mfw.themis.common.model.ResponseResult;
import com.mfw.themis.common.model.dto.AppDTO;
import com.mfw.themis.portal.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * app 信息
 * @author wenhong
 */
@RestController
@RequestMapping(value = "/portal/app/")
public class AppInfoController {

    @Autowired
    private AppService appService;

    /**
     * 应用详情
     * @param appCode
     * @return
     */
    @GetMapping(value = "detail")
    public ResponseResult<AppDTO> queryById(String appCode) {
        return ResponseResult.OK(appService.queryByAppCode(appCode));
    }

}
