package com.hackathon.finservice.Controllers;

import com.hackathon.finservice.Services.AccountServiceI;
import com.hackathon.finservice.Services.UserServiceI;
import com.hackathon.finservice.model.AccountInfoResponseDTO;
import com.hackathon.finservice.model.UserResponseDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final UserServiceI userService;
    private final AccountServiceI accountService;

    @GetMapping("/user")
    public ResponseEntity<?> getInfoUser() {
        try{
            UserResponseDTO resp = userService.getUserInfo();
            return ResponseEntity.ok(resp);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/account")
    public ResponseEntity<?> getInfoMainAccount() {
        try{
            AccountInfoResponseDTO resp = accountService.getInfoMainAccount();
            return ResponseEntity.ok(resp);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/account/{index}")
    public ResponseEntity<?> getInfoAccount(@Valid @PathVariable int index) {
        try{
            AccountInfoResponseDTO resp = accountService.getInfoAccount(index);
            return ResponseEntity.ok(resp);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
