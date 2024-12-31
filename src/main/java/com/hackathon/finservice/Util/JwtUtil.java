package com.hackathon.finservice.Util;

import com.hackathon.finservice.Entities.UserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JwtUtil {

    private final HttpServletRequest httpServletRequest;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    public JwtUtil(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }


    public String generateToken(UserEntity user) {
        return Jwts.builder()
                .setId(String.valueOf(user.getUserId()))
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    public int getUserId() {
        String authorizationHeader = httpServletRequest.getHeader("Authorization");
        String token = authorizationHeader.replace("Bearer ", "");
        String userId =Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getId();
        return Integer.parseInt(userId);
    }

    public LocalDateTime getExpired(String token) {
        token = token.replace("Bearer ", "");
        Date expired = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expired.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

    }

    public String getEmail(final String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

}
