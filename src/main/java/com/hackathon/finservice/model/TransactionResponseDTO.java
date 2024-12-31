package com.hackathon.finservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class TransactionResponseDTO {

    private int id;
    private double amount;
    private String transactionType;
    private String transactionStatus;
    private long transactionDate;
    private String sourceAccountNumber;
    private String targetAccountNumber;

}
