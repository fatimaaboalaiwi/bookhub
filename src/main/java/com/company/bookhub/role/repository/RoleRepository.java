/*package com.company.bookhub.role.repository;

import com.company.bookhub.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
}*/

package com.company.bookhub.role.repository;

import com.company.bookhub.role.entity.Role;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
    //boolean existsByUsers_Id(Long roleId);
    @Query(
            value = """
        SELECT EXISTS (
            SELECT 1
            FROM public.user_roles
            WHERE role_id = :roleId
        )
        """,
            nativeQuery = true
    )
    boolean existsByUsers_Id(@Param("roleId") Long roleId);
    @EntityGraph(attributePaths = "permissions")
    @Override
    List<Role> findAll();
}
