package com.mfw.themis.portal.controller.model;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author liuqi
 */
@Data
public class DeleteAlarmMetricDTO {

    @NotNull(message = "id 不允许为空")
    private Long id;
    @NotNull(message = "operatorUid 不允许为空")
    private Long operatorUid;
}
