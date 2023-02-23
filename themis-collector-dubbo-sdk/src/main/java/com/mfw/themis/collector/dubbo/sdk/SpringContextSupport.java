package com.mfw.themis.collector.dubbo.sdk;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author liuqi
 */
public class SpringContextSupport implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextSupport.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        if (applicationContext == null) {
            return null;
        }
        return applicationContext.getBean(clazz);
    }

    public static <T> T getBeanName(Class<T> clazz,String beanName) {
        if (applicationContext == null) {
            return null;
        }
        return applicationContext.getBean(clazz,beanName);
    }

    public static Environment getEnvironment() {
        if (applicationContext == null) {
            return null;
        }
        return applicationContext.getEnvironment();
    }

    public static String getProperty(String key) {
        if (applicationContext == null) {
            return null;
        }
        return applicationContext.getEnvironment().getProperty(key);
    }
}
