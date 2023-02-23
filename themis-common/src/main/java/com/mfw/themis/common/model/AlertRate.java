package com.mfw.themis.common.model;

import com.mfw.themis.common.constant.enums.AlertRateTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 报警频率
 *
 * @author liuqi
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlertRate {

    private AlertRateTypeEnum rateType;
    private int rateTimes;
    private int rateInterval;
}
