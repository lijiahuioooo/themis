package com.mfw.themis.common.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * 将enum转换为前端可用的筛选列表
 * @author wenhong
 */
@Slf4j
public class EnumUtils {

    public static <T> List<Map<String, Object>> enumToMapList(
            Class<T> enumT, String methodIdName, String methodValueName){

        List<Map<String, Object>> enumList = new ArrayList<>();

        T[] enums = enumT.getEnumConstants();
        if(enums == null || enums.length <= 0){
            return enumList;
        }

        for(int i=0; i<enums.length; i++){
            T obj = enums[i];

            Map<String, Object> enumMap = new HashMap<>();
            try{
                Method method = obj.getClass().getMethod(methodIdName);
                if(null != method){
                    enumMap.put("id", method.invoke(obj));
                }

               method = obj.getClass().getMethod(methodValueName);
                if(null != method){
                    enumMap.put("value", method.invoke(obj));
                }

                if(enumMap.size() > 0){
                    enumList.add(enumMap);
                }
            }catch (Exception e){
                log.error("enumToMapList error, {}", e);
            }
        }

        return enumList;
    }

}
