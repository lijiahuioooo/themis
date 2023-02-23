package com.mfw.themis.common.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mfw.themis.common.constant.enums.CompareTypeEnum;
import com.mfw.themis.common.constant.enums.EnableEnum;
import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liuqi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmRuleDTO {

    private Long id;

    @NotNull(message = "应用id不能为空 ")
    private Long appId;

    /**
     * 应用指标关系表关联id
     */
    @NotNull(message = "指标不能为空")
    private Long appMetricId;
    /**
     * 规则名称
     */
    @NotNull(message = "规则名称不能为空")
    private String ruleName;
    /**
     * 比较方式 （大于、小于等）
     */
    @NotNull(message = "比较方式不能为空")
    private CompareTypeEnum compare;
    /**
     * 阈值
     */
    @NotNull(message = "阈值不能为空")
    private String threshold;
    /**
     * 生效起始时间
     */
    private String startEffectiveTime;
    /**
     * 生效结束时间
     */
    private String endEffectiveTime;
    /**
     * 是否一直生效
     */
    private Boolean alwaysEffective;
    /**
     * 连续命中次数
     */
    private Integer continuousHitTimes;
    /**
     * 报警等级
     */
    @NotNull(message = "报警等级不能为空")
    private Long alarmLevelId;
    /**
     * 报警文案
     */
    private String alarmContent;
    /**
     * 规则报警联系人
     */
    private String contacts;

    /**
     * 指标名称
     */
    private String metricName;

    /**
     * 规则状态 1-启用 2-禁用
     */
    private EnableEnum status;

    /**
     * 创建人
     */
    private Long creater;
    /**
     * 操作人
     */
    private Long operator;
    /**
     * 删除
     */
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
