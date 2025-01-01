package com.mentorlink.service.impl;

import com.mentorlink.constant.RoleConstant;
import com.mentorlink.dto.UserRequestDTO;
import com.mentorlink.entity.RoleEntity;
import com.mentorlink.entity.UserEntity;
import com.mentorlink.repository.RoleRepository;
import com.mentorlink.repository.UserRepository;
import com.mentorlink.service.JWTService;
import com.mentorlink.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JWTService jwtService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(7);
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserEntity register(UserRequestDTO userRequestDTO) {
        // Check if the username already exists
        if (userRepository.findByUsername(userRequestDTO.getUsername()) != null) {
            throw new RuntimeException("Username already exists");
        }

        // Create a new user entity
        UserEntity user = new UserEntity();
        user.setUsername(userRequestDTO.getUsername());
        user.setPassword(encoder.encode(userRequestDTO.getPassword()));

        // Fetch roles from the request
        Set<String> requestedRoles = userRequestDTO.getRoles();

        // Set to hold the resolved roles
        Set<RoleEntity> roles = new HashSet<>();

        if (requestedRoles != null && !requestedRoles.isEmpty()) {
            requestedRoles.forEach(roleName -> {
                // Find the role by name or throw an exception if not found
                RoleEntity role = roleRepository.findByName(roleName.toUpperCase())
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                roles.add(role);
            });
        } else {
            // Assign default role if no roles are provided
            RoleEntity defaultRole = roleRepository.findByName(RoleConstant.USER_ROLE)
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            roles.add(defaultRole);
        }
        // Set roles to the user and save
        user.setRoles(roles);
        return userRepository.save(user);
    }


    public Optional<UserEntity> getUser(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public UserEntity findUserByUserName(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public String verifyUser(UserEntity user) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if (authentication.isAuthenticated()) {
            UserEntity verifiedUser = userRepository.findByUsername(user.getUsername());
            return jwtService.generateToken(verifiedUser);
        }
        throw new  RuntimeException("Authentication failed");
    }

    @Override
    public Set<RoleEntity> getRoles(String username) {
        UserEntity user = findUserByUserName(username);
        return user.getRoles();
    }

    @Override
    public UserEntity assignRoleToUser(Long userId, String roleName) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        RoleEntity role = roleRepository.findByName(roleName.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Role not found with name: " + roleName));

        user.addRole(role);
        return userRepository.save(user); // Save the updated user
    }

}
