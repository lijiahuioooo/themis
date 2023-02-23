package com.mfw.themis.portal.controller.model;

import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AlarmLevelReqDTO {

    private Long id;

    @NotNull
    private String level;

    @NotNull
    private String alarmChannels;

    @NotNull
    private String alarmScope;

    @NotNull
    private Integer rateType;

    private Integer rateTimes;

    private Integer rateInterval;

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
