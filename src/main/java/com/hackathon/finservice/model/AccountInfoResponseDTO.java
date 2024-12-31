package com.hackathon.finservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AccountInfoResponseDTO {

    private String accountNumber;
    private Double balance;
    private String accountType;

}
