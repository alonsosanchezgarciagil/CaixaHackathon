package com.hackathon.finservice.Services;

import com.hackathon.finservice.Entities.TokenEntity;
import com.hackathon.finservice.Entities.UserEntity;
import com.hackathon.finservice.Repositories.TokenRepository;
import com.hackathon.finservice.Repositories.UserRepository;
import com.hackathon.finservice.Util.JwtUtil;
import com.hackathon.finservice.model.AuthLoginRequestDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AuthService implements AuthServiceI {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public String loginUser(AuthLoginRequestDTO authLoginRequestDTO){

        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authLoginRequestDTO.getIdentifier(), authLoginRequestDTO.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Bad credentials");
        }

        UserEntity userEntity = userRepository.findByEmail(authLoginRequestDTO.getIdentifier())
                .orElseThrow(() -> new BadCredentialsException("User not found for the given identifier: " + authLoginRequestDTO.getIdentifier()));

        String token = jwtUtil.generateToken(userEntity);
        LocalDateTime localDate = jwtUtil.getExpired(token);
        tokenRepository.save(new TokenEntity(token, userEntity, localDate) );
        return token;
    }

    @Override
    @Transactional
    public void logoutUser(String token){
        tokenRepository.deleteById(token);
    }
}
