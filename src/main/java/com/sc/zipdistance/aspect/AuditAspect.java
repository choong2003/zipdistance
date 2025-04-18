package com.sc.zipdistance.aspect;

import java.util.UUID;

import com.sc.zipdistance.model.entity.CustomUserDetails;
import com.sc.zipdistance.service.AuditLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@ConditionalOnProperty(name = "audit.aspect.enabled", havingValue = "true")
public class AuditAspect {

    @Autowired
    private AuditLogService auditLogService;

    @Pointcut("execution(* com.enix.admin_rsvp.service.*.*(..)) && !within(com.enix.admin_rsvp.service.AuditLogService)")
    public void serviceLayer() {
    }

    @AfterReturning(pointcut = "serviceLayer()", returning = "result")
    public void logAudit(JoinPoint joinPoint, Object result) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails customUserDetails) {
            if (customUserDetails != null) {
                UUID userId = customUserDetails.getUserId();
                String description = "Executed method: " + joinPoint.getSignature();
                auditLogService.log(joinPoint.getSignature().getName(), userId, description);
            }
        }
    }

    @AfterThrowing(pointcut = "serviceLayer()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        String description = "Exception: " + ex.getMessage();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails customUserDetails) {
            if (customUserDetails != null) {
                UUID userId = customUserDetails.getUserId();
                auditLogService.log(joinPoint.getSignature().getName(), userId, description);
            } else {
                auditLogService.log(joinPoint.getSignature().getName(), null, description);
            }
        }
    }
}
