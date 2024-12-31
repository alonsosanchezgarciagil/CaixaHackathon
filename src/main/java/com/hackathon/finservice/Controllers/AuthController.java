package com.hackathon.finservice.Controllers;

import com.hackathon.finservice.Services.AuthServiceI;
import com.hackathon.finservice.model.AuthLoginRequestDTO;
import com.hackathon.finservice.model.AuthLoginResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class AuthController {

    private final AuthServiceI authService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthLoginRequestDTO request) {
        try {
            String token = authService.loginUser(request);
            return ResponseEntity.ok(new AuthLoginResponseDTO(token));
        }  catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        authService.logoutUser(token);
        return ResponseEntity.ok("Logged out successfully");
    }

}
