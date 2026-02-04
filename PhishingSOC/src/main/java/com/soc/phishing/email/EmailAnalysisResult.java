package com.soc.phishing.email;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Email analysis result
 */
public class EmailAnalysisResult {
    private String emailId;
    private boolean isPhishing;
    private String riskLevel;
    private double confidence;
    
    // Getters and Setters
    public String getEmailId() {
        return emailId;
    }
    
    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
    
    public boolean isPhishing() {
        return isPhishing;
    }
    
    public void setIsPhishing(boolean phishing) {
        isPhishing = phishing;
    }
    
    public String getRiskLevel() {
        return riskLevel;
    }
    
    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }
    
    public double getConfidence() {
        return confidence;
    }
    
    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}
