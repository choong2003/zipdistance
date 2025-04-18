package com.sc.zipdistance.aspect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Aspect
@Component
@ConditionalOnProperty(name = "log.aspect.enabled", havingValue = "true")
public class LoggingAspect {

    private static final Logger logger = LogManager.getLogger(LoggingAspect.class);

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerLayer() {
    }

    @Before("controllerLayer()")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Entering: {} with args: {}", joinPoint.getSignature(), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "controllerLayer()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        logger.info("Exiting: {} with result: {}", joinPoint.getSignature(), result);
    }

    @AfterThrowing(pointcut = "controllerLayer()", throwing = "e")
    public void logError(JoinPoint joinPoint, Throwable e) {
        logger.error("Exception in {} with cause: {}", joinPoint.getSignature(), e.getMessage(), e);
    }
}
