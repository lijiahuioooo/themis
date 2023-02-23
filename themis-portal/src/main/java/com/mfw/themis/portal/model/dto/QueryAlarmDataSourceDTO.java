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
public class QueryAlarmDataSourceDTO {

    /**
     * 数据源id
     */
    private Long id;


    /**
     * 数据源名称
     */
    private String name;

    /**
     * 当前页
     */
    @NotNull( message = "param page can not be null")
    @Min(value = 1, message = "param page is illegal")
    private Integer page;
    /**
     * 每页个数
     */
    @NotNull( message = "param pageSize can not be null")
    @Min(value = 1, message = "param pageSize is illegal")
    private Integer pageSize;
}
