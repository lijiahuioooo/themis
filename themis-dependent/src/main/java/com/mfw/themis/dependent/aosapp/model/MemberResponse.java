package com.mfw.themis.dependent.aosapp.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * 从Aos获取appCode成员列表
 * @see <a href="https://yapi.mfwdev.com/project/841/interface/api/14829">获取应用用户列表</a>
 * @author wenhong
 */
@Data
public class MemberResponse {

    private List<User> users;

    @Data
    public static class User {
        private String uid;
    }

    /**
     * 获取用户uids
     * @return
     */
    public List<String> getUids(){
        List<String> userIds = new ArrayList<>();

        if (this.users != null && this.users.size() > 0) {
            for(User user : this.users){
                userIds.add(user.uid);
            }
        }

        return userIds;
    }
}
