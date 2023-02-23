package com.mfw.themis.portal.auth;

import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author liuqi
 */
@Component
public class AdminRoleUtils {

    @Value("${themis.admin.uids}")
    private String adminUids;

    public boolean checkAdminRole(Long uid) {
        String[] adminList = StringUtils.split(adminUids, ',');
        return Arrays.stream(adminList).filter(StringUtils::isNotBlank).map(Long::valueOf).anyMatch(u -> u.equals(uid));
    }
}
