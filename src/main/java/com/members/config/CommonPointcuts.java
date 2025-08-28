package com.members.config;


import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CommonPointcuts {
    @Pointcut("execution(* com.members.services.*.*(..))")
    public void greetingLoggerServices(){};

    @Pointcut("execution(* com.members.controllers.*.*(..))")
    public void greetingLoggerControllers(){};
}
