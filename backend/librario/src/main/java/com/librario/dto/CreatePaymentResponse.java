package com.librario.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentResponse {
    private String orderId;
    private String keyId;
    private Long amount; // paise
    private String currency;
    private Long transactionId;
}
