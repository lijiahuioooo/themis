package com.mfw.themis.portal.controller.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mfw.themis.common.constant.enums.EnableEnum;
import com.mfw.themis.common.exception.ServiceException;
import com.mfw.themis.common.model.ResponseResult;
import com.mfw.themis.common.model.dto.CollectMetricFieldDTO;
import com.mfw.themis.portal.model.dto.QueryCollectMetricFieldParam;
import com.mfw.themis.portal.service.CollectMetricFieldService;
import com.mfw.themis.portal.service.DefaultMetricService;
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

@RestController
@RequestMapping(value = "/portal/user/collect/metric/")
@Slf4j
public class UserCollectMetricFieldController {

    @Autowired
    private CollectMetricFieldService collectMetricFieldService;
    @Autowired
    private DefaultMetricService defaultMetricService;

    /**
     * 上报事件列表
     * @param appCode
     * @param keyword
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/list")
    public ResponseResult<IPage<CollectMetricFieldDTO>> queryMetricPage(
            @RequestParam(value = "appCode", required = false) String appCode,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        if(StringUtils.isBlank(appCode)){
            throw new ServiceException("appCode不能为空");
        }
        QueryCollectMetricFieldParam req = new QueryCollectMetricFieldParam();
        req.setAppCode(appCode);
        req.setPage(page);
        req.setPageSize(pageSize);

        if(StringUtils.isNotBlank(keyword)){
            req.setKeyword(keyword);
        }

        return ResponseResult.OK(collectMetricFieldService.collectMetricFieldList(req));
    }

    /**
     * 上报事件保存
     * @param collectMetricFieldDTO
     * @return
     */
    @PostMapping("/save")
    public ResponseResult<Integer> save(@RequestBody @Validated CollectMetricFieldDTO collectMetricFieldDTO) {
        if(null == collectMetricFieldDTO.getId()){
            return ResponseResult.OK(collectMetricFieldService.create(collectMetricFieldDTO));
        }

        return ResponseResult.OK(collectMetricFieldService.update(collectMetricFieldDTO));
    }

    /**
     * 启用上报事件
     * @param req
     * @return
     */
    @PostMapping(value = "/enable")
    public ResponseResult<Integer> enable(@RequestBody CollectMetricFieldDTO req) {
        return ResponseResult.OK(collectMetricFieldService.changeStatus(
                req.getId(), EnableEnum.ENABLE.getCode(), req.getOperator()));
    }

    /**
     * 禁用上报事件
     * @param req
     * @return
     */
    @PostMapping(value = "/disable")
    public ResponseResult<Integer> disable(@RequestBody CollectMetricFieldDTO req) {
        return ResponseResult.OK(collectMetricFieldService.changeStatus(
                req.getId(), EnableEnum.DISABLE.getCode(), req.getOperator()));
    }

    /**
     * 删除上报事件
     * @param req
     * @return
     */
    @PostMapping(value = "/delete")
    public ResponseResult<Integer> delete(@RequestBody CollectMetricFieldDTO req) {
        return ResponseResult.OK(collectMetricFieldService.delete(
                req.getId(), req.getOperator()));
    }

    /**
     * 为已开通服务刷新创建默认指标
     * @param
     * @return
     */
    @GetMapping(value = "/refresh")
    public ResponseResult<String> refresh(){
        defaultMetricService.refreshDefaultMetric();
        return ResponseResult.OK();
    }
}
