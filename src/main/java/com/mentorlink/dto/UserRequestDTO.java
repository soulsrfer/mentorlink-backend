package com.mentorlink.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
    Long userId;
    String username;
    String password;
    Set<String> roles; // E.g., ["USER", "ADMIN"]

}
