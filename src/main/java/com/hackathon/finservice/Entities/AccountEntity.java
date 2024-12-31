package com.hackathon.finservice.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "accounts")
@Entity
public class AccountEntity {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int accountId;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "account_type", nullable = false)
    private String accountType;

    @Column(name = "balance", nullable = false)
    private Double balance;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(mappedBy = "sourceAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionEntity> transactionsSent;

    @OneToMany(mappedBy = "sourceTarget", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionEntity> transactionsReceived;

    public AccountEntity(String accountNumber, String accountType, UserEntity user, Double balance) {
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.user = user;
        this.balance = balance;
    }

}
