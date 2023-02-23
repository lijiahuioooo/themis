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
public class QueryAlarmMetricDTO {

    /**
     * 指标id
     */
    private Long id;


    /**
     * 指标名称
     */
    private String name;

    /**
     * 关键词
     */
    private String keyword;

    /**
     * 指标类型
     */
    private Integer metricType;

    /**
     * 收集类型
     */
    private Integer collectType;

    /**
     * 数据源类型
     */
    private Integer sourceType;

    /**
     * 是否删除
     */
    private Boolean isDelete;

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
