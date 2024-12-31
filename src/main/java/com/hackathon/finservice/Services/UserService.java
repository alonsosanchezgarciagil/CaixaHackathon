package com.hackathon.finservice.Services;

import com.hackathon.finservice.Entities.AccountEntity;
import com.hackathon.finservice.Entities.UserEntity;
import com.hackathon.finservice.Mapper.UserMapper;
import com.hackathon.finservice.Repositories.AccountRepository;
import com.hackathon.finservice.Repositories.UserRepository;
import com.hackathon.finservice.Util.JwtUtil;
import com.hackathon.finservice.model.RegisterRequestDTO;
import com.hackathon.finservice.model.UserResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService implements UserServiceI {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final JwtUtil jwtUtil;

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String ACCOUNT_TYPE_BY_DEFAULT = "Main";

    @Override
    @Transactional
    public UserResponseDTO registerUser(RegisterRequestDTO authRegisterRequestDTO) throws IllegalArgumentException {
        checkEmail(authRegisterRequestDTO.getEmail());
        checkPassword(authRegisterRequestDTO.getPassword());
        String passwordHashed = passwordEncoder.encode(authRegisterRequestDTO.getPassword());
        UserEntity userEntity = UserMapper.authRegisterRequestToUserEntity(authRegisterRequestDTO, passwordHashed);
        userRepository.save(userEntity);
        AccountEntity accountEntity = new AccountEntity(UUID.randomUUID().toString(), ACCOUNT_TYPE_BY_DEFAULT, userEntity, 0D);
        accountRepository.save(accountEntity);
        return UserMapper.toUserResponseDTO(userEntity, accountEntity);
    }

    @Transactional
    @Override
    public UserResponseDTO getUserInfo() throws Exception {
        int userId = jwtUtil.getUserId();
        UserEntity userEntity = userRepository.findByUserId(userId);
        Optional<AccountEntity> accountEntity = userEntity.getAccounts().stream()
                .filter( account -> ACCOUNT_TYPE_BY_DEFAULT.equals(account.getAccountType()))
                .findFirst();
        if(accountEntity.isEmpty()) {
            throw new Exception("User does not have a main account");
        }
        return UserMapper.toUserResponseDTO(userEntity, accountEntity.get());
    }

    private void checkPassword(String password) {
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        if (password.length() > 128) {
            throw new IllegalArgumentException("Password must be less than 128 characters long");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }

        if (!password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }

        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            throw new IllegalArgumentException("Password must contain at least one special character");
        }

        if (password.matches(".*\\s.*")) {
            throw new IllegalArgumentException("Password cannot contain whitespace");
        }
    }

    private void checkEmail(String email) {
        if(userRepository.existsUserEntitiesByEmail(email)){
            throw new IllegalArgumentException("Email already exists");
        }
        if (!email.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("Invalid email: " + email);
        }
    }

}
