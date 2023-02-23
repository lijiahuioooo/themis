package com.mfw.themis.dao.po;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @author liuqi
 */
@TableName("t_app")
@Data
public class AppPO {

    private Long id;

    private String appCode;
    private String moneAppCode;
    private String appName;
    private Integer source;
    private Integer projectType;
    private String department;
    private String description;
    private String contacts;
    private String grafanaUid;
    private Long creater;
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
