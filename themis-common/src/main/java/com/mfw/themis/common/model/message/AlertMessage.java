package com.mfw.themis.common.model.message;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 报警下发请求
 *
 * @author liuqi
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlertMessage {

    /**
     * 规则id
     */
    private Long ruleId;
    /**
     * 应用指标id
     */
    private Long appMetricId;

    /**
     * 节点值
     */
    private String endPoint;

    /**
     * 指标当前值
     */
    private String currentMetricValue;
    /**
     * 当前指标值列表
     */
    private List<String> currentMetricValues;
    /**
     * 报警标题
     */
    private String alertTitle;
    /**
     * 报警内容
     */
    private String alertContent;
    /**
     * 报警时间
     */
    private Date alertTime;
    /**
     * 更多内容
     */
    private String moreUrl;
}
