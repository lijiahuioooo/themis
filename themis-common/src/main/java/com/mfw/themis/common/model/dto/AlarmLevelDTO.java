package com.mfw.themis.common.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mfw.themis.common.model.AlertRate;
import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liuqi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmLevelDTO {

    private Long id;

    @NotNull
    private String level;

    @NotNull
    private String alarmChannels;

    @NotNull
    private String alarmScope;

    @NotNull
    private AlertRate alarmRate;

    /**
     * 删除
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
