package com.hackathon.finservice.Services;

import com.hackathon.finservice.Entities.AccountEntity;
import com.hackathon.finservice.Entities.TransactionEntity;
import com.hackathon.finservice.Entities.TransactionStatus;
import com.hackathon.finservice.Entities.TransactionType;
import com.hackathon.finservice.Mapper.AccountMapper;
import com.hackathon.finservice.Repositories.AccountRepository;
import com.hackathon.finservice.Repositories.TransactionRepository;
import com.hackathon.finservice.Util.Constants;
import com.hackathon.finservice.Util.JwtUtil;
import com.hackathon.finservice.model.*;
import jakarta.persistence.NoResultException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.webjars.NotFoundException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AccountService implements AccountServiceI {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final JwtUtil jwtUtil;

    private static final Double LIMIT_TO_CHARGE_FEE_DEPOSIT = 50000D;
    private static final Double APPLY_FEE_DEPOSIT = 0.98;
    private static final Double LIMIT_TO_CHARGE_FEE_WITHDRAW = 10000D;
    private static final Double APPLY_FEE_WITHDRAW = 1.01;
    private static final Double LIMIT_TO_BE_FRAUD_TRANSFER = 10000D;
    private static final int RAPID_TRANSACTION_LIMIT = 4;
    private static final long TIME_WINDOW_SECONDS = 5;

    @Transactional
    @Override
    public void createAccount(AccountNewRequestDTO accountNewRequestDTO){
        if(!accountRepository.existsByAccountNumber(accountNewRequestDTO.getAccountNumber())){
            throw new NotFoundException("Account number does not exist");
        }
        AccountEntity accountMain = accountRepository.findByAccountNumberAndAccountType(accountNewRequestDTO.getAccountNumber(), Constants.ACCOUNT_TYPE_BY_DEFAULT)
                .orElseThrow(() -> new NotFoundException("Account type does not exist"));
        AccountEntity accountEntity = new AccountEntity(UUID.randomUUID().toString(), accountNewRequestDTO.getAccountType(), accountMain.getUser(),  0D);
        accountRepository.save(accountEntity);
    }

    @Override
    public AccountInfoResponseDTO getInfoMainAccount(){
        AccountEntity accountEntity = this.getAccount();
        return new AccountInfoResponseDTO(accountEntity.getAccountNumber(), accountEntity.getBalance(), accountEntity.getAccountType());
    }

    @Transactional(readOnly = true)
    @Override
    public AccountInfoResponseDTO getInfoAccount(int index){
        int userId = jwtUtil.getUserId();
        List<AccountEntity> accountList = accountRepository.findByUser_UserIdOrderByAccountId(userId);
        if(CollectionUtils.isEmpty(accountList) || accountList.size() < index) {
            throw new NotFoundException("Account not found");
        }
        AccountEntity accountEntity = accountList.get(index);
        return new AccountInfoResponseDTO(accountEntity.getAccountNumber(), accountEntity.getBalance(), accountEntity.getAccountType());
    }

    @Transactional
    @Override
    public void depositMoney(MoneyRequestDTO moneyRequestDTO){
        AccountEntity accountEntity = this.getAccount();
        Double amount = moneyRequestDTO.getAmount() < LIMIT_TO_CHARGE_FEE_DEPOSIT ?
                moneyRequestDTO.getAmount() : moneyRequestDTO.getAmount() * APPLY_FEE_DEPOSIT;
        accountEntity.setBalance(accountEntity.getBalance() + amount);
        accountRepository.save(accountEntity);
        TransactionEntity transactionEntity = new TransactionEntity(amount, TransactionType.CASH_DEPOSIT, TransactionStatus.PENDING, accountEntity, null);
        transactionRepository.save(transactionEntity);
    }

    @Transactional
    @Override
    public void withdraw(MoneyRequestDTO moneyRequestDTO){
        AccountEntity accountEntity = this.getAccount();
        Double amount = moneyRequestDTO.getAmount() < LIMIT_TO_CHARGE_FEE_WITHDRAW ?
                moneyRequestDTO.getAmount() : moneyRequestDTO.getAmount() * APPLY_FEE_WITHDRAW;
        if(accountEntity.getBalance() < amount){
            throw new NoResultException("Account balance is not enough");
        }
        accountEntity.setBalance(accountEntity.getBalance()- amount);
        accountRepository.save(accountEntity);
        TransactionEntity transactionEntity = new TransactionEntity(amount, TransactionType.CASH_WITHDRAWAL, TransactionStatus.PENDING,  accountEntity, null);
        transactionRepository.save(transactionEntity);
    }

    @Transactional
    @Override
    public void transfer(TransferRequestDTO transferRequestDTO){
        AccountEntity sourceAccount = this.getAccount();
        AccountEntity targetAccount = accountRepository.findByAccountNumberAndAccountType(transferRequestDTO.getTargetAccountNumber(), Constants.ACCOUNT_TYPE_BY_DEFAULT)
                .orElseThrow( () -> new NotFoundException("Target account not found"));

        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.now().minusSeconds(TIME_WINDOW_SECONDS), ZoneId.systemDefault());
        List<TransactionEntity> recentTransactions = transactionRepository.findRecentTransactions(transferRequestDTO.getTargetAccountNumber(), localDateTime);
        TransactionStatus transactionStatus = isTransactionFraud(recentTransactions, transferRequestDTO.getAmount())
                ? TransactionStatus.FRAUD : TransactionStatus.PENDING;

        if(!TransactionStatus.FRAUD.equals(transactionStatus)){
            sourceAccount.setBalance(sourceAccount.getBalance() - transferRequestDTO.getAmount());
            targetAccount.setBalance(targetAccount.getBalance() + transferRequestDTO.getAmount());
            accountRepository.save(sourceAccount);
            accountRepository.save(targetAccount);
        }
        TransactionEntity transactionEntity = new TransactionEntity(transferRequestDTO.getAmount(), TransactionType.CASH_TRANSFER, transactionStatus,  sourceAccount, targetAccount);
        transactionRepository.save(transactionEntity);

    }

    @Override
    public TransactionHistoryResponseDTO getTransactions(){
        AccountEntity accountEntity = this.getAccount();
        List<TransactionEntity> sentTransactions = accountEntity.getTransactionsSent();
        return AccountMapper.transactionHistoryResponseDTO(sentTransactions);
    }


    private AccountEntity getAccount(){
        int userId = jwtUtil.getUserId();
        if(!accountRepository.existsByUser_UserId(userId)){
            throw new NotFoundException("Account not found");
        }
        AccountEntity accountEntity = accountRepository.findByUser_UserIdAndAccountType(userId, Constants.ACCOUNT_TYPE_BY_DEFAULT);
        if(accountEntity == null){
            throw new NotFoundException("Account not found");
        }
        return accountEntity;
    }

    private boolean isTransactionFraud(List<TransactionEntity> transactions, double amount){
        return amount > LIMIT_TO_BE_FRAUD_TRANSFER || transactions.size() >= RAPID_TRANSACTION_LIMIT;
    }

}
