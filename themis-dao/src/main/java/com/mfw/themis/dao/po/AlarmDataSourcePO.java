package com.mfw.themis.dao.po;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 挂载数据源
 *
 * @author liuqi
 */
@TableName("t_datasource")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmDataSourcePO {

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
    private Integer type;
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
    private Date ctime;
    /**
     * 更新时间
     */
    private Date mtime;

}
