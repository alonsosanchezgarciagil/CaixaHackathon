package com.hackathon.finservice.Mapper;

import com.hackathon.finservice.Entities.AccountEntity;
import com.hackathon.finservice.Entities.UserEntity;
import com.hackathon.finservice.model.RegisterRequestDTO;
import com.hackathon.finservice.model.UserResponseDTO;

public class UserMapper {



    private UserMapper(){
    }

    public static UserEntity authRegisterRequestToUserEntity(RegisterRequestDTO authRegisterRequestDTO, String passwordHashed){

        UserEntity userEntity = new UserEntity();
        userEntity.setName(authRegisterRequestDTO.getName());
        userEntity.setEmail(authRegisterRequestDTO.getEmail());
        userEntity.setPasswordHash(passwordHashed);

        return userEntity;
    }

    public static UserResponseDTO toUserResponseDTO(UserEntity userEntity, AccountEntity accountEntity){
        UserResponseDTO authRegisterResponseDTO = new UserResponseDTO();
        authRegisterResponseDTO.setName(userEntity.getName());
        authRegisterResponseDTO.setEmail(userEntity.getEmail());
        authRegisterResponseDTO.setHashedPassword(userEntity.getPasswordHash());
        authRegisterResponseDTO.setAccountType(accountEntity.getAccountType());
        authRegisterResponseDTO.setAccountNumber(accountEntity.getAccountNumber());
        return authRegisterResponseDTO;
    }
}
