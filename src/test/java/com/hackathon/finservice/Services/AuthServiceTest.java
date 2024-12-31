package com.hackathon.finservice.Services;

import com.hackathon.finservice.Entities.TokenEntity;
import com.hackathon.finservice.Entities.UserEntity;
import com.hackathon.finservice.Repositories.TokenRepository;
import com.hackathon.finservice.Repositories.UserRepository;
import com.hackathon.finservice.Util.JwtUtil;
import com.hackathon.finservice.model.AuthLoginRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    /////////////////////////  loginUser  ///////////////////////
    @Test
    void loginUser_ShouldReturnToken_WhenCredentialsAreValid() {
        // Arrange
        AuthLoginRequestDTO authLoginRequestDTO = new AuthLoginRequestDTO();
        authLoginRequestDTO.setIdentifier("test@example.com");
        authLoginRequestDTO.setPassword("password");
        UserEntity mockUser = new UserEntity();
        String generatedToken = "mockJwtToken";
        LocalDateTime tokenExpiryDate = LocalDateTime.now().plusDays(1);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByEmail(authLoginRequestDTO.getIdentifier())).thenReturn(Optional.of(mockUser));
        when(jwtUtil.generateToken(mockUser)).thenReturn(generatedToken);
        when(jwtUtil.getExpired(generatedToken)).thenReturn(LocalDateTime.now());

        // Act
        String token = authService.loginUser(authLoginRequestDTO);

        // Assert
        assertEquals(generatedToken, token);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(authLoginRequestDTO.getIdentifier());
        verify(jwtUtil).generateToken(mockUser);
        verify(jwtUtil).getExpired(generatedToken);
        verify(tokenRepository).save(any(TokenEntity.class));
    }

    @Test
    void loginUser_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        AuthLoginRequestDTO authLoginRequestDTO = new AuthLoginRequestDTO();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByEmail(authLoginRequestDTO.getIdentifier())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.loginUser(authLoginRequestDTO));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(authLoginRequestDTO.getIdentifier());
        verifyNoInteractions(jwtUtil);
        verifyNoInteractions(tokenRepository);
    }

    @Test
    void loginUser_ShouldThrowException_WhenBadCredentials() {
        // Arrange
        AuthLoginRequestDTO authLoginRequestDTO = new AuthLoginRequestDTO();

        doThrow(new BadCredentialsException("Invalid credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.loginUser(authLoginRequestDTO));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(userRepository);
        verifyNoInteractions(jwtUtil);
        verifyNoInteractions(tokenRepository);
    }

    /////////////////////////  logoutUser  ///////////////////////
    @Test
    void logoutUser_ShouldDeleteToken_WhenTokenExists() {
        // Arrange
        String token = "mockJwtToken";

        doNothing().when(tokenRepository).deleteById(token);

        // Act
        authService.logoutUser(token);

        // Assert
        verify(tokenRepository).deleteById(token);
    }

}