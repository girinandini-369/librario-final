package com.librario.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipPlanRequest {
    private String name;
    private Double price;
    private Integer maxBooks;
    private Integer durationDays;
    private Integer borrowingLimit;
    private Integer duration;
    private Double fees;
    private String type;
    private String description;

    private Double finePerDay; // 🔹 add this
}
