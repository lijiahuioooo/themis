package com.mfw.themis.portal.controller;

import com.alibaba.fastjson.JSON;
import com.mfw.themis.common.model.ResponseResult;
import com.mfw.themis.common.util.DateFormatUtils;
import com.mfw.themis.dao.mapper.AlarmDataSourceDao;
import com.mfw.themis.dao.po.AlarmDataSourcePO;
import com.mfw.themis.dependent.exception.DependentCommunicateException;
import com.mfw.themis.dependent.prometheus.PrometheusClient;
import com.mfw.themis.dependent.prometheus.model.PrometheusRequest;
import com.mfw.themis.dependent.prometheus.model.PrometheusResponse.PrometheusResponseData;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuqi
 */
@RestController
@RequestMapping(value = "/portal/datasource/")
public class DataSourceQueryController {

    @Autowired
    private AlarmDataSourceDao dataSourceDao;
    @Autowired
    private PrometheusClient prometheusClient;

    @GetMapping("/queryPrometheus")
    public ResponseResult queryByDataSource(Long id, String q, String start, String end)
            throws DependentCommunicateException, ParseException {
        AlarmDataSourcePO dataSource = dataSourceDao.selectById(id);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PrometheusRequest request = PrometheusRequest.builder()
                .query(q)
                .start(DateFormatUtils
                        .formatToRfc3339(df.parse(start)))
                .end(DateFormatUtils
                        .formatToRfc3339(df.parse(end)))
                .build();
        PrometheusResponseData data = prometheusClient.query(dataSource.getAddress(), request);
        return ResponseResult.OK(data);

    }

    @GetMapping(value = "/")
    public ResponseResult<String> index() {
        return ResponseResult.OK("OK");
    }
}
