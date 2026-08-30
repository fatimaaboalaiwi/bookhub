package com.company.bookhub.auth.service;

import com.company.bookhub.auth.dto.AuthResponse;
import com.company.bookhub.auth.dto.LoginRequest;
import com.company.bookhub.auth.dto.RegisterRequest;

public interface AuthService {

    String register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}