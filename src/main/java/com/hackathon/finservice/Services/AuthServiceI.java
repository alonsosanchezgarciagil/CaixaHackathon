package com.hackathon.finservice.Services;

import com.hackathon.finservice.model.AuthLoginRequestDTO;

public interface AuthServiceI {
    String loginUser(AuthLoginRequestDTO authLoginRequestDTO);

    void logoutUser(String token);
}
