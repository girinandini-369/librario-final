package com.librario.repository;

import com.librario.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientEmail(String email);
    List<Notification> findByReadStatus(boolean readStatus);
}
