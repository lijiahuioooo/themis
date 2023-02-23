package com.mfw.themis.portal.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mfw.themis.common.constant.enums.DataSourceTypeEnum;
import com.mfw.themis.common.constant.enums.EnableEnum;
import com.mfw.themis.common.exception.ServiceException;
import com.mfw.themis.common.model.ResponseResult;
import com.mfw.themis.common.model.bo.AppMetricBO;
import com.mfw.themis.common.model.bo.SuggestBO;
import com.mfw.themis.common.model.bo.user.UserAppMetricBO;
import com.mfw.themis.portal.manager.AppMetricManager;
import com.mfw.themis.portal.model.dto.NewAppMetricDTO;
import com.mfw.themis.portal.model.dto.QueryAppMetricDTO;
import com.mfw.themis.portal.model.dto.QueryAppMetricSuggestDTO;
import com.mfw.themis.portal.service.AppMetricService;
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

import java.util.List;

@RestController
@RequestMapping(value = "/portal/admin/metric/")
@Slf4j
public class AppMetricController {

    @Autowired
    private AppMetricManager appMetricManager;

    @Autowired
    private AppMetricService appMetricService;

    /**
     * 指标列表
     * @param appCode
     * @param keyword
     * @param metricType
     * @param collectType
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping(value = "list")
    public ResponseResult<IPage<UserAppMetricBO>> queryMetricPage(
            @RequestParam(value = "appCode", defaultValue = "") String appCode,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "metricType", required = false) Integer metricType,
            @RequestParam(value = "collectType", required = false) Integer collectType,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        QueryAppMetricDTO queryAppMetricDTO = new QueryAppMetricDTO();
        queryAppMetricDTO.setPage(page);
        queryAppMetricDTO.setPageSize(pageSize);

        if(StringUtils.isNotEmpty(appCode)){
            queryAppMetricDTO.setAppCode(appCode);
        }

        if(StringUtils.isNotEmpty(keyword)){
            if(keyword.matches("^[0-9]*$")){
                queryAppMetricDTO.setAppMetricId(Long.valueOf(keyword));
            }else{
                queryAppMetricDTO.setMetricName(keyword);
            }
        }

        if (null != metricType) {
            queryAppMetricDTO.setMetricType(metricType);
        }

        if (null != collectType) {
            queryAppMetricDTO.setCollectType(collectType);
        }

        return ResponseResult.OK(appMetricService.userQueryAppMetricByParam(queryAppMetricDTO));
    }

    /**
     * 指标详情
     * @param appMetricId
     * @return
     */
    @GetMapping(value = "detail")
    public ResponseResult<AppMetricBO> metricDetail(Long appMetricId){
        return ResponseResult.OK(appMetricService.appMetricDetail(appMetricId));
    }

    /**
     * 指标创建
     * @param newAppMetricDTO
     * @return
     */
    @PostMapping("/save")
    public ResponseResult<Long> add(@RequestBody @Validated NewAppMetricDTO newAppMetricDTO) {
        return ResponseResult.OK(appMetricManager.save(newAppMetricDTO));
    }

    /**
     * 选择指标
     * @param appCode
     * @param sourceType
     * @param metricType
     * @param keyword
     * @return
     */
    @GetMapping(value = "sug")
    public ResponseResult<List<SuggestBO>> appMetricSuggest(
            @RequestParam(value = "appCode", defaultValue = "") String appCode,
            @RequestParam(value = "sourceType", defaultValue = "0") Integer sourceType,
            @RequestParam(value = "metricType", defaultValue = "0") Integer metricType,
            @RequestParam(value = "keyword", required = false) String keyword) {


        if(sourceType == 0 || null == DataSourceTypeEnum.getByCode(sourceType)){
            throw new ServiceException("sourceType不能为空");
        }

        if(StringUtils.isBlank(appCode)){
            throw new ServiceException("appCode不能为空");
        }

        List<SuggestBO> suggestBOList = appMetricManager.appMetricSuggest(appCode, sourceType, metricType, keyword);

        return ResponseResult.OK(suggestBOList);
    }

    /**
     * 报警规则关联指标
     * @param appCode
     * @param sourceType
     * @param metricType
     * @param keyword
     * @return
     */
    @GetMapping(value = "relation")
    public ResponseResult<List<SuggestBO>> appMetricRelate(
            @RequestParam(value = "appCode", defaultValue = "") String appCode,
            @RequestParam(value = "sourceType", defaultValue = "0") Integer sourceType,
            @RequestParam(value = "metricType", defaultValue = "0") Integer metricType,
            @RequestParam(value = "keyword", required = false) String keyword) {

        if(StringUtils.isBlank(appCode)){
            throw new ServiceException("appCode不能为空");
        }

        QueryAppMetricSuggestDTO appMetricSuggestDTO = new QueryAppMetricSuggestDTO();
        appMetricSuggestDTO.setAppCode(appCode);
        appMetricSuggestDTO.setKeyword(keyword);

        List<SuggestBO> suggestBOList = appMetricService.queryAppMetricSuggestByParam(appMetricSuggestDTO);

        return ResponseResult.OK(suggestBOList);
    }

    /**
     * 指标启用
     * @param appMetricId
     * @return
     */
    @PostMapping(value = "enable")
    public ResponseResult<Integer> enable(Long  appMetricId) {
        ResponseResult<Integer> result = ResponseResult.OK(appMetricService.changeStatus(
                appMetricId, EnableEnum.ENABLE.getCode()));
        return result;
    }

    /**
     * 指标禁用
     * @param appMetricId
     * @return
     */
    @PostMapping(value = "disable")
    public ResponseResult<Integer> disable(Long  appMetricId) {
        ResponseResult<Integer> result = ResponseResult.OK(appMetricService.changeStatus(
                appMetricId, EnableEnum.DISABLE.getCode()));
        return result;
    }

    /**
     * 指标禁用
     * @param appMetricId
     * @return
     */
    @PostMapping(value = "update_collectid")
    public ResponseResult<Integer> update(Long  appMetricId, Integer collectId) {
        ResponseResult<Integer> result = ResponseResult.OK(appMetricService.updateCollectId(
                appMetricId, collectId));
        return result;
    }

    /**
     * 指标删除
     * @param appMetricId
     * @return
     */
    @PostMapping(value = "delete")
    public ResponseResult<Integer> delete(Long  appMetricId) {
        ResponseResult<Integer> result = ResponseResult.OK(appMetricService.delete(appMetricId));
        return result;
    }
}
