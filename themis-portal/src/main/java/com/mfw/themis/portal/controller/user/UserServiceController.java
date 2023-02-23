package com.mfw.themis.portal.controller.user;

import com.mfw.themis.common.constant.enums.AppSourceEnum;
import com.mfw.themis.common.constant.enums.ProjectTypeEnum;
import com.mfw.themis.common.exception.ServiceException;
import com.mfw.themis.common.model.ResponseResult;
import com.mfw.themis.common.model.dto.AppDTO;
import com.mfw.themis.portal.service.AppService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/portal/user/service/")
@Slf4j
public class UserServiceController {

    @Autowired
    private AppService appService;

    /**
     * 服务开通
     * @param appDTO
     * @return
     */
    @PostMapping(value = "open")
    public ResponseResult<Long> open(@RequestBody @Validated AppDTO appDTO) {

        if(null == ProjectTypeEnum.getByCode(appDTO.getProjectType())){
            throw new ServiceException("项目类型不支持");
        }

        appDTO.setSource(AppSourceEnum.AOS.getCode());
        appDTO.setCreater(appDTO.getOperator());
        appDTO.setContacts(appDTO.getOperator().toString());
        if(null == appDTO.getAppName() || StringUtils.isBlank(appDTO.getAppName())){
            appDTO.setAppName(appDTO.getAppCode());
        }

        return ResponseResult.OK(appService.open(appDTO));
    }

    /**
     * 服务关闭
     * @param appDTO
     * @return
     */
    @PostMapping(value = "close")
    public ResponseResult<Long> close(@RequestBody AppDTO appDTO){

        if (StringUtils.isBlank(appDTO.getAppCode())) {
            throw new ServiceException("appCode不能为空");
        }

        return ResponseResult.OK(appService.close(appDTO));
    }
}
