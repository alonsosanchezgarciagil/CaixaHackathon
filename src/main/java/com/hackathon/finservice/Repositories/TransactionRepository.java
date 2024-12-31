package com.hackathon.finservice.Repositories;

import com.hackathon.finservice.Entities.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository  extends JpaRepository<TransactionEntity, Integer> {

    @Query("SELECT t FROM TransactionEntity t WHERE t.sourceTarget.accountNumber = :targetAccountNumber AND t.transactionDate >= :localDateTime")
    List<TransactionEntity> findRecentTransactions(@Param("targetAccountNumber") String targetAccountNumber, LocalDateTime localDateTime);
}
