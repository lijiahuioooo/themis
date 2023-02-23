package com.mfw.themis.portal.config;

import com.mfw.themis.common.constant.enums.DBTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = -100)
@Slf4j
@Aspect
@MapperScan("com.mfw.themis.dao.mapper.*")
public class DataSourceSwitchAspect {

    /**
     * 拦截不包含com.mfw.themis.dao.mapper子包类的所有方法
     */
    @Pointcut("execution(* com.mfw.themis.dao.mapper.*.*(..))")
    private void db1Aspect() {
    }

    /**
     * 拦截com.mfw.themis.dao.mapper.mes包及其子包类的所有方法
     */
    @Pointcut("execution(* com.mfw.themis.dao.mapper.mes..*.*(..))")
    private void db2Aspect() {
    }

    @Before("db1Aspect()")
    public void db1() {
        log.debug("切换到themis_alarm 数据源...");
        DbContextHolder.setDbType(DBTypeEnum.THEMIS_ALARM);
    }

    @Before("db2Aspect()")
    public void db2() {
        log.debug("切换到traffic 数据源...");
        DbContextHolder.setDbType(DBTypeEnum.TRAFFIC_ALARM);
    }

}