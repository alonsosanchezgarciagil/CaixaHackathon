package com.hackathon.finservice.Repositories;

import com.hackathon.finservice.Entities.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, String> {

    boolean existsByToken(String token);
}
