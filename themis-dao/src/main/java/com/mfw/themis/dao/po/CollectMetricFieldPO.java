package com.mfw.themis.dao.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author wenhong
 */
@TableName("t_collect_metric_field")
@Data
public class CollectMetricFieldPO {

    private Integer id;

    private String appCode;
    private String metric;
    private String description;
    private String tags;
    private String fields;
    private Integer status;
    private Long operator;
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
