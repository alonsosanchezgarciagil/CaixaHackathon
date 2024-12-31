package com.hackathon.finservice.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "transactions")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int transactionId;

    @Column(name = "amount")
    private Double amount;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    @Column(name = "transaction_date", updatable = false)
    private LocalDateTime transactionDate;

    @ManyToOne
    @JoinColumn(name = "source_account")
    private AccountEntity sourceAccount;

    @ManyToOne
    @JoinColumn(name = "source_target")
    private AccountEntity sourceTarget;

    public TransactionEntity(Double amount, TransactionType transactionType, TransactionStatus transactionStatus, AccountEntity sourceAccount, AccountEntity sourceTarget) {
        this.amount = amount;
        this.transactionType = transactionType;
        this.sourceAccount = sourceAccount;
        this.transactionStatus = transactionStatus;
        this.sourceTarget = sourceTarget;
    }

    @PrePersist
    protected void onCreate() {
        transactionDate = LocalDateTime.now();
    }

}