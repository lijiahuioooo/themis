package com.mfw.themis.dependent.aosapp.model;

import lombok.Data;

import java.util.List;

/**
 * 获取appCode部门名称
 * @see <a href="https://yapi.mfwdev.com/project/841/interface/api/16616">通过 appcode 获取应用节点信息</a>
 * @author wenhong
 */
@Data
public class DepartmentResponse {

    private List<Chain> chain;

    @Data
    public static class Chain {
        private Long id;
        private String title;
    }

    /**
     * 获取用户uids
     * @return
     */
    public String getDepartmentName(){
        String buisName = "";

        if(null == chain || chain.size() <= 0){
            return buisName;
        }

        return chain.get(0).getTitle();
    }
}
