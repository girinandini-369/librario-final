package com.librario.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

    @GetMapping("/api/admin/dashboard")
    public String adminDashboard() {
        return "👑 Welcome Admin! This is your dashboard.";
    }

    @GetMapping("/api/librarian/dashboard")
    public String librarianDashboard() {
        return "📚 Welcome Librarian! This is your dashboard.";
    }

    @GetMapping("/api/member/dashboard")
    public String memberDashboard() {
        return "🙋 Welcome Member! This is your dashboard.";
    }
}
