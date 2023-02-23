package com.mfw.themis.common.util;

import com.mfw.themis.common.constant.enums.CustomTimeWindowUnitEnum;
import com.mfw.themis.common.model.TimeWindowOffset;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeWindowUtils {

    /**
     * 获取偏移分钟数
     * @param timeWindowOffset
     * @return
     */
    public static int getBeforeMinutes(TimeWindowOffset timeWindowOffset){
        int beforeMinutes = 0;

        if(null != timeWindowOffset && timeWindowOffset.getValue() > 0) {
            int beforeTime = timeWindowOffset.getValue();
            CustomTimeWindowUnitEnum typeEnum = CustomTimeWindowUnitEnum
                    .getByCode(timeWindowOffset.getUnit());
            switch (typeEnum) {
                case HOUR:
                    beforeMinutes = beforeTime * 60;
                    break;
                case MINUTE:
                    beforeMinutes = beforeTime;
                    break;
                case DAY:
                    beforeMinutes = beforeTime * 60 * 24;
                    break;
                default:
                    log.error("EsAggRequest timeWindowOffset unit error");
            }
        }

       return beforeMinutes;
    }

}
