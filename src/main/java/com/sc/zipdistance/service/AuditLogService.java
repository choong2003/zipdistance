package com.sc.zipdistance.service;

import java.time.LocalDateTime;
import java.util.UUID;

import com.sc.zipdistance.model.entity.AuditLog;
import com.sc.zipdistance.model.entity.User;
import com.sc.zipdistance.repository.AuditLogRepository;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private static final Logger logger = LogManager.getLogger(AuditLogService.class);
    @Autowired
    private AuditLogRepository auditLogRepository;
    @Value("${audit.logging.db}")
    private boolean logToDb;
    @Value("${audit.logging.file}")
    private boolean logToFile;
    @Autowired
    private EntityManager entityManager;

    public void log(String action, String description) {
        log(action, null, description);
    }

    public void log(String action, UUID userId, String description) {
        //to log into file if there is null userid
        if (userId == null) {
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            logger.info("Action: {}, User: {}, Details: {}", action, userName, description);
            return;
        }
        if (logToDb) {
            logToDatabase(action, userId, description);
        }
        if (logToFile) {
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            logger.info("Action: {}, User: {}, Details: {}", action, userName, description);
        }
    }

    private void logToDatabase(String action, UUID userId, String description) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        // Create proxy for User entity without fetching from DB
        User userProxy = entityManager.getReference(User.class, userId);
        log.setUser(userProxy);
        log.setDescription(description);
        log.setLastUpdated(LocalDateTime.now());
        auditLogRepository.save(log);
    }

    private void logToDatabase(String action, User user, String description) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setUser(user);
        log.setDescription(description);
        log.setLastUpdated(LocalDateTime.now());
        auditLogRepository.save(log);
    }

}
