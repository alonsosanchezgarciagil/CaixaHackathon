package com.hackathon.finservice.Repositories;

import com.hackathon.finservice.Entities.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Integer> {

    AccountEntity findByUser_UserIdAndAccountType(int userUserId, String accountType);

    List<AccountEntity> findByUser_UserIdOrderByAccountId(int userUserId);

    Optional<AccountEntity> findByAccountNumberAndAccountType(String accountNumber, String accountType);

    boolean existsByAccountNumber(String accountNumber);

    boolean existsByUser_UserId(int userUserId);

    List<AccountEntity> findByAccountType(String accountType);
}
