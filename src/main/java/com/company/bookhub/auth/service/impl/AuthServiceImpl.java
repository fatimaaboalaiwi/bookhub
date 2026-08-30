package com.company.bookhub.auth.service.impl;

import com.company.bookhub.auth.dto.AuthResponse;
import com.company.bookhub.auth.dto.LoginRequest;
import com.company.bookhub.auth.dto.RegisterRequest;
import com.company.bookhub.auth.service.AuthService;
import com.company.bookhub.security.JwtService;
import com.company.bookhub.user.entity.Role;
import com.company.bookhub.user.entity.User;
import com.company.bookhub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;


    @Override
    public String register(
            RegisterRequest request) {

        if (userRepository.existsByUsername(
                request.username())) {

            throw new IllegalArgumentException(
                    "Username already exists"
            );
        }

        User user = User.builder()
                .username(request.username())
                .password(
                        passwordEncoder.encode(
                                request.password()
                        )
                )
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);

        //sendSMS("961000000", request.password());

        return "success";
    }


    @Override
    public AuthResponse login(
            LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        String token =
                jwtService.generateToken(
                        request.username()
                );

        return new AuthResponse(token);
    }
}