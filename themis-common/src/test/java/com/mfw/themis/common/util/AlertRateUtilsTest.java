package com.mfw.themis.common.util;

import com.mfw.themis.common.constant.enums.AlertRateTypeEnum;
import com.mfw.themis.common.model.AlertRate;
import com.mfw.themis.dao.po.AlarmRecordPO;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author wenhong
 */
public class AlertRateUtilsTest {

    private Date nowDate = null;
    private String dateFormat = "yyyy-MM-dd HH:mm:ss";

    @Before
    public void initNowDate() throws Exception{
        String nowTime = "2020-09-11 10:00:00";
        this.nowDate = new SimpleDateFormat(this.dateFormat).parse(nowTime);
    }

    @Test
    public void everytime() throws Exception{
        // set record
        AlarmRecordPO record = new AlarmRecordPO();
        String lastAlertTime = "2020-09-11 10:00:00";
        Date dateLastAlertTime = new SimpleDateFormat(dateFormat).parse(lastAlertTime);
        record.setLastAlertTime(dateLastAlertTime);
        record.setAlertTimes(0);

        // set alertRate
        AlertRate alertRate = new AlertRate();
        alertRate.setRateTimes(1);
        alertRate.setRateInterval(1);
        alertRate.setRateType(AlertRateTypeEnum.EVERY_TIME);

        assertTrue(AlertRateUtils.canAlert(record, alertRate, this.nowDate));

        lastAlertTime = "2020-09-11 10:00:10";
        dateLastAlertTime = new SimpleDateFormat(dateFormat).parse(lastAlertTime);
        record.setLastAlertTime(dateLastAlertTime);
        record.setAlertTimes(1);
        assertTrue(AlertRateUtils.canAlert(record, alertRate, this.nowDate));
    }

    @Test
    public void fixedInterval() throws Exception{
        // set record
        AlarmRecordPO record = new AlarmRecordPO();
        String lastAlertTime = "2020-09-11 09:58:00";
        Date dateLastAlertTime = new SimpleDateFormat(dateFormat).parse(lastAlertTime);
        record.setLastAlertTime(dateLastAlertTime);
        record.setAlertTimes(0);

        // set alertRate
        AlertRate alertRate = new AlertRate();
        alertRate.setRateTimes(1);
        alertRate.setRateInterval(1);
        alertRate.setRateType(AlertRateTypeEnum.FIXED_INTERVAL);

        assertTrue("应该触发报警", AlertRateUtils.canAlert(record, alertRate, this.nowDate));

        lastAlertTime = "2020-09-11 09:59:05";
        dateLastAlertTime = new SimpleDateFormat(dateFormat).parse(lastAlertTime);
        record.setLastAlertTime(dateLastAlertTime);
        record.setAlertTimes(1);

        assertFalse("不应该触发报警", AlertRateUtils.canAlert(record, alertRate, this.nowDate));
    }

    @Test
    public void fixedTimes() throws Exception{
        // set record
        AlarmRecordPO record = new AlarmRecordPO();
        String lastAlertTime = "2020-09-11 09:58:00";
        Date dateLastAlertTime = new SimpleDateFormat(dateFormat).parse(lastAlertTime);
        record.setLastAlertTime(dateLastAlertTime);
        record.setAlertTimes(0);

        // set alertRate
        AlertRate alertRate = new AlertRate();
        alertRate.setRateTimes(2);
        alertRate.setRateInterval(1);
        alertRate.setRateType(AlertRateTypeEnum.FIXED_TIMES);

        assertTrue("应该触发报警", AlertRateUtils.canAlert(record, alertRate, this.nowDate));

        // set record
        record.setAlertTimes(1);
        assertTrue("应该触发报警", AlertRateUtils.canAlert(record, alertRate, this.nowDate));

        // set record
        record.setAlertTimes(2);
        assertFalse("不应该触发报警", AlertRateUtils.canAlert(record, alertRate, this.nowDate));
    }

    @Test
    public void increaseInterval() throws Exception {
        // set record
        AlarmRecordPO record = new AlarmRecordPO();
        String lastAlertTime = "2020-09-11 09:56:00";
        Date dateLastAlertTime = new SimpleDateFormat(dateFormat).parse(lastAlertTime);
        record.setLastAlertTime(dateLastAlertTime);
        record.setAlertTimes(0);

        // set alertRate
        AlertRate alertRate = new AlertRate();
        alertRate.setRateTimes(0);
        alertRate.setRateInterval(0);


        alertRate.setRateType(AlertRateTypeEnum.INCREASE_INTERVAL);

        // 5min
        assertFalse("不应该触发报警", AlertRateUtils.canAlert(record, alertRate, this.nowDate));

        // set nowDate
        String nowDateStr = "2020-09-11 10:01:01";
        Date curDate = new SimpleDateFormat(this.dateFormat).parse(nowDateStr);
        assertTrue("应该触发报警", AlertRateUtils.canAlert(record, alertRate, curDate));

        // 10min
        // set record
        lastAlertTime = "2020-09-11 10:00:00";
        dateLastAlertTime = new SimpleDateFormat(dateFormat).parse(lastAlertTime);
        record.setLastAlertTime(dateLastAlertTime);
        record.setAlertTimes(1);

        nowDateStr = "2020-09-11 10:09:00";
        curDate = new SimpleDateFormat(this.dateFormat).parse(nowDateStr);
        assertFalse("不应该触发报警", AlertRateUtils.canAlert(record, alertRate, curDate));

        nowDateStr = "2020-09-11 10:10:01";
        curDate = new SimpleDateFormat(this.dateFormat).parse(nowDateStr);
        assertTrue("应该触发报警", AlertRateUtils.canAlert(record, alertRate, curDate));

        // 15min
        // set record
        lastAlertTime = "2020-09-11 10:00:00";
        dateLastAlertTime = new SimpleDateFormat(dateFormat).parse(lastAlertTime);
        record.setLastAlertTime(dateLastAlertTime);
        record.setAlertTimes(2);

        nowDateStr = "2020-09-11 10:15:01";
        curDate = new SimpleDateFormat(this.dateFormat).parse(nowDateStr);
        assertTrue("应该触发报警", AlertRateUtils.canAlert(record, alertRate, curDate));

        // 30min
        lastAlertTime = "2020-09-11 10:00:00";
        dateLastAlertTime = new SimpleDateFormat(dateFormat).parse(lastAlertTime);
        record.setLastAlertTime(dateLastAlertTime);
        record.setAlertTimes(3);

        nowDateStr = "2020-09-11 10:25:01";
        curDate = new SimpleDateFormat(this.dateFormat).parse(nowDateStr);
        assertFalse("不应该触发报警", AlertRateUtils.canAlert(record, alertRate, curDate));

        nowDateStr = "2020-09-11 10:30:01";
        curDate = new SimpleDateFormat(this.dateFormat).parse(nowDateStr);
        assertTrue("应该触发报警", AlertRateUtils.canAlert(record, alertRate, curDate));

        // 30min
        lastAlertTime = "2020-09-11 10:00:00";
        dateLastAlertTime = new SimpleDateFormat(dateFormat).parse(lastAlertTime);
        record.setLastAlertTime(dateLastAlertTime);
        record.setAlertTimes(4);

        nowDateStr = "2020-09-11 10:25:01";
        curDate = new SimpleDateFormat(this.dateFormat).parse(nowDateStr);
        assertFalse("不应该触发报警", AlertRateUtils.canAlert(record, alertRate, curDate));

        nowDateStr = "2020-09-11 10:30:01";
        curDate = new SimpleDateFormat(this.dateFormat).parse(nowDateStr);
        assertTrue("应该触发报警", AlertRateUtils.canAlert(record, alertRate, curDate));
    }
}
