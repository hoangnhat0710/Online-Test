package com.thanhtam.backend.controller;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.thanhtam.backend.dto.*;
import com.thanhtam.backend.entity.Role;
import com.thanhtam.backend.entity.User;
import com.thanhtam.backend.service.UserService;
import com.thanhtam.backend.ultilities.ERole;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(value = "/api/users")

public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping(value = "/profile")
    public ResponseEntity<?> getUser(@RequestParam String username) {

        User user = userService.getUserByUsername(username);

        if (user == null) {
            return ResponseEntity.ok(new ServiceResult(HttpStatus.NOT_FOUND.value(),
                    "Tên đăng nhâp " + username + " không tìm thấy!", null));
        }
        return ResponseEntity
                .ok(new ServiceResult(HttpStatus.OK.value(), "Lấy thông tin user " + username + " thành công!", user));
    }

    @PutMapping("/{id}/email/updating")
    public ResponseEntity<?> updateEmail(@RequestBody EmailUpdate data, @PathVariable Long id) {

        User user = userService.findById(id);

        boolean isCorrectPassword = passwordEncoder.matches(data.getPassword(), user.getPassword());
        if (isCorrectPassword == false) {
            return ResponseEntity.ok(new ServiceResult(HttpStatus.BAD_REQUEST.value(), "Password is wrong", null));

        }

        user.setEmail(data.getEmail());
        userService.updateUser(user);
        return ResponseEntity
                .ok(new ServiceResult(HttpStatus.OK.value(), "Update email successfully", data.getEmail()));

    }

    @PutMapping("/{id}/password/updating")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdate passwordUpdate, @PathVariable Long id) {

        User user = userService.findById(id);
        boolean isCorrectPassword = passwordEncoder.matches(passwordUpdate.getCurrentPassword(), user.getPassword());

        if (isCorrectPassword == false) {
            return ResponseEntity.ok(new ServiceResult(HttpStatus.BAD_REQUEST.value(), "Password is wrong", null));
        } else if (passwordUpdate.getNewPassword().equals(passwordUpdate.getCurrentPassword())) {

            return ResponseEntity.ok(new ServiceResult(HttpStatus.CONFLICT.value(), "This is old password", null));

        } else {
            String encodedPassword = passwordEncoder.encode(passwordUpdate.getNewPassword());
            user.setPassword(encodedPassword);
            userService.updateUser(user);
            return ResponseEntity.ok(new ServiceResult(HttpStatus.OK.value(), "Update password successfully", null));

        }

    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody CreateNewUser dto) {

        if (userService.findByEmail(dto.getEmail()) != null) {
            return ResponseEntity.badRequest()
                    .body(new ServiceResult(HttpStatus.CONFLICT.value(), "Email đã có người sử dụng!", ""));

        }

        User user = userService.create(dto);

        return ResponseEntity.ok(new ServiceResult(HttpStatus.OK.value(), "User created successfully!", user));

    }

    

}
