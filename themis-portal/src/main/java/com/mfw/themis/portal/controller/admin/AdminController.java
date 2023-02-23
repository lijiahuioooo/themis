package com.mfw.themis.portal.controller.admin;

import com.mfw.themis.common.model.ResponseResult;
import com.mfw.themis.portal.auth.AdminRoleUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 指标模板
 *
 * @author wenhong
 */
@RestController
@RequestMapping(value = "/portal/admin")
@Slf4j
public class AdminController {

    @Autowired
    private AdminRoleUtils adminRoleUtils;

    @GetMapping("/checkAdmin")
    public ResponseResult<Boolean> checkRoles(Long uid) {
        return ResponseResult.OK(adminRoleUtils.checkAdminRole(uid));
    }
}
