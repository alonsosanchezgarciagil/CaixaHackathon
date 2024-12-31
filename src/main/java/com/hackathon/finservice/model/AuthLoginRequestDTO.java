package com.hackathon.finservice.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class AuthLoginRequestDTO {

    @NotBlank(message = "Identifier cannot be blank")
    private String identifier;
    @NotBlank(message = "Password cannot be blank")
    private String password;
}
