package com.hackathon.finservice.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegisterRequestDTO {
    @NotBlank(message = "name cannot be null or empty")
    private String name;
    @NotBlank(message = "password cannot be null or empty")
    private String password;
    @NotBlank(message = "email cannot be null or empty")
    private String email;
}
