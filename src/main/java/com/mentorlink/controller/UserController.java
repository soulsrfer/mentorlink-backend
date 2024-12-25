package com.mentorlink.controller;

import com.mentorlink.dto.AuthDTO;
import com.mentorlink.dto.UserRequestDTO;
import com.mentorlink.entity.RoleEntity;
import com.mentorlink.entity.UserEntity;
import com.mentorlink.service.JWTService;
import com.mentorlink.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    JWTService jwtService;


    @GetMapping("/{id}")
    public Optional<UserEntity> getUser(@PathVariable("id") Long userId) {
        System.out.println(userId);
        return userService.getUser(userId);
    }

    @PostMapping("/register")
    public UserEntity registerUser(@RequestBody UserRequestDTO user) {
        return userService.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDTO> loginUser(@RequestBody UserEntity user) {

        String token = userService.verifyUser(user);
        String username = jwtService.getSubject(token);
//        Claims claims = jwtService.extractAllClaims(token);
        AuthDTO authDTO = new AuthDTO();
        UserEntity userEntity = userService.findUserByUserName(user.getUsername());
        authDTO.setId(userEntity.getId());
        authDTO.setToken(token);
        authDTO.setScopes(jwtService.getScopes(token));
        authDTO.setSubject(username);
        authDTO.setExpiresAt(jwtService.getExpiration(token));

        String[] chunks = token.split("\\.");

        Base64.Decoder decoder = Base64.getUrlDecoder();
        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));

        System.out.println(header);
        System.out.println(payload);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                        .body(authDTO);
    }

    @PostMapping("/assign-role")
    public UserEntity assignRoleToUser(@RequestBody UserRequestDTO user) {
        user.getRoles().forEach(role -> {
            userService.assignRoleToUser(user.getUserId(), role);
        });
        return userService.findUserByUserName(user.getUsername());
    }
}
