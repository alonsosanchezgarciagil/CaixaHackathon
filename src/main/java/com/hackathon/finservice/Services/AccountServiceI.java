package com.hackathon.finservice.Services;

import com.hackathon.finservice.model.*;
import org.springframework.transaction.annotation.Transactional;

public interface AccountServiceI {


    @Transactional
    void createAccount(AccountNewRequestDTO accountNewRequestDTO);

    AccountInfoResponseDTO getInfoMainAccount();

    @Transactional(readOnly = true)
    AccountInfoResponseDTO getInfoAccount(int index);

    @Transactional
    void depositMoney(MoneyRequestDTO moneyRequestDTO);

    @Transactional
    void withdraw(MoneyRequestDTO moneyRequestDTO);

    @Transactional
    void transfer(TransferRequestDTO transferRequestDTO);

    TransactionHistoryResponseDTO getTransactions();
}
