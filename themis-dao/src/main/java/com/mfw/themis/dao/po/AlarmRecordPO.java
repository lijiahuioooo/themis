package com.mfw.themis.dao.po;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @author liuqi
 */
@TableName("t_alarm_record")
@Data
public class AlarmRecordPO {

    private Long id;
    private Long appId;
    private Long ruleId;
    private Date startTime;
    private Date endTime;
    private Integer status;
    private String endPoint;
    private String level;
    private Integer alertTimes;
    private Date lastAlertTime;
    private String receivers;
    private String content;
    /**
     * 创建时间
     */
    private Date ctime;
    /**
     * 更新时间
     */
    private Date mtime;
}
