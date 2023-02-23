package com.mfw.themis.portal.model.dto.mes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * QueryAlarmRuleDTO
 * @author wenhong
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryAlarmRuleDTO {

    @NotNull( message = "appCode should not be null")
    private String appCode;

    /**
     * 当前页
     */
    @NotNull( message = "param page can not be null")
    @Min(value = 1, message = "param page is illegal")
    private Long page;
    /**
     * 每页个数
     */
    @NotNull( message = "param pageSize can not be null")
    @Min(value = 1, message = "param pageSize is illegal")
    private Long pageSize;
}
