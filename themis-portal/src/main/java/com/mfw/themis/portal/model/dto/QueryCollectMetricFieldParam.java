package com.mfw.themis.portal.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author wenhong
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryCollectMetricFieldParam {
    private String keyword;

    @NotNull(message = "appCode不能为空")
    private String appCode;

    private Integer status;

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
