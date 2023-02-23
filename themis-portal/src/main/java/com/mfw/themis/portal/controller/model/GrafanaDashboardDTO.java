package com.mfw.themis.portal.controller.model;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author liuqi
 */
@Data
public class GrafanaDashboardDTO {

    @NotNull(message = "appCode 不能为空")
    private String appCode;
}
