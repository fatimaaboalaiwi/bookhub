package com.company.bookhub.user.service.impl;

import com.company.bookhub.common.exception.ResourceNotFoundException;
import com.company.bookhub.user.entity.User;
import com.company.bookhub.user.repository.UserRepository;
import com.company.bookhub.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public void deleteUser(Long userId) {

        User targetUser = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "user.not_found"
                        )
                );

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String currentUsername = authentication.getName();

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "user.not_found"
                        )
                );

        /*
         * Business protection:
         *
         * A system user cannot delete himself.
         */
        if (currentUser.getId().equals(targetUser.getId())
                && currentUser.isSystemUser()) {

            throw new AccessDeniedException(
                    "user.system_user_cannot_delete_self"
            );
        }

        userRepository.delete(targetUser);
    }
}