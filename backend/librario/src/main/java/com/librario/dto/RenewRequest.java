package com.librario.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RenewRequest {
    private Long transactionId;
    private int extraDays;
}
