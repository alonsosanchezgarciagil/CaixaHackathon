package com.hackathon.finservice.Controllers;

import com.hackathon.finservice.Services.AccountServiceI;
import com.hackathon.finservice.model.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountServiceI accountService;

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@Valid @RequestBody AccountNewRequestDTO accountNewRequestDTO) {
        try {
            accountService.createAccount(accountNewRequestDTO);
            return ResponseEntity.ok(new MessageResponseDTO("New account added successfully for user"));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@Valid@RequestBody MoneyRequestDTO moneyRequestDTO) {
        try {
            accountService.depositMoney(moneyRequestDTO);
            return ResponseEntity.ok(new MessageResponseDTO("Cash deposited successfully"));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@Valid@RequestBody MoneyRequestDTO moneyRequestDTO) {
        try {
            accountService.withdraw(moneyRequestDTO);
            return ResponseEntity.ok(new MessageResponseDTO("Cash withdrawn successfully"));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/fund-transfer")
    public ResponseEntity<?> transfer(@Valid@RequestBody TransferRequestDTO transferRequestDTO) {
        try {
            accountService.transfer(transferRequestDTO);
            return ResponseEntity.ok(new MessageResponseDTO("Fund transferred successfully"));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions() {
        try {
            TransactionHistoryResponseDTO res = accountService.getTransactions();
            return ResponseEntity.ok(res);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
