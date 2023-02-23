package com.mfw.themis.metric.event;

import com.mfw.themis.common.model.dto.AlarmRuleDTO;
import com.mfw.themis.common.model.dto.AppMetricDTO;
import com.mfw.themis.metric.model.MetricExecuteResult;
import java.util.Date;
import java.util.List;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

/**
 * 指标执行事件
 *
 * @author liuqi
 */
@Data
public class MetricExecuteEvent extends ApplicationEvent {

    /**
     * 单次执行指标结果
     */
    private MetricExecuteResult result;
    /**
     * 连续执行指标结果
     */
    private List<MetricExecuteResult> resultList;
    /**
     * 指标规则
     */
    private AlarmRuleDTO alarmRule;
    private Date executeTime;

    public MetricExecuteEvent() {
        super(new AppMetricDTO());
    }

    public MetricExecuteEvent(AppMetricDTO appMetric, AlarmRuleDTO alarmRule, MetricExecuteResult result,
            List<MetricExecuteResult> resultList,
            Date executeTime) {
        super(appMetric);
        this.result = result;
        this.alarmRule = alarmRule;
        this.resultList = resultList;
        this.executeTime = executeTime;
    }
}
