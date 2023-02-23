package com.mfw.themis.common.model.dto;

import com.mfw.themis.common.constant.enums.CompareTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author guosp
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmRuleTemplateDTO {

    /**
     * 指标id
     */
    private Long metricId;

    /**
     * 规则名称
     */
    private String ruleName;
    /**
     * 比较方式 （大于、小于等）
     */
    private Integer compare;
    /**
     * 阈值
     */
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
    private Long alarmLevelId;
    /**
     * 报警文案
     */
    private String alarmContent;
    /**
     * 规则报警联系人
     */
    private String contacts;

}
