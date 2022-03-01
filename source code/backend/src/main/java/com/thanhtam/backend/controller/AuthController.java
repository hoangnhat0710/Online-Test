package com.thanhtam.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import javax.validation.Valid;

import com.thanhtam.backend.config.JwtUtils;
import com.thanhtam.backend.dto.CreateNewUser;
import com.thanhtam.backend.dto.LoginUser;
import com.thanhtam.backend.entity.ResponseMessage;
import com.thanhtam.backend.entity.User;
import com.thanhtam.backend.payload.response.JwtResponse;
import com.thanhtam.backend.service.UserDetailsImpl;
import com.thanhtam.backend.service.UserService;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AuthController {
    JwtUtils jwtUtils;

    private AuthenticationManager authenticationManager;

    private UserService userService;

    @Autowired
    public AuthenticationController(JwtUtils jwtUtils, AuthenticationManager authenticationManager, UserService userService) {
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> register(@RequestBody CreateNewUser req) {

        User user = userService.findByEmail(req.getEmail());
        if (!ObjectUtils.isEmpty(user)) {
            return ResponseEntity
                    .badRequest()
                    .body(new ResponseMessage("Error: Email is already in use!"));
        }

        user = userService.create(req);
        return ResponseEntity.ok(new ResponseMessage("User registered successfully!", user));
    }

    @PostMapping("/signin")

    public ResponseEntity<?> authenticateUser(@RequestBody LoginUser loginUser) {

        String username = loginUser.getUsername();
        User user = userService.getUserByUsername(username);
        if (ObjectUtils.isEmpty(user)) {
            return ResponseEntity.badRequest().build();
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }
}
