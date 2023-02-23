package com.mfw.themis.portal.model.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author guosp
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryAlarmRuleParam {

    private String keyword;


    @NotNull(message = "appId不能为空")
    @Min(value = 1, message = "appId不能为空")
    private Long appId;


    private Integer compare;

    private Long alarmLevelId;

    private Integer status;

    /**
     * 当前页
     */
    @NotNull(message = "param page can not be null")
    @Min(value = 1, message = "param page is illegal")
    private Long page;
    /**
     * 每页个数
     */
    @NotNull(message = "param pageSize can not be null")
    @Min(value = 1, message = "param pageSize is illegal")
    private Long pageSize;
}
