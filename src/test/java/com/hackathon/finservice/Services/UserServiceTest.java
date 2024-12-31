package com.hackathon.finservice.Services;

import com.hackathon.finservice.Entities.AccountEntity;
import com.hackathon.finservice.Entities.UserEntity;
import com.hackathon.finservice.Repositories.AccountRepository;
import com.hackathon.finservice.Repositories.UserRepository;
import com.hackathon.finservice.Util.JwtUtil;
import com.hackathon.finservice.model.RegisterRequestDTO;
import com.hackathon.finservice.model.UserResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    //////////////////////////// registerUser  /////////////////////

    @Test
    void registerUser_ShouldReturnUserResponse_WhenDataIsValid() {
        // Arrange
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO("name","Password1!", "test@example.com");
        UserEntity mockUserEntity = new UserEntity();
        mockUserEntity.setEmail(registerRequestDTO.getEmail());
        AccountEntity mockAccountEntity = new AccountEntity(UUID.randomUUID().toString(), "Main", mockUserEntity, 0D);

        when(userRepository.existsUserEntitiesByEmail(registerRequestDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequestDTO.getPassword())).thenReturn("hashedPassword");
        when(accountRepository.save(any(AccountEntity.class))).thenReturn(mockAccountEntity);
        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUserEntity);

        // Act
        UserResponseDTO response = userService.registerUser(registerRequestDTO);

        // Assert
        assertNotNull(response);
        verify(userRepository).existsUserEntitiesByEmail(registerRequestDTO.getEmail());
        verify(passwordEncoder).encode(registerRequestDTO.getPassword());
        verify(userRepository).save(any(UserEntity.class));
        verify(accountRepository).save(any(AccountEntity.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO("name","Password1!", "test@example.com");

        when(userRepository.existsUserEntitiesByEmail(registerRequestDTO.getEmail())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(registerRequestDTO));
        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository).existsUserEntitiesByEmail(registerRequestDTO.getEmail());
        verifyNoInteractions(passwordEncoder, accountRepository);
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailDoesNotMatchRegex() {
        // Arrange
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO("name","Password1!", "test@example");

        when(userRepository.existsUserEntitiesByEmail(registerRequestDTO.getEmail())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(registerRequestDTO));
        assertEquals("Invalid email: test@example", exception.getMessage());
        verify(userRepository).existsUserEntitiesByEmail(registerRequestDTO.getEmail());
        verifyNoInteractions(passwordEncoder, accountRepository);
    }

    @Test
    void registerUser_ShouldThrowException_WhenPasswordHasShortLength() {
        // Arrange
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO("name","Pass", "test@example.com");

        when(userRepository.existsUserEntitiesByEmail(registerRequestDTO.getEmail())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(registerRequestDTO));
        assertEquals("Password must be at least 8 characters long", exception.getMessage());
        verify(userRepository).existsUserEntitiesByEmail(registerRequestDTO.getEmail());
        verifyNoInteractions(passwordEncoder, accountRepository);
    }

    @Test
    void registerUser_ShouldThrowException_WhenPasswordHasLongLength() {
        // Arrange
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO("name","Password1!aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "test@example.com");

        when(userRepository.existsUserEntitiesByEmail(registerRequestDTO.getEmail())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(registerRequestDTO));
        assertEquals("Password must be less than 128 characters long", exception.getMessage());
        verify(userRepository).existsUserEntitiesByEmail(registerRequestDTO.getEmail());
        verifyNoInteractions(passwordEncoder, accountRepository);
    }

    @Test
    void registerUser_ShouldThrowException_WhenPasswordHasNoUppercaseLetter() {
        // Arrange
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO("name","password1!", "test@example.com");

        when(userRepository.existsUserEntitiesByEmail(registerRequestDTO.getEmail())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(registerRequestDTO));
        assertEquals("Password must contain at least one uppercase letter", exception.getMessage());
        verify(userRepository).existsUserEntitiesByEmail(registerRequestDTO.getEmail());
        verifyNoInteractions(passwordEncoder, accountRepository);
    }

    @Test
    void registerUser_ShouldThrowException_WhenPasswordHasNoDigit() {
        // Arrange
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO("name","Password!", "test@example.com");

        when(userRepository.existsUserEntitiesByEmail(registerRequestDTO.getEmail())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(registerRequestDTO));
        assertEquals("Password must contain at least one digit", exception.getMessage());
        verify(userRepository).existsUserEntitiesByEmail(registerRequestDTO.getEmail());
        verifyNoInteractions(passwordEncoder, accountRepository);
    }

    @Test
    void registerUser_ShouldThrowException_WhenPasswordHasNoSpecialCharacter() {
        // Arrange
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO("name","Password1", "test@example.com");

        when(userRepository.existsUserEntitiesByEmail(registerRequestDTO.getEmail())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(registerRequestDTO));
        assertEquals("Password must contain at least one special character", exception.getMessage());
        verify(userRepository).existsUserEntitiesByEmail(registerRequestDTO.getEmail());
        verifyNoInteractions(passwordEncoder, accountRepository);
    }

    @Test
    void registerUser_ShouldThrowException_WhenPasswordHasWhitespace() {
        // Arrange
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO("name","Passwo rd1!", "test@example.com");

        when(userRepository.existsUserEntitiesByEmail(registerRequestDTO.getEmail())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(registerRequestDTO));
        assertEquals("Password cannot contain whitespace", exception.getMessage());
        verify(userRepository).existsUserEntitiesByEmail(registerRequestDTO.getEmail());
        verifyNoInteractions(passwordEncoder, accountRepository);
    }

    //////////////////////////// getUserInfo  /////////////////////
    @Test
    void getUserInfo_ShouldReturnUserResponse_WhenUserHasMainAccount() throws Exception {
        // Arrange
        int userId = 1;
        UserEntity mockUserEntity = new UserEntity();
        mockUserEntity.setUserId(userId);
        AccountEntity mockAccountEntity = new AccountEntity(UUID.randomUUID().toString(), "Main", mockUserEntity, 0D);
        mockUserEntity.setAccounts(List.of(mockAccountEntity));

        when(jwtUtil.getUserId()).thenReturn(userId);
        when(userRepository.findByUserId(userId)).thenReturn(mockUserEntity);

        // Act
        UserResponseDTO response = userService.getUserInfo();

        // Assert
        assertNotNull(response);
        verify(jwtUtil).getUserId();
        verify(userRepository).findByUserId(userId);
    }

    @Test
    void getUserInfo_ShouldThrowException_WhenUserHasNoMainAccount() {
        // Arrange
        int userId = 1;
        UserEntity mockUserEntity = new UserEntity();
        mockUserEntity.setUserId(userId);
        AccountEntity mockAccountEntity = new AccountEntity(UUID.randomUUID().toString(), "Invest", mockUserEntity, 0D);
        mockUserEntity.setAccounts(List.of(mockAccountEntity));

        when(jwtUtil.getUserId()).thenReturn(userId);
        when(userRepository.findByUserId(userId)).thenReturn(mockUserEntity);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> userService.getUserInfo());
        assertEquals("User does not have a main account", exception.getMessage());
        verify(jwtUtil).getUserId();
        verify(userRepository).findByUserId(userId);
    }
}