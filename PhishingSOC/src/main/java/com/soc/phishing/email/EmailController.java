package com.soc.phishing.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.soc.phishing.logs.ActivityLogger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/email")
public class EmailController {
    
    @Autowired
    private ActivityLogger activityLogger;
    
    private static final Random random = new Random();
    
    @PostMapping("/upload")
    public ResponseEntity<?> uploadEmail(@RequestParam String filename, 
                                        @RequestParam(required = false) String userEmail) {
        String username = userEmail != null ? userEmail : "UNKNOWN";
        
        try {
            // Log email upload
            activityLogger.logUserAction(username, "EMAIL_UPLOAD", 
                "Uploaded file: " + filename);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Email uploaded: " + filename);
            response.put("emailId", "email_" + System.currentTimeMillis());
            response.put("filename", filename);
            response.put("status", "UPLOADED");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            activityLogger.logSystemError("EmailController", 
                "Upload failed: " + e.getMessage(), e.getStackTrace().toString());
            return ResponseEntity.status(500).body(
                Map.of("error", "Upload failed: " + e.getMessage())
            );
        }
    }
    
    @GetMapping("/analyze/{emailId}")
    public ResponseEntity<?> analyzeEmail(@PathVariable String emailId,
                                         @RequestParam(required = false) String userEmail) {
        String username = userEmail != null ? userEmail : "UNKNOWN";
        
        try {
            // Simulate email analysis
            String emailFrom = "sender@example.com";
            String emailSubject = "Test Email Subject";
            
            // Log email scan start
            activityLogger.logEmailScan(username, emailFrom, emailSubject, "ANALYSIS_STARTED");
            
            // Simulate analysis
            Thread.sleep(500);
            
            // Simulate phishing detection (30% chance)
            boolean isPhishing = random.nextInt(100) < 30;
            String riskLevel = "LOW";
            
            if (isPhishing) {
                riskLevel = "HIGH";
                String details = "Suspicious links detected, spoofed domain identified";
                activityLogger.logPhishingDetection(emailFrom, emailSubject, riskLevel, details);
            }
            
            // Log analysis completion
            activityLogger.logEmailScan(username, emailFrom, emailSubject, "ANALYSIS_COMPLETE");
            
            Map<String, Object> response = new HashMap<>();
            response.put("emailId", emailId);
            response.put("analysis", Map.of(
                "isPhishing", isPhishing,
                "riskLevel", riskLevel,
                "confidence", random.nextDouble() * 100,
                "details", isPhishing ? "Phishing email detected" : "Email appears legitimate"
            ));
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            activityLogger.logSystemError("EmailController", 
                "Analysis failed: " + e.getMessage(), e.getStackTrace().toString());
            return ResponseEntity.status(500).body(
                Map.of("error", "Analysis failed: " + e.getMessage())
            );
        }
    }
    
    @GetMapping("/scan-status/{emailId}")
    public ResponseEntity<?> getScanStatus(@PathVariable String emailId) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("emailId", emailId);
            response.put("status", "COMPLETED");
            response.put("percentage", 100);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            activityLogger.logSystemError("EmailController", 
                "Get status failed: " + e.getMessage(), e.getStackTrace().toString());
            return ResponseEntity.status(500).body(
                Map.of("error", "Get status failed: " + e.getMessage())
            );
        }
    }
    
    @GetMapping("/list")
    public ResponseEntity<?> listEmails(@RequestParam(required = false) String userEmail) {
        String username = userEmail != null ? userEmail : "UNKNOWN";
        
        try {
            activityLogger.logUserAction(username, "EMAILS_LIST_VIEW", 
                "User viewed emails list");
            
            Map<String, Object> response = new HashMap<>();
            response.put("emails", new Object[]{});
            response.put("total", 0);
            response.put("message", "No emails found");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            activityLogger.logSystemError("EmailController", 
                "List emails failed: " + e.getMessage(), e.getStackTrace().toString());
            return ResponseEntity.status(500).body(
                Map.of("error", "List failed: " + e.getMessage())
            );
        }
    }
}
