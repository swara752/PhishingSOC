package com.soc.phishing.dashboard;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    
    @GetMapping("/stats")
    public String getStats() {
        // TODO: Implement stats retrieval
        return "{\"phishingDetected\": 42, \"emails\": 1200}";
    }
    
    @GetMapping("/threats")
    public String getThreats() {
        // TODO: Implement threat retrieval
        return "[]";
    }
}
