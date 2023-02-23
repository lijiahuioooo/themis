package com.mfw.themis.common.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import java.util.Map;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author wenhong
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppDTO {

    @JsonProperty("appId")
    private Long id;

    @NotNull(message = "appCode不能为空")
    @Size(min = 1, message = "appCode不能为空")
    private String appCode;

    /**
     * 大交通mone系统的app_code
     */
    private String moneAppCode;

    @NotNull(message = "appName不能为空")
    @Size(min = 1, message = "appName不能为空")
    private String appName;

    private Integer source;
    /**
     * 来源类型
     */
    private String sourceDesc;

    /**
     * 项目类型
     */
    private Integer projectType;
    /**
     * 项目类型描述
     */
    private String projectTypeDesc;

    private String department;
    private String description;

    @NotNull(message = "操作人不能为空")
    @Min(value = 1, message = "操作人不能为空")
    private Long operator;

    /**
     * 负责人uids
     */
    private String contacts;

    /**
     * 负责人姓名列表
     */
    private List<Map<Long, String>> contactList;

    /**
     * 创建人uid
     */
    private Long creater;

    /**
     * 创建人姓名
     */
    private String createrUname;

    private Boolean isDelete;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date ctime;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date mtime;
}
