package com.mfw.themis.common.util;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

/**
 * 指标占位符 ${appCode}...
 *
 * @author guosp
 */
@Slf4j
public class PlaceHolderUtils {

    private static final String pattern = "\\$\\{([\\w\\.]*)\\}";

    /**
     * 获取占位符列表
     *
     * @param variableTpl
     * @return
     */
    public static List<String> getPlaceHolderKeyList(String variableTpl) {
        List<String> list = new ArrayList<>();
        Matcher m = Pattern.compile(pattern).matcher(variableTpl);
        while (m.find()) {
            String group = m.group();
            group = group.replaceAll("\\$|\\{|\\}", "");

            list.add(group);
        }

        return list;
    }

    /**
     * 占位符替换
     *
     * @param variableTpl
     * @param data
     * @return
     */
    public static String replace(String variableTpl, Map<String, String> data) {
        Matcher m = Pattern.compile(pattern).matcher(variableTpl);
        while (m.find()) {
            String group = m.group();
            group = group.replaceAll("\\$|\\{|\\}", "");
            String value = "";
            if (null != data.get(group)) {
                value = String.valueOf(data.get(group));
            }
            variableTpl = variableTpl.replace(m.group(), value);
        }

        return variableTpl;
    }

    /**
     * 检测模版里的占位符是否都存在于data map中
     *
     * @param variableTpl
     * @param data
     * @return
     */
    public static Boolean checkContains(String variableTpl, Map<String, Object> data) {
        List<String> list = getPlaceHolderKeyList(variableTpl);
        for (String key : list) {
            if (data == null || !data.containsKey(key)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检测模版里的占位符是否都存在于data map中
     *
     * @param variableTpl
     * @param data
     * @return
     */
    public static Boolean checkContainsStringParams(String variableTpl, Map<String, String> data) {
        Map<String, Object> params = Maps.newHashMap();
        if(null != data) {
            params.putAll(data);
        }
        return checkContains(variableTpl, params);
    }
}
