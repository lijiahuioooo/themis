package com.mfw.themis.common.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mfw.themis.common.constant.enums.DataSourceTypeEnum;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liuqi
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlarmDataSourceDTO {

    /**
     * 主键
     */
    private Long id;
    /**
     * 数据源名称
     */
    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 数据源类型
     */
    private DataSourceTypeEnum type;
    /**
     * 数据源地址
     */
    private String address;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 负责人
     */
    private String contacts;
    /**
     * 扩展数据
     */
    private String properties;
    /**
     * 是否删除
     */
    private Boolean isDelete;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date ctime;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date mtime;
}
