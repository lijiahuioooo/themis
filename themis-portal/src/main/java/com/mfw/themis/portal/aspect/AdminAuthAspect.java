package com.mfw.themis.portal.aspect;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * admin 后台权限校验
 * @author wenhong
 */
@Slf4j
@Aspect
@Component
public class AdminAuthAspect {

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PostMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void controllerMethodPointcut() {}


    @Around("controllerMethodPointcut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable{
        // 获取参数
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();


        Cookie[] cookies = request.getCookies();
        if(null == cookies){
            // todo no permission
        }else{
            String token = "";

            for (Cookie cookie : cookies) {
                if (StringUtils.equals("X-Token", cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }

            // 校验token是否合法 todo

            // 校验权限 todo
        }

        // 执行目标方法
        Object result = pjp.proceed();

        return result;
    }
}
