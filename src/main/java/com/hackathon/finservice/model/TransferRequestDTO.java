package com.hackathon.finservice.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDTO {

    @NotBlank(message = "targetAccountNumber cannot be null or empty")
    private String targetAccountNumber;
    @NotBlank(message = "amount cannot be null or empty")
    private double amount;
}
