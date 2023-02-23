package com.mfw.themis.common.model.bo.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author wenhong
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAppMetricBO {

    private Long id;

    private Long appId;

    private String appCode;

    private String appName;

    /*** alarm metric name ***/
    private String name;

    /**
     * 指标分类（系统指标、应用指标、业务指标）
     */
    private Integer metricType;
    private String metricTypeDesc;

    /**
     * 收集类型（单一指标，复合指标）
     */
    private Integer collectType;
    private String collectTypeDesc;

    private Long creater;
    private String createrUname;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date ctime;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date mtime;
}
