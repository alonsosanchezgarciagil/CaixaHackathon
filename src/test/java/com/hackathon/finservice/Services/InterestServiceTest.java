package com.hackathon.finservice.Services;

import com.hackathon.finservice.Entities.AccountEntity;
import com.hackathon.finservice.Repositories.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterestServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private InterestService interestService;

    @Test
    void applyInterestToInvestAccounts_ShouldIncreaseBalance_ForInvestAccounts() {
        // Arrange
        AccountEntity account1 = new AccountEntity("123", "Invest", null, 1000.0);
        AccountEntity account2 = new AccountEntity("456", "Invest", null, 2000.0);
        List<AccountEntity> investAccounts = List.of(account1, account2);

        when(accountRepository.findByAccountType("Invest")).thenReturn(investAccounts);

        // Act
        interestService.applyInterestToInvestAccounts();

        // Assert
        assertEquals(1100.0, account1.getBalance());
        assertEquals(2200.0, account2.getBalance());
        verify(accountRepository).findByAccountType("Invest");
        verify(accountRepository).save(account1);
        verify(accountRepository).save(account2);
    }
}