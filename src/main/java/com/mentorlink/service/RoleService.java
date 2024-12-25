package com.mentorlink.service;

import com.mentorlink.entity.RoleEntity;
import org.springframework.stereotype.Service;


public interface RoleService {
    RoleEntity setRole(RoleEntity role);

    int removeRole(RoleEntity role);
}
