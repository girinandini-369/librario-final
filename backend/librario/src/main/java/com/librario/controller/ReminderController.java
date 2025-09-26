package com.librario.controller;

import com.librario.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class ReminderController {

    private final TransactionService transactionService;

    // Trigger due reminders manually
    @GetMapping("/due")
    public ResponseEntity<String> sendDueReminders() {
        System.out.println("🔔 Triggering DUE reminders...");
        transactionService.sendDueReminders();
        return ResponseEntity.ok("✅ Due reminders sent successfully (check logs for details)");
    }

    // Trigger overdue alerts manually
    @GetMapping("/overdue")
    public ResponseEntity<String> sendOverdueAlerts() {
        System.out.println("🔔 Triggering OVERDUE alerts...");
        transactionService.sendOverdueAlerts();
        return ResponseEntity.ok("✅ Overdue alerts sent successfully (check logs for details)");
    }
}
