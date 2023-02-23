package com.mfw.themis.dao.po.mes;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * AlarmRulePO
 * @author wenhong
 */
@TableName("alarm_rule")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmRulePO {
    private Long id;

    private String eventCode;

    private Integer metricType;

    private String compositeMetric;

    private String formula;

    private String metric;

    private String metricValue;

    private String filterMetric;

    private String name;

    private String tags;

    private String ruleDesc;

    private Integer timeWindow;

    private Integer type;

    private Integer compare;

    private Integer threshold;

    private Boolean isAlwaysEffective;

    private String startEffectiveTime;

    private String endEffectiveTime;

    private Integer continuousHitTimes;

    private Integer level;

    private String msgChannels;

    private Boolean isEnable;

    private Boolean isDelete;

    private String tips;

    private Integer samplingCount;

    private String samplingField;

    private Long applicationId;

    private String appCode;

    private String receivers;

    private Date ctime;

    private Date mtime;

    private String operatorId;

    private String operatorName;
}
