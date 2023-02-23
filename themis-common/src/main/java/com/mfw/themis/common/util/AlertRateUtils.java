package com.mfw.themis.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.mfw.themis.common.constant.enums.AlertRateTypeEnum;
import com.mfw.themis.common.model.AlertRate;
import com.mfw.themis.dao.po.AlarmRecordPO;
import java.util.Date;
import java.util.Map;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.StringUtils;

/**
 * 报警频率解析
 *
 * @author liuqi
 */
public class AlertRateUtils {

    private static final String RATE_TYPE = "rateType";
    private static final String RATE_TIMES = "rateTimes";
    private static final String RATE_INTERVAL = "rateInterval";

    public static AlertRate parseAlertRate(String rateStr) {
        if (StringUtils.isEmpty(rateStr)) {
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(rateStr);
        return AlertRate.builder()
                .rateType(AlertRateTypeEnum.getByCode(jsonObject.getIntValue(RATE_TYPE)))
                .rateTimes(jsonObject.getInteger(RATE_TIMES))
                .rateInterval(jsonObject.getIntValue(RATE_INTERVAL)).build();
    }

    public static String toString(AlertRate alertRate) {
        if (alertRate == null) {
            return null;
        }
        Map<String, Object> map = Maps.newHashMap();
        map.put(RATE_TYPE, alertRate.getRateType().getCode());
        map.put(RATE_TIMES, alertRate.getRateTimes());
        map.put(RATE_INTERVAL, alertRate.getRateInterval());
        return JSON.toJSONString(map);
    }

    /**
     * 是否达到报警下发的条件
     *
     * @param record
     * @param alertRate
     * @param alertTime
     * @return
     */
    public static boolean canAlert(AlarmRecordPO record, AlertRate alertRate, Date alertTime) {
        if (alertRate == null) {
            return false;
        }
        if (record.getAlertTimes() == null || record.getLastAlertTime() == null) {
            return true;
        }
        switch (alertRate.getRateType()) {
            case EVERY_TIME:
                return true;
            case FIXED_INTERVAL:
                return alertTime.after(DateUtils.addMinutes(record.getLastAlertTime(), alertRate.getRateInterval()));
            case FIXED_TIMES:
                return alertRate.getRateTimes() > record.getAlertTimes() && alertTime
                        .after(DateUtils.addMinutes(record.getLastAlertTime(), alertRate.getRateInterval()));
            case INCREASE_INTERVAL:
                // 5min 10min 15min 30min 30min
                if (record.getAlertTimes() < 3) {
                    return alertTime.after(DateUtils.addMinutes(
                            record.getLastAlertTime(), 5 * (record.getAlertTimes() + 1)));
                }

                return alertTime.after(DateUtils.addMinutes(record.getLastAlertTime(), 30));
            default:
                return false;
        }
    }
}
