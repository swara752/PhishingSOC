package com.soc.phishing.logs;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Logger Service for tracking user activities, security events, and system operations
 */
@Service
public class ActivityLogger {
    
    private static final String LOG_DIR = "logs";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public ActivityLogger() {
        // Create logs directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(LOG_DIR));
        } catch (IOException e) {
            System.err.println("Failed to create logs directory: " + e.getMessage());
        }
    }
    
    /**
     * Log user login
     */
    public void logUserLogin(String username, String ipAddress, boolean success) {
        String message = String.format(
            "[%s] LOGIN_ATTEMPT | Username: %s | IP: %s | Success: %s",
            getCurrentTimestamp(),
            username,
            ipAddress,
            success
        );
        writeLog("debug.log", message);
        
        if (!success) {
            writeLog("security.log", message);
        }
    }
    
    /**
     * Log user logout
     */
    public void logUserLogout(String username, String ipAddress) {
        String message = String.format(
            "[%s] LOGOUT | Username: %s | IP: %s",
            getCurrentTimestamp(),
            username,
            ipAddress
        );
        writeLog("debug.log", message);
    }
    
    /**
     * Log email scan
     */
    public void logEmailScan(String userEmail, String emailFrom, String emailSubject, String result) {
        String message = String.format(
            "[%s] EMAIL_SCAN | User: %s | From: %s | Subject: %s | Result: %s",
            getCurrentTimestamp(),
            userEmail,
            emailFrom,
            emailSubject,
            result
        );
        writeLog("debug.log", message);
    }
    
    /**
     * Log phishing detection
     */
    public void logPhishingDetection(String emailFrom, String emailSubject, String riskLevel, String details) {
        String message = String.format(
            "[%s] PHISHING_DETECTED | From: %s | Subject: %s | Risk: %s | Details: %s",
            getCurrentTimestamp(),
            emailFrom,
            emailSubject,
            riskLevel,
            details
        );
        writeLog("security.log", message);
    }
    
    /**
     * Log failed authentication attempt
     */
    public void logFailedAuthAttempt(String username, String ipAddress, String reason) {
        String message = String.format(
            "[%s] FAILED_AUTH | Username: %s | IP: %s | Reason: %s",
            getCurrentTimestamp(),
            username,
            ipAddress,
            reason
        );
        writeLog("security.log", message);
    }
    
    /**
     * Log user action
     */
    public void logUserAction(String username, String action, String details) {
        String message = String.format(
            "[%s] USER_ACTION | User: %s | Action: %s | Details: %s",
            getCurrentTimestamp(),
            username,
            action,
            details
        );
        writeLog("debug.log", message);
    }
    
    /**
     * Log admin action
     */
    public void logAdminAction(String adminName, String action, String target, String details) {
        String message = String.format(
            "[%s] ADMIN_ACTION | Admin: %s | Action: %s | Target: %s | Details: %s",
            getCurrentTimestamp(),
            adminName,
            action,
            target,
            details
        );
        writeLog("admin_actions.log", message);
    }
    
    /**
     * Log system error
     */
    public void logSystemError(String module, String errorMessage, String stackTrace) {
        String message = String.format(
            "[%s] ERROR | Module: %s | Error: %s | Trace: %s",
            getCurrentTimestamp(),
            module,
            errorMessage,
            stackTrace
        );
        writeLog("security.log", message);
    }
    
    /**
     * Log warning event
     */
    public void logWarning(String module, String warning) {
        String message = String.format(
            "[%s] WARNING | Module: %s | Warning: %s",
            getCurrentTimestamp(),
            module,
            warning
        );
        writeLog("security.log", message);
    }
    
    /**
     * Get log file content
     */
    public List<String> getLogContent(String logType, int lines) throws IOException {
        String logFile = LOG_DIR + "/" + logType + ".log";
        Path path = Paths.get(logFile);
        
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        
        List<String> allLines = Files.readAllLines(path);
        int startIdx = Math.max(0, allLines.size() - lines);
        return allLines.subList(startIdx, allLines.size());
    }
    
    /**
     * Get log file size
     */
    public long getLogFileSize(String logType) throws IOException {
        String logFile = LOG_DIR + "/" + logType + ".log";
        Path path = Paths.get(logFile);
        
        if (!Files.exists(path)) {
            return 0;
        }
        
        return Files.size(path);
    }
    
    /**
     * Get logs summary
     */
    public Map<String, Object> getLogsSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("timestamp", getCurrentTimestamp());
        
        Map<String, Object> logs = new HashMap<>();
        
        String[] logTypes = {"debug", "security", "admin_actions"};
        String[] descriptions = {
            "Application Debug Logs",
            "Security Events Logs",
            "Admin Actions Logs"
        };
        
        for (int i = 0; i < logTypes.length; i++) {
            Map<String, Object> logInfo = new HashMap<>();
            try {
                long size = getLogFileSize(logTypes[i]);
                List<String> content = getLogContent(logTypes[i], Integer.MAX_VALUE);
                
                logInfo.put("description", descriptions[i]);
                logInfo.put("size_bytes", size);
                logInfo.put("size_readable", formatBytes(size));
                logInfo.put("lines", content.size());
                
                logs.put(logTypes[i] + ".log", logInfo);
            } catch (IOException e) {
                logInfo.put("error", e.getMessage());
            }
        }
        
        summary.put("logs", logs);
        return summary;
    }
    
    /**
     * Write log to file
     */
    private synchronized void writeLog(String filename, String message) {
        try {
            Path logFile = Paths.get(LOG_DIR, filename);
            Files.write(logFile, (message + "\n").getBytes(), 
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Failed to write log: " + e.getMessage());
        }
    }
    
    /**
     * Get current timestamp
     */
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(TIMESTAMP_FORMAT);
    }
    
    /**
     * Format bytes to human readable format
     */
    private String formatBytes(long bytes) {
        if (bytes <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
        return String.format("%.2f %s", bytes / Math.pow(1024, digitGroups), units[digitGroups]);
    }
}
