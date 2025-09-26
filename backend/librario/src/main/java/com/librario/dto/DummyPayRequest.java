package com.librario.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DummyPayRequest {
    private Long transactionId;   // Required always
}
