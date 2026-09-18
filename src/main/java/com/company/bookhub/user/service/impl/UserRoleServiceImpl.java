package com.company.bookhub.user.service.impl;

import com.company.bookhub.common.exception.DuplicateResourceException;
import com.company.bookhub.common.exception.ResourceNotFoundException;
import com.company.bookhub.role.dto.RoleResponse;
import com.company.bookhub.role.entity.Role;
import com.company.bookhub.role.repository.RoleRepository;
import com.company.bookhub.user.entity.User;
import com.company.bookhub.user.repository.UserRepository;
import com.company.bookhub.user.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public void assignRoleToUser(
            Long userId,
            Long roleId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "user.not_found"
                        )
                );

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "role.not_found"
                        )
                );

        if (user.getRoles().contains(role)) {
            throw new DuplicateResourceException(
                    "role.already_assigned"
            );
        }

        user.getRoles().add(role);

        userRepository.save(user);
    }

    @Override
    public void removeRoleFromUser(
            Long userId,
            Long roleId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "user.not_found"
                        )
                );

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "role.not_found"
                        )
                );

        if (!user.getRoles().contains(role)) {
            throw new ResourceNotFoundException(
                    "role.not_assigned"
            );
        }
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String currentUsername = authentication.getName();

//        if ("ADMIN".equals(role.getName())
//                && user.getUsername().equals(currentUsername)) {
//
//            throw new IllegalStateException(
//                    "Admin cannot remove ADMIN role from himself"
//            );
//        }


        user.getRoles().remove(role);

        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getUserRoles(
            Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "user.not_found"
                        )
                );

        return user.getRoles()
                .stream()
                .map(RoleResponse::from)
                .toList();
    }

//    #TODO assign permissions to role
}