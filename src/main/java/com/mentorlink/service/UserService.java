package com.mentorlink.service;


import com.mentorlink.dto.UserRequestDTO;
import com.mentorlink.entity.RoleEntity;
import com.mentorlink.entity.UserEntity;

import java.util.Optional;
import java.util.Set;

public interface UserService {

    UserEntity register(UserRequestDTO user);

    Optional<UserEntity> getUser(Long userId);

    UserEntity findUserByUserName(String username);

    String verifyUser(UserEntity user);

    Set<RoleEntity> getRoles(String username);

    UserEntity assignRoleToUser(Long userId, String roleName);

}
