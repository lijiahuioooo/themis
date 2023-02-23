package com.mfw.themis.dao.po.union;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wenhong
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryAppPO {

    /**
     * 应用id
     */
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * app来源
     */
    private String source;

    /**
     * 是否删除
     */
    private Boolean isDelete;

    /**
     * 当前页
     */
    private Long page;

    /**
     * 每页个数
     */
    private Long pageSize;
}
