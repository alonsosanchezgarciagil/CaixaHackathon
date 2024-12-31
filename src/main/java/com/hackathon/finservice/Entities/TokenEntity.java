package com.hackathon.finservice.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tokens")
public class TokenEntity {

    @Id
    @Column(name = "token_id", unique = true, nullable = false)
    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "expired_at", updatable = false)
    private LocalDateTime expiredAt;

    public TokenEntity(String token, UserEntity userId, LocalDateTime expiredAt) {
        this.token = token;
        this.user = userId;
        this.expiredAt = expiredAt;
    }
}