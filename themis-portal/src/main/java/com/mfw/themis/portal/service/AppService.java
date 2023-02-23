package com.mfw.themis.portal.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mfw.themis.common.model.bo.admin.AppSuggestBO;
import com.mfw.themis.common.model.dto.AppDTO;
import com.mfw.themis.dao.po.AppPO;
import com.mfw.themis.portal.model.dto.QueryAppDTO;

/**
 * @author wenhong
 */
public interface AppService {

    /**
     * 应用列表查询
     * @param req
     * @return
     */
    IPage<AppDTO> queryAppPage(QueryAppDTO req);

    /**
     * 应用列表查询
     * @param req
     * @return
     */
    IPage<AppSuggestBO> queryAppSuggestPage(QueryAppDTO req);

    /**
     * 根据id获取应用
     * @param id
     * @return
     */
    AppDTO queryById(Long id);

    /**
     * 根据appCode获取应用
     * @param appCode
     * @return
     */
    AppDTO queryByAppCode(String appCode);

    /**
     * 更新App应用
     * @param appDTO
     * @return
     */
    Boolean update(AppDTO appDTO);

    /**
     * 服务开通
     * @param appDTO
     * @return
     */
    Long open(AppDTO appDTO);

    /**
     * 服务关闭
     * @param appDTO
     * @return
     */
    Long close(AppDTO appDTO);

}
