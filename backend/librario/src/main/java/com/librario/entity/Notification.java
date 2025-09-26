package com.librario.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @Column(name = "recipient_email", nullable = false)
    private String recipientEmail;

    @Column(name = "read_status")
    private boolean readStatus;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;
}
