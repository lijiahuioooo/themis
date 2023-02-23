package com.mfw.themis.common.model.dto;

import com.mfw.themis.common.constant.enums.EnableEnum;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wenhong
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppMetricDTO {

    private Long id;

    @NotNull(message = "appId不能为空")
    private Long appId;

    @NotNull(message = "metricId不能为空")
    private Long metricId;

    /**
     * 复合指标对应关系
     */
    private List<CompositeAppMetricDTO> compositeAppMetricList;

    /**
     * 数据上报ID
     */
    private Integer collectId;

    @NotNull(message = "datasourceId不能为空")
    private Long datasourceId;

    private EnableEnum status;

    private Boolean isDelete;

    private Map<String,String> attrValue;

    /**
     * 创建时间
     */
    private Date ctime;
    /**
     * 更新时间
     */
    private Date mtime;
}
