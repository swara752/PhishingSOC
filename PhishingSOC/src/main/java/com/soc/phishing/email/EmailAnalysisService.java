package com.soc.phishing.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.soc.phishing.logs.ActivityLogger;
import java.util.*;

/**
 * Service for email analysis and phishing detection
 * Example implementation showing proper logging usage
 */
@Service
public class EmailAnalysisService {
    
    @Autowired
    private ActivityLogger activityLogger;
    
    /**
     * Analyze email for phishing indicators
     */
    public EmailAnalysisResult analyzeEmail(String emailId, String userEmail, 
                                           EmailContent emailContent) {
        try {
            activityLogger.logUserAction(userEmail, "EMAIL_ANALYSIS_START", 
                "Analyzing email: " + emailId);
            
            EmailAnalysisResult result = new EmailAnalysisResult();
            result.setEmailId(emailId);
            
            // Check for phishing indicators
            boolean hasPhishingIndicators = checkPhishingIndicators(emailContent);
            String riskLevel = calculateRiskLevel(emailContent);
            
            result.setIsPhishing(hasPhishingIndicators);
            result.setRiskLevel(riskLevel);
            result.setConfidence(calculateConfidence(emailContent));
            
            // Log analysis result
            if (hasPhishingIndicators) {
                String details = "Links: " + emailContent.getLinkCount() + 
                               ", Attachments: " + emailContent.getAttachmentCount();
                activityLogger.logPhishingDetection(
                    emailContent.getFromAddress(),
                    emailContent.getSubject(),
                    riskLevel,
                    details
                );
            } else {
                activityLogger.logEmailScan(userEmail, emailContent.getFromAddress(), 
                    emailContent.getSubject(), "ANALYSIS_COMPLETE");
            }
            
            return result;
        } catch (Exception e) {
            activityLogger.logSystemError("EmailAnalysisService", 
                "Analysis failed: " + e.getMessage(), e.getStackTrace().toString());
            throw new RuntimeException("Email analysis failed", e);
        }
    }
    
    /**
     * Check for phishing indicators
     */
    private boolean checkPhishingIndicators(EmailContent content) {
        boolean hasPhishingLinks = content.getLinkCount() > 5;
        boolean hasSuspiciousDomain = isSuspiciousDomain(content.getFromAddress());
        boolean hasAttachments = content.getAttachmentCount() > 0;
        
        return hasPhishingLinks || hasSuspiciousDomain || hasAttachments;
    }
    
    /**
     * Calculate risk level
     */
    private String calculateRiskLevel(EmailContent content) {
        int riskScore = 0;
        
        // Check domain reputation
        if (isSuspiciousDomain(content.getFromAddress())) {
            riskScore += 40;
        }
        
        // Check links
        if (content.getLinkCount() > 5) {
            riskScore += 30;
        }
        
        // Check attachments
        if (content.getAttachmentCount() > 0) {
            riskScore += 20;
        }
        
        // Check content patterns
        if (content.getContent().toLowerCase().contains("verify account") ||
            content.getContent().toLowerCase().contains("confirm password")) {
            riskScore += 30;
        }
        
        if (riskScore >= 70) {
            return "CRITICAL";
        } else if (riskScore >= 50) {
            return "HIGH";
        } else if (riskScore >= 30) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }
    
    /**
     * Calculate confidence percentage
     */
    private double calculateConfidence(EmailContent content) {
        return 0.60 + (Math.random() * 0.40);
    }
    
    /**
     * Check if domain is suspicious
     */
    private boolean isSuspiciousDomain(String emailAddress) {
        // Simple check - in production, use domain reputation service
        String domain = emailAddress.substring(emailAddress.indexOf("@") + 1).toLowerCase();
        List<String> suspiciousDomains = Arrays.asList(
            "phishing.com", "fake.com", "scam.com", "malicious.net"
        );
        return suspiciousDomains.contains(domain);
    }
}
