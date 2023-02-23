package com.mfw.themis.dao.po;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liuqi
 */
@TableName("t_alarm_metric")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmMetricPO {

    private Long id;
    private String name;
    private String expression;
    private String description;
    private Integer timeWindowType;
    private String timeWindowOffset;
    private Integer timeWindow;
    private String customTimeWindow;
    private Integer sourceType;
    private Integer metricType;
    private Integer collectType;
    private String metricTag;
    private String groupField;
    private Integer groupType;
    private String compositeMetricExpression;
    private Integer metricUnit;
    private String formula;
    private Long operator;

    private String extData;

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
