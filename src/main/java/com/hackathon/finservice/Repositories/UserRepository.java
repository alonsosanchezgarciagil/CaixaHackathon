package com.hackathon.finservice.Repositories;

import com.hackathon.finservice.Entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    boolean existsUserEntitiesByEmail(String email);

    Optional<UserEntity> findByEmail(String email);

    UserEntity findByUserId(int userId);
}
