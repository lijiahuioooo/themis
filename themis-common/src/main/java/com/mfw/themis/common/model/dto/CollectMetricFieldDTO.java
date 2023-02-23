package com.mfw.themis.common.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mfw.themis.common.constant.enums.EnableEnum;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author wenhong
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollectMetricFieldDTO {

    private Integer id;

    @NotNull(message = "appCode不能为空")
    private String appCode;

    @NotNull(message = "metric不能为空")
    private String metric;

    private String description;

    private EnableEnum status;

    /**
     * 操作人
     */
    private Long operator;

    /**
     * 操作人姓名
     */
    private String operatorUname;

    private Boolean isDelete;

    @NotNull(message = "tags不能为空")
    @Size(min = 1, message = "tags不能为空")
    private List<Field> tags;

    private List<Field> fields;

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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Field {
        private String metric;
        private String type;
        private String desc;
    }
}
