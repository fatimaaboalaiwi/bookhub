
package com.company.bookhub.user.repository;

import com.company.bookhub.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {
            "roles",
            "roles.permissions"
    })
    Optional<User> findByUsername(String username);
    long countByRoles_Id(Long roleId);

    boolean existsByUsername(String username);

    @Override
    @EntityGraph(attributePaths = {
            "roles",
            "roles.permissions"
    })
    List<User> findAll();
}
