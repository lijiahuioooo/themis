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
public class QueryAppDTO {

    /**
     * 应用id
     */
    private Long Id;

    /**
     * 应用编码
     */
    private String appCode;

    /**
     * Mone应用编码
     */
    private String moneAppCode;

    /**
     * 应用来源
     */
    private String source;

    /**
     * 应用名称
     */
    private String appName;


    /**
     * 项目类型
     */
    private Integer projectType;

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
