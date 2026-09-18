package com.company.bookhub.Controller.user;

import com.company.bookhub.auth.dto.AuthResponse;
import com.company.bookhub.auth.dto.LoginRequest;
import com.company.bookhub.auth.dto.RegisterRequest;
import com.company.bookhub.auth.service.AuthService;
import com.company.bookhub.common.constant.ApiPaths;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping(ApiPaths.AUTH)
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity.ok(
                authService.register(request)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

}