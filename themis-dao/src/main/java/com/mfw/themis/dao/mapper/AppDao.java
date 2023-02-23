package com.mfw.themis.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mfw.themis.dao.po.AppPO;
import com.mfw.themis.dao.po.union.QueryAppPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * app 应用
 * @author wenhong
 */
public interface AppDao extends BaseMapper<AppPO> {

    /**
     * 应用列表
     * @param page
     * @param param
     * @return
     */
    IPage<AppPO> selectAppPage(Page<AppPO> page, @Param("param") QueryAppPO param);

    /**
     * 应用列表
     * @param page
     * @param param
     * @return
     */
    IPage<AppPO> selectAppSuggestPage(Page<AppPO> page, @Param("param") QueryAppPO param);
}
