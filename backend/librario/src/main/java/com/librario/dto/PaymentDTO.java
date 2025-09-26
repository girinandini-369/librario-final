package com.librario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private Long transactionId;
    private Long memberId;
    private String memberName;
    private Double amount;   // in INR
    private String currency;
    private String status;
    private String orderId;
    private String paymentId;
    private LocalDateTime createdAt;
}
