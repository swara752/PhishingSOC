# PhishingSOC - Logging Integration Quick Start Guide

## Overview
This guide helps developers integrate the ActivityLogger into their services and controllers.

---

## Table of Contents
1. [Quick Setup](#quick-setup)
2. [Basic Usage](#basic-usage)
3. [Integration Examples](#integration-examples)
4. [Viewing Logs](#viewing-logs)
5. [Best Practices](#best-practices)

---

## Quick Setup

### 1. Inject ActivityLogger

In any Spring service or controller, inject the ActivityLogger:

```java
@Service
public class YourService {
    
    @Autowired
    private ActivityLogger activityLogger;
    
    // Your code here
}
```

### 2. Log User Activities

```java
// In your method
activityLogger.logUserAction(username, "ACTION_NAME", "Additional details");
```

---

## Basic Usage

### Authentication Events

#### Log User Login
```java
activityLogger.logUserLogin(username, ipAddress, success);
```

**Parameters:**
- `username`: The user's username
- `ipAddress`: Client IP address
- `success`: true if login successful, false otherwise

**Example:**
```java
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    String ipAddress = getClientIpAddress();
    
    if (authenticateUser(request.getUsername(), request.getPassword())) {
        activityLogger.logUserLogin(request.getUsername(), ipAddress, true);
        return ResponseEntity.ok("Login successful");
    } else {
        activityLogger.logUserLogin(request.getUsername(), ipAddress, false);
        return ResponseEntity.badRequest().body("Invalid credentials");
    }
}
```

#### Log User Logout
```java
activityLogger.logUserLogout(username, ipAddress);
```

#### Log Failed Authentication
```java
activityLogger.logFailedAuthAttempt(username, ipAddress, reason);
```

**Example:**
```java
if (!credentialsValid) {
    activityLogger.logFailedAuthAttempt(
        username, 
        ipAddress, 
        "Password mismatch"
    );
}
```

---

### Email/Phishing Events

#### Log Email Scan
```java
activityLogger.logEmailScan(userEmail, emailFrom, emailSubject, result);
```

**Parameters:**
- `userEmail`: Email of the user who scanned
- `emailFrom`: Sender's email address
- `emailSubject`: Email subject
- `result`: Scan result (e.g., "ANALYSIS_COMPLETE", "ANALYSIS_STARTED")

**Example:**
```java
@PostMapping("/analyze")
public ResponseEntity<?> analyzeEmail(@RequestBody EmailData email) {
    try {
        activityLogger.logEmailScan(
            currentUser, 
            email.getFrom(), 
            email.getSubject(), 
            "ANALYSIS_STARTED"
        );
        
        // Perform analysis
        AnalysisResult result = performAnalysis(email);
        
        activityLogger.logEmailScan(
            currentUser, 
            email.getFrom(), 
            email.getSubject(), 
            "ANALYSIS_COMPLETE"
        );
        
        return ResponseEntity.ok(result);
    } catch (Exception e) {
        activityLogger.logSystemError("EmailService", e.getMessage(), 
            e.getStackTrace().toString());
        return ResponseEntity.status(500).body("Analysis failed");
    }
}
```

#### Log Phishing Detection
```java
activityLogger.logPhishingDetection(emailFrom, emailSubject, riskLevel, details);
```

**Parameters:**
- `emailFrom`: Sender's email address
- `emailSubject`: Email subject
- `riskLevel`: Risk level (LOW, MEDIUM, HIGH, CRITICAL)
- `details`: Additional details about the threat

**Example:**
```java
if (isPhishingEmail(email)) {
    activityLogger.logPhishingDetection(
        email.getFrom(),
        email.getSubject(),
        "HIGH",
        "Spoofed domain detected, 3 malicious links found"
    );
}
```

---

### General User Actions

#### Log Any User Action
```java
activityLogger.logUserAction(username, action, details);
```

**Parameters:**
- `username`: User performing the action
- `action`: Action name (e.g., "EXPORT_DATA", "CREATE_REPORT")
- `details`: Additional context

**Examples:**
```java
// Track exports
activityLogger.logUserAction(currentUser, "EXPORT_DATA", 
    "Exported 50 emails with HIGH risk level");

// Track dashboard views
activityLogger.logUserAction(currentUser, "DASHBOARD_VIEW", 
    "Viewed security statistics");

// Track report generation
activityLogger.logUserAction(currentUser, "GENERATE_REPORT", 
    "Generated monthly phishing report for January");

// Track searches
activityLogger.logUserAction(currentUser, "EMAIL_SEARCH", 
    "Searched for emails from domain: example.com");
```

---

### Error & Warning Logging

#### Log System Errors
```java
activityLogger.logSystemError(module, errorMessage, stackTrace);
```

**Example:**
```java
try {
    // Code that might throw exception
    analyzeEmail(email);
} catch (Exception e) {
    activityLogger.logSystemError(
        "EmailAnalyzer",
        e.getMessage(),
        e.getStackTrace().toString()
    );
    // Handle error
}
```

#### Log Warnings
```java
activityLogger.logWarning(module, warningMessage);
```

**Example:**
```java
if (responsetime > 5000) {
    activityLogger.logWarning("EmailController", 
        "Slow response: Email analysis took " + responsetime + "ms");
}
```

---

### Admin Actions

#### Log Admin Operations
```java
activityLogger.logAdminAction(adminName, action, target, details);
```

**Example:**
```java
@PostMapping("/admin/users/create")
public ResponseEntity<?> createUser(@RequestBody UserData user) {
    String adminName = getCurrentAdmin();
    
    try {
        User newUser = createUserInDatabase(user);
        
        activityLogger.logAdminAction(
            adminName,
            "USER_CREATED",
            user.getUsername(),
            "Email: " + user.getEmail() + ", Role: " + user.getRole()
        );
        
        return ResponseEntity.ok("User created");
    } catch (Exception e) {
        return ResponseEntity.status(500).body("Failed to create user");
    }
}
```

---

## Integration Examples

### Complete Authentication Service Example

```java
@Service
public class AuthenticationService {
    
    @Autowired
    private ActivityLogger activityLogger;
    
    public boolean login(String username, String password, String ipAddress) {
        try {
            // Validate credentials
            if (!validateCredentials(username, password)) {
                activityLogger.logUserLogin(username, ipAddress, false);
                activityLogger.logFailedAuthAttempt(username, ipAddress, 
                    "Invalid credentials");
                return false;
            }
            
            // Log successful login
            activityLogger.logUserLogin(username, ipAddress, true);
            activityLogger.logUserAction(username, "LOGIN_SUCCESS", 
                "User logged in from IP: " + ipAddress);
            
            return true;
        } catch (Exception e) {
            activityLogger.logSystemError("AuthenticationService", 
                e.getMessage(), e.getStackTrace().toString());
            return false;
        }
    }
    
    public void logout(String username, String ipAddress) {
        try {
            activityLogger.logUserLogout(username, ipAddress);
            activityLogger.logUserAction(username, "LOGOUT", 
                "User logged out");
        } catch (Exception e) {
            activityLogger.logSystemError("AuthenticationService", 
                e.getMessage(), e.getStackTrace().toString());
        }
    }
}
```

### Complete Email Analysis Service Example

```java
@Service
public class EmailAnalysisService {
    
    @Autowired
    private ActivityLogger activityLogger;
    
    public AnalysisResult analyzeEmail(String emailId, String userEmail, 
                                      EmailContent content) {
        try {
            // Log start of analysis
            activityLogger.logEmailScan(userEmail, content.getFromAddress(), 
                content.getSubject(), "ANALYSIS_STARTED");
            
            // Perform analysis
            AnalysisResult result = performDeepAnalysis(content);
            
            // Log completion
            activityLogger.logEmailScan(userEmail, content.getFromAddress(), 
                content.getSubject(), "ANALYSIS_COMPLETE");
            
            // If phishing detected, log security event
            if (result.isPhishing()) {
                activityLogger.logPhishingDetection(
                    content.getFromAddress(),
                    content.getSubject(),
                    result.getRiskLevel(),
                    buildPhishingDetails(result)
                );
            }
            
            return result;
        } catch (Exception e) {
            activityLogger.logSystemError("EmailAnalysisService", 
                e.getMessage(), e.getStackTrace().toString());
            throw new RuntimeException("Email analysis failed", e);
        }
    }
    
    private String buildPhishingDetails(AnalysisResult result) {
        return "Risk Score: " + result.getConfidence() + 
               "%, Indicators: " + String.join(", ", result.getIndicators());
    }
}
```

---

## Viewing Logs

### Via REST API (Admin Only)

#### Get Logs Summary
```bash
curl http://localhost:8080/api/logs/summary
```

#### Get Log Content
```bash
# View last 50 lines of debug log
curl http://localhost:8080/api/logs/debug?lines=50

# View security log
curl http://localhost:8080/api/logs/security?lines=100

# View admin actions
curl http://localhost:8080/api/logs/admin_actions
```

#### Download Log File
```bash
curl http://localhost:8080/api/logs/debug/download -O
```

### Via Terminal

```bash
# View real-time logs
tail -f logs/debug.log

# Search for specific events
grep "LOGIN_ATTEMPT" logs/debug.log
grep "PHISHING_DETECTED" logs/security.log
grep "ADMIN_ACTION" logs/admin_actions.log

# Count occurrences
grep -c "LOGIN_ATTEMPT" logs/debug.log
```

---

## Best Practices

### 1. Always Log at Critical Points

```java
// Good: Log authentication attempts
activityLogger.logUserLogin(username, ipAddress, success);

// Good: Log security-critical operations
activityLogger.logPhishingDetection(from, subject, riskLevel, details);

// Good: Log admin operations
activityLogger.logAdminAction(admin, "USER_DELETED", username, details);
```

### 2. Include Contextual Information

```java
// Bad
activityLogger.logUserAction(username, "EMAIL_SCAN", "Done");

// Good
activityLogger.logUserAction(username, "EMAIL_SCAN", 
    "Scanned 5 emails, detected 1 phishing attempt");
```

### 3. Always Log Errors

```java
try {
    // Processing code
} catch (Exception e) {
    activityLogger.logSystemError("ServiceName", 
        e.getMessage(), 
        e.getStackTrace().toString());
    // Handle gracefully
}
```

### 4. Use Appropriate Log Types

```java
// User activity → logUserAction()
activityLogger.logUserAction(user, "ACTION", "details");

// Authentication → logUserLogin() / logUserLogout()
activityLogger.logUserLogin(user, ip, success);

// Security threats → logPhishingDetection() / logFailedAuthAttempt()
activityLogger.logPhishingDetection(from, subject, risk, details);

// Admin changes → logAdminAction()
activityLogger.logAdminAction(admin, "ACTION", "target", "details");

// Errors → logSystemError()
activityLogger.logSystemError("Module", "error", "trace");
```

### 5. Get Client IP Address

```java
private String getClientIpAddress() {
    ServletRequestAttributes attributes = 
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes == null) return "UNKNOWN";
    
    HttpServletRequest request = attributes.getRequest();
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty()) {
        ip = request.getRemoteAddr();
    }
    return ip;
}
```

### 6. Log Before and After Operations

```java
// For long-running operations
activityLogger.logUserAction(user, "REPORT_GENERATION", "Started");

try {
    // Generate report
    generateReport();
    activityLogger.logUserAction(user, "REPORT_GENERATION", "Completed");
} catch (Exception e) {
    activityLogger.logSystemError("ReportGenerator", 
        e.getMessage(), e.getStackTrace().toString());
}
```

---

## Summary of Methods

| Method | Purpose | Log File |
|--------|---------|----------|
| `logUserLogin()` | Track login attempts | debug.log / security.log |
| `logUserLogout()` | Track logouts | debug.log |
| `logUserAction()` | Track user activities | debug.log |
| `logEmailScan()` | Track email analysis | debug.log |
| `logPhishingDetection()` | Alert on threats | security.log |
| `logFailedAuthAttempt()` | Track failed logins | security.log |
| `logAdminAction()` | Track admin operations | admin_actions.log |
| `logSystemError()` | Log errors | security.log |
| `logWarning()` | Log warnings | security.log |

---

## Questions?

Refer to the main LOGGING_GUIDE.md for detailed API documentation and architecture.
