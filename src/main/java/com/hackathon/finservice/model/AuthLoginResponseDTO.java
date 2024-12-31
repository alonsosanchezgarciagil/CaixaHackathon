package com.hackathon.finservice.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthLoginResponseDTO {

    @NotBlank(message = "Token cannot be blank")
    private String token;
}
