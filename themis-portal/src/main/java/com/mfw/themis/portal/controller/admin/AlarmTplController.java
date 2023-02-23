package com.mfw.themis.portal.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mfw.themis.common.constant.enums.DataSourceTypeEnum;
import com.mfw.themis.common.model.ResponseResult;
import com.mfw.themis.common.model.dto.AlarmMetricDTO;
import com.mfw.themis.portal.controller.model.DeleteAlarmMetricDTO;
import com.mfw.themis.common.model.dto.SaveAlarmMetricDTO;
import com.mfw.themis.portal.model.dto.QueryAlarmMetricDTO;
import com.mfw.themis.portal.service.AlarmMetricService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 指标模板
 *
 * @author wenhong
 */
@RestController
@RequestMapping(value = "/portal/admin/alarmtpl/")
@Slf4j
public class AlarmTplController {

    @Autowired
    private AlarmMetricService alarmMetricService;

    /**
     * 指标模板列表 只有prometheus数据源是模板
     *
     * @param keyword
     * @param metricType
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping(value = "list")
    public ResponseResult<IPage<AlarmMetricDTO>> queryMetricPage(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "metricType", required = false) Integer metricType,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        QueryAlarmMetricDTO queryAlarmMetric = new QueryAlarmMetricDTO();
        queryAlarmMetric.setSourceType(DataSourceTypeEnum.PROMETHEUS.getCode());
        queryAlarmMetric.setIsDelete(false);
        queryAlarmMetric.setPage(page);
        queryAlarmMetric.setPageSize(pageSize);

        if (StringUtils.isNotEmpty(keyword)) {
            if (keyword.matches("^[0-9]*$")) {
                queryAlarmMetric.setId(Long.valueOf(keyword));
            } else {
                queryAlarmMetric.setKeyword(keyword);
            }
        }

        if (null != metricType) {
            queryAlarmMetric.setMetricType(metricType);
        }

        return ResponseResult.OK(alarmMetricService.queryMetricPage(queryAlarmMetric));
    }

    /**
     * 模板编辑创建/修改
     *
     * @param alarmMetric
     * @return
     */
    @PostMapping("/save")
    public ResponseResult<Boolean> save(@RequestBody @Validated SaveAlarmMetricDTO alarmMetric) {
        if (null != alarmMetric.getId()) {
            return ResponseResult.OK(alarmMetricService.update(alarmMetric));
        }
        SaveAlarmMetricDTO alarmMetricRes = alarmMetricService.create(alarmMetric);
        if (null == alarmMetricRes.getId()) {
            return ResponseResult.OK(false);
        }

        return ResponseResult.OK(true);
    }

    /**
     * 模板删除
     *
     * @param request
     * @return
     */
    @PostMapping(value = "delete")
    public ResponseResult<Boolean> delete(@RequestBody DeleteAlarmMetricDTO request) {
        return ResponseResult.OK(alarmMetricService.deleteTpl(request.getId(), request.getOperatorUid()));
    }
}
