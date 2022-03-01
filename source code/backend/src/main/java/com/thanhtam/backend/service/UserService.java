package com.thanhtam.backend.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.thanhtam.backend.dto.CreateNewUser;
import com.thanhtam.backend.entity.Role;
import com.thanhtam.backend.entity.User;
import com.thanhtam.backend.repository.UserRepository;
import com.thanhtam.backend.ultilities.ERole;

import org.apache.poi.hssf.record.PageBreakRecord.Break;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id).get();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User create(CreateNewUser dto) {

        Set<Role> roles = new HashSet<>();

        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();

        String roleName;

        switch (dto.getRoleName()) {
            case ROLE_ADMIN:
                roleName = ERole.ROLE_ADMIN.toString();
                break;

            case ROLE_LECTURER:
                roleName = ERole.ROLE_LECTURER.toString();
                break;

            default:
                roleName = ERole.ROLE_STUDENT.toString();
                break;

        }

        Role role = roleService.findByName(roleName);
        roles.add(role);

        user.setRoles(roles);

        return userRepository.save(user);

    }

    public List<User> findAllByIntakeId(Long intakeId) {
        return userRepository.findByIntakeId(intakeId);
    }

}
