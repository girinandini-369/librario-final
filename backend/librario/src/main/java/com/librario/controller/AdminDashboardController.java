package com.librario.controller;

import com.librario.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/api/admin/summary")
    public Map<String,Object> getAdminSummary(){
        Map<String,Object> summary = new HashMap<>();
        summary.put("totalBooks", adminDashboardService.getTotalBooks());
        summary.put("totalUsers", adminDashboardService.getTotalUsers());
        summary.put("booksBorrowed", adminDashboardService.getBooksBorrowed());
        summary.put("activeBorrowRecords", adminDashboardService.getActiveBorrowRecords());
        return summary;
    }
}
