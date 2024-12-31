package com.hackathon.finservice.Services;

import com.hackathon.finservice.Entities.AccountEntity;
import com.hackathon.finservice.Entities.TransactionEntity;
import com.hackathon.finservice.Entities.TransactionStatus;
import com.hackathon.finservice.Entities.TransactionType;
import com.hackathon.finservice.Mapper.AccountMapper;
import com.hackathon.finservice.Repositories.AccountRepository;
import com.hackathon.finservice.Repositories.TransactionRepository;
import com.hackathon.finservice.Util.JwtUtil;
import com.hackathon.finservice.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AccountService accountService;

    //////////////////////////// createAccount  /////////////////////
    @Test
    void createAccount_ShouldCreateAccount_WhenDataIsValid() {
        // Arrange
        AccountNewRequestDTO requestDTO = new AccountNewRequestDTO("123456", "Savings");
        AccountEntity mockAccountMain = new AccountEntity("123456", "Main", null, 0.0);

        when(accountRepository.existsByAccountNumber(requestDTO.getAccountNumber())).thenReturn(true);
        when(accountRepository.findByAccountNumberAndAccountType(requestDTO.getAccountNumber(), "Main"))
                .thenReturn(Optional.of(mockAccountMain));

        // Act
        accountService.createAccount(requestDTO);

        // Assert
        verify(accountRepository).existsByAccountNumber(requestDTO.getAccountNumber());
        verify(accountRepository).findByAccountNumberAndAccountType(requestDTO.getAccountNumber(), "Main");
        verify(accountRepository).save(any(AccountEntity.class));
    }

    @Test
    void createAccount_ShouldThrowException_WhenAccountDoesNotExist() {
        // Arrange
        AccountNewRequestDTO requestDTO = new AccountNewRequestDTO("123456", "Savings");

        when(accountRepository.existsByAccountNumber(requestDTO.getAccountNumber())).thenReturn(false);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> accountService.createAccount(requestDTO));
        assertEquals("Account number does not exist", exception.getMessage());
        verify(accountRepository).existsByAccountNumber(requestDTO.getAccountNumber());
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void createAccount_ShouldThrowException_WhenAccountTypeDoesNotExist() {

        AccountNewRequestDTO requestDTO = new AccountNewRequestDTO("123456", "Savings");
        AccountEntity mockAccountMain = new AccountEntity("123456", "Main", null, 0.0);

        when(accountRepository.existsByAccountNumber(requestDTO.getAccountNumber())).thenReturn(true);
        when(accountRepository.findByAccountNumberAndAccountType(requestDTO.getAccountNumber(), "Main"))
                .thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> accountService.createAccount(requestDTO));
        assertEquals("Account type does not exist", exception.getMessage());
        verify(accountRepository).existsByAccountNumber(requestDTO.getAccountNumber());
        verifyNoMoreInteractions(accountRepository);
    }

    //////////////////////////// getInfoMainAccount  /////////////////////
    @Test
    void getInfoMainAccount_ShouldReturnAccountInfo_WhenAccountExists() {
        AccountEntity mockAccount = new AccountEntity("123456", "Main", null, 1000.0);

        when(accountRepository.existsByUser_UserId(anyInt())).thenReturn(true);
        when(accountRepository.findByUser_UserIdAndAccountType(anyInt(), eq("Main"))).thenReturn(mockAccount);

        AccountInfoResponseDTO response = accountService.getInfoMainAccount();

        assertNotNull(response);
        assertEquals("123456", response.getAccountNumber());
        assertEquals(1000.0, response.getBalance());
        assertEquals("Main", response.getAccountType());
        verify(accountRepository).existsByUser_UserId(anyInt());
        verify(accountRepository).findByUser_UserIdAndAccountType(anyInt(), eq("Main"));
    }

    @Test
    void getInfoMainAccount_ShouldThrowException_WhenUserDoesNotExist() {

        when(accountRepository.existsByUser_UserId(anyInt())).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> accountService.getInfoMainAccount());
        assertEquals("Account not found", exception.getMessage());
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void getInfoMainAccount_ShouldThrowException_WhenAccountDoesNotExist() {

        when(accountRepository.existsByUser_UserId(anyInt())).thenReturn(true);
        when(accountRepository.findByUser_UserIdAndAccountType(anyInt(), eq("Main"))).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> accountService.getInfoMainAccount());
        assertEquals("Account not found", exception.getMessage());
        verify(accountRepository).existsByUser_UserId(anyInt());
        verify(accountRepository).findByUser_UserIdAndAccountType(anyInt(), eq("Main"));
    }

    //////////////////////////// getInfoAccount  /////////////////////
    @Test
    void getInfoAccount_ShouldReturnAccountInfo_WhenIndexIsValid() {
        int userId = 1;
        List<AccountEntity> accountList = List.of(
                new AccountEntity("123456", "Main", null, 1000.0),
                new AccountEntity("789012", "Savings", null, 2000.0)
        );

        when(jwtUtil.getUserId()).thenReturn(userId);
        when(accountRepository.findByUser_UserIdOrderByAccountId(userId)).thenReturn(accountList);

        AccountInfoResponseDTO response = accountService.getInfoAccount(1);

        assertNotNull(response);
        assertEquals("789012", response.getAccountNumber());
        assertEquals(2000.0, response.getBalance());
        assertEquals("Savings", response.getAccountType());
        verify(jwtUtil).getUserId();
        verify(accountRepository).findByUser_UserIdOrderByAccountId(userId);
    }

    @Test
    void getInfoAccount_ShouldThrowException_WhenListIsEmpty() {
        int userId = 1;
        List<AccountEntity> accountList = Collections.emptyList();

        when(jwtUtil.getUserId()).thenReturn(userId);
        when(accountRepository.findByUser_UserIdOrderByAccountId(userId)).thenReturn(accountList);

        Exception exception = assertThrows(Exception.class, () -> accountService.getInfoAccount(0));
        assertEquals("Account not found", exception.getMessage());
        verify(jwtUtil).getUserId();
        verify(accountRepository).findByUser_UserIdOrderByAccountId(userId);
    }

    @Test
    void getInfoAccount_ShouldThrowException_WhenIndexIsInvalid() {
        int userId = 1;
        List<AccountEntity> accountList = List.of(
                new AccountEntity("123456", "Main", null, 1000.0),
                new AccountEntity("789012", "Savings", null, 2000.0)
        );

        when(jwtUtil.getUserId()).thenReturn(userId);
        when(accountRepository.findByUser_UserIdOrderByAccountId(userId)).thenReturn(accountList);

        Exception exception = assertThrows(Exception.class, () -> accountService.getInfoAccount(5));
        assertEquals("Account not found", exception.getMessage());
        verify(jwtUtil).getUserId();
        verify(accountRepository).findByUser_UserIdOrderByAccountId(userId);
    }

    //////////////////////////// depositMoney  /////////////////////
    @Test
    void depositMoney_ShouldIncreaseBalance_WhenAmountIsValid() {
        // Arrange
        MoneyRequestDTO moneyRequestDTO = new MoneyRequestDTO(1000.0);
        AccountEntity mockAccount = new AccountEntity("123456", "Main", null, 5000.0);

        when(accountRepository.existsByUser_UserId(anyInt())).thenReturn(true);
        when(accountRepository.findByUser_UserIdAndAccountType(anyInt(), eq("Main"))).thenReturn(mockAccount);

        // Act
        accountService.depositMoney(moneyRequestDTO);

        // Assert
        assertEquals(6000.0, mockAccount.getBalance());
        verify(accountRepository).save(mockAccount);
        verify(transactionRepository).save(any(TransactionEntity.class));
    }

    @Test
    void depositMoney_ShouldIncreaseBalance_WhenAmountIsHigherThanLimit() {
        // Arrange
        MoneyRequestDTO moneyRequestDTO = new MoneyRequestDTO(50000.0);
        AccountEntity mockAccount = new AccountEntity("123456", "Main", null, 5000.0);

        when(accountRepository.existsByUser_UserId(anyInt())).thenReturn(true);
        when(accountRepository.findByUser_UserIdAndAccountType(anyInt(), eq("Main"))).thenReturn(mockAccount);

        // Act
        accountService.depositMoney(moneyRequestDTO);

        // Assert
        assertEquals(54000.0, mockAccount.getBalance());
        verify(accountRepository).save(mockAccount);
        verify(transactionRepository).save(any(TransactionEntity.class));
    }

    //////////////////////////// withdraw  /////////////////////

    @Test
    void withdraw_ShouldReduceBalance_WhenSufficientBalance() {
        MoneyRequestDTO moneyRequestDTO = new MoneyRequestDTO(500.0);
        AccountEntity mockAccount = new AccountEntity("123456", "Main", null, 1000.0);

        when(accountRepository.existsByUser_UserId(anyInt())).thenReturn(true);
        when(accountRepository.findByUser_UserIdAndAccountType(anyInt(), eq("Main"))).thenReturn(mockAccount);

        accountService.withdraw(moneyRequestDTO);

        assertEquals(500.0, mockAccount.getBalance());
        verify(accountRepository).save(mockAccount);
        verify(transactionRepository).save(any(TransactionEntity.class));
    }

    @Test
    void withdraw_ShouldThrowException_WhenInsufficientBalance() {
        MoneyRequestDTO moneyRequestDTO = new MoneyRequestDTO(1500.0);
        AccountEntity mockAccount = new AccountEntity("123456", "Main", null, 100.0);

        when(accountRepository.existsByUser_UserId(anyInt())).thenReturn(true);
        when(accountRepository.findByUser_UserIdAndAccountType(anyInt(), eq("Main"))).thenReturn(mockAccount);

        Exception exception = assertThrows(Exception.class, () -> accountService.withdraw(moneyRequestDTO));
        assertEquals("Account balance is not enough", exception.getMessage());
        verify(accountRepository, never()).save(mockAccount);
        verify(transactionRepository, never()).save(any(TransactionEntity.class));
    }

    @Test
    void withdraw_ShouldApplyFee_WhenAmountIsHigherThanLimit() {
        MoneyRequestDTO moneyRequestDTO = new MoneyRequestDTO(10000.0);
        AccountEntity mockAccount = new AccountEntity("123456", "Main", null, 15000.0);

        when(accountRepository.existsByUser_UserId(anyInt())).thenReturn(true);
        when(accountRepository.findByUser_UserIdAndAccountType(anyInt(), eq("Main"))).thenReturn(mockAccount);

        accountService.withdraw(moneyRequestDTO);

        assertEquals(4900.0, mockAccount.getBalance());
        verify(accountRepository).save(mockAccount);
        verify(transactionRepository).save(any(TransactionEntity.class));
    }

    //////////////////////////// transfer  /////////////////////
    @Test
    void transfer_ShouldUpdateBalances_WhenTransactionIsValid() {
        // Arrange
        TransferRequestDTO transferRequestDTO = new TransferRequestDTO("123456", 200.0);
        AccountEntity sourceAccount = new AccountEntity("789012", "Main", null, 1000.0);
        AccountEntity targetAccount = new AccountEntity("123456", "Main", null, 500.0);
        List<TransactionEntity> recentTransactions = List.of();

        when(accountRepository.findByAccountNumberAndAccountType(transferRequestDTO.getTargetAccountNumber(), "Main"))
                .thenReturn(Optional.of(targetAccount));
        when(accountRepository.existsByUser_UserId(anyInt())).thenReturn(true);
        when(accountRepository.findByUser_UserIdAndAccountType(anyInt(), eq("Main"))).thenReturn(sourceAccount);
        when(transactionRepository.findRecentTransactions(eq(transferRequestDTO.getTargetAccountNumber()), any(LocalDateTime.class)))
                .thenReturn(recentTransactions);

        // Act
        accountService.transfer(transferRequestDTO);

        // Assert
        assertEquals(800.0, sourceAccount.getBalance());
        assertEquals(700.0, targetAccount.getBalance());
        verify(accountRepository).save(sourceAccount);
        verify(accountRepository).save(targetAccount);
        verify(transactionRepository).save(any(TransactionEntity.class));
    }

    @Test
    void transfer_ShouldThrowException_WhenAccountNotFound() {
        // Arrange
        TransferRequestDTO transferRequestDTO = new TransferRequestDTO("123456", 200.0);
        AccountEntity sourceAccount = new AccountEntity("789012", "Main", null, 1000.0);

        when(accountRepository.existsByUser_UserId(anyInt())).thenReturn(true);
        when(accountRepository.findByUser_UserIdAndAccountType(anyInt(), eq("Main"))).thenReturn(sourceAccount);
        when(accountRepository.findByAccountNumberAndAccountType(transferRequestDTO.getTargetAccountNumber(), "Main"))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> accountService.transfer(transferRequestDTO));
        assertEquals("Target account not found", exception.getMessage());

        // Assert
        verifyNoMoreInteractions(accountRepository);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void transfer_ShouldBeFraud_WhenAmountIsHigherThanLimit() {
        // Arrange
        TransferRequestDTO transferRequestDTO = new TransferRequestDTO("123456", 20000.0);
        AccountEntity sourceAccount = new AccountEntity("789012", "Main", null, 100000.0);
        AccountEntity targetAccount = new AccountEntity("123456", "Main", null, 500000.0);
        List<TransactionEntity> recentTransactions = List.of();

        when(accountRepository.findByAccountNumberAndAccountType(transferRequestDTO.getTargetAccountNumber(), "Main"))
                .thenReturn(Optional.of(targetAccount));
        when(accountRepository.existsByUser_UserId(anyInt())).thenReturn(true);
        when(accountRepository.findByUser_UserIdAndAccountType(anyInt(), eq("Main"))).thenReturn(sourceAccount);
        when(transactionRepository.findRecentTransactions(eq(transferRequestDTO.getTargetAccountNumber()), any(LocalDateTime.class)))
                .thenReturn(recentTransactions);

        // Act
        accountService.transfer(transferRequestDTO);

        // Assert
        assertEquals(100000.0, sourceAccount.getBalance());
        assertEquals(500000.0, targetAccount.getBalance());
        verifyNoMoreInteractions(accountRepository);
        verify(transactionRepository).save(any(TransactionEntity.class));
    }

    @Test
    void transfer_ShouldBeFraud_WhenNumTransactionsIsHigherThanLimit() {
        // Arrange
        TransferRequestDTO transferRequestDTO = new TransferRequestDTO("123456", 200.0);
        AccountEntity sourceAccount = new AccountEntity("789012", "Main", null, 100000.0);
        AccountEntity targetAccount = new AccountEntity("123456", "Main", null, 500000.0);
        List<TransactionEntity> recentTransactions = List.of(
                new TransactionEntity(100.0, TransactionType.CASH_DEPOSIT, TransactionStatus.PENDING, sourceAccount, targetAccount),
                new TransactionEntity(100.0, TransactionType.CASH_DEPOSIT, TransactionStatus.PENDING, sourceAccount, targetAccount),
                new TransactionEntity(100.0, TransactionType.CASH_DEPOSIT, TransactionStatus.PENDING, sourceAccount, targetAccount),
                new TransactionEntity(100.0, TransactionType.CASH_DEPOSIT, TransactionStatus.PENDING, sourceAccount, targetAccount),
                new TransactionEntity(100.0, TransactionType.CASH_DEPOSIT, TransactionStatus.PENDING, sourceAccount, targetAccount)
        );

        when(accountRepository.findByAccountNumberAndAccountType(transferRequestDTO.getTargetAccountNumber(), "Main"))
                .thenReturn(Optional.of(targetAccount));
        when(accountRepository.existsByUser_UserId(anyInt())).thenReturn(true);
        when(accountRepository.findByUser_UserIdAndAccountType(anyInt(), eq("Main"))).thenReturn(sourceAccount);
        when(transactionRepository.findRecentTransactions(eq(transferRequestDTO.getTargetAccountNumber()), any(LocalDateTime.class)))
                .thenReturn(recentTransactions);

        // Act
        accountService.transfer(transferRequestDTO);

        // Assert
        assertEquals(100000.0, sourceAccount.getBalance());
        assertEquals(500000.0, targetAccount.getBalance());
        verifyNoMoreInteractions(accountRepository);
        verify(transactionRepository).save(any(TransactionEntity.class));
    }

    //////////////////////////// getTransactions  /////////////////////
    @Test
    void getTransactions_ShouldReturnTransactionHistory_WhenTransactionsExist() {
        AccountEntity mockAccount = new AccountEntity("123456", "Main", null, 1000.0);
        MockedStatic<AccountMapper> mockedStatic = mockStatic(AccountMapper.class);
        List<TransactionResponseDTO> transactionsResponse = List.of(
                new TransactionResponseDTO(1, 100.0, "CASH_DEPOSIT", "TransactionStatus.PENDING", 7686, "sourceAccount", "N/A")
        );
        List<TransactionEntity> transactions = List.of(
                new TransactionEntity(100.0, TransactionType.CASH_DEPOSIT, TransactionStatus.PENDING, mockAccount, null)
        );
        TransactionHistoryResponseDTO res = new TransactionHistoryResponseDTO();
        res.setTransactions(transactionsResponse);
        mockAccount.setTransactionsSent(transactions);

        when(accountRepository.existsByUser_UserId(anyInt())).thenReturn(true);
        when(accountRepository.findByUser_UserIdAndAccountType(anyInt(), eq("Main"))).thenReturn(mockAccount);
        mockedStatic.when(() ->AccountMapper.transactionHistoryResponseDTO(transactions)).thenReturn(res);

        TransactionHistoryResponseDTO response = accountService.getTransactions();

        assertNotNull(response);
        assertEquals(TransactionType.CASH_DEPOSIT.toString(), response.getTransactions().getFirst().getTransactionType());
        assertEquals(100.0, response.getTransactions().getFirst().getAmount());
        verify(accountRepository).existsByUser_UserId(anyInt());
        verify(accountRepository).findByUser_UserIdAndAccountType(anyInt(), eq("Main"));
        mockedStatic.close();
    }

}