package com.mfw.themis.portal.controller.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mfw.themis.common.constant.enums.DataSourceTypeEnum;
import com.mfw.themis.common.exception.ServiceException;
import com.mfw.themis.common.model.ResponseResult;
import com.mfw.themis.common.model.bo.AppMetricBO;
import com.mfw.themis.common.model.bo.SuggestBO;
import com.mfw.themis.common.model.bo.user.UserAppMetricBO;
import com.mfw.themis.dao.mapper.AppMetricDao;
import com.mfw.themis.portal.controller.model.GrafanaDashboardDTO;
import com.mfw.themis.portal.manager.AppMetricManager;
import com.mfw.themis.portal.model.dto.NewAppMetricDTO;
import com.mfw.themis.portal.model.dto.QueryAppMetricDTO;
import com.mfw.themis.portal.model.dto.QueryAppMetricSuggestDTO;
import com.mfw.themis.portal.service.AppMetricService;
import java.util.Map;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/portal/user/metric/")
@Slf4j
public class UserAppMetricController {

    @Autowired
    private AppMetricService appMetricService;

    @Autowired
    private AppMetricManager appMetricManager;

    @Autowired
    private AppMetricDao appMetricDao;

    @Value("${app.env}")
    private String appEnv;

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

        if(StringUtils.isBlank(appCode)){
            throw new ServiceException("appCode不能为空");
        }
        queryAppMetricDTO.setAppCode(appCode);

        if(StringUtils.isNotBlank(keyword)){
            if(keyword.matches("^[0-9]*$")){
                queryAppMetricDTO.setAppMetricId(Long.valueOf(keyword));
            }else{
                queryAppMetricDTO.setName(keyword);
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
     * 指标模板详情
     * @param appMetricId
     * @return
     */
    @GetMapping(value = "tpl_detail")
    public ResponseResult<AppMetricBO> metricTplDetail(Long appMetricId, Integer sourceType){
        return ResponseResult.OK(appMetricService.appMetricTplDetail(appMetricId, sourceType));
    }

    /**
     * 指标保存
     * @param newAppMetricDTO
     * @return
     */
    @PostMapping("/save")
    public ResponseResult<Long> create(@RequestBody @Validated NewAppMetricDTO newAppMetricDTO) {
        if(null != newAppMetricDTO.getSourceType()
                && newAppMetricDTO.getSourceType().equals(DataSourceTypeEnum.PROMETHEUS.getCode())){
            // 用户强制指定某个数据源
            newAppMetricDTO.setDatasourceId(appEnv.equals("prod") ? 2L : 19L);
        }

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
     * 获取grafana看板url
     * @param appCode
     * @return
     */
    @GetMapping(value = "/grafana/dashboard")
    public ResponseResult<Map<String, String>> grafanaDashboard(String appCode){

        return ResponseResult.OK(appMetricService.grafanaDashboardUrl(appCode));
    }

    /**
     * 自定义业务指标一键生成到grafana看板
     * @param dto
     * @return
     */
    @PostMapping("/grafana/create")
    public ResponseResult<Boolean> createGrafanaDashboard(@RequestBody @Valid GrafanaDashboardDTO dto) {
        return ResponseResult.OK(appMetricService.createGrafanaDashboard(dto.getAppCode()));
    }

    /**
     * 移除grafana看板
     * @param dto
     * @return
     */
    @PostMapping("/grafana/remove")
    public ResponseResult<Boolean> removeGrafanaDashboard(@RequestBody @Valid GrafanaDashboardDTO dto) {
        return ResponseResult.OK(appMetricService.removeGrafanaDashboard(dto.getAppCode()));
    }
}
