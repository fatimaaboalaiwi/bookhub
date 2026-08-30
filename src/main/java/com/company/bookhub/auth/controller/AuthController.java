package com.company.bookhub.auth.controller;

import com.company.bookhub.auth.dto.AuthResponse;
import com.company.bookhub.auth.dto.LoginRequest;
import com.company.bookhub.auth.dto.RegisterRequest;
import com.company.bookhub.auth.service.AuthService;
import com.company.bookhub.common.constant.ApiPaths;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.AUTH)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/register")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity.ok(
                authService.register(request)
        );
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }
}