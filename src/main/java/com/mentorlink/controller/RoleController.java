package com.mentorlink.controller;

import com.mentorlink.entity.RoleEntity;
import com.mentorlink.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/role")
public class RoleController {

    @Autowired
    RoleService roleService;

    @PostMapping("/set")
    public ResponseEntity<RoleEntity> setRole(@RequestBody RoleEntity role) {
        RoleEntity newRole =roleService.setRole(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(newRole);
    }

    @PostMapping("/remove")
    public ResponseEntity<Integer> removeRole(@RequestBody RoleEntity role) {
        int newRole =roleService.removeRole(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(newRole);
    }

}
