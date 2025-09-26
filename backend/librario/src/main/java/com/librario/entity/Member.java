package com.librario.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // link to User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // optional: link to MembershipPlan
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_plan_id")
    private MembershipPlan membershipPlan;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String status; // ACTIVE / INACTIVE

    @Column(nullable = false)
    private String membershipType; // ✅ Added field for BASIC / PREMIUM etc.

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}
