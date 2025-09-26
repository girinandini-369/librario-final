package com.librario.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "membership_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;  // plan name

    @Column(nullable = false)
    private Double price; // plan price

    @Column(name = "max_books", nullable = false)
    private Integer maxBooks;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Column(name = "borrowing_limit", nullable = false)
    private Integer borrowingLimit;

    @Column(nullable = false)
    private Integer duration; // in days

    @Column(nullable = false)
    private Double fees;

    private String type; // BASIC, PREMIUM etc.
    private String description;

    // ✅ Auto timestamps
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "fine_per_day", nullable = false)
    private Double finePerDay;  // Fine charged per overdue day

}
