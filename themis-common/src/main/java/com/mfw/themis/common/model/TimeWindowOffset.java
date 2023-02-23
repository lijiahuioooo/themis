package com.mfw.themis.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义时间窗口
 *
 * @author wenhong
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimeWindowOffset {

    private Integer value;
    private Integer unit;
}
