package com.hackathon.finservice.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AccountNewRequestDTO {

    @NotBlank(message = "AccountNumber cannot be blank")
    private String accountNumber;
    @NotBlank(message = "AccountType cannot be blank")
    private String accountType;
}
