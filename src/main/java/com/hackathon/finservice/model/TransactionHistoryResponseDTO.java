package com.hackathon.finservice.model;

import lombok.Data;

import java.util.List;

@Data
public class TransactionHistoryResponseDTO {

    List<TransactionResponseDTO> transactions;
}
