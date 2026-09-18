package com.company.bookhub.user.service;

import com.company.bookhub.permission.entity.Permission;
import com.company.bookhub.role.entity.Role;
import com.company.bookhub.user.entity.User;
import com.company.bookhub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found: " + username
                        )
                );

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (Role role : user.getRoles()) {

            // Permission authorities
            for (Permission permission : role.getPermissions()) {

                authorities.add(
                        new SimpleGrantedAuthority(
                                permission.getName()
                        )
                );
            }
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}