package com.hackathon.finservice.Mapper;

import com.hackathon.finservice.Entities.TransactionEntity;
import com.hackathon.finservice.model.TransactionHistoryResponseDTO;
import com.hackathon.finservice.model.TransactionResponseDTO;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class AccountMapper {

    private AccountMapper() {
    }

    public static TransactionHistoryResponseDTO transactionHistoryResponseDTO(List<TransactionEntity> transactionEntities) {

        if(CollectionUtils.isEmpty(transactionEntities)){
            return null;
        }
        TransactionHistoryResponseDTO transactionHistoryResponseDTO = new TransactionHistoryResponseDTO();
        List<TransactionResponseDTO> transactions = new ArrayList<>();
        for(TransactionEntity transactionEntity : transactionEntities){
            TransactionResponseDTO transactionResponseDTO = new TransactionResponseDTO();
            transactionResponseDTO.setId(transactionEntity.getTransactionId());
            transactionResponseDTO.setAmount(transactionEntity.getAmount());
            transactionResponseDTO.setTransactionStatus(transactionEntity.getTransactionStatus().toString());
            transactionResponseDTO.setTransactionType(transactionEntity.getTransactionType().toString());
            transactionResponseDTO.setTransactionDate(getDateInEpoch(transactionEntity.getTransactionDate()));
            transactionResponseDTO.setSourceAccountNumber(transactionEntity.getSourceAccount().getAccountNumber());
            if(transactionEntity.getSourceTarget() == null){
                transactionResponseDTO.setTargetAccountNumber("N/A");
            }
            else {
                transactionResponseDTO.setTargetAccountNumber(transactionEntity.getSourceTarget().getAccountNumber());
            }
            transactions.add(transactionResponseDTO);
        }
        transactionHistoryResponseDTO.setTransactions(transactions);
        return transactionHistoryResponseDTO;
    }


    private static long getDateInEpoch (LocalDateTime localDateTime){
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        Instant instant = zonedDateTime.toInstant();
        return instant.toEpochMilli();
    }
}
