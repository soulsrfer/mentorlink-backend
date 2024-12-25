package com.mentorlink.service.impl;

import com.mentorlink.entity.RoleEntity;
import com.mentorlink.repository.RoleRepository;
import com.mentorlink.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleRepository roleRepository;
    @Override
    public RoleEntity setRole(RoleEntity role) {
        if(roleRepository.findByName(role.getName().toUpperCase()) != null){
            return null;    // Role already exists
        }
        role.setName(role.getName().toUpperCase());
        return roleRepository.save(role);
    }
    @Override
    public int removeRole(RoleEntity role) {
        return roleRepository.deleteByName(role.getName().toUpperCase());
    }


}
