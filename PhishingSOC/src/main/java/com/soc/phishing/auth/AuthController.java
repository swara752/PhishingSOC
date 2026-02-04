package com.soc.phishing.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.soc.phishing.logs.ActivityLogger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private ActivityLogger activityLogger;
    
    @Autowired
    private JwtUtil jwtUtil;

    // Simple in-memory token blacklist for demo logout
    private final ConcurrentHashMap<String, Boolean> tokenBlacklist = new ConcurrentHashMap<>();
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String ipAddress = getClientIpAddress();
        
        // TODO: Implement actual authentication logic with database
        // For now, accepting any username as demo
        
        try {
            // Simulate authentication
            if (request.getEmail() != null && !request.getEmail().isEmpty()) {
                // Log successful login
                activityLogger.logUserLogin(request.getEmail(), ipAddress, true);
                
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Login successful");
                response.put("email", request.getEmail());
                String jwt = jwtUtil.generateToken(request.getEmail());
                response.put("token", jwt);
                
                return ResponseEntity.ok(response);
            } else {
                // Log failed login
                activityLogger.logUserLogin(request.getEmail(), ipAddress, false);
                activityLogger.logFailedAuthAttempt(request.getEmail(), ipAddress, "Empty email");
                
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Invalid credentials")
                );
            }
        } catch (Exception e) {
            activityLogger.logSystemError("AuthController", e.getMessage(), e.getStackTrace().toString());
            return ResponseEntity.status(500).body(
                Map.of("error", "Authentication error: " + e.getMessage())
            );
        }
    }

    @GetMapping("/login")
    public ResponseEntity<?> loginInfo() {
        // Helpful message for browser GET requests
        Map<String, Object> info = new HashMap<>();
        info.put("error", "Use POST with JSON {email, password} to authenticate or visit /login.html for the interactive form.");
        info.put("post_example", Map.of("email", "demo@user.local", "password", "userpass"));
        return ResponseEntity.status(405).body(info);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        String ipAddress = getClientIpAddress();
        try {
            if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Missing Authorization Bearer token"));
            }
            String token = authHeader.substring(7);
            tokenBlacklist.put(token, true);
            // Optionally log
            activityLogger.logUserAction("UNKNOWN", "LOGOUT", "Logout token blacklisted from IP: " + ipAddress);
            return ResponseEntity.ok(Map.of("message", "Logout successful"));
        } catch (Exception e) {
            activityLogger.logSystemError("AuthController", e.getMessage(), e.getStackTrace().toString());
            return ResponseEntity.status(500).body(Map.of("error", "Logout error: " + e.getMessage()));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginRequest request) {
        String ipAddress = getClientIpAddress();
        
        try {
            String email = request.getEmail();
            String password = request.getPassword();
            
            // Validation
            if (email == null || email.isEmpty() || !email.contains("@")) {
                activityLogger.logFailedAuthAttempt(email, ipAddress, "Invalid email format");
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Invalid email format")
                );
            }
            
            if (password == null || password.length() < 8) {
                activityLogger.logFailedAuthAttempt(email, ipAddress, "Password too short");
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Password must be at least 8 characters")
                );
            }
            
            // TODO: Check if email already exists in database
            // TODO: Hash password and store in database
            
            activityLogger.logUserAction(email, "REGISTRATION_ATTEMPT", 
                "User registration successful from IP: " + ipAddress);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("email", email);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            activityLogger.logSystemError("AuthController", e.getMessage(), e.getStackTrace().toString());
            return ResponseEntity.status(500).body(
                Map.of("error", "Registration error: " + e.getMessage())
            );
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Missing Authorization Bearer token"));
        }
        String token = authHeader.substring(7);
        if (tokenBlacklist.containsKey(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token revoked"));
        }
        try {
            Jws<Claims> claims = jwtUtil.validateToken(token);
            String subject = claims.getBody().getSubject();
            return ResponseEntity.ok(Map.of("email", subject));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid token: " + e.getMessage()));
        }
    }
    
    /**
     * Get client IP address from request
     */
    private String getClientIpAddress() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "UNKNOWN";
        }
        
        HttpServletRequest request = attributes.getRequest();
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
    
    /**
     * Get authenticated username from security context
     * TODO: Implement with Spring Security
     */
    private String getAuthenticatedUsername() {
        return null;
    }
}
