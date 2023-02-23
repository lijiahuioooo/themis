package com.mfw.themis.portal.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author wenhong
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryAlarmRecordParam {

    @NotNull(message = "appId不能为空")
    @Min(value = 1, message = "appId不能为空")
    private Long appId;

    private Integer status;

    private Date startTime;
    private Date endTime;

    /**
     * 当前页
     */
    @NotNull(message = "param page can not be null")
    @Min(value = 1, message = "param page is illegal")
    private Integer page;
    /**
     * 每页个数
     */
    @NotNull(message = "param pageSize can not be null")
    @Min(value = 1, message = "param pageSize is illegal")
    private Integer pageSize;
}
