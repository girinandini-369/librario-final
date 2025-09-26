package com.librario.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
    private String name; // used during registration
}
