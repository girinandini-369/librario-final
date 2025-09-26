package com.librario.controller;

import com.librario.service.LibrarianDashboardService; // ✅ import the service here
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/librarian/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // allow all requests
public class LibrarianDashboardController {

    private final LibrarianDashboardService dashboardService;

    @GetMapping("/metrics")
    public ResponseEntity<?> getMetrics() {
        try {
            return ResponseEntity.ok(dashboardService.getDashboardMetrics());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"error\":\"INTERNAL_ERROR\", \"message\":\"" + e.getMessage() + "\"}");
        }
    }
}
