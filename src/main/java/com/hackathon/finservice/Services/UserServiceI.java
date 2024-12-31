package com.hackathon.finservice.Services;

import com.hackathon.finservice.model.RegisterRequestDTO;
import com.hackathon.finservice.model.UserResponseDTO;
import org.springframework.transaction.annotation.Transactional;

public interface UserServiceI {
    UserResponseDTO registerUser(RegisterRequestDTO authRegisterRequestDTO) throws IllegalArgumentException;

    @Transactional
    UserResponseDTO getUserInfo() throws Exception;
}
