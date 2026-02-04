# Phishing Email Detection - Logging System Documentation

## Overview
A comprehensive logging system has been set up for the PhishingSOC email phishing detection application to track:
- User authentication and activities
- Email scanning and analysis
- Phishing detection events
- Security warnings and errors
- Admin actions

---

## 📁 Log Files Location
```
PhishingSOC/logs/
```

### Log Types

#### 1. **debug.log** - Application Debug Logs
- User login/logout events
- Email scan activities
- User actions (searches, exports, etc.)
- System operations
- General application events

#### 2. **security.log** - Security Events
- Failed login attempts
- Phishing detection alerts
- Suspicious activities
- Security warnings
- System errors
- Password/permission changes

#### 3. **admin_actions.log** - Admin Panel Actions
- User management (create, modify, delete)
- Group and permission management
- System configuration changes
- Admin panel activities

---

## 🚀 Using the Logger

### 1. Inject ActivityLogger in Spring Service

```java
@Service
public class EmailService {
    
    @Autowired
    private ActivityLogger activityLogger;
    
    public void scanEmail(String userEmail, String emailFrom, String subject) {
        // Scan logic here
        activityLogger.logEmailScan(userEmail, emailFrom, subject, "ANALYSIS_COMPLETE");
    }
    
    public void detectPhishing(String emailFrom, String subject, String riskLevel) {
        String details = "Suspicious links detected: 2, Spoofed domain: true";
        activityLogger.logPhishingDetection(emailFrom, subject, riskLevel, details);
    }
}
```

### 2. Log User Authentication

```java
@Autowired
private ActivityLogger activityLogger;

// In authentication success
public void loginSuccess(String username, String ipAddress) {
    activityLogger.logUserLogin(username, ipAddress, true);
}

// In authentication failure
public void loginFailure(String username, String ipAddress) {
    activityLogger.logUserLogin(username, ipAddress, false);
    activityLogger.logFailedAuthAttempt(username, ipAddress, "Invalid credentials");
}
```

### 3. Log User Actions

```java
activityLogger.logUserAction(
    username, 
    "EMAIL_EXPORT", 
    "Exported 25 emails with phishing risk HIGH"
);

activityLogger.logUserAction(
    username,
    "DASHBOARD_VIEW",
    "Viewed phishing statistics"
);
```

### 4. Log Admin Actions

```java
activityLogger.logAdminAction(
    adminName,
    "USER_CREATED",
    username,
    "New user email: user@example.com, Role: ANALYST"
);

activityLogger.logAdminAction(
    adminName,
    "SYSTEM_CONFIG",
    "Phishing Detection Sensitivity",
    "Changed from MEDIUM to HIGH"
);
```

### 5. Log Errors and Warnings

```java
try {
    // Email processing
    processEmail(email);
} catch (Exception e) {
    activityLogger.logSystemError(
        "EmailProcessor",
        e.getMessage(),
        e.getStackTrace().toString()
    );
}

// Log warnings
activityLogger.logWarning(
    "EmailAnalyzer",
    "Database connection slow: 2500ms response time"
);
```

---

## 📊 REST API Endpoints (Admin Only)

All endpoints require authentication and admin role.

### 1. Get Logs Summary
```
GET /api/logs/summary
```

**Response:**
```json
{
  "timestamp": "2025-02-02 14:30:45",
  "logs": {
    "debug.log": {
      "description": "Application Debug Logs",
      "size_bytes": 102400,
      "size_readable": "100.00 KB",
      "lines": 512
    },
    "security.log": {
      "description": "Security Events Logs",
      "size_bytes": 51200,
      "size_readable": "50.00 KB",
      "lines": 256
    },
    "admin_actions.log": {
      "description": "Admin Actions Logs",
      "size_bytes": 25600,
      "size_readable": "25.00 KB",
      "lines": 128
    }
  }
}
```

### 2. Get Log Content
```
GET /api/logs/{logType}?lines=50
```

**Parameters:**
- `logType`: debug, security, or admin_actions
- `lines`: Number of lines to retrieve (default: 100)

**Example:**
```
GET /api/logs/debug?lines=50
```

**Response:**
```json
{
  "log_type": "debug",
  "file": "debug.log",
  "displayed_lines": 50,
  "content": [
    "[2025-02-02 14:30:45] LOGIN_ATTEMPT | Username: user1 | IP: 192.168.1.100 | Success: true",
    "[2025-02-02 14:31:20] EMAIL_SCAN | User: user1@company.com | From: suspicious@phishing.com | Subject: Urgent Action Required | Result: ANALYSIS_COMPLETE",
    "..."
  ]
}
```

### 3. Download Log File
```
GET /api/logs/{logType}/download
```

**Example:**
```
GET /api/logs/security/download
```

Downloads the log file as attachment.

---

## 📝 Logger Methods Reference

### User Activity Logging

```java
// User login
activityLogger.logUserLogin(String username, String ipAddress, boolean success)

// User logout
activityLogger.logUserLogout(String username, String ipAddress)

// Generic user action
activityLogger.logUserAction(String username, String action, String details)
```

### Email & Phishing Logging

```java
// Log email scan
activityLogger.logEmailScan(String userEmail, String emailFrom, 
                            String emailSubject, String result)

// Log phishing detection
activityLogger.logPhishingDetection(String emailFrom, String emailSubject, 
                                    String riskLevel, String details)
```

### Security Logging

```java
// Failed authentication
activityLogger.logFailedAuthAttempt(String username, String ipAddress, String reason)

// System error
activityLogger.logSystemError(String module, String errorMessage, String stackTrace)

// Security warning
activityLogger.logWarning(String module, String warning)
```

### Admin Logging

```java
// Admin action
activityLogger.logAdminAction(String adminName, String action, 
                              String target, String details)
```

### Retrieval Methods

```java
// Get log content
List<String> getLogContent(String logType, int lines) throws IOException

// Get log file size
long getLogFileSize(String logType) throws IOException

// Get logs summary
Map<String, Object> getLogsSummary()
```

---

## 🔒 Security Features

1. **Access Control**: All API endpoints require admin role
2. **Log Isolation**: Different log types for different concerns
3. **Synchronized Writing**: Thread-safe log file operations
4. **Error Handling**: Comprehensive exception handling
5. **Data Integrity**: All logs include timestamps and context

---

## 📂 Log Format

Each log entry follows this format:
```
[TIMESTAMP] EVENT_TYPE | Field1: Value1 | Field2: Value2 | ...
```

**Example entries:**
```
[2025-02-02 14:30:45] LOGIN_ATTEMPT | Username: user1 | IP: 192.168.1.100 | Success: true
[2025-02-02 14:31:20] EMAIL_SCAN | User: user1@company.com | From: suspicious@phishing.com | Subject: Urgent Action Required | Result: ANALYSIS_COMPLETE
[2025-02-02 14:32:10] PHISHING_DETECTED | From: admin@bank-fake.com | Subject: Verify Your Account | Risk: CRITICAL | Details: Spoofed domain, malicious links detected
[2025-02-02 14:32:15] FAILED_AUTH | Username: attacker | IP: 10.0.0.5 | Reason: Invalid credentials
[2025-02-02 14:33:00] ADMIN_ACTION | Admin: admin1 | Action: USER_CREATED | Target: newuser | Details: New user email: newuser@company.com, Role: ANALYST
```

---

## 🛠️ Troubleshooting

### Logs directory doesn't exist
The application automatically creates the logs directory if it doesn't exist.

### Can't write logs
Ensure the application has write permissions to the project directory:
```bash
chmod -R 755 /path/to/PhishingSOC
```

### Log file too large
The application currently appends all logs. For production, consider implementing log rotation:
- Archive logs daily
- Compress old logs
- Keep logs for 30 days

### Viewing logs programmatically
```java
@Autowired
private ActivityLogger activityLogger;

@GetMapping("/admin/logs/stats")
public ResponseEntity<?> getLogStats() {
    return ResponseEntity.ok(activityLogger.getLogsSummary());
}
```

---

## 📋 Best Practices

1. **Log at Critical Points**: Login, logout, email analysis, phishing detection
2. **Include Context**: Always add IP address, username, and relevant details
3. **Monitor Security Logs**: Regularly check security.log for threats
4. **Archive Old Logs**: Implement regular log archival for compliance
5. **Protect Log Files**: Restrict access to log files to authorized personnel
6. **Review Logs**: Set up regular log reviews for security audit trail

---

## 📊 Example Use Cases

### Track Suspicious Login Attempts
```bash
grep "FAILED_AUTH" logs/security.log | grep "invalid"
```

### Monitor Phishing Detections
```bash
grep "PHISHING_DETECTED" logs/security.log
```

### Audit User Activity
```bash
grep "USERNAME" logs/debug.log
```

### Track Admin Changes
```bash
grep "ADMIN_ACTION" logs/admin_actions.log
```

---

## Integration Checklist

- [ ] ActivityLogger bean is autowired in services
- [ ] User authentication calls logUserLogin/logFailedAuthAttempt
- [ ] Email scanning calls logEmailScan
- [ ] Phishing detection calls logPhishingDetection
- [ ] Admin actions call logAdminAction
- [ ] Error handlers call logSystemError
- [ ] Critical operations call logUserAction
- [ ] API endpoints are secured with @PreAuthorize
- [ ] Regular log reviews scheduled
- [ ] Log storage location documented

---

**Logging system is ready for use in PhishingSOC!**
