package com.mfw.themis.dao.po;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @author liuqi
 */
@TableName("t_app_metric")
@Data
public class AppMetricPO {

    private Long id;
    /**
     * 应用id
     */
    private Long appId;
    /**
     * 指标id
     */
    private Long metricId;
    /**
     * 数据源id
     */
    private Long datasourceId;
    /**
     * 数据上报id
     */
    private Integer collectId;
    private Integer status;
    private Boolean isDelete;
    private String attrValue;

    /**
     * 创建人
     */
    private Long creater;
    /**
     * 创建时间
     */
    private Date ctime;
    /**
     * 更新时间
     */
    private Date mtime;
}
