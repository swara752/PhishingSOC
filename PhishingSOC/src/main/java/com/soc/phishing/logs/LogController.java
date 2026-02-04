package com.soc.phishing.logs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * REST Controller for accessing logs
 * All endpoints require admin authentication
 */
@RestController
@RequestMapping("/api/logs")
@PreAuthorize("hasRole('ADMIN')")
public class LogController {
    
    @Autowired
    private ActivityLogger activityLogger;
    
    /**
     * Get logs summary
     * GET /api/logs/summary
     */
    @GetMapping("/summary")
    public ResponseEntity<?> getLogsSummary() {
        try {
            Map<String, Object> summary = activityLogger.getLogsSummary();
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                Collections.singletonMap("error", e.getMessage())
            );
        }
    }
    
    /**
     * Get specific log content
     * GET /api/logs/{type}?lines=50
     */
    @GetMapping("/{logType}")
    public ResponseEntity<?> getLogContent(
            @PathVariable String logType,
            @RequestParam(defaultValue = "100") int lines) {
        try {
            List<String> content = activityLogger.getLogContent(logType, lines);
            
            Map<String, Object> response = new HashMap<>();
            response.put("log_type", logType);
            response.put("file", logType + ".log");
            response.put("displayed_lines", content.size());
            response.put("content", content);
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(404).body(
                Collections.singletonMap("error", logType + " log file not found")
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                Collections.singletonMap("error", e.getMessage())
            );
        }
    }
    
    /**
     * Download log file
     * GET /api/logs/{type}/download
     */
    @GetMapping("/{logType}/download")
    public ResponseEntity<?> downloadLog(@PathVariable String logType) {
        try {
            String logFile = "logs/" + logType + ".log";
            byte[] content = Files.readAllBytes(Paths.get(logFile));
            
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + logType + ".log\"")
                    .header("Content-Type", "text/plain")
                    .body(content);
        } catch (IOException e) {
            return ResponseEntity.status(404).body(
                Collections.singletonMap("error", logType + " log file not found")
            );
        }
    }
}
