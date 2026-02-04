package com.soc.phishing.logs;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logs")
public class LogsController {
    
    @GetMapping("")
    public String getLogs(@RequestParam(defaultValue = "100") int limit) {
        // TODO: Implement log retrieval
        return "[]";
    }
    
    @PostMapping("/clear")
    public String clearLogs() {
        // TODO: Implement log clearing
        return "Logs cleared";
    }
}
