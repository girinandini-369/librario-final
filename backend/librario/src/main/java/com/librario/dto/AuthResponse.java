package com.librario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String message;
    private String role;
    private Long memberId;  // ✅ add this
//private String email;   // ✅ add this
}
