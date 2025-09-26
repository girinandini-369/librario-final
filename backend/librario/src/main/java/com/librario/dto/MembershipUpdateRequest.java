package com.librario.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class MembershipUpdateRequest {
    private String membershipType; // BASIC or PREMIUM
    private LocalDate startDate;
    private LocalDate endDate;
}
