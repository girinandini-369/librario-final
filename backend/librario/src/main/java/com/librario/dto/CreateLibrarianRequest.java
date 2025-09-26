package com.librario.dto;

import lombok.Data;

@Data
public class CreateLibrarianRequest {
    private String name;
    private String email;
    private String password;
    private String taskName; // optional if you want to store task
}
