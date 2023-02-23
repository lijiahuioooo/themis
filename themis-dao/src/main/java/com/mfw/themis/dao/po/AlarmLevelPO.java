package com.mfw.themis.dao.po;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 报警等级
 * @author liuqi
 */
@TableName("t_alarm_level")
@Data
public class AlarmLevelPO {
    private Long id;
    private String level;
    private String alarmChannels;
    private String alarmScope;
    private String alarmRate;
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
