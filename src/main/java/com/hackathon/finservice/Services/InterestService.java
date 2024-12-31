package com.hackathon.finservice.Services;

import com.hackathon.finservice.Entities.AccountEntity;
import com.hackathon.finservice.Repositories.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class InterestService {

    private final AccountRepository accountRepository;

    private static final double INTEREST_RATE = 1.10; // 10%
    private static final long INTEREST_INTERVAL_SECONDS = 10;

    @Scheduled(fixedRate = INTEREST_INTERVAL_SECONDS * 1000)
    @Transactional
    public void applyInterestToInvestAccounts() {
        List<AccountEntity> investAccounts = accountRepository.findByAccountType("Invest");
        for (AccountEntity account : investAccounts) {
            double interest = account.getBalance() * INTEREST_RATE;
            account.setBalance(interest);
            accountRepository.save(account);
        }
    }
}
