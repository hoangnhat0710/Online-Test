package com.thanhtam.backend.service;

import com.amazonaws.services.frauddetector.model.Role;
import com.thanhtam.backend.repository.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public com.thanhtam.backend.entity.Role findByName(String name) {
        return roleRepository.findByName(name);
    }


    
}
