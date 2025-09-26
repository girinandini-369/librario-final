package com.librario.controller;

import com.librario.entity.Notification;
import com.librario.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/add")
    public ResponseEntity<Notification> addNotification(@RequestBody Notification notification) {
        return ResponseEntity.ok(notificationService.saveNotification(notification));
    }

    @GetMapping("/recipient/{email}")
    public ResponseEntity<List<Notification>> getByRecipient(@PathVariable String email) {
        return ResponseEntity.ok(notificationService.getNotificationsByRecipient(email));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnread() {
        return ResponseEntity.ok(notificationService.getUnreadNotifications());
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAll() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }
}
