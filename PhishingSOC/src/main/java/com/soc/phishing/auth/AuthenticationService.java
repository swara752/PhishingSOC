package com.soc.phishing.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.soc.phishing.logs.ActivityLogger;

/**
 * Authentication service
 * Example implementation showing proper logging usage
 */
@Service
public class AuthenticationService {
    
    @Autowired
    private ActivityLogger activityLogger;
    
    /**
     * Authenticate user with username and password
     */
    public boolean authenticateUser(String username, String password, String ipAddress) {
        try {
            // TODO: Implement actual authentication with database
            
            // For demo: accept credentials with at least 3 characters
            if (username != null && username.length() >= 3 && 
                password != null && password.length() >= 3) {
                
                activityLogger.logUserLogin(username, ipAddress, true);
                activityLogger.logUserAction(username, "LOGIN_SUCCESSFUL", 
                    "User logged in from IP: " + ipAddress);
                
                return true;
            } else {
                activityLogger.logUserLogin(username, ipAddress, false);
                activityLogger.logFailedAuthAttempt(username, ipAddress, 
                    "Invalid credentials provided");
                
                return false;
            }
        } catch (Exception e) {
            activityLogger.logSystemError("AuthenticationService", 
                "Authentication failed: " + e.getMessage(), 
                e.getStackTrace().toString());
            return false;
        }
    }
    
    /**
     * Register new user
     */
    public boolean registerUser(String username, String email, String password, String ipAddress) {
        try {
            // TODO: Implement actual user registration with database
            
            if (username == null || username.isEmpty() || 
                email == null || email.isEmpty() ||
                password == null || password.isEmpty()) {
                
                activityLogger.logFailedAuthAttempt(username, ipAddress, 
                    "Invalid registration data");
                return false;
            }
            
            // Log user creation by themselves
            activityLogger.logUserAction(username, "USER_REGISTRATION", 
                "User registered from IP: " + ipAddress + ", Email: " + email);
            
            return true;
        } catch (Exception e) {
            activityLogger.logSystemError("AuthenticationService", 
                "Registration failed: " + e.getMessage(), 
                e.getStackTrace().toString());
            return false;
        }
    }
    
    /**
     * Logout user
     */
    public void logoutUser(String username, String ipAddress) {
        try {
            activityLogger.logUserLogout(username, ipAddress);
            activityLogger.logUserAction(username, "LOGOUT", 
                "User logged out from IP: " + ipAddress);
        } catch (Exception e) {
            activityLogger.logSystemError("AuthenticationService", 
                "Logout failed: " + e.getMessage(), 
                e.getStackTrace().toString());
        }
    }
}
