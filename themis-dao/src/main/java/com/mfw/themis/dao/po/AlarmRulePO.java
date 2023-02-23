package com.mfw.themis.dao.po;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @author liuqi
 */
@TableName("t_alarm_rule")
@Data
public class AlarmRulePO {

    private Long id;

    private Long appId;
    /**
     * 应用指标关系表关联id
     */
    private Long appMetricId;
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

    /**
     * 规则状态 1-启用 2-禁用
     */
    private Integer status;


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
    private Date ctime;
    /**
     * 更新时间
     */
    private Date mtime;
}
