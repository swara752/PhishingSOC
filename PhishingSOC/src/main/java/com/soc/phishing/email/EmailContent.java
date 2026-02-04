package com.soc.phishing.email;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Email content data structure
 */
public class EmailContent {
    private String fromAddress;
    private String toAddress;
    private String subject;
    private String content;
    private int linkCount;
    private int attachmentCount;
    
    // Getters and Setters
    public String getFromAddress() {
        return fromAddress;
    }
    
    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }
    
    public String getToAddress() {
        return toAddress;
    }
    
    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public int getLinkCount() {
        return linkCount;
    }
    
    public void setLinkCount(int linkCount) {
        this.linkCount = linkCount;
    }
    
    public int getAttachmentCount() {
        return attachmentCount;
    }
    
    public void setAttachmentCount(int attachmentCount) {
        this.attachmentCount = attachmentCount;
    }
}
