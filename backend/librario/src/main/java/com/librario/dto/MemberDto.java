package com.librario.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private String status;
    private String membershipPlanName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
}
