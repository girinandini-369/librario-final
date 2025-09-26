package com.librario.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRequest {
    private Long userId;              // required for register
    private Long planId;              // optional
    private String membershipPlanName; // 👈 added for flexibility
    private String name;              // fallback to user.name
    private String email;             // fallback to user.email
    private String status;            // default = ACTIVE
    private LocalDate startDate;
    private LocalDate endDate;
}
