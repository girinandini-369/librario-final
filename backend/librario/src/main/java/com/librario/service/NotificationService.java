package com.librario.service;

import com.librario.entity.Notification;
import com.librario.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * In-app notification service used by controllers and transaction flows.
 * Provides:
 *  - saveNotification(Notification)
 *  - createNotification(recipientEmail, message)
 *  - getNotificationsByRecipient(email)
 *  - getUnreadNotifications()
 *  - getAllNotifications()
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * Save (or update) a Notification entity.
     * Ensures sentAt and readStatus defaults.
     */
    public Notification saveNotification(Notification notification) {
        if (notification == null) throw new IllegalArgumentException("Notification cannot be null");
        if (notification.getSentAt() == null) notification.setSentAt(LocalDateTime.now());
        // default unread if not explicitly set
        // (primitive boolean defaults to false if not set, but leave safe check)
        // notification.setReadStatus(notification.isReadStatus()); // not necessary
        return notificationRepository.save(notification);
    }

    /**
     * Convenience factory + save for a simple notification.
     */
    public Notification createNotification(String recipientEmail, String message) {
        Notification n = Notification.builder()
                .recipientEmail(recipientEmail)
                .message(message)
                .readStatus(false)
                .sentAt(LocalDateTime.now())
                .build();
        return notificationRepository.save(n);
    }

    /**
     * Get notifications for a specific recipient (by email).
     */
    public List<Notification> getNotificationsByRecipient(String email) {
        return notificationRepository.findByRecipientEmail(email);
    }

    /**
     * Get unread notifications (readStatus = false).
     */
    public List<Notification> getUnreadNotifications() {
        return notificationRepository.findByReadStatus(false);
    }

    /**
     * Return all notifications.
     */
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
}
